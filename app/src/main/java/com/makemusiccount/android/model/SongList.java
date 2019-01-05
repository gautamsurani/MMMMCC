package com.makemusiccount.android.model;

/**
 * Created by Welcome on 24-01-2018.
 */

public class SongList {
    String ID;
    String name;
    String image;
    String status;

    public SongList(String ID, String name, String image, String status, String play_songs) {
        this.ID = ID;
        this.name = name;
        this.image = image;
        this.status = status;
        this.play_songs = play_songs;
    }

    String play_songs;

    public SongList() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlay_songs() {
        return play_songs;
    }

    public void setPlay_songs(String play_songs) {
        this.play_songs = play_songs;
    }
}
