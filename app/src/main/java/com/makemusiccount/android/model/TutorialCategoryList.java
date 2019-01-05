package com.makemusiccount.android.model;

public class TutorialCategoryList {
    String ID, name, image, status, complete_status,video;

    public TutorialCategoryList() {
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComplete_status() {
        return complete_status;
    }

    public void setComplete_status(String complete_status) {
        this.complete_status = complete_status;
    }
}
