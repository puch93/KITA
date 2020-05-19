package kr.co.core.kita.data;

import lombok.Data;

@Data
public class SelectData {
    private String data;
    private boolean select;

    public SelectData(String data, boolean select) {
        this.data = data;
        this.select = select;
    }
}
