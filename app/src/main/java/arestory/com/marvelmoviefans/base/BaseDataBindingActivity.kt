package arestory.com.marvelmoviefans.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View

abstract class BaseDataBindingActivity<T : ViewDataBinding> : AppCompatActivity(){


    lateinit var dataBinding:T

    abstract fun getLayoutId():Int
    abstract fun doMain()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this,getLayoutId())
        doMain()
    }


    /**
     * 设置toolbar
     *
     * @param toolbar
     * @param title   标题
     */
     fun initToolbarSetting(@NonNull toolbar: Toolbar, title: String) {
        toolbar.title = title
        initToolbarSetting(toolbar)
    }

    /**
     * 设置toolbar，显示返回按钮，点击可结束当前activity
     *
     * @param toolbar
     */
     fun initToolbarSetting(@NonNull toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener{

            finish()
        }


    }


    /**
     * 设置 toolbar 显示返回按钮
     *
     * @param toolbar
     * @param backClickListener 返回按钮回调
     */
     fun initToolbarSetting(@NonNull toolbar: Toolbar, backClickListener: View.OnClickListener) {
        initToolbarSetting(toolbar)
        toolbar.setNavigationOnClickListener(backClickListener)
    }

    /**
     * 设置 toolbar
     *
     * @param toolbar
     * @param title             标题
     * @param backClickListener 返回按钮回调
     */
     fun initToolbarSetting(@NonNull toolbar: Toolbar, title: String, backClickListener: View.OnClickListener) {
        initToolbarSetting(toolbar, title)
        toolbar.setNavigationOnClickListener(backClickListener)
    }
}
