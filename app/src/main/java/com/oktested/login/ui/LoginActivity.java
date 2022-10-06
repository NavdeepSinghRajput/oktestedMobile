package com.oktested.login.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.oktested.R;
import com.oktested.dashboard.ui.DashboardActivity;
import com.oktested.entity.AppSetting;
import com.oktested.login.model.CreateUserResponse;
import com.oktested.login.model.SocialLoginResponse;
import com.oktested.login.presenter.LoginPresenter;
import com.oktested.updateMobile.ui.UpdateMobileActivity;
import com.oktested.userDetail.ui.UserDetailActivity;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;
import com.oktested.verifyotp.ui.VerifyOtpActivity;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginView {

    private EditText mobileET;
    private TextView tvErrorTV;
    private Button continueButton;
    private SpinKitView spinKitView;
    private LoginPresenter loginPresenter;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private LoginButton loginButton;
    private Context context;
    private String id = "", displayName = "", email = "";
    private FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        inUi();
        fetchRemoteConfig();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Error", "getInstanceId failed", task.getException());
                            return;
                        }
                        String refreshedToken = Objects.requireNonNull(task.getResult()).getToken();
                        AppPreferences.getInstance(context).savePreferencesString(AppConstants.FIREBASE_TOKEN, refreshedToken);
                        Log.d("Refresh Token", refreshedToken);
                    }
                });
    }

    private void inUi() {
        loginPresenter = new LoginPresenter(this, this);
        LinearLayout facebookLL = findViewById(R.id.facebookLL);
        LinearLayout googleLL = findViewById(R.id.googleLL);
        mobileET = findViewById(R.id.mobileET);
        tvErrorTV = findViewById(R.id.tvErrorTV);
        continueButton = findViewById(R.id.continueButton);
        spinKitView = findViewById(R.id.spinKit);

        facebookLL.setOnClickListener(this);
        googleLL.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        // Google Login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Facebook Login
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.fbLoginButton);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Message", "facebook:onSuccess:" + loginResult);
                if (loginResult != null) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }
            }

            @Override
            public void onCancel() {
                Log.d("Message", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Message", "facebook:onError", error);
            }
        });
    }

    private void fetchRemoteConfig() {
        // Fetch singleton FirebaseRemoteConfig object
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10800)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        //setting the default values for the UI
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        setRemoteConfigValues();
    }

    private void setRemoteConfigValues() {
        String appSetting = firebaseRemoteConfig.getString(AppConstants.APP_SETTING);
        DataHolder.getInstance().appSetting = new Gson().fromJson(appSetting, AppSetting.class);
        if (DataHolder.getInstance().appSetting != null) {
            AppPreferences.getInstance(context).savePreferencesString(AppConstants.COMMUNITY_VISIBLE, String.valueOf(DataHolder.getInstance().appSetting.is_community_visible));
        }

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    String appSetting = firebaseRemoteConfig.getString(AppConstants.APP_SETTING);
                    DataHolder.getInstance().appSetting = new Gson().fromJson(appSetting, AppSetting.class);
                    if (DataHolder.getInstance().appSetting != null) {
                        AppPreferences.getInstance(context).savePreferencesString(AppConstants.COMMUNITY_VISIBLE, String.valueOf(DataHolder.getInstance().appSetting.is_community_visible));
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebookLL:
                loginButton.performClick();
                break;

            case R.id.googleLL:
                googleSignIn();
                break;

            case R.id.continueButton:
                validate(mobileET.getText().toString().trim());
                break;

            default:
                break;
        }
    }

    private void validate(String mobile) {
        if (!Helper.isContainValue(mobile)) {
            tvErrorTV.setVisibility(View.VISIBLE);
            tvErrorTV.setText(getString(R.string.mobile_error));
        } else if (mobile.length() != 10) {
            tvErrorTV.setVisibility(View.VISIBLE);
            tvErrorTV.setText(getString(R.string.mobile_not_valid));
        } else if (!Helper.isNetworkAvailable(context)) {
            showMessage(getString(R.string.please_check_internet_connection));
        } else {
            showLoader();
            tvErrorTV.setVisibility(View.GONE);
            loginPresenter.createUser(mobile);
        }
    }

    @Override
    public void showLoader() {
        spinKitView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        spinKitView.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(continueButton, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        loginPresenter.onDestroyedView();
        super.onDestroy();
    }

    @Override
    public void onResponse(CreateUserResponse createUserResponse) {
        hideLoader();
        if (createUserResponse.data != null) {
            String accessToken = "";
            if (Helper.isContainValue(createUserResponse.data.access_token)) {
                accessToken = createUserResponse.data.access_token;
            }
            String mobile = mobileET.getText().toString();
            startActivity(new Intent(this, VerifyOtpActivity.class)
                    .putExtra("mobile", mobile)
                    .putExtra("accessToken", accessToken)
                    .putExtra("uid", createUserResponse.data.id)
                    .putExtra("gender", createUserResponse.data.gender)
                    .putExtra("dob", createUserResponse.data.dob)
                    .putExtra("name", createUserResponse.data.name)
                    .putExtra("displayName", createUserResponse.data.display_name));
        } else {
            showMessage(createUserResponse.message);
        }
    }

    @Override
    public void socialLoginResponse(SocialLoginResponse socialLoginResponse) {
        hideLoader();
        try {
            if (Helper.isContainValue(socialLoginResponse.data.accessToken)) {
                AppPreferences.getInstance(context).savePreferencesString(AppConstants.ACCESS_TOKEN, socialLoginResponse.data.accessToken);
            }
            if (Helper.isContainValue(socialLoginResponse.data.id)) {
                AppPreferences.getInstance(context).savePreferencesString(AppConstants.UID, socialLoginResponse.data.id);
            }

            AppPreferences.getInstance(context).savePreferencesString(AppConstants.MOBILE_NUMBER, socialLoginResponse.data.mobile);
            if (Helper.isContainValue(socialLoginResponse.data.dob) && Helper.isContainValue(socialLoginResponse.data.gender)) {
                AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_DETAIL, "detailSaved");

                if (Helper.isContainValue(socialLoginResponse.data.display_name)) {
                    AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_NAME_DETAIL, "detailSaved");

                    if (Helper.isContainValue(socialLoginResponse.data.mobile)) {
                        AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_MOBILE_DETAIL, "detailSaved");
                        startActivity(new Intent(context, DashboardActivity.class));
                    } else {
                        startActivity(new Intent(context, UpdateMobileActivity.class));
                    }
                } else {
                    if (Helper.isContainValue(socialLoginResponse.data.name)) {
                        AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_NAME_DETAIL, "detailSaved");

                        if (Helper.isContainValue(socialLoginResponse.data.mobile)) {
                            AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_MOBILE_DETAIL, "detailSaved");
                            startActivity(new Intent(context, DashboardActivity.class));
                        } else {
                            startActivity(new Intent(context, UpdateMobileActivity.class));
                        }
                    } else {
                        startActivity(new Intent(context, UserDetailActivity.class).putExtra("displayType", "name"));
                    }
                }
            } else {
                if (Helper.isContainValue(socialLoginResponse.data.display_name)) {
                    AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_NAME_DETAIL, "detailSaved");
                } else {
                    if (Helper.isContainValue(socialLoginResponse.data.name)) {
                        AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_NAME_DETAIL, "detailSaved");
                    }
                }
                startActivity(new Intent(context, UserDetailActivity.class).putExtra("displayType", "all"));
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, AppConstants.GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (result.isSuccessful()) {
                handleGoogleSignInResult(result);
            } else {
                //  showMessage("Unauthorized access");
            }
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                if (Helper.isContainValue(account.getEmail())) {
                    if (Helper.isNetworkAvailable(context)) {
                        showLoader();
                        loginPresenter.getSocialLogin("Google", account.getDisplayName(), account.getEmail(), account.getId(), account.getIdToken());
                    } else {
                        showMessage(getString(R.string.please_check_internet_connection));
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Error", "signInResult:failed code=" + e.getMessage());
        }
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && token.getToken() != null) {
                    GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                id = response.getJSONObject().optString("id");
                                displayName = response.getJSONObject().optString("name");
                                email = response.getJSONObject().optString("email");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (Helper.isContainValue(email)) {
                                if (Helper.isNetworkAvailable(context)) {
                                    showLoader();
                                    loginPresenter.getSocialLogin("Facebook", displayName, email, id, token.getToken());
                                } else {
                                    showMessage(getString(R.string.please_check_internet_connection));
                                }
                            }
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,first_name,last_name,name,gender,email,picture");
                    request.setParameters(parameters);
                    request.executeAsync();
                } else {
                    // showMessage("Unauthorized access");
                }
            }
        });
    }
}