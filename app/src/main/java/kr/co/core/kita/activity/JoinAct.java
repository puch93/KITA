package kr.co.core.kita.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import kr.co.core.kita.BuildConfig;
import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityJoinBinding;
import kr.co.core.kita.dialog.PictureDlg;
import kr.co.core.kita.dialog.SelectDlg;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class JoinAct extends BaseAct implements View.OnClickListener {
    ActivityJoinBinding binding;
    Activity act;

    private Uri photoUri;
    private String mImgFilePath;

    private static final int SELECT = 101;
    private static final int PICK_DIALOG = 100;
    private static final int PHOTO_GALLERY = 1001;
    private static final int PHOTO_TAKE = 1002;
    private static final int PHOTO_CROP = 1003;
    TextView selectedView;

    private String REGEX_ID = "^[a-z0-9]{5,11}$"; // 영문 소문자, 숫자 (5~11자) (선택적)
    private String REGEX_PW = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{6,12}$"; // 영문 대/소문자, 특수문자 (6~12자) (필수적)
    private String REGEX_NICK = "^[A-Za-z0-9ㄱ-ㅎ가-힣]{2,10}$"; // 영문 대/소문자, 숫자, 한글 (2~10자) (선택적)


    private String token, join_type = "general";
    private String fcm = "";
    private String id = "";
    private String pw = "";
    private String nick = "";
    private String gender = "";
    private String image = "";
    private String location = "";
    private String location2 = "";
    private String intro = "";
    private String referrer = "";
    private String facebook_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join, null);
        act = this;

        // set click listener
        binding.flBack.setOnClickListener(this);
        binding.tvRecentWork01.setOnClickListener(this);
        binding.tvGender.setOnClickListener(this);
        binding.ivProfileReg.setOnClickListener(this);
        binding.tvSingUp.setOnClickListener(this);

        join_type = getIntent().getStringExtra("join_type");
        token = getIntent().getStringExtra("token");

        if(!join_type.equalsIgnoreCase("general")) {
            binding.llNormalArea.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.tv_recent_work_01:
                selectedView = (TextView) v;

                if (binding.tvGender.length() == 0) {
                    Common.showToast(act, "Please select your gender first");
                } else {
                    intent = new Intent(act, SelectDlg.class);
                    intent.putExtra("type", binding.tvGender.getText().toString());
                    intent.putExtra("data", binding.tvRecentWork01.getText().toString());
                    startActivityForResult(intent, SELECT);
                }
                break;

            case R.id.tv_gender:
                selectedView = (TextView) v;

                intent = new Intent(act, SelectDlg.class);
                intent.putExtra("type", SelectDlg.TYPE_GENDER);
                intent.putExtra("data", binding.tvGender.getText().toString());
                startActivityForResult(intent, SELECT);
                break;

            case R.id.iv_profile_reg:
                startActivityForResult(new Intent(act, PictureDlg.class), PICK_DIALOG);
                break;

            case R.id.tv_sing_up:
                if (join_type.equalsIgnoreCase("general")) {
                    if (StringUtil.isNull(mImgFilePath)) {
                        Common.showToast(act, "Please register your Photo");
                    } else if (binding.etId.length() == 0 || !Pattern.matches(REGEX_ID, binding.etId.getText().toString())) {
                        Common.showToast(act, "Please check your ID");
                    } else if (binding.etPw.length() == 0 || !Pattern.matches(REGEX_PW, binding.etPw.getText().toString())) {
                        Common.showToast(act, "Please check your Password");
                    } else if (binding.etPwConfirm.length() == 0 || !binding.etPw.getText().toString().equalsIgnoreCase(binding.etPwConfirm.getText().toString())) {
                        Common.showToast(act, "Please check your Password Confirm");
                    } else if (binding.etNick.length() == 0 || !Pattern.matches(REGEX_NICK, binding.etNick.getText().toString())) {
                        Common.showToast(act, "Please check your Nickname");
                    } else if (binding.tvGender.length() == 0) {
                        Common.showToast(act, "Please check your Gender selection");
                    } else if (binding.etIntro.length() == 0) {
                        Common.showToast(act, "Please check your Introduction");
                    } else if (binding.tvRecentWork01.length() == 0) {
                        Common.showToast(act, "Please check your Location");
                    } else {
                        fcm = AppPreference.getProfilePref(act, AppPreference.PREF_FCM);
                        id = binding.etId.getText().toString();
                        pw = binding.etPw.getText().toString();
                        nick = binding.etNick.getText().toString();
                        gender = binding.tvGender.getText().toString().equalsIgnoreCase("Male") ? Common.GENDER_M : Common.GENDER_W;
                        image = mImgFilePath;
                        location = binding.tvRecentWork01.getText().toString();
                        location2 = binding.etRecentWork02.getText().toString();
                        intro = binding.etIntro.getText().toString();
                        referrer = binding.etReferrer.getText().toString();
                        facebook_id = binding.etFacebookId.getText().toString();

                        doJoin();
                    }
                } else {
                    if (binding.etNick.length() == 0 || !Pattern.matches(REGEX_NICK, binding.etNick.getText().toString())) {
                        Common.showToast(act, "Please check your Nickname");
                    } else if (binding.tvGender.length() == 0) {
                        Common.showToast(act, "Please check your Gender selection");
                    } else if (binding.etIntro.length() == 0) {
                        Common.showToast(act, "Please check your Introduction");
                    } else if (binding.tvRecentWork01.length() == 0) {
                        Common.showToast(act, "Please check your Location");
                    } else {
                        fcm = AppPreference.getProfilePref(act, AppPreference.PREF_FCM);
                        id = token;
                        pw = token;
                        nick = binding.etNick.getText().toString();
                        gender = binding.tvGender.getText().toString().equalsIgnoreCase("Male") ? Common.GENDER_M : Common.GENDER_W;
                        image = mImgFilePath;
                        location = binding.tvRecentWork01.getText().toString();
                        location2 = binding.etRecentWork02.getText().toString();
                        intro = binding.etIntro.getText().toString();
                        referrer = binding.etReferrer.getText().toString();
                        facebook_id = binding.etFacebookId.getText().toString();

                        doJoin();
                    }
                }
                break;
        }
    }

    private void doJoin() {
        ReqBasic server = new ReqBasic(act, NetUrls.JOIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            if(gender.equalsIgnoreCase("female")) {

                            } else {
                                Common.showToast(act, "joined successfully");
                                doLogin();
                            }
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "value"));
                            Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "value"));
                            Common.showToastNetwork(act);
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

        server.setTag("Join");
        server.addParams("join_type", join_type);
        server.addParams("fcm", fcm);
        server.addParams("id", id);
        server.addParams("pw", pw);
        server.addParams("pw_confirm", pw);
        server.addParams("nick", nick);
        server.addParams("gender", gender);

        File imageFile = new File(image);
        server.addFileParams("image", imageFile);

        server.addParams("location", location);
        server.addParams("location2", location2);
        server.addParams("intro", intro);
        server.addParams("referrer", referrer);
        server.addParams("facebook_id", facebook_id);
        server.execute(true, true);
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

                            switch (join_type) {
                                case "facebook":
                                case "naver":
                                    AppPreference.setProfilePref(act, AppPreference.PREF_ID, token);
                                    AppPreference.setProfilePref(act, AppPreference.PREF_PW, token);
                                    break;

                                case "general":
                                    AppPreference.setProfilePref(act, AppPreference.PREF_ID, binding.etId.getText().toString());
                                    AppPreference.setProfilePref(act, AppPreference.PREF_PW, binding.etPw.getText().toString());
                                    break;
                            }

                            if (LoginAct.act != null) {
                                LoginAct.act.finish();
                            }

                            startActivity(new Intent(act, MainAct.class));
                            finish();
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "value"));
                            Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "value"));
                            Common.showToast(act, "Login failed");
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
        if(join_type.equalsIgnoreCase("general")) {
            server.addParams("id", binding.etId.getText().toString());
            server.addParams("pw", binding.etPw.getText().toString());
        } else {
            server.addParams("id", token);
            server.addParams("pw", token);
        }
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));
        server.execute(true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT:
                    String value = data.getStringExtra("value");

                    if (selectedView != null) {
                        selectedView.setText(data.getStringExtra("value"));
                        if (selectedView.getId() == R.id.tv_gender) {
                            binding.tvRecentWork01.setText(null);
                        }
                    }
                    break;

                case PICK_DIALOG:
                    String type = data.getStringExtra("type");

                    if (type.equalsIgnoreCase(PictureDlg.CAMERA)) {
                        //촬영하기
                        takePhoto();
                    } else {
                        //갤러리
                        getAlbum();
                    }
                    break;

                //사진 갤러리 결과
                case PHOTO_GALLERY:
                    if (data == null) {
                        Common.showToast(act, "사진불러오기 실패! 다시 시도해주세요.");
                        return;
                    }

                    photoUri = data.getData();
                    cropImage();
                    break;

                //사진 촬영 결과
                case PHOTO_TAKE:
                    cropImage();
                    break;

                //사진 크롭 결과
                case PHOTO_CROP:
                    mImgFilePath = photoUri.getPath();
                    if (StringUtil.isNull(mImgFilePath)) {
                        Common.showToast(act, "사진자르기 실패! 다시 시도해주세요.");
                        return;
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bm = BitmapFactory.decodeFile(mImgFilePath, options);

                    Bitmap resize = null;
                    try {
                        File resize_file = new File(mImgFilePath);
                        FileOutputStream out = new FileOutputStream(resize_file);

                        int width = bm.getWidth();
                        int height = bm.getHeight();

                        if (width > 1024) {
                            int resizeHeight = 0;
                            if (height > 768) {
                                resizeHeight = 768;
                            } else {
                                resizeHeight = height / (width / 1024);
                            }

                            resize = Bitmap.createScaledBitmap(bm, 1024, resizeHeight, true);
                            resize.compress(Bitmap.CompressFormat.PNG, 100, out);
                        } else {
                            resize = Bitmap.createScaledBitmap(bm, width, height, true);
                            resize.compress(Bitmap.CompressFormat.PNG, 100, out);
                        }
                        Log.e("TEST_HOME", "mImgFilePath: " + mImgFilePath);


                        // 사진 추가
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(act)
                                        .load(mImgFilePath)
                                        .into(binding.ivProfile);
                            }
                        });

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    MediaScannerConnection.scanFile(act, new String[]{photoUri.getPath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });
                    break;
            }
        }
    }

    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PHOTO_GALLERY);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Common.showToast(act, "이미지 처리 오류! 다시 시도해주세요.");
            e.printStackTrace();
        }

        if (photoFile != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(act,
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
            } else {
                photoUri = Uri.fromFile(photoFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PHOTO_TAKE);
        }
    }

    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
//        String imageFileName = "thefiven" + timeStamp;
        String imageFileName = String.valueOf(System.currentTimeMillis());

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TheFiveN");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return File.createTempFile(imageFileName, ".png", storageDir);
    }


    private void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoUri, "image/*");

        // 파일 생성
        try {
            File albumFile = createImageFile();
            Log.e(StringUtil.TAG, "cropImage: " + albumFile.getAbsolutePath());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(act, BuildConfig.APPLICATION_ID + ".provider", albumFile);
            } else {
                photoUri = Uri.fromFile(albumFile);
            }

        } catch (IOException e) {
            Log.e(StringUtil.TAG, "cropImage: 에러");
            e.printStackTrace();
        }

        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", photoUri);

        // 여러 카메라어플중 기본앱 세팅
        List<ResolveInfo> list = act.getPackageManager().queryIntentActivities(cropIntent, 0);

        Intent i = new Intent(cropIntent);
        ResolveInfo res = list.get(0);

        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        act.grantUriPermission(res.activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        startActivityForResult(i, PHOTO_CROP);
    }
}
