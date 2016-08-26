package com.cavytech.wear2.entity;

import java.io.Serializable;

/**
 * Created by longjining on 16/5/13.
 */
public class BandInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVeersion() {
        return veersion;
    }

    public void setVeersion(String veersion) {
        this.veersion = veersion;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address = "";
    private String  name = "";
    private String  veersion = "";
}
