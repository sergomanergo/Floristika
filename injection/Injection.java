package ru.kazachkov.florist.injection;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import ru.kazachkov.florist.api.FloristApi;
import ru.kazachkov.florist.api.MockInterceptor;
import ru.kazachkov.florist.api.SoapConverter;
import ru.kazachkov.florist.app.AppPreferences;
import ru.kazachkov.florist.data.DataBaseHelper;
import ru.kazachkov.florist.data.DataController;
import ru.kazachkov.florist.data.adapters.LocalDataSource;
import ru.kazachkov.florist.data.adapters.RemoteDataSource;
import ru.kazachkov.florist.tools.Const;

/**
 * Created by Dinis Ishmukhametov on 26.08.17.
 * dinis.ish@gmail.com
 */

public class Injection {

    private static FloristApi floristApi;
    private static DataController dataController;

    private static OkHttpClient.Builder mHttpClient = null;
    private static Retrofit mRetrofit = null;

    public static FloristApi providesApi(Context context) {
        if (floristApi == null) {
            floristApi = provideRetrofit(context).create(FloristApi.class);
        }
        return floristApi;
    }

    public static RemoteDataSource provideRemoteDataSource(Context context) {
        return new RemoteDataSource(providesApi(context));
    }


    public static LocalDataSource provideLocalDataSource(Context context) {
        return new LocalDataSource(provideRealm(context));
    }


    public static DataController provideDataController(Context context) {
        if (dataController == null) {
            dataController = new DataController(provideRemoteDataSource(context), provideLocalDataSource(context));
        }
        return dataController;
    }


    public static Realm provideRealm(Context context) {
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        return Realm.getInstance(configuration);

    }


    public static SQLiteOpenHelper provideOpenHelper(Application application) {
        return new DataBaseHelper(application);
    }


    public static AppPreferences providesAppPreferences(Context app) {
        return AppPreferences.getInstance(app);
    }


    public static Cache provideOkHttpCache(Context application) {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(application.getCacheDir(), cacheSize);
    }


    private static Converter.Factory getGsonConverterFactory() {
        return GsonConverterFactory.create(new GsonBuilder().create());
    }


    public static OkHttpClient provideMockOkHttpClient(Cache cache, Application application) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient().newBuilder()
                .addInterceptor(new MockInterceptor(application))
                .cache(cache)
                .build();
    }


    public static OkHttpClient provideOkHttpClient(Cache cache) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor requestInterceptor = chain -> {
            Request request = chain.request();
            okhttp3.Response response = chain.proceed(request);
            if (!response.isSuccessful()) {

            }
            if (response.code() == 500) {
                return response;
            }
            return response;
        };

            if(mHttpClient == null)
                mHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(interceptor)
                .addInterceptor(requestInterceptor)
                .addNetworkInterceptor(new StethoInterceptor())
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .cache(cache);

        return mHttpClient.build();
    }


    public static Retrofit provideRetrofit(Context context) {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(Const.API_ENDPOINT)
                    .client(provideOkHttpClient(provideOkHttpCache(context)))
                    .addConverterFactory(new SoapConverter(SimpleXmlConverterFactory.create(), getGsonConverterFactory()))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return mRetrofit;

    }

}
