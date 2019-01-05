package com.makemusiccount.android.model;

/**
 * Created by Welcome on 26-01-2018.
 */

public class SongEquationList {
    String sr;
    String label;
    String value;
    String key_value;
    String type;
    String image;
    String change_char;

    public String getOctave() {
        return octave;
    }

    public void setOctave(String octave) {
        this.octave = octave;
    }

    String octave;

    public SongEquationList() {
    }

    public String getSr() {

        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChange_char() {
        return change_char;
    }

    public void setChange_char(String change_char) {
        this.change_char = change_char;
    }

    public SongEquationList(String sr, String label, String value, String key_value, String type, String image, String change_char) {

        this.sr = sr;
        this.label = label;
        this.value = value;
        this.key_value = key_value;
        this.type = type;
        this.image = image;
        this.change_char = change_char;
    }
}
