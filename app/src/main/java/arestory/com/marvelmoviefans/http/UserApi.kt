package arestory.com.marvelmoviefans.http

import arestory.com.marvelmoviefans.bean.QuestionEntity
import arestory.com.marvelmoviefans.bean.UpdateUserRequestBody
import arestory.com.marvelmoviefans.bean.UserInfo
import arestory.com.marvelmoviefans.bean.UserRequestBody
import arestory.com.marvelmoviefans.bean.FeedbackEntity
import com.ares.movie.entity.UserPoint
import com.ares.movie.http.CommonResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface UserApi {

    @POST("/user/register")
    fun register(@Body requestBody: UserRequestBody): Observable<CommonResponse<UserInfo>>

    @POST("/user/login")
    fun login(@Body requestBody: UserRequestBody) : Observable<CommonResponse<UserInfo>>

    @GET("/user/{id}")
    fun getUserInfo(@Path("id")id:String): Observable<CommonResponse<UserInfo>>

    @GET("/user/list/{page}/{count}")
    fun getUserList(@Path("page")page:Int,@Path("count")count:Int) : Observable<CommonResponse<List<UserInfo>>>

    @Multipart
    @POST("/user/{id}/uploadAvatar")
    fun uploadAvatar(@Path("id")id:String,@Part avatarFile:MultipartBody.Part): Observable<CommonResponse<UserInfo>>


    @POST("/user/{id}/update")
    fun updateUserInfo(@Path("id")id:String?,@Body  requestBody: UpdateUserRequestBody): Observable<CommonResponse<UserInfo>>

    @GET("/user/{id}/getPoint")
    fun getUserPoint(@Path("id")id:String?):Observable<CommonResponse<Int>>

    @POST("/user/feedback/commit")
    fun commitFeedback(@Body body: FeedbackEntity):Observable<CommonResponse<String>>

    @GET("/user/getPointRank/{page}/{count}")
    fun getPointRank(@Path("page")page: Int,@Path("count")count: Int):Observable<CommonResponse<List<UserPoint>>>

    @POST("/user/{userId}/answer/{questionId}")
    fun answerQuestion(@Path("userId")userId:String?,@Path("questionId")questionId:String):Observable<CommonResponse<String>>


    @Multipart
    @POST("/question/upload")
    fun  uploadQuestion(@Part avatarFile: MultipartBody.Part?, @Query("answer") answer:String?,
                        @Query("title") title:String?, @Query("point") point:Int?,
                        @Query(value = "keywords") keywords:String?, @Query(value = "createUserId")  createUserId:String?):Observable<CommonResponse<String>>

    @GET("/user/{userId}/questions/{page}/{count}")
    fun getUserQuestion(@Path("userId")userId:String,@Path("page")page:Int,@Path("count")count:Int):Observable<CommonResponse<List<QuestionEntity>>>

}