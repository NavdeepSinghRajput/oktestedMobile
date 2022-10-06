package com.oktested.core.network;

import com.google.gson.GsonBuilder;
import com.oktested.BuildConfig;

import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitClient {

    static Retrofit getClient(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory
                        .create(new GsonBuilder().serializeNulls()
                                .excludeFieldsWithModifiers(Modifier.FINAL,
                                        Modifier.TRANSIENT, Modifier.STATIC)
                                .create()))
                .build();
    }

    static Retrofit getClientForContacts(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                .client(getOkHttpClientContact())
                .addConverterFactory(GsonConverterFactory
                        .create(new GsonBuilder().serializeNulls()
                                .excludeFieldsWithModifiers(Modifier.FINAL,
                                        Modifier.TRANSIENT, Modifier.STATIC)
                                .create()))
                .build();
    }

    /**
     * Method to get OkHttpClient with headers and all timeout and api logging
     * details
     *
     * @return OkHttpClient
     */
    private static OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient httpClient = new OkHttpClient();
        OkHttpClient.Builder builder = httpClient.newBuilder();
        builder.addInterceptor(interceptor);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        return builder.build();
    }
    private static OkHttpClient getOkHttpClientContact() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient httpClient = new OkHttpClient();
        OkHttpClient.Builder builder = httpClient.newBuilder();
        builder.addInterceptor(interceptor);
        builder.connectTimeout(300, TimeUnit.SECONDS);
        builder.readTimeout(300, TimeUnit.SECONDS);
        return builder.build();
    }
}