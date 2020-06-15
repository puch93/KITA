package kr.co.core.kita.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.ChatAct;
import kr.co.core.kita.activity.MainAct;
import kr.co.core.kita.activity.rtc.ConnectAct;
import kr.co.core.kita.activity.rtc.VideoCallAct;
import kr.co.core.kita.activity.rtc.VideoReceiveAct;
import kr.co.core.kita.dialog.GiftDlg;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private Context ctx;

    private static final String TYPE_CALL = "video_request";
    private static final String TYPE_DISCONNECT = "video_disconnect";
    private static final String TYPE_CHAT = "chatting";
    private static final String TYPE_GIFT = "gift_send";

    Timer timer;
    TimerTask adTask;

    private long currentTime;
    private long lastTime;
    private final long limitTime = 10000;

    @Override
    public void onNewToken(String token) {
        Log.e(StringUtil.TAG_PUSH, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * 푸시종류
     * 1. 심쿵
     * 2. 매칭
     * 3. 채팅
     * 4. 그외의 알림 (CGPMS 등)
     */

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        ctx = getApplicationContext();

        Log.e(StringUtil.TAG_PUSH, "remoteMessage.getData: " + remoteMessage.getData());
        JSONObject jo = new JSONObject(remoteMessage.getData());

        String type = StringUtil.getStr(jo, "type");
        String target_idx = "";
        switch (type) {
            case TYPE_CALL:
                if (!AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX).equalsIgnoreCase(StringUtil.getStr(jo, "msg_from"))) {
                    if (checkCallState()) {
                        cancelTimer();
                        process_call(jo);
                    } else {
                        //통화중
                        doDisconnectPass(StringUtil.getStr(jo, "msg_from"));
                    }
                }
                break;

            case TYPE_DISCONNECT:
                if (!AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX).equalsIgnoreCase(StringUtil.getStr(jo, "msg_from"))) {
                    timer = new Timer();
                    lastTime = System.currentTimeMillis();
                    checkDisconnect();
                }

                break;

            case TYPE_GIFT:
                // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
                target_idx = StringUtil.getStr(jo, "target_idx");
                if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
                    sendGiftPushPopup(jo);
                }
                break;
            case TYPE_CHAT:
                // 채팅액티비티 인지 or 해당회원 채팅방인지
                if (ChatAct.real_act == null || !ChatAct.room_idx.equalsIgnoreCase(StringUtil.getStr(jo, "room_idx"))) {

                    target_idx = StringUtil.getStr(jo, "target_idx");
                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
                        if (MainAct.act != null) {
                            ((MainAct) MainAct.act).refreshChatFrag();
                        }
                        sendChattingNotification(jo);
                    }
                }
                break;
        }
    }

    private void sendGiftPushPopup(JSONObject jo) {

        String gender = StringUtil.getStr(jo, "gender");
        String room_type = StringUtil.getStr(jo, "room_type");
        String host_idx = StringUtil.getStr(jo, "host_idx");
        String sender_location = StringUtil.getStr(jo, "sender_location");
        String room_idx = StringUtil.getStr(jo, "room_idx");
        String nick = StringUtil.getStr(jo, "nick");
        String type = StringUtil.getStr(jo, "type");
        String target_idx = StringUtil.getStr(jo, "target_idx");
        String message = StringUtil.getStr(jo, "message");
        String sender_img = StringUtil.getStr(jo, "sender_img");

        String[] datas = message.split("\\|");
        for (String data : datas) {
            Log.i(StringUtil.TAG, "sendGiftPushPopup: " + data);
        }


        Intent intent = new Intent(this, GiftDlg.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("nick", nick);
        intent.putExtra("gift_name", datas[0]);

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private boolean isImage(String msg) {
        String reg = "^([\\S]+(\\.(?i)(jpg|png|jpeg))$)";

        return msg.matches(reg);
    }


    private void sendChattingNotification(JSONObject jo) {
        String msg_from = StringUtil.getStr(jo, "msg_from");
        String gender = StringUtil.getStr(jo, "gender");
        String room_type = StringUtil.getStr(jo, "room_type");
        String host_idx = StringUtil.getStr(jo, "host_idx");
        String sender_location = StringUtil.getStr(jo, "sender_location");
        String room_idx = StringUtil.getStr(jo, "room_idx");
        String nick = StringUtil.getStr(jo, "nick");
        String type = StringUtil.getStr(jo, "type");
        String target_idx = StringUtil.getStr(jo, "target_idx");
        String message = Common.decodeEmoji(StringUtil.getStr(jo, "message"));
        String sender_img = StringUtil.getStr(jo, "sender_img");

        String title = nick + "'s Chat";

        if (isImage(message)) {
            message = "이미지";
        }

        //매니저 설정
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //채널설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "더 파이브", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("더 파이브 알림설정");

            notificationManager.createNotificationChannel(channel);
        }

        //인텐트 설정
        Intent intent = new Intent(ctx, ChatAct.class)
                .putExtra("room_idx", room_idx)
                .putExtra("yidx", msg_from)
                .putExtra("otherImage", sender_img);


        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //노티 설정
        Notification notification = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .build();

        //푸시 날리기
        notificationManager.notify(0, notification);
    }


    private void doDisconnectPass(String u_idx) {

        ReqBasic server = new ReqBasic(ctx, NetUrls.CALL_DISCONNECT) {
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
                    }
                } else {
                }
            }
        };

        server.setTag("Call Disconnect Pass");
        server.addParams("u_idx", AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX));
        server.addParams("t_idx", u_idx);
        server.addParams("vcl_sdate", "");
        server.addParams("vcl_edate", "");
        server.addParams("room_id", "");
        server.execute(true, false);
    }

    private void process_call(JSONObject jo) {
        try {
            String msg_from = StringUtil.getStr(jo, "msg_from");
            String room_id = StringUtil.getStr(new JSONObject(jo.getString("room_idx")), "vc_refidx");
            String nick = StringUtil.getStr(jo, "nick");
            String profile_img = StringUtil.getStr(jo, "sender_img");
            String location = StringUtil.getStr(jo, "sender_location");
            String gender = StringUtil.getStr(jo, "gender");

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), ConnectAct.class);
                    intent.putExtra("type", "receive");
                    intent.putExtra("roomId", room_id);
                    intent.putExtra("u_idx", msg_from);
                    intent.putExtra("u_nick", nick);
                    intent.putExtra("u_region", location);
                    intent.putExtra("u_profile_img", profile_img);
                    intent.putExtra("u_gender", gender);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    try {
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            }, 4000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //통화종료 타이머실행
    public void checkDisconnect() {
        adTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (!checkCallState()) {
                            checkDisconnectState();
                        }

                        currentTime = System.currentTimeMillis();
                        if ((currentTime - lastTime) > limitTime) {
                            cancelTimer();
                        }

                        Log.i("TEST_TIME", "currentTime: " + currentTime);
                        Log.i("TEST_TIME", "currentTime - lastTime: " + (currentTime - lastTime));
                    }
                });
            }
        };
        timer.schedule(adTask, 0, 500);
    }

    private void checkDisconnectState() {
        VideoReceiveAct act_video_receive = (VideoReceiveAct) VideoReceiveAct.act;
        VideoCallAct act_video = (VideoCallAct) VideoCallAct.act;
        if (act_video_receive != null) {
            cancelTimer();
            act_video_receive.disconnectFromService();

            Log.i("TEST_TIME", "act_video_receive");
        }

        if (act_video != null) {
            cancelTimer();
            act_video.disconnectFromService();

            Log.i("TEST_TIME", "act_video");
        }
    }

    private void cancelTimer() {
        Log.i("TEST_TIME", "cancelTimer");
        if (adTask != null) {
            adTask.cancel();
            adTask = null;
        }

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private boolean checkCallState() {
        VideoReceiveAct act_video_receive = (VideoReceiveAct) VideoReceiveAct.act;
        VideoCallAct act_video = (VideoCallAct) VideoCallAct.act;


        if (act_video != null || act_video_receive != null) {
            return false;
        } else {
            return true;
        }
    }
}
