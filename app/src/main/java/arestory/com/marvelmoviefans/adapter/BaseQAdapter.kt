package arestory.com.marvelmoviefans.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

abstract class BaseQAdapter<T, D : ViewDataBinding, K : BaseViewHolder> : BaseQuickAdapter<T, K> {
    constructor(layoutResId: Int, data: List<T>?) : super(layoutResId, data) {}

    constructor(data: List<T>?) : super(data) {}

    constructor(layoutResId: Int) : super(layoutResId) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): K {
        if (viewType != BaseQuickAdapter.LOADING_VIEW && viewType != BaseQuickAdapter.HEADER_VIEW && viewType != BaseQuickAdapter.EMPTY_VIEW && viewType != BaseQuickAdapter.FOOTER_VIEW) {
            val d = DataBindingUtil.inflate<D>(LayoutInflater.from(parent.context), this.mLayoutResId, null, false)
            d.executePendingBindings()
            val mvViewHolder = MVViewHolder(d)
            bindViewClickListener(mvViewHolder)
            mvViewHolder.setQAdapter(this)
            return mvViewHolder as K
        } else {
            return super.onCreateViewHolder(parent, viewType)
        }
    }


    private fun bindViewClickListener(baseViewHolder: BaseViewHolder?) {
        if (baseViewHolder == null) {
            return
        }
        val view = baseViewHolder.itemView ?: return
        if (onItemClickListener != null) {
            view.setOnClickListener { v ->
                onItemClickListener.onItemClick(
                    this@BaseQAdapter,
                    v,
                    baseViewHolder.layoutPosition - headerLayoutCount
                )
            }
        }
        if (onItemLongClickListener != null) {
            view.setOnLongClickListener { v ->
                onItemLongClickListener.onItemLongClick(
                    this@BaseQAdapter,
                    v,
                    baseViewHolder.layoutPosition - headerLayoutCount
                )
                false
            }
        }
    }
}