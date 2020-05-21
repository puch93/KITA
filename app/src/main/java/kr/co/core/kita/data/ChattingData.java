package kr.co.core.kita.data;

import lombok.Data;

@Data
public class ChattingData {
    private String user_idx;

    private String data_type;
    private String send_time;
    private String date_line;
    private String contents;

    private boolean read;

    public ChattingData(String user_idx, String data_type, String send_time, String date_line, String contents, boolean read) {
        this.user_idx = user_idx;
        this.data_type = data_type;
        this.send_time = send_time;
        this.date_line = date_line;
        this.contents = contents;
        this.read = read;
    }
}