package arestory.com.marvelmoviefans.http;

import io.reactivex.disposables.Disposable;

/**
 * 用于管理 retrofit 的请求
 * Created by ares on 2017/5/3.
 */

public interface RxActionManager<T> {

    void add(T tag, Disposable disposable);


    void remove(T tag);

    void cancel(T tag);

    void cancel(T... tag);

    void cancelAll();
}
