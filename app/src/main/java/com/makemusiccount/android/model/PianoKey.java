package com.makemusiccount.android.model;

import android.graphics.Region;

/*
 * Created by Welcome on 20-01-2018.
 */

public class PianoKey {

    private Region region = new Region();

    private String Name, margeKeyName;

    public PianoKey() {
    }

    public PianoKey(Region region, String name, String margeKeyName) {
        this.region = region;
        Name = name;
        this.margeKeyName = margeKeyName;
    }

    public String getMargeKeyName() {
        return margeKeyName;
    }

    public void setMargeKeyName(String margeKeyName) {
        this.margeKeyName = margeKeyName;
    }

    public Region getRegion() {

        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
