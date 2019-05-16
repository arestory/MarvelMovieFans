package com.ares.datacontentlayout

 import android.content.Context
 import android.support.annotation.DrawableRes
 import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


class DataContentLayout : FrameLayout {


    private lateinit var loadingLayout: LinearLayout
    private lateinit var errorLayout: LinearLayout
    private lateinit var emptyLayout: LinearLayout

   private var dataStatus = DataStatus.LOADING


    public fun getDataStatus():DataStatus{
        return dataStatus
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        initView(context)
    }


    private fun initView(context: Context) {

        View.inflate(context, R.layout.data_content_layout, this)
        loadingLayout = findViewById(R.id.loading_layout)

        errorLayout = findViewById(R.id.error_layout)

        emptyLayout = findViewById(R.id.empty_layout)
    }

    enum class DataStatus {

        LOADING,
        DATA_CONTENT,
        EMPTY_CONTENT,
        ERROR

    }

    /**
     * 显示数据内容
     */
    fun showContent() {

        dataStatus = DataStatus.DATA_CONTENT



        (0 until childCount).map {

            getChildAt(it)
        }.forEach {

            when (it.id) {

                R.id.rootLayout -> it.visibility = View.GONE
                else -> it.visibility = View.VISIBLE

            }

        }


    }


    /**
     * 显示加载页面
     */
    fun showLoading() {


        dataStatus = DataStatus.LOADING


        (0 until childCount).map {

            getChildAt(it)
        }.map {

            when (it.id) {


                R.id.rootLayout -> it.visibility = View.VISIBLE
                else -> it.visibility = View.GONE

            }

        }
        emptyLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        loadingLayout.visibility = View.VISIBLE


    }

    fun nonShow(){
        emptyLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        loadingLayout.visibility = View.GONE


    }


    /**
     * @param emptyContent 空提示
     * @param emptyDrawableId 图片ID
     */
    fun showEmptyContent(emptyContent: String = "数据为空", @DrawableRes emptyDrawableId: Int = R.drawable.ic_weekend_black_24dp) {


        dataStatus = DataStatus.EMPTY_CONTENT
        emptyLayout.findViewById<TextView>(R.id.emptyTv).text = emptyContent
        emptyLayout.findViewById<ImageView>(R.id.ivEmpty).setBackgroundResource(emptyDrawableId)

        (0 until childCount).map {

            getChildAt(it)
        }.map {

            when (it.id) {


                R.id.rootLayout -> it.visibility = View.VISIBLE
                else -> it.visibility = View.GONE

            }

        }
        emptyLayout.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        loadingLayout.visibility = View.GONE
    }


    /**
     * @param errorTip 错误提示
     * @param listener  点击回调
     */
    fun showError(errorTip: String, listener: ErrorListener?) {


        showError(errorTip, R.drawable.ic_signal_wifi_off_red_48dp, listener)


    }


    /**
     * @param listener 回调
     * @param errorDrawableId 错误图片ID
     */
    fun showError(listener: ErrorListener?, @DrawableRes errorDrawableId: Int = R.drawable.ic_signal_wifi_off_red_48dp) {


        showError("点击重试", errorDrawableId, listener)


    }


    /**
     * @param errorTips 错误提示
     * @param errorDrawableId 错误图片ID
     * @param listener 回调
     */
    fun showError(errorTips: String, @DrawableRes errorDrawableId: Int, listener: ErrorListener?) {


        errorLayout.findViewById<TextView>(R.id.errorTips).text = errorTips
        errorLayout.findViewById<ImageView>(R.id.ivError).setBackgroundResource(errorDrawableId)

        errorLayout.setOnClickListener {

            listener?.showError(it)
        }
        dataStatus = DataStatus.ERROR

        (0 until childCount).map {

            getChildAt(it)
        }.map {

            when (it.id) {


                R.id.rootLayout -> it.visibility = View.VISIBLE
                else -> it.visibility = View.GONE


            }

        }

        emptyLayout.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        loadingLayout.visibility = View.GONE
    }

    /**
     *
     * @param listener 点击回调
     */
    fun showError(listener: ErrorListener) {


        showError("点击重试", R.drawable.ic_signal_wifi_off_red_48dp, listener)
    }


    interface ErrorListener {

        fun showError(view: View)
    }

}
