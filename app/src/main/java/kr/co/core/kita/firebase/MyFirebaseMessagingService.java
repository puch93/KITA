package kr.co.core.kita.firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.MainAct;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.StringUtil;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private Context ctx;

    private static final String TYPE_CHAT = "chatting";

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

//        switch (type) {
//            case TYPE_CHAT:
//                // 알람설정 켰는지 확인
//                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_CHATTING)) {
//                    // 채팅액티비티 인지 or 해당회원 채팅방인지
//                    if (ChattingAct.real_act == null || !ChattingAct.room_idx.equalsIgnoreCase(StringUtil.getStr(jo, "room_idx"))) {
//
//                        String target_idx = StringUtil.getStr(jo, "target_idx");
//                        // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
//                        if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
//                            // 메인액티비티 살아있을때 채팅 프래그먼트 리프래쉬
//                            if (MainAct.act != null) {
//                                ((MainAct) MainAct.act).refreshAboutChatting();
//                            }
//
//                            if (StringUtil.getStr(jo, "room_type").equalsIgnoreCase("heartmessage")) {
//                                sendChattingNotification(jo, true);
//                            } else {
//                                checkChattingPay(jo, StringUtil.getStr(jo, "room_idx"));
//                            }
//                        }
//                    }
//                }
//                break;
//
//            case TYPE_LIKE:
//                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_LIKE)) {
//
//                    String target_idx = StringUtil.getStr(jo, "target_idx");
//                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
//                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
//                        sendSystemNotification(jo, "심쿵", 2);
//                    }
//                }
//                break;
//
//            case TYPE_MATCHING:
//                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_MATCHING)) {
//                    String target_idx = StringUtil.getStr(jo, "target_idx");
//                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
//                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
//                        sendSystemNotification(jo, "매칭", 3);
//                    }
//                }
//                break;
//
//
//            case TYPE_LIKE_MESSAGE:
//                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_LIKE_MESSAGE)) {
//
//                    String target_idx = StringUtil.getStr(jo, "target_idx");
//                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
//                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
//                        sendMessageNotification(jo, 7);
//                    }
//                }
//                break;
//
//            case TYPE_LIKE_MESSAGE_CHAT:
//                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_LIKE_MESSAGE)) {
//
//                    String target_idx = StringUtil.getStr(jo, "target_idx");
//                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
//                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
//                        sendChattingRoomNotification(jo, 5);
//                    }
//                }
//                break;
//
//            case TYPE_CGPMS:
//                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_OTHER)) {
//                    sendCgpmsNotification(jo, 4);
//                }
//                break;
//
//            case TYPE_GIFT:
//                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_OTHER)) {
//                    String target_idx = StringUtil.getStr(jo, "target_idx");
//                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
//                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
//                        sendSystemNotification(jo, "기프티콘", 6);
//                    }
//                }
//                break;
//        }
    }





    private void sendCgpmsNotification(JSONObject jo, int id) {
        String status = StringUtil.getStr(jo, "status");
        String idx = StringUtil.getStr(jo, "idx");
        String msg = StringUtil.getStr(jo, "msg");
        String url = StringUtil.getStr(jo, "url");
        String send = StringUtil.getStr(jo, "send");
        String type = StringUtil.getStr(jo, "type");
        String cgpms = StringUtil.getStr(jo, "cgpms");
        String edate = StringUtil.getStr(jo, "edate");
        String sdate = StringUtil.getStr(jo, "sdate");
        String title = StringUtil.getStr(jo, "title");
        String regdate = StringUtil.getStr(jo, "regdate");
        String senddate = StringUtil.getStr(jo, "senddate");


        //매니저 설정
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //채널설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "더 파이브", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("더 파이브 알림설정");

            notificationManager.createNotificationChannel(channel);
        }

        //인텐트 설정
        Intent intent = null;
        if (StringUtil.isNull(url)) {
//            intent = new Intent(ctx, PushAct.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //노티 설정
        Notification notification = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .build();

        //푸시 날리기
        notificationManager.notify(id, notification);
    }

}
