package arestory.com.marvelmoviefans.constants;

import android.os.Environment;

import java.io.File;

/**
 * Created by ares on 2017/12/20.
 */
public  final class AppConstants {

    public static class DATA{

        public static final int PAGE_DATA_COUNT =10;

    }

    public static class HTTP{


        public static final int TIME_OUT=20000;
    }

    public static class URL{


//        public static final String HOST_URL = "https://api.arestory.info/";
        public static final String HOST_URL = "http://212.64.93.216:7777/";
//        public static final String HOST_URL = "http://192.168.43.211:7575/";
        public static final String FILE_PRE_URL = "http://212.64.93.216:8000/file/";
    }


    public static class LOCAL{

        public static final String ROOT = Environment.getExternalStorageDirectory() + File.separator +"MarvelMovieFans"+File.separator;
        public static final String NETWORK_CACHE =ROOT+"Cache";
        public static final String IMAGE_CACHE =ROOT+"Image";
    }
}
