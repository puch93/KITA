package kr.co.core.kita.data;

import lombok.Data;

@Data
public class HistoryGiftData {
    private String g_idx;
    private String u_idx;
    private String u_profile_img;
    private String u_nick;

    private String g_name;
    private String g_price;

    public HistoryGiftData(String g_idx, String u_idx, String u_profile_img, String u_nick, String g_name, String g_price) {
        this.g_idx = g_idx;
        this.u_idx = u_idx;
        this.u_profile_img = u_profile_img;
        this.u_nick = u_nick;
        this.g_name = g_name;
        this.g_price = g_price;
    }
}
