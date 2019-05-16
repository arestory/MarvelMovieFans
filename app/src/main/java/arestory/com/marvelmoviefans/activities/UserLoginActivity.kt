package arestory.com.marvelmoviefans.activities

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.databinding.ActivityLoginBinding
import android.view.WindowManager
import arestory.com.marvelmoviefans.bean.UserInfo
import arestory.com.marvelmoviefans.bean.UserRequestBody
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.UserDataSource
import android.view.View
import android.widget.Toast
import arestory.com.marvelmoviefans.util.RxBus
import arestory.com.marvelmoviefans.util.ToastUtil


class UserLoginActivity: BaseDataBindingActivity<ActivityLoginBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_login

    companion object {

        const val TRY_ANSWER_QUESTION = 200
        fun start(context:Context){

            context.startActivity(Intent(context,UserLoginActivity::class.java))
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==TRY_ANSWER_QUESTION&&resultCode==Activity.RESULT_OK){

            val account = dataBinding.etAccount.text.toString()
            val pwd = dataBinding.etPwd.text.toString()
            val pwd2 = dataBinding.etConfirmPwd.text.toString()
            val body  = UserRequestBody()
            body.account =account
            body.password =pwd
            body.confirmPassword=pwd2
//            ToastUtil.showToast(this@UserLoginActivity,"回答正确！正在提交注册")
            register(body)

        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setStatusBarColor( statusColor:Int,  activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.getWindow()
            //取消设置Window半透明的Flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏为透明
            window.statusBarColor = Color.TRANSPARENT
        }
        val  decor = window.decorView;
        var ui = decor.systemUiVisibility
        ui = ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //设置状态栏中字体的颜色为黑色
        decor.systemUiVisibility = ui

    }

   private fun initLayout(){

        dataBinding.btnRegister.setOnClickListener {


            dataBinding.btnRegister.visibility = View.INVISIBLE
            dataBinding.btnLogin.text="注册"

            dataBinding.pwdConfirmLayout.visibility = View.VISIBLE

            dataBinding.toolbar.title="注册"

        }

       dataBinding.btnLogin.setOnClickListener {

           val account = dataBinding.etAccount.text.toString()
           val pwd = dataBinding.etPwd.text.toString()
           if((account.length >12 || account.length < 6)){
               dataBinding.etAccount.error = "账号长度只能在6-11之间"
               dataBinding.etAccount.requestFocus()
               return@setOnClickListener
           }
           if((pwd.length >=12 || pwd.length < 6)){
               dataBinding.etPwd.error = "密码长度只能在6-11之间"
               dataBinding.etPwd.requestFocus()

               return@setOnClickListener
           }


           when(dataBinding.pwdConfirmLayout.visibility){


               //注册
               View.VISIBLE->{


                   val pwd2 = dataBinding.etConfirmPwd.text.toString()
                   val body  = UserRequestBody()
                   body.account =account
                   body.password =pwd
                   body.confirmPassword=pwd2


                   if((pwd2.length >12 || pwd2.length < 6)){
                       dataBinding.etConfirmPwd.error = "密码长度只能在6-11之间"
                       dataBinding.etConfirmPwd.requestFocus()

                       return@setOnClickListener
                   }
                   if(pwd2!=pwd){
                       dataBinding.etConfirmPwd.error = "两次密码不一致"
                       dataBinding.etConfirmPwd.requestFocus()

                       return@setOnClickListener
                   }
                   ToastUtil.showLongToast(this@UserLoginActivity,"请至少答对一条问题，才能完成注册！")

                   AnswerQuestionActivity.startActivityForResult(this@UserLoginActivity, TRY_ANSWER_QUESTION)

//                   register(body)

               }
               //登录
               else ->{

                   enableInput(false)
                   val body  = UserRequestBody()
                   body.account =account
                   body.password =pwd
                   UserDataSource.login(body,object :DataCallback<UserInfo>{

                       override fun onSuccess(data: UserInfo) {

                           enableInput(true)

                           UserDataSource.saveLoginUser(this@UserLoginActivity,userInfo = data)

                           Toast.makeText(this@UserLoginActivity,"登录成功",Toast.LENGTH_SHORT).show()
                           RxBus.get().post(data)

                           print(data)

                           finish()


                       }
                       override fun onFail(msg: String?) {

                           dataBinding.etPwd.error=msg
                           enableInput(true)

                       }
                   })
               }


           }
       }
    }

    private fun register(body:UserRequestBody){

        enableInput(false)
        UserDataSource.register(body,object :DataCallback<UserInfo>{

            override fun onSuccess(data: UserInfo) {


                enableInput(true)
                print(data)
                UserDataSource.saveLoginUser(this@UserLoginActivity,userInfo = data)
                Toast.makeText(this@UserLoginActivity,"注册成功",Toast.LENGTH_SHORT).show()
                RxBus.get().post(data)
                finish()
            }
            override fun onFail(msg: String?) {

                ToastUtil.showLongToast(this@UserLoginActivity,msg)
                enableInput(true)

            }
        })
    }

    fun enableInput(enable:Boolean){

        dataBinding.etAccount.isEnabled = enable
        dataBinding.etPwd.isEnabled = enable
        dataBinding.etConfirmPwd.isEnabled = enable
        dataBinding.btnLogin.isEnabled = enable

        dataBinding.pb.visibility =when(enable){

            true -> View.GONE
            false->View.VISIBLE
        }
    }

    override fun doMain() {

        initToolbarSetting(dataBinding.toolbar)
        initLayout()
//        setStatusBarColor(resources.getColor(R.color.white),this@UserLoginActivity)

    }
}