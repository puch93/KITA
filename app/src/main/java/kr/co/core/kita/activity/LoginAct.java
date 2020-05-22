package kr.co.core.kita.activity;

import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityLoginBinding;
import kr.co.core.kita.util.StringUtil;

public class LoginAct extends BaseAct implements View.OnClickListener {
    ActivityLoginBinding binding;
    Activity act;

    OAuthLogin mOAuthLoginModule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login, null);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.llLoginBtn.setOnClickListener(this);
        binding.llNaverBtn.setOnClickListener(this);
        binding.llFacebookBtn.setOnClickListener(this);
        binding.tvJoin.setOnClickListener(this);

        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                act
                ,getResources().getString(R.string.oauth_client_id)
                ,getResources().getString(R.string.oauth_client_secret)
                ,getResources().getString(R.string.oauth_client_name)
        );
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
                                Log.i(StringUtil.TAG, "naver token: " + naverResponse.getString("id"));
                                //TODO
//                                reqJoinCheck("naver", naverResponse.getString("id"));
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
        };
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_login_btn:
                startActivity(new Intent(act, MainAct.class));
                finish();
                break;
            case R.id.ll_naver_btn:
                mOAuthLoginModule.startOauthLoginActivity(LoginAct.this, mOAuthLoginHandler);
                break;
            case R.id.ll_facebook_btn:
                break;

            case R.id.tv_join:
                startActivity(new Intent(act, TermJoinAct.class));
                break;
        }
    }
}
