package arestory.com.marvelmoviefans.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.databinding.ActivityRewardBinding
import arestory.com.marvelmoviefans.datasource.SettingDataSource
import arestory.com.marvelmoviefans.util.ToastUtil
import cn.waps.AppConnect
import cn.waps.AppListener
import com.ares.datacontentlayout.DataContentLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xiaomi.ad.common.pojo.AdType
import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory
import android.view.ViewGroup
import com.miui.zeus.mimo.sdk.MimoSdk
import com.miui.zeus.mimo.sdk.listener.MimoAdListener


class RewardActivity:BaseDataBindingActivity<ActivityRewardBinding>() {

    private lateinit var mRewardedAd: RewardedAd
    
  

    override fun getLayoutId(): Int = R.layout.activity_reward
    override fun doMain(savedInstanceState: Bundle?) {
        initToolbarSetting(dataBinding.toolbar)
        MobileAds.initialize(this, "ca-app-pub-8884790662094305~4611016162");
//        mRewardedAd = MobileAds.getRewardedVideoAdInstance(this)
        loadMiAdv()
        val permissions = RxPermissions(this)
        val isGranted = permissions.isGranted(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val isGranted2 = permissions.isGranted(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val isGranted3 = permissions.isGranted(android.Manifest.permission.READ_PHONE_STATE)

        if(isGranted&&isGranted2&&isGranted3){

            loadWpsAdv()
            loadGoogleAdv()
        }else{
            val dis =  permissions.requestEachCombined(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_PHONE_STATE)
                .subscribe {

                    if(it.granted){

                        loadWpsAdv()
                        loadGoogleAdv()
                    }else{
                        ToastUtil.showToast(this,"请允许该权限")
                    }
                }
        }


    }

    private fun loadGoogleAdv(){

        dataBinding.dataContentLayout.showLoading()
        mRewardedAd = RewardedAd(
            this,
            "ca-app-pub-8884790662094305/3949930360"
        )
        dataBinding.dataContentLayout.showLoading()
        mRewardedAd.loadAd(AdRequest.Builder().build(),object :RewardedAdLoadCallback(){


            override fun onRewardedAdFailedToLoad(p0: Int) {

                Log.i(TAG,"onRewardedAdFailedToLoad:$p0")
//                dataBinding.dataContentLayout.showError("广告加载失败，点击重新加载",object :DataContentLayout.ErrorListener{
//                    override fun showError(view: View) {
//
//
//                        loadGoogleAdv()
//                    }
//                })

                showWpsAdv()


            }

            override fun onRewardedAdLoaded() {
                super.onRewardedAdLoaded()
                Log.i(TAG,"onRewardedAdLoaded")

                dataBinding.dataContentLayout.showContent()

                showGoogleAdv()



            }
        })

    }


    private fun loadMiAdv(){

        val isready = MimoSdk.isSdkReady()

        if(isready){

        val myAdWorker = AdWorkerFactory.getAdWorker(this,null, object :MimoAdListener{
            override fun onAdFailed(p0: String?) {

                Log.i(TAG,"onAdFailed$p0")
             }

            override fun onAdDismissed() {
                Log.i(TAG,"onAdDismissed")

            }

            override fun onAdPresent() {
                Log.i(TAG,"onAdPresent")

            }

            override fun onAdClick() {
                Log.i(TAG,"onAdClick")

            }

            override fun onStimulateSuccess() {
                Log.i(TAG,"onStimulateSuccess")

            }

            override fun onAdLoaded(p0: Int) {
                Log.i(TAG,"onAdLoaded$p0")

            }


        }, AdType.AD_STIMULATE_DOWNLOAD)
        myAdWorker.load("8df8bb781e8a6399ecafb5501deb4cb7")
//        myAdWorker.load("8df8bb781e8a6399ecafb5501deb4cb7")

//        dataBinding.dataContentLayout.addView(myAdWorker.updateAdView(null,0))

        }else{
            ToastUtil.showToast(this,"插件没准备")
        }
    }

    private fun loadWpsAdv(){
        AppConnect.getInstance(this)
        AppConnect.getInstance(this).initPopAd(this)
    }

    fun showWpsAdv(){

        AppConnect.getInstance(this).showPopAd(this,object : AppListener() {

            override fun onPopClose() {
                super.onPopClose()
                SettingDataSource.addTodayTipsCount(this@RewardActivity,5)
                ToastUtil.showLongToast(this@RewardActivity,"恭喜你，可用提示次数增加了5次")

                finish()

            }
        })
    }

    fun showGoogleAdv(){



        mRewardedAd.show(this,object : RewardedAdCallback(){

            override fun onRewardedAdClosed() {
                super.onRewardedAdClosed()
                Log.i(TAG,"onRewardedAdClosed")

            }

            override fun onRewardedAdFailedToShow(p0: Int) {
                super.onRewardedAdFailedToShow(p0)
                Log.i(TAG,"onRewardedAdFailedToShow:$p0")

            }

            override fun onRewardedAdOpened() {
                super.onRewardedAdOpened()
                Log.i(TAG,"onRewardedAdOpened")

            }

            override fun onUserEarnedReward(p0: com.google.android.gms.ads.rewarded.RewardItem) {
                super.onUserEarnedReward(p0)
                Log.i(TAG,"onUserEarnedReward:${p0.amount}")
                SettingDataSource.addTodayTipsCount(this@RewardActivity,p0.amount)
                ToastUtil.showLongToast(this@RewardActivity,"恭喜你，可用提示次数增加了${p0.amount}次")

                finish()
            }
        })
    }


    companion object {

        const val TAG ="RewardActivity"
        fun start(context: Context){


            context.startActivity(Intent(context,RewardActivity::class.java))
        }
    }

 
}