package kr.co.core.kita.activity.rtc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.corertc.coresdk.rtc.AppRTCAudioManager;
import com.corertc.coresdk.rtc.AppRTCClient;
import com.corertc.coresdk.rtc.DirectRTCClient;
import com.corertc.coresdk.rtc.PeerConnectionClient;
import com.corertc.coresdk.rtc.UnhandledExceptionHandler;
import com.corertc.coresdk.rtc.WebSocketRTCClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSink;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.BaseAct;
import kr.co.core.kita.dialog.CallGiftDlg;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.SSLConnect;
import kr.co.core.kita.util.StringUtil;

public class VideoCallAct extends BaseAct implements AppRTCClient.SignalingEvents,
        PeerConnectionClient.PeerConnectionEvents {

    private static final String TAG = "CallActivity";

    static {
        try {
            System.loadLibrary("c++_shared");
            System.loadLibrary("boringssl.cr");
            System.loadLibrary("protobuf_lite.cr");
        } catch (UnsatisfiedLinkError e) {
        }
    }

    public static final String EXTRA_ROOMID = "ROOMID";
    public static final String EXTRA_URLPARAMETERS = "URLPARAMETERS";
    public static final String EXTRA_LOOPBACK = "LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = "VIDEO_CALL";
    public static final String EXTRA_CAMERA2 = "CAMERA2";
    public static final String EXTRA_VIDEO_WIDTH = "VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "VIDEO_FPS";
    public static final String EXTRA_VIDEO_BITRATE = "VIDEO_BITRATE";
    public static final String EXTRA_VIDEOCODEC = "VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = "HWCODEC";
    public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "CAPTURETOTEXTURE";
    public static final String EXTRA_FLEXFEC_ENABLED = "FLEXFEC";
    public static final String EXTRA_AUDIO_BITRATE = "AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = "AUDIOCODEC";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED = "NOAUDIOPROCESSING";
    public static final String EXTRA_AECDUMP_ENABLED = "AECDUMP";
    public static final String EXTRA_OPENSLES_ENABLED = "OPENSLES";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = "DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = "DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = "DISABLE_BUILT_IN_NS";
    public static final String EXTRA_ENABLE_LEVEL_CONTROL = "ENABLE_LEVEL_CONTROL";
    public static final String EXTRA_DISABLE_WEBRTC_AGC_AND_HPF = "DISABLE_WEBRTC_GAIN_CONTROL";
    public static final String EXTRA_TRACING = "TRACING";
    public static final String EXTRA_CMDLINE = "CMDLINE";
    public static final String EXTRA_RUNTIME = "RUNTIME";
    public static final String EXTRA_VIDEO_FILE_AS_CAMERA = "VIDEO_FILE_AS_CAMERA";
    public static final String EXTRA_DATA_CHANNEL_ENABLED = "DATA_CHANNEL_ENABLED";
    public static final String EXTRA_ORDERED = "ORDERED";
    public static final String EXTRA_MAX_RETRANSMITS_MS = "MAX_RETRANSMITS_MS";
    public static final String EXTRA_MAX_RETRANSMITS = "MAX_RETRANSMITS";
    public static final String EXTRA_PROTOCOL = "PROTOCOL";
    public static final String EXTRA_NEGOTIATED = "NEGOTIATED";
    public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED = "VIDEO_CAPTUREQUALITYSLIDER";
    public static final String EXTRA_DISPLAY_HUD = "DISPLAY_HUD";
    public static final String RECEIVE_IDX = "RECEIVE_IDX";
    public static final String EXTRA_ID = "ID";

    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;
    private static final int STAT_CALLBACK_PERIOD = 1000;

    private static class ProxyRenderer implements VideoRenderer.Callbacks {
        private VideoRenderer.Callbacks target;

        @Override
        synchronized public void renderFrame(VideoRenderer.I420Frame frame) {
            if (target == null) {
                VideoRenderer.renderFrameDone(frame);
                return;
            }
            target.renderFrame(frame);
        }

        synchronized public void setTarget(VideoRenderer.Callbacks target) {
            this.target = target;
        }
    }

    private static class ProxyVideoSink implements VideoSink {
        private VideoSink target;

        @Override
        synchronized public void onFrame(VideoFrame frame) {
            if (target == null) {
                return;
            }
            target.onFrame(frame);
        }

        synchronized public void setTarget(VideoSink target) {
            this.target = target;
        }
    }

    private final ProxyRenderer remoteProxyRenderer = new ProxyRenderer();
    private final ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
    private PeerConnectionClient peerConnectionClient = null;
    private AppRTCClient appRtcClient;
    private AppRTCClient.SignalingParameters signalingParameters;
    private AppRTCAudioManager audioManager = null;
    private SurfaceViewRenderer pipRenderer;
    private SurfaceViewRenderer fullscreenRenderer;
    private SurfaceViewRenderer toggleRenderer;

    private VideoFileRenderer videoFileRenderer;
    private final List<VideoRenderer.Callbacks> remoteRenderers = new ArrayList<>();
    private boolean commandLineRun;
    private AppRTCClient.RoomConnectionParameters roomConnectionParameters;
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    private boolean iceConnected;
    private long callStartedTimeMs = 0;
    private boolean micEnabled = true;
    private boolean isSwappedFeeds;

    private int peso = 0;
    boolean isMoney = false;

    public boolean callingState = false;
    public boolean disconnectBtnState = false;

    TextView profileNickname, profileRegion;
    ImageView profileImage;
    LinearLayout disconnectButton01, disconnectButton02, convertCameraButton;

    MediaPlayer callSound;
    boolean soundStatus = false;
    boolean isToggleMirror = false;
    private Timer timer;

    public static Activity act;
    private Activity real_act;
    private long calledStartedTime = 0;
    private int time = 0;
    String roomId;
    private static final int POINT_GIFT = 4;

    boolean receiveState = true;

    private int last_peso = 0;


    private final String BROADCAST_MESSAGE = "android.intent.action.PHONE_STATE";
    private BroadcastReceiver mReceiver = null;

    public static int possible_point = 0;

    boolean isSelected = true;
    boolean isFunctionCalled = false;

    Intent intent;

    // 유저정보
    private String u_idx, u_nick, u_region, u_profile_img, u_gender;
    String startTimeStr, endTimeStr;
    LinearLayout ll_wait_area, ll_gift;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
        setContentView(R.layout.activity_video_call);
        act = this;
        real_act = this;

        SSLConnect ssl = new SSLConnect();
        ssl.postHttps("https://appr.tc", 1000, 1000);

        iceConnected = false;
        signalingParameters = null;

        // view 초기화
        pipRenderer = findViewById(R.id.pip_video_view);
        fullscreenRenderer = findViewById(R.id.fullscreen_video_view);

        profileRegion = (TextView) findViewById(R.id.tv_region);
        profileNickname = (TextView) findViewById(R.id.tv_nick);
        profileImage = (ImageView) findViewById(R.id.iv_profile);

        disconnectButton01 = (LinearLayout) findViewById(R.id.ll_disconnect01);
        disconnectButton02 = (LinearLayout) findViewById(R.id.ll_disconnect02);
        convertCameraButton = (LinearLayout) findViewById(R.id.ll_convert_camera);

        ll_wait_area = (LinearLayout) findViewById(R.id.ll_wait_area);
        ll_gift = (LinearLayout) findViewById(R.id.ll_gift);

        intent = getIntent();

        // 내 페소 확인
        if (AppPreference.getProfilePref(real_act, AppPreference.PREF_GENDER).equalsIgnoreCase("male"))
            getMyInfo();

        /* 상대 데이터 세팅 */
        u_idx = getIntent().getStringExtra("u_idx");
        u_nick = getIntent().getStringExtra("u_nick");
        u_region = getIntent().getStringExtra("u_region");
        u_profile_img = getIntent().getStringExtra("u_profile_img");


        u_gender = getIntent().getStringExtra("u_gender");

        // 닉네임
        profileNickname.setText(u_nick);
        // 프로필 이미지
        if (StringUtil.isNull(u_profile_img)) {
            Glide.with(real_act)
                    .load(R.drawable.img_chatlist_noimg)
                    .transform(new CircleCrop())
                    .into(profileImage);
        } else {
            Glide.with(real_act)
                    .load(u_profile_img)
                    .transform(new CircleCrop())
                    .into(profileImage);
        }
        // 지역
        profileRegion.setText(u_region);


        // 카메라 전환 버튼
        convertCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraSwitch();
            }
        });

        // 선물
        ll_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(real_act, CallGiftDlg.class).putExtra("yidx", u_idx).putExtra("nick", u_nick));
            }
        });

        // 소리 재생
        callSound = MediaPlayer.create(real_act, R.raw.call_sound);
        callSound.setLooping(true);
        callSound.start();
        soundStatus = true;

        //통화끊기
        disconnectButton01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected) {
                    isSelected = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onCallHangUp();
                        }
                    }, 500);
                }
            }
        });

        disconnectButton02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected) {
                    isSelected = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onCallHangUp();
                        }
                    }, 500);
                }
            }
        });


        //음소거
//        muteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean enabled = onToggleMic();
//                muteBtn.setAlpha(enabled ? 1.0f : 0.3f);
//                tv_mute.setSelected(!enabled);
//            }
//        });


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCallControlFragmentVisibility();
            }
        };

        // Swap feeds on pip view click.
        pipRenderer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwappedFeeds(!isSwappedFeeds);
            }
        });

        fullscreenRenderer.setOnClickListener(listener);
        remoteRenderers.add(remoteProxyRenderer);


        // Create peer connection client.
        peerConnectionClient = new PeerConnectionClient();


        // Create video renderers.
        pipRenderer.init(peerConnectionClient.getRenderContext(), null);
        pipRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);

        fullscreenRenderer.init(peerConnectionClient.getRenderContext(), null);
        fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);

        pipRenderer.setZOrderMediaOverlay(true);
        pipRenderer.setEnableHardwareScaler(true /* enabled */);
        fullscreenRenderer.setEnableHardwareScaler(true /* enabled */);
        // Start with local feed in fullscreen and swap it to the pip when the call is connected.
        setSwappedFeeds(true /* isSwappedFeeds */);


        Uri roomUri = intent.getData();
        roomId = intent.getStringExtra(EXTRA_ROOMID);


        boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
        boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false);

        int videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0);
        int videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0);

        PeerConnectionClient.DataChannelParameters dataChannelParameters = null;
        if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, false)) {
            dataChannelParameters = new PeerConnectionClient.DataChannelParameters(intent.getBooleanExtra(EXTRA_ORDERED, true),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(EXTRA_PROTOCOL),
                    intent.getBooleanExtra(EXTRA_NEGOTIATED, false), intent.getIntExtra(EXTRA_ID, -1));
        }
        peerConnectionParameters =
                new PeerConnectionClient.PeerConnectionParameters(intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), loopback,
                        tracing, videoWidth, videoHeight, intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
                        intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0), intent.getStringExtra(EXTRA_VIDEOCODEC),
                        intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true),
                        intent.getBooleanExtra(EXTRA_FLEXFEC_ENABLED, false),
                        intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0), intent.getStringExtra(EXTRA_AUDIOCODEC),
                        intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false),
                        intent.getBooleanExtra(EXTRA_ENABLE_LEVEL_CONTROL, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false), dataChannelParameters);
        commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);
        int runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0);

        // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the
        // standard WebSocketRTCClient.
        if (loopback || !DirectRTCClient.IP_PATTERN.matcher(roomId).matches()) {
            appRtcClient = new WebSocketRTCClient(this);
        } else {
            Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.");
            appRtcClient = new DirectRTCClient(this);
        }
        // Create connection parameters.
        String urlParameters = intent.getStringExtra(EXTRA_URLPARAMETERS);
        roomConnectionParameters =
                new AppRTCClient.RoomConnectionParameters(roomUri.toString(), roomId, loopback, urlParameters);

        // For command line execution run connection for <runTimeMs> and exit.
        if (commandLineRun && runTimeMs > 0) {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    disconnect();
                }
            }, runTimeMs);
        }

        if (loopback) {
            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
            options.networkIgnoreMask = 0;
            peerConnectionClient.setPeerConnectionFactoryOptions(options);
        }

        peerConnectionClient.createPeerConnectionFactory(
                this, peerConnectionParameters, VideoCallAct.this);

        startCall();
        callOtherUser();
        //TODO
//        callOtherUser(u_idx);

//        timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        callingStateText.setText("응답 대기중");
//                    }
//                }, 0);
//
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        callingStateText.setText("응답 대기중.");
//                    }
//                }, 500);
//
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        callingStateText.setText("응답 대기중..");
//                    }
//                }, 1000);
//
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        callingStateText.setText("응답 대기중...");
//                    }
//                }, 1500);
//            }
//        };
//
//        timer.schedule(timerTask, 0, 2000);
    }

    private void getMyInfo() {
        ReqBasic server = new ReqBasic(real_act, NetUrls.INFO_ME) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONObject job = jo.getJSONObject("value");
                            peso = Integer.parseInt(StringUtil.getStr(job, "peso"));
                            if (peso == 0) {
                                onCallHangUp();
                                Common.showToast(real_act, "Your PHP is not enough");
                            }
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(real_act);
                    }
                } else {
                    Common.showToastNetwork(real_act);
                }
            }
        };

        server.setTag("My Info");
        server.addParams("u_idx", AppPreference.getProfilePref(real_act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    private void callOtherUser() {
        ReqBasic server = new ReqBasic(real_act, NetUrls.CALL) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {

                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(real_act);
                    }
                } else {
                    Common.showToastNetwork(real_act);
                }
            }
        };

        server.setTag("Call Other User");
        server.addParams("u_idx", AppPreference.getProfilePref(real_act, AppPreference.PREF_MIDX));
        server.addParams("t_idx", u_idx);
        server.addParams("room_id", roomId);
        server.execute(true, false);
    }


    private void registerReceiver() {
        if (mReceiver != null) return;

        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(BROADCAST_MESSAGE);

        this.mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

                switch (tm.getCallState()) {
                    //통화중
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        onCallHangUp();
                        break;
                }
            }
        };
        registerReceiver(this.mReceiver, theFilter);

    }

    private void unregisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }


    private void doDisconnect() {
        int count = 0;
        if(AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase(Common.GENDER_M))
            ++count;
        if(u_gender.equalsIgnoreCase(Common.GENDER_M))
            ++count;

        last_peso *= count;

        callingState = false;
        endTimeStr = StringUtil.convertCallTime(System.currentTimeMillis(), "yyyy-MM-dd hh:mm:ss");
        ReqBasic server = new ReqBasic(real_act, NetUrls.CALL_DISCONNECT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            isFunctionCalled = true;
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(real_act);
                    }
                } else {
                    Common.showToastNetwork(real_act);
                }
            }
        };

        server.setTag("Call Disconnect");
        server.addParams("u_idx", AppPreference.getProfilePref(real_act, AppPreference.PREF_MIDX));
        server.addParams("t_idx", u_idx);
        server.addParams("vcl_sdate", startTimeStr);
        server.addParams("vcl_edate", endTimeStr);
        server.addParams("room_id", roomId);
        server.addParams("peso", String.valueOf(last_peso));
        server.execute(true, false);
    }

    private void doDisconnectPass() {
        callingState = false;
        endTimeStr = StringUtil.convertCallTime(System.currentTimeMillis(), "yyyy-MM-dd hh:mm:ss");
        ReqBasic server = new ReqBasic(real_act, NetUrls.CALL_DISCONNECT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            isFunctionCalled = true;
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(real_act);
                    }
                } else {
                    Common.showToastNetwork(real_act);
                }
            }
        };

        server.setTag("Call Disconnect Pass");
        server.addParams("u_idx", AppPreference.getProfilePref(real_act, AppPreference.PREF_MIDX));
        server.addParams("t_idx", u_idx);
        server.addParams("vcl_sdate", "");
        server.addParams("vcl_edate", "");
        server.addParams("room_id", roomId);
        server.execute(true, false);
    }

    @Override
    public void onBackPressed() {
        if (isSelected) {
            isSelected = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onCallHangUp();
                }
            }, 500);
        }
    }


    public void onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
            toggleRenderer.setMirror(isToggleMirror);
            isToggleMirror = !isToggleMirror;
        }
    }

    private void result_cancel() {
        Log.i(TAG, "result_cancel: ");
        setResult(RESULT_CANCELED);

//        if (calledStartedTime != 0) {
//            Intent resultIntent = new Intent(VideoCallAct.this, ConnectActivity.class);
//            resultIntent.putExtra("result_time", callTime.getText().toString());
//
//            setResult(RESULT_CANCELED, resultIntent);
//        } else {
//            setResult(RESULT_CANCELED);
//        }
    }

    private void result_ok() {
        Log.i(TAG, "result_ok: ");
        if (calledStartedTime != 0) {
            long result_time_long = (System.currentTimeMillis() - calledStartedTime) / 1000;
            double result_multiply = (double) result_time_long / 60;
            String result_point = String.valueOf((int) (result_multiply * 20));

            Intent resultIntent = new Intent();
            if (isMoney) {
                resultIntent.putExtra("result_point", String.valueOf(peso));
            } else {
                resultIntent.putExtra("result_point", result_point);
            }
            setResult(RESULT_OK, resultIntent);

            calledStartedTime = 0;
        }
    }


    private void startCall() {
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }
        callStartedTimeMs = System.currentTimeMillis();

        // Start room connection.
        appRtcClient.connectToRoom(roomConnectionParameters);

        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        audioManager = AppRTCAudioManager.create(getApplicationContext());
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d(TAG, "Starting the audio manager...");
        audioManager.start(new AppRTCAudioManager.AudioManagerEvents() {
            // This method will be called each time the number of available audio
            // devices has changed.
            @Override
            public void onAudioDeviceChanged(
                    AppRTCAudioManager.AudioDevice audioDevice, Set<AppRTCAudioManager.AudioDevice> availableAudioDevices) {
                onAudioManagerDevicesChanged(audioDevice, availableAudioDevices);
            }
        });
    }


    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }

//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
//            return;
//        startCall();
//    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && getIntent().getBooleanExtra(EXTRA_CAMERA2, true);
    }

    private boolean captureToTexture() {
        return getIntent().getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false);
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("TEST_HOME", "onStop");
        if (peerConnectionClient != null) {
            peerConnectionClient.stopVideoSource();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (peerConnectionClient != null) {
            peerConnectionClient.startVideoSource();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("TEST_HOME", "onDestroy");
        Thread.setDefaultUncaughtExceptionHandler(null);
        disconnect();

        if (soundStatus) {
            callSound.stop();
            callSound.release();
            soundStatus = false;
        }

        Common.showToast(real_act, "The call has been terminated.");

        super.onDestroy();
        unregisterReceiver();
    }

    public void onCallHangUp() {
        disconnectBtnState = true;
        disconnect();
    }

    public boolean onToggleMic() {
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled;
            peerConnectionClient.setAudioEnabled(micEnabled);
        }
        return micEnabled;
    }

    private void toggleCallControlFragmentVisibility() {
        if (!iceConnected) {
            return;
        }
    }

    // Should be called from UI thread
    private void callConnected() {
        calledStartedTime = System.currentTimeMillis();
        callingState = true;
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        startTimeStr = StringUtil.convertCallTime(System.currentTimeMillis(), "yyyy-MM-dd hh:mm:ss");
        Log.i(TAG, "Call connected: delay=" + delta + "ms");
        if (peerConnectionClient == null) {
            Log.w(TAG, "Call is connected in closed or error state");
            return;
        }
        // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
        setSwappedFeeds(false /* isSwappedFeeds */);

        // 소리끄기
        if (soundStatus) {
            callSound.stop();
            callSound.release();
            soundStatus = false;
        }

        // 응답대기중 글자 삭제
//        if (timer != null) {
//            timer.cancel();
//        }
//        callingStateText.setVisibility(View.GONE);

        //시간 세팅
        calledStartedTime = System.currentTimeMillis();
//        callTime.setVisibility(View.VISIBLE);

        // 레이아웃 전환
        ll_wait_area.setVisibility(View.GONE);
//        muteBtn.setVisibility(View.VISIBLE);
    }

    // This method is called when the audio manager reports audio device change,
    // e.g. from wired headset to speakerphone.
    private void onAudioManagerDevicesChanged(
            final AppRTCAudioManager.AudioDevice device, final Set<AppRTCAudioManager.AudioDevice> availableDevices) {
        Log.d(TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
                + "selected: " + device);
        // TODO(henrika): add callback handler.
    }

    public void disconnectFromService() {
        disconnect();
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private void disconnect() {
        if (!isFunctionCalled) {
            if (callingState) {
                doDisconnect();
            } else {
                if(disconnectBtnState) {
                    doDisconnectPass();
                }
            }
        }

        Log.i("TEST_HOME", "disconnect");
        if (soundStatus) {
            callSound.stop();
            callSound.release();
            soundStatus = false;
        }

        remoteProxyRenderer.setTarget(null);
        localProxyVideoSink.setTarget(null);
        if (appRtcClient != null) {
            appRtcClient.disconnectFromRoom();
            appRtcClient = null;
        }
        if (pipRenderer != null) {
            pipRenderer.release();
            pipRenderer = null;
        }
        if (videoFileRenderer != null) {
            videoFileRenderer.release();
            videoFileRenderer = null;
        }
        if (fullscreenRenderer != null) {
            fullscreenRenderer.release();
            fullscreenRenderer = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        if (iceConnected) {
            result_ok();
        } else {
            result_cancel();
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                act = null;
            }
        }, 1000);
    }

    private VideoCapturer createVideoCapturer() {
        final VideoCapturer videoCapturer;
        String videoFileAsCamera = getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);
        if (videoFileAsCamera != null) {
            videoCapturer = new FileVideoCapturer(videoFileAsCamera);
        } else if (useCamera2()) {
            if (!captureToTexture()) {
                disconnect();
                return null;
            }

            Logging.d(TAG, "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            Logging.d(TAG, "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            disconnect();
            return null;
        }
        return videoCapturer;
    }

    private void setSwappedFeeds(boolean isSwappedFeeds) {
        Logging.d(TAG, "setSwappedFeeds: " + isSwappedFeeds);
        this.isSwappedFeeds = isSwappedFeeds;
        localProxyVideoSink.setTarget(isSwappedFeeds ? fullscreenRenderer : pipRenderer);
        remoteProxyRenderer.setTarget(isSwappedFeeds ? pipRenderer : fullscreenRenderer);
        fullscreenRenderer.setMirror(isSwappedFeeds);
        pipRenderer.setMirror(!isSwappedFeeds);

        toggleRenderer = isSwappedFeeds ? fullscreenRenderer : pipRenderer;
//        fullscreenRenderer.setMirror(false);
//        pipRenderer.setMirror(false);
    }

    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and
    // are routed to UI thread.
    private void onConnectedToRoomInternal(final AppRTCClient.SignalingParameters params) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;

        signalingParameters = params;
        VideoCapturer videoCapturer = null;

        if (peerConnectionParameters.videoCallEnabled) {
            videoCapturer = createVideoCapturer();
        }

        peerConnectionClient.createPeerConnection(
                localProxyVideoSink, remoteRenderers, videoCapturer, signalingParameters);

        if (signalingParameters.initiator) {
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createOffer();
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(params.offerSdp);
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    @Override
    public void onConnectedToRoom(final AppRTCClient.SignalingParameters params) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onConnectedToRoomInternal(params);
            }
        });
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                    return;
                }
                peerConnectionClient.setRemoteDescription(sdp);
                if (!signalingParameters.initiator) {
                    peerConnectionClient.createAnswer();
                }
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.addRemoteIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.removeRemoteIceCandidates(candidates);
            }
        });
    }

    @Override
    public void onChannelClose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disconnect();
            }
        });
    }

    @Override
    public void onChannelError(final String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Common.showToast(real_act, "The connection is not smooth. Please try again in a few minutes.");
                disconnect();
            }
        });
    }

    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    if (signalingParameters.initiator) {
                        appRtcClient.sendOfferSdp(sdp);
                    } else {
                        appRtcClient.sendAnswerSdp(sdp);
                    }
                }
                if (peerConnectionParameters.videoMaxBitrate > 0) {
                    Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
                    peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
                }
            }
        });
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidate(candidate);
                }
            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidateRemovals(candidates);
                }
            }
        });
    }

    @Override
    public void onIceConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iceConnected = true;

                callConnected();
            }
        });
    }

    @Override
    public void onIceDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iceConnected = false;
                disconnect();
            }
        });
    }

    @Override
    public void onPeerConnectionClosed() {
    }

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (iceConnected) {
                    if (real_act != null) {
                        long originTime = System.currentTimeMillis() - calledStartedTime;
//                        callTime.setText(getDate(originTime, "mm:ss"));

                        if (AppPreference.getProfilePref(real_act, AppPreference.PREF_GENDER).equalsIgnoreCase("male")) {
                            long result_time_long = (System.currentTimeMillis() - calledStartedTime) / 1000;

                            double result_multiply = (double) result_time_long / 60;
                            int result_point = (int) (result_multiply * 20);
                            if (result_point > peso) {
                                isMoney = true;
                                onCallHangUp();
                            }

                            int real_time = (int) result_time_long % 60;
                            if (result_time_long != 0) {
                                if (real_time == 0) {
                                    last_peso += 40;
                                    deduct_peso();
                                }
                            }
                        } else {
                            long result_time_long = (System.currentTimeMillis() - calledStartedTime) / 1000;
                            int real_time = (int) result_time_long % 60;
                            if (result_time_long != 0) {
                                if (real_time == 0) {
                                    last_peso += 40;
                                }
                            }
                        }
                    }

                }
            }
        });
    }

    private void deduct_peso() {
        ReqBasic server = new ReqBasic(act, NetUrls.DEDUCT_PESO) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {

                        } else {
                            Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "msg"));
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

        server.setTag("Deduct Peso");
        server.addParams("u_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("peso", "40");
        server.execute(true, false);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        Log.i("TEST_RTC", "getDate");
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onPeerConnectionError(final String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disconnect();
            }
        });
    }
}