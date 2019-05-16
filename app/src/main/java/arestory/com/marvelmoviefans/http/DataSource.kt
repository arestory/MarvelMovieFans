package ywq.ares.funapp.http

import arestory.com.marvelmoviefans.constants.AppConstants
import arestory.com.marvelmoviefans.http.RetrofitServiceManager
import arestory.com.marvelmoviefans.http.QuestionApi

object DataSource {

//    private val CacheDataManager = CacheDataManager.cacheExist()!!

    private val api = RetrofitServiceManager.getManager().create(AppConstants.URL.HOST_URL, QuestionApi::class.java)


    interface DataListener<T> {

        fun onSuccess(t: T)
        fun onFail()
    }


}
