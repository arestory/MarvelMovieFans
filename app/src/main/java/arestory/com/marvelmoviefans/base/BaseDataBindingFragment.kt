package arestory.com.marvelmoviefans.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseDataBindingFragment<T : ViewDataBinding> :Fragment(){

    lateinit var dataBinding:T

    abstract fun getLayoutId():Int
    abstract fun doMain()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater,getLayoutId(),container,false)
        doMain()
        return dataBinding.root
    }

}