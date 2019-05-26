package arestory.com.marvelmoviefans;

import android.app.Application;
import cn.waps.AppConnect;
import com.google.android.gms.ads.MobileAds;
import com.miui.zeus.mimo.sdk.MimoSdk;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

public class MovieApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "8d7193eaf2", BuildConfig.DEBUG);
        MobileAds.initialize(this, "ca-app-pub-8884790662094305~4611016162");
//        MimoSdk.init(this, "2882303761518008528", "fake_app_key", "fake_app_token");
        MimoSdk.init(this, "2882303761517411490", "fake_app_key", "fake_app_token");
        MimoSdk.setDebugOn(); // 打开调试，输出调试信息
        MimoSdk.setStageOn(); // 打开测试请求开关，请求测试广告
    }
}
