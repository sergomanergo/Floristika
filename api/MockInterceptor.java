package ru.kazachkov.florist.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.kazachkov.florist.tools.MockResponse;

/**
 * Created by ishmukhametov on 24.03.16.
 */
public class MockInterceptor implements Interceptor {

    Context context;

    public MockInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response;

        Uri uri = Uri.parse(chain.request().url().toString());
        Log.d("MOCK SERVER", "fetching uri: " + uri.toString());
        String responseString;

        if (uri.getPath().contains("/update")) {
            responseString = MockResponse.upadeGoodsFastAuth(context);
        } else {
            responseString = "OTHER JSON RESPONSE STRING";
        }

        response = new Response.Builder()
                .code(200)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .message(responseString)
                .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                .addHeader("content-type", "application/json")
                .build();
        return response;
    }
}
