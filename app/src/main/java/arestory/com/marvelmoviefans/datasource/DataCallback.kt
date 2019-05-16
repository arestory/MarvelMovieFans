package arestory.com.marvelmoviefans.datasource

interface DataCallback<T> {



    fun onSuccess(data:T)
    fun onFail(msg:String?)
}