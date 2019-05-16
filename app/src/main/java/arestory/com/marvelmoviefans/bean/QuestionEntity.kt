package arestory.com.marvelmoviefans.bean

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName="tbl_question")
open class QuestionEntity:Serializable {


    /**
     * 问题id
     */
    @PrimaryKey
    var id: String? = null
    /**
     * 问题标题
     */
    @ColumnInfo(name = "title")
    var title: String? = null
    /**
     * 问题答案
     */
    @ColumnInfo(name = "answer")
    var answer: String? = null
    /**
     * 关键字列表
     */
    @ColumnInfo(name = "keywords")
    var keywords: String? = null
    /**
     * 分数
     */
    @ColumnInfo(name = "point")
    var point: Int = 0
    /**
     * 资源链接
     */
    @ColumnInfo(name = "url")
    var url: String? = null
    /**
     * 问题类型
     */
    @ColumnInfo(name = "type")
    var type: String? = null


    var hadAnswer:Boolean=false

    var createUserId: String? = null
    override fun equals(other: Any?): Boolean {
        if(other is QuestionEntity){

            return this.id == other.id
        }
        return super.equals(other)
    }

    var auth: Int = 0

}
