package kr.co.core.kita.activity.rtc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class ConnectAct extends Activity {
    private static boolean commandLineRun = false;
    private static final int CONNECTION_REQUEST = 1001;
    private static final int PERMISSIONS_REQUEST = 0;
    Activity act;

    String roomUrl, videoCodec, audioCodec, protocol;
    boolean videoCallEnabled, useCamera2, hwCodec, captureToTexture, flexfecEnabled, noAudioProcessing, aecDump,
            useOpenSLES, disableBuiltInAEC, disableBuiltInAGC, disableBuiltInNS, enableLevelControl, disableWebRtcAGCAndHPF, captureQualitySlider,
            tracing, dataChannelEnabled, ordered, negotiated, useScreencapture, displayHud, useValuesFromIntent;

//    String[] permissions = new String[]{
//            android.Manifest.permission.RECORD_AUDIO,
//            android.Manifest.permission.CAMERA,
//            android.Manifest.permission.READ_PHONE_STATE
//    };

    int maxRetrMs, maxRetr, id, audioStartBitrate;

    String roomId, type;

    private ProgressDialog pd;

    private int[] RESULT_ARRAY = {RTC_VOICE_CALL, RTC_VOICE_RECEIVE, RTC_VIDEO_CALL, RTC_VIDEO_RECEIVE};

    private static final int RTC_VOICE_CALL = 0;
    private static final int RTC_VOICE_RECEIVE = 1;
    private static final int RTC_VIDEO_CALL = 2;
    private static final int RTC_VIDEO_RECEIVE = 3;

    private String u_idx, u_nick, u_region, u_profile_img, u_gender;
    private long calledStartedTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        type = getIntent().getStringExtra("type");

        u_idx = getIntent().getStringExtra("u_idx");
        u_nick = getIntent().getStringExtra("u_nick");
        u_region = getIntent().getStringExtra("u_region");
        u_profile_img = getIntent().getStringExtra("u_profile_img");
        u_gender = getIntent().getStringExtra("u_gender");

        //Permissions Granted
        coreSetup();

        connectToRoom(type);
    }


    private static String getRandomString() {
        StringBuilder buffer = new StringBuilder();
        Random random = new Random();

        String[] nums = "1,2,3,4,5,6,7,8".split(",");

        for (int i = 0; i < 8; i++) {
            buffer.append(nums[random.nextInt(nums.length)]);
        }

        Log.i(StringUtil.TAG, "roomId: " + buffer.toString());
        return buffer.toString();
    }


    //RTC value setting
    public void coreSetup() {
        //Connect server
        roomUrl = "https://appr.tc";

        //Video call enabled
        videoCallEnabled = false;
        useScreencapture = false;

        //Camera2 use
        useCamera2 = false;

        //video codec , default VP8
        videoCodec = "VP8";

        //audio codec , default OPUS
        audioCodec = "OPUS";

        //HW codec enable
        hwCodec = true;

        //Capture texture enable
        captureToTexture = false;

        //Flexfec enable
        flexfecEnabled = false;

        //Disable Audio Processing
        noAudioProcessing = false;

        //Disable Audio Processing
        aecDump = false;

        //OpenSL ES enable
        useOpenSLES = false;

        //Disable built-in Acoustic Echo Canceler
        disableBuiltInAEC = false;

        //Disable built-in Automatic Gain Control
        disableBuiltInAGC = false;

        //Disable built-in Network Simulator
        disableBuiltInNS = false;

        //Level Control enable
        enableLevelControl = false;

        //Disable gain control
        disableWebRtcAGCAndHPF = false;


        audioStartBitrate = 32;

        // Check statistics display option.
        displayHud = false;

        tracing = false;

        //Data Channel enable
        dataChannelEnabled = true;

        //Data Channel option
        ordered = true;
        negotiated = false;

        maxRetrMs = -1;
        maxRetr = -1;
        id = -1;
        protocol = "";

        useValuesFromIntent = false;
    }

    //Connect RTC
    public void connectToRoom(String type) {
        ConnectAct.commandLineRun = false;

        Uri uri = Uri.parse(roomUrl);

        int state = 0;

        Intent intent = null;
        if (type.equals("call")) {
            state = RTC_VIDEO_CALL;
            roomId = getRandomString();

            intent = new Intent(this, VideoCallAct.class);
            intent.putExtra("u_idx", u_idx);
            intent.putExtra("u_nick", u_nick);
            intent.putExtra("u_region", u_region);
            intent.putExtra("u_profile_img", u_profile_img);
            intent.putExtra("u_gender", u_gender);
        } else {
            state = RTC_VIDEO_RECEIVE;
            roomId = getIntent().getStringExtra("roomId");

            intent = new Intent(this, VideoReceiveAct.class);
            intent.putExtra("u_idx", u_idx);
            intent.putExtra("u_nick", u_nick);
            intent.putExtra("u_region", u_region);
            intent.putExtra("u_profile_img", u_profile_img);
        }

        Log.i(StringUtil.TAG, "roomId: " + roomId);

        intent.setData(uri);
        intent.putExtra(VideoCallAct.EXTRA_ROOMID, roomId);
        intent.putExtra(VideoCallAct.EXTRA_LOOPBACK, false);
        intent.putExtra(VideoCallAct.EXTRA_VIDEO_CALL, true);
        intent.putExtra(VideoCallAct.EXTRA_CAMERA2, useCamera2);
        intent.putExtra(VideoCallAct.EXTRA_VIDEO_WIDTH, 0);
        intent.putExtra(VideoCallAct.EXTRA_VIDEO_HEIGHT, 0);
        intent.putExtra(VideoCallAct.EXTRA_VIDEO_FPS, 0);
        intent.putExtra(VideoCallAct.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
        intent.putExtra(VideoCallAct.EXTRA_VIDEO_BITRATE, 0);
        intent.putExtra(VideoCallAct.EXTRA_VIDEOCODEC, videoCodec);
        intent.putExtra(VideoCallAct.EXTRA_HWCODEC_ENABLED, hwCodec);
        intent.putExtra(VideoCallAct.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
        intent.putExtra(VideoCallAct.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
        intent.putExtra(VideoCallAct.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
        intent.putExtra(VideoCallAct.EXTRA_AECDUMP_ENABLED, aecDump);
        intent.putExtra(VideoCallAct.EXTRA_OPENSLES_ENABLED, useOpenSLES);
        intent.putExtra(VideoCallAct.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
        intent.putExtra(VideoCallAct.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
        intent.putExtra(VideoCallAct.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
        intent.putExtra(VideoCallAct.EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl);
        intent.putExtra(VideoCallAct.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF);
        intent.putExtra(VideoCallAct.EXTRA_AUDIO_BITRATE, 0);
        intent.putExtra(VideoCallAct.EXTRA_AUDIOCODEC, audioCodec);
        intent.putExtra(VideoCallAct.EXTRA_DISPLAY_HUD, displayHud);
        intent.putExtra(VideoCallAct.EXTRA_TRACING, tracing);
        intent.putExtra(VideoCallAct.EXTRA_CMDLINE, commandLineRun);
        intent.putExtra(VideoCallAct.EXTRA_RUNTIME, 0);
        intent.putExtra(VideoCallAct.RECEIVE_IDX, "10");

        intent.putExtra(VideoCallAct.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);

        if (dataChannelEnabled) {
            intent.putExtra(VideoCallAct.EXTRA_ORDERED, ordered);
            intent.putExtra(VideoCallAct.EXTRA_MAX_RETRANSMITS_MS, -1);
            intent.putExtra(VideoCallAct.EXTRA_MAX_RETRANSMITS, -1);
            intent.putExtra(VideoCallAct.EXTRA_PROTOCOL, "");
            intent.putExtra(VideoCallAct.EXTRA_NEGOTIATED, negotiated);
            intent.putExtra(VideoCallAct.EXTRA_ID, -1);
        }

        if (useValuesFromIntent) {
            if (getIntent().hasExtra(VideoCallAct.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                String videoFileAsCamera =
                        getIntent().getStringExtra(VideoCallAct.EXTRA_VIDEO_FILE_AS_CAMERA);
                intent.putExtra(VideoCallAct.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
            }
        }

        startActivityForResult(intent, RESULT_ARRAY[state]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case RTC_VIDEO_CALL:
                case RTC_VIDEO_RECEIVE:
                    if (AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase("male")) {
                        Log.e(StringUtil.TAG, "success call in ConnectActivity");
//                        deduct_peso(data.getStringExtra("result_point"));
                    }
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case RTC_VIDEO_CALL:
                    Log.e(StringUtil.TAG, "cancel call in ConnectActivity");
                    break;

                case RTC_VIDEO_RECEIVE:
                    break;
            }
        }

        finish();
    }

    private void deduct_peso(String point) {
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
        server.addParams("peso", point);
        server.execute(true, false);
    }
}
