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

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import kr.co.core.kita.BuildConfig;
import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityEditProfileBinding;
import kr.co.core.kita.dialog.PictureDlg;
import kr.co.core.kita.dialog.SelectDlg;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class EditProfileAct extends BaseAct implements View.OnClickListener {
    ActivityEditProfileBinding binding;
    Activity act;

    private Uri photoUri;
    private String mImgFilePath;
    private String mImgFilePath_origin;

    private static final int SELECT = 101;
    private static final int PICK_DIALOG = 100;
    private static final int PHOTO_GALLERY = 1001;
    private static final int PHOTO_TAKE = 1002;
    private static final int PHOTO_CROP = 1003;

    boolean isPhotoChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile, null);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.tvEdit.setOnClickListener(this);
        binding.tvRecentWork01.setOnClickListener(this);
        binding.ivProfileReg.setOnClickListener(this);

        getMyInfo();
    }

    private File downloadImage(String imgUrl) {
        Bitmap img = null;
        File f = null;
        Log.e(StringUtil.TAG, "imgUrl: " + imgUrl);

        try {
            f = createImageFile();
            URL url = new URL(imgUrl);
            URLConnection conn = url.openConnection();

            int nSize = conn.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
            img = BitmapFactory.decodeStream(bis);

            bis.close();

            FileOutputStream out = new FileOutputStream(f);
            img.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            img.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return f;
    }

    private void getMyInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.INFO_ME) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONObject job = jo.getJSONObject("value");
                            String intro = Common.decodeEmoji(StringUtil.getStr(job, "intro"));
                            String location = StringUtil.getStr(job, "location");
                            String location2 = StringUtil.getStr(job, "location2");
                            mImgFilePath_origin = StringUtil.getStr(job, "p_image1");

                            //TODO 추가 -- 선물 받은 개수 / 영상통화시 몇 peso 썼는지

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 닉네임
                                    binding.etIntro.setText(intro);
                                    // 선물 받은 개수
                                    binding.tvRecentWork01.setText(location);
                                    // 영상통화시 몇 peso 썼는지
                                    binding.etRecentWork02.setText(location2);
                                    // 프로필 사진 등록
                                    if (!StringUtil.isNull(mImgFilePath_origin))
                                        Glide.with(act).load(mImgFilePath_origin).into(binding.ivProfile);
                                }
                            });
                        } else {

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

        server.setTag("My Info");
        server.addParams("u_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    private void doEdit() {
        ReqBasic server = new ReqBasic(act, NetUrls.EDIT_PROFILE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "Edited successfully.");
                            finish();
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
                            Log.i(StringUtil.TAG, "value: " + StringUtil.getStr(jo, "value"));
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


        try {
            server.setTag("Edit Profile");
            server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
            server.addParams("intro", URLEncoder.encode(binding.etIntro.getText().toString(),"UTF-8"));

            server.addParams("location", binding.tvRecentWork01.getText().toString());
            server.addParams("location2", binding.etRecentWork02.getText().toString());
            if (!isPhotoChanged && !StringUtil.isNull(mImgFilePath_origin)) {
                File file = downloadImage(mImgFilePath_origin);
                server.addFileParams("image", file);
            } else {
                File file = new File(mImgFilePath);
                server.addFileParams("image", file);
            }
            server.execute(true, false);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.tv_edit:
                if (binding.etIntro.length() == 0) {
                    Common.showToast(act, "Please enter your Introduction");
                } else if (binding.tvRecentWork01.length() == 0) {
                    Common.showToast(act, "Please select a region");
                } else {
                    doEdit();
                }
                break;

            case R.id.tv_recent_work_01:
                Intent intent = new Intent(act, SelectDlg.class);
                intent.putExtra("type", AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase(Common.GENDER_M) ? "Male" : "Female");
                intent.putExtra("data", binding.tvRecentWork01.getText().toString());
                startActivityForResult(intent, SELECT);
                break;

            case R.id.iv_profile_reg:
                startActivityForResult(new Intent(act, PictureDlg.class), PICK_DIALOG);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case SELECT:
                    String value = data.getStringExtra("value");
                    binding.tvRecentWork01.setText(value);
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
                                isPhotoChanged = true;

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
