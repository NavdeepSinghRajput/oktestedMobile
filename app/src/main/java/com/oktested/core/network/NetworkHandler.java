package com.oktested.core.network;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.stream.MalformedJsonException;
import com.oktested.BuildConfig;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.ui.MasterView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class NetworkHandler<T> implements Callback<T> {

    private final Context context;
    private final MasterView masterView;

    public NetworkHandler(Context context, MasterView masterView) {
        this.context = context;
        this.masterView = masterView;
    }

    /**
     * Override this method in your class for handling success response
     *
     * @param request  request called
     * @param response response received
     * @return boolean (whether you have handled its response in your class or
     * not)
     */
    public boolean handleResponse(Request request, BaseResponse response) {
        return false;
    }

    /**
     * Method for handling errors. Override this method in your class for
     * handling error
     *
     * @param request request called
     * @param error   error received from api
     * @return boolean (whether you have handled its response in your class or
     * not)
     */
    public boolean handleError(Request request, Error error) {
        return false;
    }

    /**
     * Method for handling failure response Override this method for failure
     * action
     *
     * @param request request called
     * @param ex      failure exception
     * @param message
     * @return boolean (handled or not)
     */
    public boolean handleFailure(Request request, Exception ex, String message) {
        return false;
    }

    /**
     * Override method from {@link Callback}, will be called when successful
     * response will come from server
     *
     * @param call     api call request
     * @param response response received
     */
    @Override
    public final void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        Request request = call.request();
        String message = "Server sent an invalid response. Please try again later.";
        // checking whether response was successful else throw failure
        if (isRequestSuccessful(response) && masterView != null) {
            BaseResponse baseResponse = (BaseResponse) response.body();
            if (!handleResponse(call.request(), baseResponse)) {
                if (baseResponse != null) {
                    Log.e(getClass().getSimpleName(), String.format("Request: (%s), Response not handled: (%s)",
                            call.request().url(), baseResponse.toString()));
                }
            }
        } else {
            Throwable error;
            try {
                JSONObject jsonObject = new JSONObject();
                try {
                    String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    jsonObject.put("device_id", android_id);
                    jsonObject.put("device_name", Build.MANUFACTURER);
                    jsonObject.put("app_version", BuildConfig.VERSION_NAME);
                    jsonObject.put("android_version", Build.VERSION.SDK_INT);
                    jsonObject.put("api_url", request.url());
                    jsonObject.put("api_params", request.body());
                    jsonObject.put("error_message", message);
                    jsonObject.put("error_code", response.code());
                    jsonObject.put("method", request.method());
                    JSONArray array;
                    if (TextUtils.isEmpty(AppPreferences.getInstance(context).getPreferencesString(AppConstants.ERROR_LOG))) {
                        array = new JSONArray();
                        array.put(jsonObject);
                    } else {
                        String value = AppPreferences.getInstance(context).getPreferencesString(AppConstants.ERROR_LOG);
                        array = new JSONArray(value);
                        array.put(jsonObject);
                    }
                    AppPreferences.getInstance(context).savePreferencesString(AppConstants.ERROR_LOG, array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                error = new ErrorUtils.ApiErrorException(response.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
                error = e;
            }
            onFailure(call, error);
        }
    }

    /**
     * Override method from {@link Callback}, will be called when api failure
     * has occurred
     *
     * @param call      api call requested
     * @param throwable error as {@link Throwable}
     */
    @Override
    public final void onFailure(Call<T> call, Throwable throwable) {
        // check view available then proceed
        if (masterView != null && context != null) {
            Exception exception = new Exception(throwable);
            // check whether network available or not
            if (exception.getCause() instanceof UnknownHostException) {
                Log.e("UnknownHostException", "Unable to connect. Please make sure you are connected to a network.");
//                masterView.showMessage("Unable to connect. Please make sure you are connected to a network.");
            }
            // check whether server sent valid json format
            else if (exception.getCause() instanceof MalformedJsonException) {
                Log.e("MalformedJsonException", "Server sent an invalid response. Please try again later.");
//                masterView.showMessage("Server sent an invalid response. Please try again later.");
            }
            // check for custom api exception
            else if (exception.getCause() instanceof ErrorUtils.ApiErrorException) {
                Log.e("ApiErrorException", "Server sent an invalid response. Please try again later.");
//                masterView.showMessage("Server sent an invalid response. Please try again later.");
            }
            // check for time out exception
            else if (exception.getCause() instanceof SocketTimeoutException) {
                Log.e("SocketTimeoutException", "Timeout occurred. Please try again later.");
//                masterView.showMessage("Timeout occurred. Please try again later.");
            }
            // send callback to {@link handleFailure}
            String message = "";
            try {
                if (Helper.isContainValue(throwable.getMessage())) {
                    JSONObject jsonObject = new JSONObject(throwable.getMessage());
                    message = jsonObject.optString("Message");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!handleFailure(call.request(), exception, message)) {
                Log.e(getClass().getSimpleName(), String.format("Request: (%s), Failure not handled: (%s)",
                        call.request().url(), throwable.getMessage()));
            }
        }
    }

    private boolean isRequestSuccessful(Response<T> response) {
        return response != null && response.isSuccessful() && response.errorBody() == null;
    }
}