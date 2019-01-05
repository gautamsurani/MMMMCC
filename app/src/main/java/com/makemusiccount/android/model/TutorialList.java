package com.makemusiccount.android.model;

/**
 * Created by Welcome on 14-02-2018.
 */

public class TutorialList {
    String ID, name, url, image, status;

    public TutorialList() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public TutorialList(String ID, String name, String url, String image, String status) {

        this.ID = ID;
        this.name = name;
        this.url = url;
        this.image = image;
        this.status = status;
    }
}
