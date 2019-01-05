package com.makemusiccount.android.model;

/**
 * Created by Welcome on 25-01-2018.
 */

public class CategoryList {
    private String catID, name, image, sub_cats;

    public String getSub_cats() {
        return sub_cats;
    }

    public void setSub_cats(String sub_cats) {
        this.sub_cats = sub_cats;
    }

    public CategoryList(String catID, String name, String image, String sub_cats) {
        this.catID = catID;
        this.name = name;
        this.image = image;
        this.sub_cats = sub_cats;
    }

    public CategoryList() {

    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
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

    public CategoryList(String catID, String name, String image) {
        this.catID = catID;
        this.name = name;
        this.image = image;
    }
}
