package kr.co.core.kita.data;

import lombok.Data;

@Data
public class HistoryCallData {
    private String c_idx;
    private String u_idx;
    private String u_nick;
    private String u_region;
    private boolean login;

    private String c_time;

    public HistoryCallData(String c_idx, String u_idx, String u_nick, String u_region, boolean login, String c_time) {
        this.c_idx = c_idx;
        this.u_idx = u_idx;
        this.u_nick = u_nick;
        this.u_region = u_region;
        this.login = login;
        this.c_time = c_time;
    }
}
