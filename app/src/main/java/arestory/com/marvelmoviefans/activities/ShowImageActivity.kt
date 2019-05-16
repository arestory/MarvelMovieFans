package ywq.ares.funapp.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseActivity
import arestory.com.marvelmoviefans.common.GlideApp
import com.ares.datacontentlayout.DataContentLayout
import com.bm.library.PhotoView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

import java.util.ArrayList


class ShowImageActivity : BaseActivity() {

    private lateinit var imageUrl: String

    private var saveBitmap: Bitmap? = null
    override fun getLayoutId(): Int {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        return R.layout.activity_show_image
    }

    override fun doMain() {

        imageUrl = intent.getStringExtra(IMAGE)
        val loadingLayout =  findViewById<DataContentLayout>(R.id.dataLayout)
        val photoView =  findViewById<PhotoView>(R.id.ivTarget)
        photoView.enable()
        photoView.scaleType = ImageView.ScaleType.FIT_CENTER

        loadImage(this,imageUrl,photoView,loadingLayout)
        loadingLayout.setOnClickListener {
            finish()
        }
        photoView.setOnClickListener { v ->

            finish()
        }
    }

    fun loadImage(context: Context, imageUrl: String, photoView: PhotoView, loadingLayout: DataContentLayout) {

        loadingLayout.showLoading()

        GlideApp.with(context).asBitmap().load(imageUrl)

            .diskCacheStrategy(DiskCacheStrategy.ALL)

            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {

                    loadingLayout.showError(object : DataContentLayout.ErrorListener {
                        override fun showError(view: View) {

                            loadImage(context, imageUrl, photoView, loadingLayout)
                        }
                    })
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: Target<Bitmap>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {

                    println("onResourceReady....")
                    photoView.setImageBitmap(resource)
                    loadingLayout.showContent()
                    saveBitmap = resource
                    return false
                }
            }).submit()

    }


    companion object {


       const val IMAGE = "image"
        fun start(context: Context, imageUrl:String) {
            val starter = Intent(context, ShowImageActivity::class.java)
            starter.putExtra(IMAGE, imageUrl)
            context.startActivity(starter)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (saveBitmap != null) {
            saveBitmap = null
        }
    }

}
