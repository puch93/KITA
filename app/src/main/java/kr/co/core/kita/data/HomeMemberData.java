package kr.co.core.kita.data;

import lombok.Data;

@Data
public class HomeMemberData {
    private String idx;
    private String nick;
    private String intro;
    private String profile_img;
    private boolean login;

    public HomeMemberData(String idx, String nick, String intro, String profile_img, boolean login) {
        this.idx = idx;
        this.nick = nick;
        this.intro = intro;
        this.profile_img = profile_img;
        this.login = login;
    }
}
