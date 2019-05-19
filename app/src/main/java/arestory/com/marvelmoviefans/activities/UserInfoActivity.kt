package arestory.com.marvelmoviefans.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseActivity
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.bean.UpdateUserRequestBody
import arestory.com.marvelmoviefans.bean.UserInfo
import arestory.com.marvelmoviefans.common.GlideApp
import arestory.com.marvelmoviefans.constants.AppConstants
import arestory.com.marvelmoviefans.databinding.ActivityUserInfoBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.*
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.ares.datacontentlayout.DataContentLayout
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import ywq.ares.funapp.activity.ShowImageActivity
import java.io.File
import java.util.jar.Manifest

class UserInfoActivity : BaseDataBindingActivity<ActivityUserInfoBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_user_info

    private var userId: String? = null
    fun notifyUserInfoChange(userInfo: UserInfo) {

        UserDataSource.saveLoginUser(this@UserInfoActivity, userInfo)
        dataBinding.user = userInfo
        RxBus.get().post(userInfo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (userId == null && requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            val list = Matisse.obtainResult(data)
            val photo = list[0]
            if (!File(AppConstants.LOCAL.IMAGE_CACHE).exists()) {

                File(AppConstants.LOCAL.IMAGE_CACHE).mkdirs()

            }
            val uploadDialog = MaterialDialog.Builder(this@UserInfoActivity).content("正在上传头像")
                .canceledOnTouchOutside(false)
                .progress(true, -1)
                .build()
            uploadDialog.show()

            Luban.with(this@UserInfoActivity).load(UriUtil.getRealFilePath(this@UserInfoActivity, photo))
                .setTargetDir(AppConstants.LOCAL.IMAGE_CACHE)
                .setCompressListener(object : OnCompressListener {
                    override fun onSuccess(file: File?) {


                        UserDataSource.uploadAvatar(
                            UserDataSource.getLoginUser(this@UserInfoActivity)?.id!!,
                            file!!,
                            object : DataCallback<UserInfo> {
                                override fun onSuccess(data: UserInfo) {

                                    notifyUserInfoChange(data)
                                    uploadDialog.dismiss()
                                    ToastUtil.showToast(this@UserInfoActivity, "修改成功")

                                }

                                override fun onFail(msg: String?) {

                                    ToastUtil.showToast(this@UserInfoActivity, msg)
                                    uploadDialog.dismiss()

                                }


                            })


                    }

                    override fun onError(e: Throwable?) {
                        ToastUtil.showToast(this@UserInfoActivity, e?.message)
                        uploadDialog.dismiss()
                        e?.printStackTrace()
                    }

                    override fun onStart() {
                        print("start compress")
                    }


                }).launch()
        }
    }


    override fun doMain(savedInstanceState: Bundle?) {

        userId = intent.getStringExtra(USER_ID)
        initToolbarSetting(dataBinding.toolbar)

        dataBinding.isLoginUser = userId == null
        if (userId == null) {

            dataBinding.dataContentLayout.showContent()
            dataBinding.user = UserDataSource.getLoginUser(this)

            GlideApp.with(this).load(AppConstants.URL.FILE_PRE_URL + dataBinding.user?.avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL) .placeholder(R.drawable.placeholder).transform(
                    GlideCircleTransform(1, resources.getColor(R.color.white))
                )
                .into(dataBinding.ivAvatar)
            dataBinding.layoutExit.setOnClickListener {


                MaterialDialog.Builder(this@UserInfoActivity).title("是否退出当前用户")
                    .positiveText("确定").onPositive { dialog, _ ->

                        UserDataSource.clearLoginUser(this@UserInfoActivity)
                        ToastUtil.showToast(this@UserInfoActivity, "已退出")
                        dialog.dismiss()
                        finish()
                    }.negativeText("取消").onNegative { dialog, _ ->
                        dialog.dismiss()
                    }.show()


            }
            dataBinding.layoutAvatar.setOnClickListener {


                val permissions = RxPermissions(this@UserInfoActivity)
                val isGranted = permissions.isGranted(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                val isGranted2 = permissions.isGranted(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (isGranted && isGranted2) {

                    Matisse.from(this@UserInfoActivity).choose(MimeType.ofImage()).maxSelectable(1)
                        .imageEngine(GlideFixedEngine()).thumbnailScale(0.8f)
                        .theme(R.style.SelectPhoto)
                        .forResult(CHOOSE_IMAGE)
                } else {
                    permissions.requestEachCombined(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                        .subscribe {

                            if (it.granted) {
                                Matisse.from(this@UserInfoActivity).choose(MimeType.ofImage()).maxSelectable(1)
                                    .imageEngine(GlideFixedEngine()).thumbnailScale(0.8f).theme(R.style.SelectPhoto)

                                    .forResult(CHOOSE_IMAGE)
                            } else {
                                ToastUtil.showToast(this@UserInfoActivity, "请允许该权限，以便上传头像")
                            }
                        }

                }

            }
            dataBinding.layoutNickName.setOnClickListener {

                val uploadDialog = MaterialDialog.Builder(this@UserInfoActivity).title("修改昵称")
                    .canceledOnTouchOutside(true)
                    .input(
                        "请输入新昵称",
                        UserDataSource.getLoginUser(this@UserInfoActivity)?.nickName

                    ) { _, input ->


                    }
                    .onPositive { dialog, which ->

                        val nickName = dialog.inputEditText?.text.toString()

                        if (nickName.isEmpty()) {

                            ToastUtil.showToast(this@UserInfoActivity, "昵称不能为空")

                            return@onPositive
                        }
                        if (nickName == UserDataSource.getLoginUser(this@UserInfoActivity)?.nickName) {

                            ToastUtil.showToast(this@UserInfoActivity, "修改成功")

                            return@onPositive
                        }


                        val updateUserRequestBody = UpdateUserRequestBody()
                        updateUserRequestBody.nickName = nickName
                        UserDataSource.updateUserInfo(
                            UserDataSource.getLoginUserId(this@UserInfoActivity),
                            updateUserRequestBody,
                            object : DataCallback<UserInfo> {
                                override fun onSuccess(data: UserInfo) {

                                    notifyUserInfoChange(data)
                                    ToastUtil.showToast(this@UserInfoActivity, "修改成功")

                                }

                                override fun onFail(msg: String?) {

                                    ToastUtil.showToast(this@UserInfoActivity, msg)

                                }


                            })

                    }
                    .build()
                uploadDialog.show()

            }
            dataBinding.layoutSex.setOnClickListener {

                val uploadDialog = MaterialDialog.Builder(this@UserInfoActivity).title("修改族群")
                    .canceledOnTouchOutside(true)
                    .input(
                        "你要修改族群？",
                        UserDataSource.getLoginUser(this@UserInfoActivity)?.sex

                    ) { _, input ->


                    }
                    .onPositive { dialog, which ->

                        val sex = dialog.inputEditText?.text.toString()

                        if (sex.isEmpty()) {

                            ToastUtil.showToast(this@UserInfoActivity, "族群不能为空")

                            return@onPositive
                        }

                        if (sex == UserDataSource.getLoginUser(this@UserInfoActivity)?.sex) {

                            ToastUtil.showToast(this@UserInfoActivity, "修改成功")

                            return@onPositive
                        }

                        val updateUserRequestBody = UpdateUserRequestBody()
                        updateUserRequestBody.sex = sex
                        UserDataSource.updateUserInfo(
                            UserDataSource.getLoginUserId(this@UserInfoActivity),
                            updateUserRequestBody,
                            object : DataCallback<UserInfo> {
                                override fun onSuccess(data: UserInfo) {

                                    notifyUserInfoChange(data)
                                    ToastUtil.showToast(this@UserInfoActivity, "修改成功")

                                }

                                override fun onFail(msg: String?) {

                                    ToastUtil.showToast(this@UserInfoActivity, msg)

                                }


                            })

                    }
                    .build()
                uploadDialog.show()
            }
            dataBinding.layoutAge.setOnClickListener {


                val uploadDialog = MaterialDialog.Builder(this@UserInfoActivity).title("修改年龄")
                    .canceledOnTouchOutside(true)
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input(
                        "你到底几岁？",
                        UserDataSource.getLoginUser(this@UserInfoActivity)?.age.toString()

                    ) { _, input ->


                    }
                    .onPositive { dialog, which ->

                        val age = dialog.inputEditText?.text.toString()

                        if (age.isEmpty()) {

                            ToastUtil.showToast(this@UserInfoActivity, "年龄不能为空")

                            return@onPositive
                        }
                        if (age.toInt() == UserDataSource.getLoginUser(this@UserInfoActivity)?.age) {

                            if (age.toInt() > 70) {
                                ToastUtil.showToast(this@UserInfoActivity, "修改成功,不过你年龄真是很大啊")

                            } else {
                                ToastUtil.showToast(this@UserInfoActivity, "修改成功")
                            }

                            return@onPositive
                        }

                        val updateUserRequestBody = UpdateUserRequestBody()
                        updateUserRequestBody.age = age.toInt()
                        UserDataSource.updateUserInfo(
                            UserDataSource.getLoginUserId(this@UserInfoActivity),
                            updateUserRequestBody,
                            object : DataCallback<UserInfo> {
                                override fun onSuccess(data: UserInfo) {

                                    notifyUserInfoChange(data)
                                    if (data.age!! > 70) {
                                        ToastUtil.showToast(this@UserInfoActivity, "修改成功,不过你年龄真是很大啊")

                                    } else {
                                        ToastUtil.showToast(this@UserInfoActivity, "修改成功")

                                    }

                                }

                                override fun onFail(msg: String?) {

                                    ToastUtil.showToast(this@UserInfoActivity, msg)

                                }


                            })

                    }
                    .build()
                uploadDialog.show()

            }
            dataBinding.layoutSlogan.setOnClickListener {


                val uploadDialog = MaterialDialog.Builder(this@UserInfoActivity).title("个性签名")
                    .canceledOnTouchOutside(true)
                    .input(
                        "你想说点啥？",
                        UserDataSource.getLoginUser(this@UserInfoActivity)?.slogan
                    ) { _, input ->


                    }
                    .onPositive { dialog, _ ->

                        val slogan = dialog.inputEditText?.text.toString()
                        if (slogan == UserDataSource.getLoginUser(this@UserInfoActivity)?.slogan) {


                            ToastUtil.showToast(this@UserInfoActivity, "修改成功")

                            return@onPositive
                        }
                        val updateUserRequestBody = UpdateUserRequestBody()
                        updateUserRequestBody.slogan = slogan
                        UserDataSource.updateUserInfo(
                            UserDataSource.getLoginUserId(this@UserInfoActivity),
                            updateUserRequestBody,
                            object : DataCallback<UserInfo> {
                                override fun onSuccess(data: UserInfo) {

                                    notifyUserInfoChange(data)
                                    ToastUtil.showToast(this@UserInfoActivity, "修改成功")

                                }

                                override fun onFail(msg: String?) {

                                    ToastUtil.showToast(this@UserInfoActivity, msg)

                                }


                            })

                    }
                    .build()
                uploadDialog.show()
            }

        } else {

            getUserInfo(userId!!)

        }
    }

    private fun getUserInfo(userId: String) {


        dataBinding.dataContentLayout.showLoading()
        UserDataSource.getUserInfo(userId, object : DataCallback<UserInfo> {
            override fun onSuccess(data: UserInfo) {

                dataBinding.user = data
                dataBinding.dataContentLayout.showContent()

                initToolbarSetting(dataBinding.toolbar,data.nickName!!)
                dataBinding .layoutQuestion.setOnClickListener {


                    UserQuestionActivity.start(this@UserInfoActivity,userId,data.nickName!!)
                }
                dataBinding.layoutAvatar.setOnClickListener {

                    ShowImageActivity.start(this@UserInfoActivity,AppConstants.URL.FILE_PRE_URL + data.avatar)
                }
                GlideApp.with(this@UserInfoActivity).load(AppConstants.URL.FILE_PRE_URL + data.avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) .placeholder(R.drawable.placeholder).transform(
                        GlideCircleTransform(1, resources.getColor(R.color.white))
                    )
                    .into(dataBinding.ivAvatar)
            }

            override fun onFail(msg: String?) {

                dataBinding.dataContentLayout.showError(object : DataContentLayout.ErrorListener {
                    override fun showError(view: View) {

                        ToastUtil.showShortToast(this@UserInfoActivity, msg)
                    }

                })

            }


        })

    }


    companion object {

        const val CHOOSE_IMAGE = 200

        private const val USER_ID = "userId"
        fun start(context: Activity) {

            context.startActivity(Intent(context, UserInfoActivity::class.java))
        }
        fun startWithAnim(context: Activity,avatar:ImageView){
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(context,avatar,"avatar").toBundle();

            val intent = Intent(context, UserInfoActivity::class.java)
            context.startActivity(intent,bundle)
        }
        fun startWithAnim(context: Activity,userId: String,avatar:ImageView){
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(context,avatar,"avatar").toBundle();

            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra(USER_ID, userId)

            context.startActivity(intent,bundle)
        }

        fun start(context: Context, userId: String) {
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra(USER_ID, userId)
            context.startActivity(intent)

        }

    }
}