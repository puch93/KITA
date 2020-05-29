package kr.co.core.kita.data;

import lombok.Data;

@Data
public class TalkListData {
    private String t_idx;
    private String u_idx;
    private String nick;
    private String gender;
    private String contents;
    private String reg_date;
    private String profile_img;

    public TalkListData(String t_idx, String u_idx, String nick, String gender, String contents, String reg_date, String profile_img) {
        this.t_idx = t_idx;
        this.u_idx = u_idx;
        this.nick = nick;
        this.gender = gender;
        this.contents = contents;
        this.reg_date = reg_date;
        this.profile_img = profile_img;
    }
}
