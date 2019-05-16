package arestory.com.marvelmoviefans;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

public class MovieApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "8d7193eaf2", BuildConfig.DEBUG);
        MobileAds.initialize(this, "ca-app-pub-8884790662094305~5985336261");

    }
}
