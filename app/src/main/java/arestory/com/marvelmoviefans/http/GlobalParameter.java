package arestory.com.marvelmoviefans.http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;

/**
 * 全局请求处理
 *
 * @author zhongrongguang
 *         2017/11/29.
 */

public class GlobalParameter implements Interceptor {

    private static final String MEDIA_TYPE = "application/x-www-form-urlencoded";

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();
        Request.Builder newBuilder = original.newBuilder();
        RequestBody body = original.body();
        if (body != null) {
//            MediaType contentType = body.contentType();
//            if (contentType == null) {
//                return chain.proceed(newBuilder.build());
//            }
            //如果是表单提交,将body的数据进行utf编码,防止中文乱码
//            if (MEDIA_TYPE.equals(contentType.toString())) {
//                newBuilder.post(RequestBody.create(MediaType.parse(MEDIA_TYPE), URLDecoder.decode(bodyToString(original.body()), "UTF-8")));
//            }
        }

        newBuilder
                .header("Content-Type", "application/json")
                .method(original.method(), body);

        return chain.proceed(newBuilder.build());
    }


    /**
     * 将请求体utf编码,防止中文乱码
     *
     * @param request
     * @return
     */
    private String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null) {
                copy.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
