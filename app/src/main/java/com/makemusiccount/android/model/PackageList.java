package com.makemusiccount.android.model;

import java.io.Serializable;

public class PackageList implements Serializable {
    private String packID;
    private String name;
    private String plan_price_info;
    private String package_desc;
    private String point1;
    private String point2;
    private String point3;
    private String point4;
    boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private String price;

    public String getPackID() {
        return packID;
    }

    public void setPackID(String packID) {
        this.packID = packID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlan_price_info() {
        return plan_price_info;
    }

    public void setPlan_price_info(String plan_price_info) {
        this.plan_price_info = plan_price_info;
    }

    public String getPackage_desc() {
        return package_desc;
    }

    public void setPackage_desc(String package_desc) {
        this.package_desc = package_desc;
    }

    public String getPoint1() {
        return point1;
    }

    public void setPoint1(String point1) {
        this.point1 = point1;
    }

    public String getPoint2() {
        return point2;
    }

    public void setPoint2(String point2) {
        this.point2 = point2;
    }

    public String getPoint3() {
        return point3;
    }

    public void setPoint3(String point3) {
        this.point3 = point3;
    }

    public String getPoint4() {
        return point4;
    }

    public void setPoint4(String point4) {
        this.point4 = point4;
    }
}
