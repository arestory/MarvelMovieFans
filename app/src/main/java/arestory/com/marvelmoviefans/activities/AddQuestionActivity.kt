package arestory.com.marvelmoviefans.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.bean.QuestionUploadBean
import arestory.com.marvelmoviefans.bean.UserInfo
import arestory.com.marvelmoviefans.common.GlideApp
import arestory.com.marvelmoviefans.constants.AppConstants
import arestory.com.marvelmoviefans.databinding.ActivityAddQuestionBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.GlideFixedEngine
import arestory.com.marvelmoviefans.util.RxBus
import arestory.com.marvelmoviefans.util.ToastUtil
import arestory.com.marvelmoviefans.util.UriUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

class AddQuestionActivity: BaseDataBindingActivity<ActivityAddQuestionBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_add_question

    private  var imageFile: File?=null

    fun gotoGallery(){


        val permissions = RxPermissions(this@AddQuestionActivity)
        val isGranted = permissions.isGranted(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val isGranted2 = permissions.isGranted(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (isGranted && isGranted2) {

            Matisse.from(this@AddQuestionActivity).
                choose(MimeType.ofImage()).maxSelectable(1)
                .imageEngine(GlideFixedEngine()).thumbnailScale(0.8f)
                .theme(R.style.SelectPhoto)
                .forResult(AddQuestionActivity.CHOOSE_IMAGE)
        } else {
          val dis =  permissions.requestEachCombined(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                .subscribe {

                    if (it.granted) {
                        Matisse.from(this@AddQuestionActivity).choose(MimeType.ofImage()).maxSelectable(1)
                            .imageEngine(GlideFixedEngine()).thumbnailScale(0.8f).theme(R.style.SelectPhoto)

                            .forResult(AddQuestionActivity.CHOOSE_IMAGE)
                    } else {
                        ToastUtil.showToast(this@AddQuestionActivity, "请允许该权限，以便上传图片")
                    }
                }

        }




    }

    fun enableInputLayout(enable:Boolean){
        dataBinding.tvAddPhoto.isEnabled = enable
        dataBinding.ivQuestion.isEnabled = enable
        dataBinding.titleInputLayout.isEnabled = enable
        dataBinding.answerInputLayout.isEnabled = enable
        dataBinding.keywordInputLayout.isEnabled = enable
        dataBinding.pointInputLayout.isEnabled = enable
        dataBinding.btnCommit.isEnabled = enable
        dataBinding.btnCommit.text =if(enable){

            "提交"
        }else{
            "提交中..."
        }

    }
    override fun doMain(savedInstanceState: Bundle?) {

        initToolbarSetting(dataBinding.toolbar)

        dataBinding.tvAddPhoto.setOnClickListener {


            gotoGallery()
            
            
        }
        dataBinding.btnCommit.setOnClickListener { 
            
            val title = dataBinding.titleEt.text.toString()
            if(title.isEmpty()){
                dataBinding.titleEt.requestFocus()
                dataBinding.titleEt.error ="问题不能为空"
                return@setOnClickListener
            }
            val answer = dataBinding.answerEt.text.toString()
            if(answer.isEmpty()){
                dataBinding.answerEt.requestFocus()
                dataBinding.answerEt.error ="答案不能为空"
                return@setOnClickListener
            }
            val keywords = dataBinding.keywordEt.text.toString()
            if(keywords.isEmpty()){
                dataBinding.keywordEt.requestFocus()
                dataBinding.keywordEt.error ="关键字不能为空"
                return@setOnClickListener
            }else if(keywords.length<21){
                dataBinding.keywordEt.requestFocus()
                dataBinding.keywordEt.error ="关键字不能少于21个"
                return@setOnClickListener
            }
            val point = dataBinding.pointEt.text.toString()
            if(point.isEmpty()){
                dataBinding.pointEt.requestFocus()
                dataBinding.pointEt.error ="分数不能为空"
                return@setOnClickListener
            }

            if(imageFile==null){
                ToastUtil.showToast(this@AddQuestionActivity,"图片不能为空")
                return@setOnClickListener

            }
            val uploadBean = QuestionUploadBean()
            uploadBean.file = imageFile
            uploadBean.keywords = keywords
            uploadBean.title = title
            uploadBean.answer = answer
            uploadBean.point = point.toInt()
            uploadBean.createUserId = UserDataSource.getLoginUserId(this)

            enableInputLayout(false)
            UserDataSource.uploadQuestion(uploadBean,object :DataCallback<String>{
                override fun onSuccess(data: String) {

                    enableInputLayout(true)

                    ToastUtil.showToast(this@AddQuestionActivity,data)

                    RxBus.get().post(MyQuestionActivity.REFRESH_MY_QUESTION)
                    finish()
                }

                override fun onFail(msg: String?) {
                    enableInputLayout(true)

                    ToastUtil.showToast(this@AddQuestionActivity,msg)

                }

            })

            
            
            
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AddQuestionActivity.CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {


            val list = Matisse.obtainResult(data)
            val photo = list[0]
            if (!File(AppConstants.LOCAL.IMAGE_CACHE).exists()) {

                File(AppConstants.LOCAL.IMAGE_CACHE).mkdirs()
            }
            val filePath =UriUtil.getRealFilePath(this@AddQuestionActivity, photo)
           if( filePath!!.endsWith(".gif")){
               imageFile = File(filePath)
               dataBinding.ivQuestion.visibility = View.VISIBLE
               dataBinding.tvAddPhoto.visibility = View.GONE
               dataBinding.ivQuestion.setOnClickListener {
                   gotoGallery()
               }
               GlideApp.with(this@AddQuestionActivity).load(photo).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(dataBinding.ivQuestion)

           }else{
               Luban.with(this@AddQuestionActivity).load(filePath)
                   .setTargetDir(AppConstants.LOCAL.IMAGE_CACHE)
                   .setCompressListener(object : OnCompressListener {
                       override fun onSuccess(file: File?) {



                           imageFile = file
                           dataBinding.ivQuestion.visibility = View.VISIBLE
                           dataBinding.tvAddPhoto.visibility = View.GONE
                           dataBinding.ivQuestion.setOnClickListener {
                               gotoGallery()
                           }
                           GlideApp.with(this@AddQuestionActivity).load(file).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(dataBinding.ivQuestion)

                       }

                       override fun onError(e: Throwable?) {
                           ToastUtil.showToast(this@AddQuestionActivity, e?.message)

                       }

                       override fun onStart() {

                       }


                   }).launch()
           }
        }
    }

    companion object {
        const val CHOOSE_IMAGE = 200

        fun start(context:Context){



            context.startActivity(Intent(context,AddQuestionActivity::class.java))
        }

    }
}