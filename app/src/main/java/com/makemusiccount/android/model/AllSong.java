package com.makemusiccount.android.model;

public class AllSong {
    String ID, name, song_file;

    public AllSong() {
    }

    public String getSong_file() {
        return song_file;
    }

    public void setSong_file(String song_file) {
        this.song_file = song_file;
    }

    public String getID() {

        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
