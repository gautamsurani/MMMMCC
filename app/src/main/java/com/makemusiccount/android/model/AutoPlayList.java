package com.makemusiccount.android.model;

public class AutoPlayList {
    private String sr, value, key_value, type;

    public String getSr() {
        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey_value() {
        return key_value;
    }

    public void setKey_value(String key_value) {
        this.key_value = key_value;
    }

    public String getType() {
        return type;
    }

    public AutoPlayList() {
    }

    public void setType(String type) {
        this.type = type;
    }

    public AutoPlayList(String sr, String value, String key_value, String type) {

        this.sr = sr;
        this.value = value;
        this.key_value = key_value;
        this.type = type;
    }
}
