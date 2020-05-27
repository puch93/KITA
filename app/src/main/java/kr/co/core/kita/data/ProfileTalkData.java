package kr.co.core.kita.data;

import lombok.Data;

@Data
public class ProfileTalkData {
    private String u_idx;
    private String t_idx;
    private String image;

    public ProfileTalkData(String u_idx, String t_idx, String image) {
        this.u_idx = u_idx;
        this.t_idx = t_idx;
        this.image = image;
    }
}
