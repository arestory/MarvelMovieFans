package arestory.com.marvelmoviefans.adapter

import android.databinding.ViewDataBinding
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MVViewHolder<T : ViewDataBinding>(binding: T) : BaseViewHolder(binding.root) {
      var dataViewBinding: T

    init {
        binding.root.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.dataViewBinding = binding
    }

    fun setQAdapter(adapter: BaseQuickAdapter<*, *>): BaseViewHolder {
        super.setAdapter(adapter)
        return this
    }
}