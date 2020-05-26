package kr.co.core.kita.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import kr.co.core.kita.R;


public class AllOfDecoration extends RecyclerView.ItemDecoration {
    public static final String HOME_LIST = "home";
    public static final String PROFILE_DETAIL = "profile_detail";
    public static final String TALK_LIST = "talk_list";
    public static final String HISTORY_GIFT_LIST = "history_gift_list";
    public static final String HISTORY_CALL_LIST = "history_call_list";
    public static final String TALK_IMAGE_LIST = "talk_image_list";
    private Activity act;
    private String type;

    public AllOfDecoration(Activity act, String type) {
        this.act = act;
        this.type = type;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        switch (type) {
            case HOME_LIST:
            case HISTORY_CALL_LIST:
                if (position < 2)
                    outRect.top = act.getResources().getDimensionPixelSize(R.dimen.deco_20);
                break;

            case PROFILE_DETAIL:
                if (position < 3)
                    outRect.top = act.getResources().getDimensionPixelSize(R.dimen.deco_20);
                break;

            case HISTORY_GIFT_LIST:
            case TALK_LIST:
                if (position == 0)
                    outRect.top = act.getResources().getDimensionPixelSize(R.dimen.deco_16);
                break;

            case TALK_IMAGE_LIST:
                if(position == 0) {
                    outRect.left = act.getResources().getDimensionPixelSize(R.dimen.deco_20);
                }
                break;

        }
    }
}