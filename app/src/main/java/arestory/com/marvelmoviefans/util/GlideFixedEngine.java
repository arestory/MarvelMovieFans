package arestory.com.marvelmoviefans.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import arestory.com.marvelmoviefans.common.GlideApp;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.zhihu.matisse.engine.ImageEngine;

/**
 * 用于修复Glide版本不一致的图片选择加载引擎
 * @Author yuwenque125
 * @Date 2018/9/30-下午2:34
 * @Email yuwenque125@pingan.com.cn
 */
public class GlideFixedEngine implements ImageEngine {
    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(placeholder)//这里可自己添加占位图
                .override(resize, resize);
        GlideApp.with(context)
                .asBitmap()  // some .jpeg files are actually gif
                .load(uri)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(placeholder)//这里可自己添加占位图
                .override(resize, resize);
        GlideApp.with(context)
                .asGif()  // some .jpeg files are actually gif
                .load(uri)
                .apply(options)
                .into(imageView);
    }


    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .override(resizeX, resizeY)
                .priority(Priority.HIGH);
        GlideApp.with(context)
                .load(uri)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .override(resizeX, resizeY);
        GlideApp.with(context)
                .asGif()  // some .jpeg files are actually gif
                .load(uri)
                .apply(options)
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }
 
}
