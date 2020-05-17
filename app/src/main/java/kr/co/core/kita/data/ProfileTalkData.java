package kr.co.core.kita.data;

import lombok.Data;

@Data
public class ProfileTalkData {
    private String idx;
    private String image;

    public ProfileTalkData(String idx, String image) {
        this.idx = idx;
        this.image = image;
    }
}
