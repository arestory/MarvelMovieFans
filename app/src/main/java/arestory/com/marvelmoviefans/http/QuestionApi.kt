package arestory.com.marvelmoviefans.http

import arestory.com.marvelmoviefans.bean.NoAdminQuestion
import arestory.com.marvelmoviefans.bean.QuestionEntity
import com.ares.movie.http.CommonResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface QuestionApi {


    @GET("/question/getQuestionCount")
    fun getQuestionCount(): Observable<CommonResponse<Int>>


    @GET("/question/getRandomOne")
    fun getRandomOne(@Query("userId") userId: String?): Observable<CommonResponse<QuestionEntity>>


    @GET("/question/list/{page}/{count}")
    fun getQuestions(@Path("page") page: Int, @Path("count") count: Int, @Query("userId") userId: String?):
             Observable<CommonResponse<List<QuestionEntity>>>


    @GET("/question/notAdmin/{page}/{count}")
    fun getNotAdminQuestions(@Path("page") page: Int, @Path("count") count: Int,@Query("createUserId")createUserId: String?, @Query("userId") userId: String?, @Query("auth") auth: Int = 1): Observable<CommonResponse<List<NoAdminQuestion>>>


    @POST("/question/upload")
    fun uploadQuestion(
        @Part("file") part: Multipart, @Field("answer") answer: String,
        @Field("title") title: String, @Field("point") point: Int,
        @Field(value = "keywords") keywords: String, @Field(value = "createUserId") createUserId: String
    )

    @POST("/question/update/{id}")
    fun updateQuestion(
        @Path("id") id: String, @Field("title") title: String?,
        @Field("answer") answer: String?, @Field("keywords") keywords: String?, @Field("point") point: Int?,
        @Field("auth") auth: Int?
    )

    @POST("/question/updateStatus/{id}")
    fun updateQuestionStatus(@Path("id") id: String, @Field("auth") auth: Int)
}
