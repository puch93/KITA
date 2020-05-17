package kr.co.core.kita.data;

import lombok.Data;

@Data
public class CommentData {
    private String idx;
    private String nick;
    private String contents;
    private String reg_date;
    private String profile_img;

    public CommentData(String idx, String nick, String contents, String reg_date, String profile_img) {
        this.idx = idx;
        this.nick = nick;
        this.contents = contents;
        this.reg_date = reg_date;
        this.profile_img = profile_img;
    }
}
