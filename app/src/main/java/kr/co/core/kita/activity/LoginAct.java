package kr.co.core.kita.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityLoginBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class LoginAct extends BaseAct implements View.OnClickListener {
    ActivityLoginBinding binding;
    public static Activity act;

    private String joinType = "general";
    private String joinToken = "";
    CallbackManager facebookCallbackManager;
    OAuthLogin mOAuthLoginModule;

    private String REGEX_ID = "^[a-z0-9]{5,11}$"; // 영문 소문자, 숫자 (5~11자) (선택적)
    private String REGEX_PW = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{6,12}$"; // 영문 대/소문자, 특수문자 (6~12자) (필수적)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login, null);
        act = this;

        FacebookSdk.fullyInitialize();
        LoginManager.getInstance().logOut();

        binding.flBack.setOnClickListener(this);
        binding.llLoginBtn.setOnClickListener(this);
        binding.llNaverBtn.setOnClickListener(this);
        binding.llFacebookBtn.setOnClickListener(this);
        binding.tvJoin.setOnClickListener(this);

        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                act
                , getResources().getString(R.string.oauth_client_id)
                , getResources().getString(R.string.oauth_client_secret)
                , getResources().getString(R.string.oauth_client_name)
        );

        setFaceBookCallBack();
    }

    private void setFaceBookCallBack() {
        /* set facebook callback */
        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // 로그인에 성공하면 LoginResult 매개변수에 새로운 AccessToken 과 최근에 부여되거나 거부된 권한이 포함됩니다.
                final GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                if (response.getError() != null) {
                                    Log.e(StringUtil.TAG, "onCompleted error message: " + response.getError().getErrorMessage());
                                } else {
                                    Log.i(StringUtil.TAG, "onCompleted success data: " + object.toString());
                                    try {
                                        joinType = "facebook";
                                        joinToken = object.getString("id");
                                        Log.i(StringUtil.TAG, "facebook result token: " + joinToken);

                                        doLogin();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e(StringUtil.TAG, "facebook onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(StringUtil.TAG, "facebook onError: " + error.getMessage());
            }
        });
    }


    @SuppressLint("HandlerLeak")
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginModule.getAccessToken(act);
                String refreshToken = mOAuthLoginModule.getRefreshToken(act);
                long expiresAt = mOAuthLoginModule.getExpiresAt(act);
                String tokenType = mOAuthLoginModule.getTokenType(act);

                Log.i(StringUtil.TAG, "accessToken: " + accessToken);
                Log.i(StringUtil.TAG, "refreshToken: " + refreshToken);
                Log.i(StringUtil.TAG, "expiresAt: " + expiresAt);
                Log.i(StringUtil.TAG, "tokenType: " + tokenType);
                Log.i(StringUtil.TAG, "authState: " + mOAuthLoginModule.getState(act).toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String header = "Bearer " + accessToken;
                        try {
                            String apiURL = "https://openapi.naver.com/v1/nid/me";
                            URL url = new URL(apiURL);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setRequestMethod("GET");
                            con.setRequestProperty("Authorization", header);
                            int responseCode = con.getResponseCode();
                            BufferedReader br;
                            if (responseCode == 200) { // 정상 호출
                                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            } else {  // 에러 발생
                                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                            }
                            String inputLine;
                            StringBuffer response = new StringBuffer();
                            while ((inputLine = br.readLine()) != null) {
                                response.append(inputLine);
                            }
                            br.close();
                            System.out.println(response.toString());
                            Log.i(StringUtil.TAG, "naver res: " + response.toString());

                            JSONObject naverData = new JSONObject(response.toString());
                            if (StringUtil.getStr(naverData, "message").equalsIgnoreCase("success")) {
                                JSONObject naverResponse = naverData.getJSONObject("response");

                                OAuthLogin.getInstance().logout(act);


                                joinType = "naver";
                                joinToken = naverResponse.getString("id");
                                Log.i(StringUtil.TAG, "naver result token: " + joinToken);

                                doLogin();
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }).start();
            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(act).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(act);
                Log.i(StringUtil.TAG, "naver errorCode:" + errorCode + ", naver errorDesc:" + errorDesc);
            }
        }

        ;
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doLogin() {
        ReqBasic server = new ReqBasic(act, NetUrls.LOGIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONObject job = jo.getJSONObject("value");
                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, StringUtil.getStr(job, "idx"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, StringUtil.getStr(job, "gender"));
                            AppPreference.setProfilePrefBool(act, AppPreference.PREF_AUTO_LOGIN_STATE, true);

                            switch (joinType) {
                                case "facebook":
                                case "naver":
                                    AppPreference.setProfilePref(act, AppPreference.PREF_ID, joinToken);
                                    AppPreference.setProfilePref(act, AppPreference.PREF_PW, joinToken);
                                    break;

                                case "general":
                                    AppPreference.setProfilePref(act, AppPreference.PREF_ID, binding.etId.getText().toString());
                                    AppPreference.setProfilePref(act, AppPreference.PREF_PW, binding.etPw.getText().toString());
                                    break;
                            }

                            startActivity(new Intent(act, MainAct.class));
                            finish();
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "value"));
                            Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "value"));

                            switch (joinType) {
                                case "facebook":
                                case "naver":
                                    startActivity(new Intent(act, JoinAct.class).putExtra("join_type", joinType).putExtra("token", joinToken));
                                    break;

                                case "general":
                                    Common.showToast(act, "Login failed");
                                    break;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Login");
        if(joinType.equalsIgnoreCase("general")) {
            server.addParams("id", binding.etId.getText().toString());
            server.addParams("pw", binding.etPw.getText().toString());
        } else {
            server.addParams("id", joinToken);
            server.addParams("pw", joinToken);
        }
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));
        server.execute(true, false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_login_btn:
                joinType = "general";

                if(binding.etId.length() == 0) {
                    Common.showToast(act, "Please enter your ID");
                } else if(binding.etPw.length() == 0) {
                    Common.showToast(act, "Please enter your Password");
                } else {
                    doLogin();
                }
                break;

            case R.id.ll_naver_btn:
                mOAuthLoginModule.startOauthLoginActivity(LoginAct.this, mOAuthLoginHandler);
                break;
            case R.id.ll_facebook_btn:
                LoginManager.getInstance().logInWithReadPermissions(act, Arrays.asList("public_profile"));
                break;

            case R.id.tv_join:
                startActivity(new Intent(act, JoinAct.class).putExtra("join_type", Common.JOIN_TYPE_GENERAL));
                break;
        }
    }
}
