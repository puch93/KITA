package kr.co.core.kita.data;

import lombok.Data;

@Data
public class ChattingLIstData {
    private String idx;

    private String u_idx;
    private String u_nick;
    private String u_profile_img;

    private String contents;
    private String reg_date;
    private String read_count;

    public ChattingLIstData(String idx, String u_idx, String u_nick, String u_profile_img, String contents, String reg_date, String read_count) {
        this.idx = idx;
        this.u_idx = u_idx;
        this.u_nick = u_nick;
        this.u_profile_img = u_profile_img;
        this.contents = contents;
        this.reg_date = reg_date;
        this.read_count = read_count;
    }
}