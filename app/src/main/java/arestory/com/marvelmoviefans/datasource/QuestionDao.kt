package arestory.com.marvelmoviefans.datasource

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import arestory.com.marvelmoviefans.bean.QuestionEntity
import io.reactivex.Observable
import io.reactivex.Single


@Dao
interface QuestionDao {


    @Query("select * from tbl_question")
    fun getAll(): Observable<List<QuestionEntity>>



}