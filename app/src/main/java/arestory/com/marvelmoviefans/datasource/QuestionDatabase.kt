package arestory.com.marvelmoviefans.datasource

import android.arch.persistence.db.SupportSQLiteOpenHelper
import android.arch.persistence.room.*
import android.content.Context
import arestory.com.marvelmoviefans.bean.QuestionEntity

@Database(entities = [QuestionEntity::class], version = 1)
abstract class QuestionDatabase : RoomDatabase() {


    /**
     * 关闭链接
     */
    override fun close() {
        try {
            if (INSTANCE != null) {
                INSTANCE!!.close()
            }
        } catch (e: Exception) {

        }

    }

    abstract fun questionDao(): QuestionDao

    companion object {

        private lateinit var INSTANCE: QuestionDatabase
        fun getInstance(context: Context): QuestionDatabase {

            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    QuestionDatabase::class.java, "moviefans.db"
                )
                    .fallbackToDestructiveMigration()//如果更新版本，没有加入迁移，则会删除所有数据
                    .build()
            }
            return INSTANCE
        }
    }


}
