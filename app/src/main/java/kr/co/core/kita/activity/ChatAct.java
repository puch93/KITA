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
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import kr.co.core.kita.BuildConfig;
import kr.co.core.kita.R;
import kr.co.core.kita.activity.rtc.ConnectAct;
import kr.co.core.kita.adapter.ChatAdapter;
import kr.co.core.kita.data.ChattingData;
import kr.co.core.kita.databinding.ActivityChatBinding;
import kr.co.core.kita.dialog.PictureDlg;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.ChatValues;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;
import okhttp3.OkHttpClient;

public class ChatAct extends BaseAct implements View.OnClickListener {
    ActivityChatBinding binding;
    public static Activity act;
    public static Activity real_act;

    private ArrayList<ChattingData> list = new ArrayList<>();
    private ChatAdapter adapter;

    private String otherImage, yidx;
    public static String room_idx = "";

    private io.socket.client.Socket mSocket;
    private boolean exitState = false;

    private boolean isPossible = false;
    private boolean isFirstMsg = true;
    private String peso = "";


    /* 이미지 보내기 관련 */
    private Uri photoUri;
    private String mImgFilePath;
    private static final int PICK_DIALOG = 1000;
    private static final int PHOTO_GALLERY = 1001;
    private static final int PHOTO_TAKE = 1002;
    private static final int PHOTO_CROP = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat, null);
        act = this;
        real_act = this;

        room_idx = getIntent().getStringExtra("room_idx");
        yidx = getIntent().getStringExtra("yidx");
        otherImage = getIntent().getStringExtra("otherImage");

        // 내 페소 정보가져오기
        getMyInfo();

        checkChattingTicket();

        setLayout();

        setupSocketClient();

        checkFirstMsg();
    }

    private void checkFirstMsg() {
        ReqBasic server = new ReqBasic(act, NetUrls.FIRST_MSG) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            isFirstMsg = false;
                        } else {
                            isFirstMsg = true;
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

        server.setTag("First Msg");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("room_idx", room_idx);
        server.execute(true, false);
    }

    private void checkFirstMsgAfter() {
        ReqBasic server = new ReqBasic(act, NetUrls.FIRST_MSG) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            isFirstMsg = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showAlert(act, "Video Call", "Would you like to make a video call?", new OnAfterConnection() {
                                        @Override
                                        public void onAfter() {
                                            if(AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase("male")) {
                                                if(!peso.equalsIgnoreCase("0")) {
                                                    getOtherInfo();
                                                } else {
                                                    Common.showToast(act, "Your PHP is not enough");
                                                }
                                            } else {
                                                getOtherInfo();
                                            }
                                        }
                                    });
                                }
                            });

                        } else {
                            isFirstMsg = true;
                            Common.showToast(act, "After sending a chat, you can make a video call.");
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

        server.setTag("First Msg");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("room_idx", room_idx);
        server.execute(true, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TEST_TEST", "onDestroy: ");
        if (!exitState) {
            if (mSocket != null && mSocket.connected()) {
                mSocket.disconnect();
            }
            real_act = null;
            exitState = true;
        }
    }

    private void setLayout() {
        // set click listener
        binding.flBack.setOnClickListener(this);
        binding.flMore.setOnClickListener(this);
        binding.flCall.setOnClickListener(this);
        binding.flSend.setOnClickListener(this);
        binding.flDelete.setOnClickListener(this);
        binding.flGift.setOnClickListener(this);


        // EditText 포커스될때 키보드가 UI 가리는 것 막음
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // set recycler view
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));

        adapter = new ChatAdapter(act, room_idx, list, otherImage);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    binding.recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
    }

    private void doLeave() {
        ReqBasic server = new ReqBasic(act, NetUrls.LEAVE_ROOM) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "The chat room has been deleted.");
                            finish();
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

        server.setTag("Chat Leave");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("room_idx", room_idx);
        server.execute(true, false);
    }

    //채팅푸시전송
    private void sendMessage(final String contents) {
        ReqBasic server = new ReqBasic(act, NetUrls.SEND_CHAT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            //실제채팅전송
                            JSONObject sendData = new JSONObject();
                            sendData.put(ChatValues.ROOMIDX, room_idx);
                            sendData.put(ChatValues.TALKER, AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
                            sendData.put(ChatValues.MSG, contents);
                            mSocket.emit(ChatValues.SEND_MSG, sendData);
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
                            Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "msg"));
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

        server.setTag("Send Message");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("contents", contents);
        server.addParams("room_idx", room_idx);
        server.execute(true, false);
    }

    //채팅 이미지 전송
    private void sendImage() {
        ReqBasic server = new ReqBasic(act, NetUrls.SEND_IMAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date_s = dateFormat.format(date);

                    String msg = resultData.getResult().replaceAll("\"", "");

                    sendMessage(msg);
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        File image = new File(mImgFilePath);
        server.setTag("Chat Send Image");
        server.addFileParams("pimg", image);
        server.execute(true, false);
    }

    private void checkChattingTicket() {
        ReqBasic server = new ReqBasic(act, NetUrls.CHAT_TICKET_PURCHASED) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            isPossible = true;
                        } else {
                            isPossible = false;
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

        server.setTag("Check Chatting Ticket");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }


    private void checkChattingPossible(boolean isText) {
        ReqBasic server = new ReqBasic(act, NetUrls.CHAT_POSSIBLE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            if(isText) {
                                sendMessage(binding.etContents.getText().toString());
                                binding.etContents.setText("");
                            } else {
                                sendImage();
                            }
                        } else {
                           Common.showToast(act, "Please use after purchasing unlimited chat pass.");
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

        server.setTag("Check Chatting Possible");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("room_idx", room_idx);
        server.execute(true, false);
    }

    private void getOtherInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.INFO_OTHER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONObject job = jo.getJSONObject("value");
                            String idx = StringUtil.getStr(job, "idx");
                            String nick = StringUtil.getStr(job, "nick");
                            String p_image1 = StringUtil.getStr(job, "p_image1");
                            String location = StringUtil.getStr(job, "location");

                            Intent intent = new Intent(act, ConnectAct.class);
                            intent.putExtra("type", "call");
                            intent.putExtra("u_idx", idx);
                            intent.putExtra("u_nick", nick);
                            intent.putExtra("u_region", location);
                            intent.putExtra("u_profile_img", p_image1);
                            startActivity(intent);
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

        server.setTag("Other Info");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("y_idx", yidx);
        server.execute(true, false);
    }

    private void getOtherInfo_gift() {
        ReqBasic server = new ReqBasic(act, NetUrls.INFO_OTHER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONObject job = jo.getJSONObject("value");
                            String nick = StringUtil.getStr(job, "nick");

                            startActivity(new Intent(act, GiftAct.class).putExtra("yidx", yidx).putExtra("nick", nick));

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

        server.setTag("Other Info");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("y_idx", yidx);
        server.execute(true, false);
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
                            peso = StringUtil.getStr(job, "peso");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_send:
                if (binding.etContents.length() == 0) {
                    Common.showToast(act, "Please enter your chat content");
                } else {
                    if (isPossible) {
                        Log.i(StringUtil.TAG, "isPossible: true");
                        sendMessage(binding.etContents.getText().toString());
                        binding.etContents.setText("");
                    } else {
                        Log.i(StringUtil.TAG, "isPossible: false");
                        checkChattingPossible(true);
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }, 300);
                break;

            case R.id.fl_back:
                finish();
                break;

            case R.id.fl_gift:
                getOtherInfo_gift();
                break;

            case R.id.fl_more:
                startActivityForResult(new Intent(act, PictureDlg.class), PICK_DIALOG);
                break;

            case R.id.fl_call:
                if(isFirstMsg) {
                    checkFirstMsgAfter();
                } else {
                    showAlert(act, "Video Call", "Would you like to make a video call?", new OnAfterConnection() {
                        @Override
                        public void onAfter() {
                            if(AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase("male")) {
                                if(!peso.equalsIgnoreCase("0")) {
                                    getOtherInfo();
                                } else {
                                    Common.showToast(act, "Your PHP is not enough");
                                }
                            } else {
                                getOtherInfo();
                            }
                        }
                    });
                }
                break;

            case R.id.fl_delete:
                showAlert(act, "Leave", "Are you leaving this room?", new OnAfterConnection() {
                    @Override
                    public void onAfter() {
                        doLeave();
                    }
                });
                break;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_DIALOG:
                    if (data.getStringExtra("type").equalsIgnoreCase(PictureDlg.CAMERA)) {
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


                        if (isPossible) {
                            sendImage();
                        } else {
                            checkChattingPossible(false);
                        }

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

    /* 소켓 */
    private void setupSocketClient() {
        try {
            Log.i(StringUtil.TAG, "setupSocketClient");
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                    return myTrustedAnchors;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .sslSocketFactory(sc.getSocketFactory()).build();

            // default settings for all sockets
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
            IO.setDefaultOkHttpCallFactory(okHttpClient);

            // set as an option
            IO.Options opts = new IO.Options();
            opts.callFactory = okHttpClient;
            opts.webSocketFactory = okHttpClient;

            mSocket = IO.socket(ChatValues.SOCKET_URL);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(ChatValues.CHATTING_HISTORY, onMessageReceived);
            mSocket.on(ChatValues.CHATTING_SYSTEM_MSG, onChatReceive);
            mSocket.connect();
            System.out.println("socket setup!!! ");
        } catch (URISyntaxException e) {
            Log.i(StringUtil.TAG, "URISyntaxException");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Log.i(StringUtil.TAG, "NoSuchAlgorithmException");
            e.printStackTrace();
        } catch (KeyManagementException e) {
            Log.i(StringUtil.TAG, "KeyManagementException");
            e.printStackTrace();
        }
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject sendData = new JSONObject();
            Log.e(StringUtil.TAG_CHAT, "onConnect");
            System.out.println("socket onConnect : " + sendData);
            try {
                sendData.put(ChatValues.ROOMIDX, room_idx);
                sendData.put(ChatValues.TALKER, AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
                mSocket.emit(ChatValues.CHATTING_HISTORY, sendData);

                Log.e(StringUtil.TAG, "onConnect Put: " + sendData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // 채팅 내역(이전 대화 내용)
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(StringUtil.TAG_CHAT, "onMessageReceived");
            JSONObject rcvData = (JSONObject) args[0];

            try {
                list = new ArrayList<>();

                JSONArray ja = new JSONArray(StringUtil.getStr(rcvData, "chats"));
                if (ja.length() > 0) {
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        Log.e(StringUtil.TAG, "chat_list(" + i + "): " + jo);

                        // 유저 인덱스
                        String u_idx = StringUtil.getStr(jo, "user_idx");

                        // 메시지
                        String contents = StringUtil.getStr(jo, "msg");

                        // 보낸 시간
                        String send_time = StringUtil.converTime(StringUtil.getStr(jo, "created_at"), "a hh:mm");

                        // 데이트라인 데이터 추가
                        String dateLine = StringUtil.converTime(StringUtil.getStr(jo, "created_at"), "yyyy.MM.dd");

                        // 읽음 처리
                        boolean isRead = false;
                        String[] idxs = StringUtil.getStr(jo, "read_user_idx").split(",");
                        if (idxs.length > 1)
                            isRead = true;

                        // 데이트라인 확인 후 추가
                        if (i > 0) {
                            if (!list.get(list.size() - 1).getDate_line().equals(dateLine)) {
                                ChattingData data = new ChattingData(dateLine, ChatValues.MSG_DATELINE);
                                list.add(data);
                            }
                        } else {
                            ChattingData data = new ChattingData(dateLine, ChatValues.MSG_DATELINE);
                            list.add(data);
                        }

                        list.add(new ChattingData(u_idx, isImage(contents), send_time, dateLine, contents, isRead));
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setList(list);
                            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    });
                }

            } catch (
                    JSONException e) {
                Log.i(StringUtil.TAG, "JSONException: " + e.toString());
                e.printStackTrace();
            }

        }
    };


    private String isImage(String msg) {
        String reg = "^([\\S]+(\\.(?i)(jpg|png|jpeg))$)";

        return msg.matches(reg) ? "image" : "text";
    }


    // 실시간 메세지 처리
    private Emitter.Listener onChatReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(StringUtil.TAG_CHAT, "onChatReceive");
            JSONObject rcvData = (JSONObject) args[0];
            String selectDate = null;

            try {
                JSONObject from = new JSONObject(rcvData.getString("from"));
                Log.e(StringUtil.TAG, "onChatReceive (from): " + from);

                JSONObject chat = new JSONObject(from.getString("chat"));
                Log.e(StringUtil.TAG, "onChatReceive (chat): " + chat);

                // 유저 인덱스
                String u_idx = StringUtil.getStr(chat, "user_idx");

                // 메시지
                String contents = StringUtil.getStr(chat, "msg");

                // 보낸 시간
                String send_time = StringUtil.converTime(StringUtil.getStr(chat, "created_at"), "a hh:mm");

                //읽음처리
                boolean isRead = false;
                String[] idxs = StringUtil.getStr(chat, "read_user_idx").split(",");
                if (idxs.length > 1)
                    isRead = true;


                list.add(new ChattingData(u_idx, isImage(contents), send_time, "", contents, isRead));


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setList(list);
                        binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
