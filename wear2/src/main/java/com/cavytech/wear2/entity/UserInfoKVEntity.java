package com.cavytech.wear2.entity;

/**
 * Created by longjining on 16/4/11.
 */

public class UserInfoKVEntity{
    String key;
    String value;

    public UserInfoKVEntity(String key, String value){

        this.key   = key;
        this.value = value;
    }

    public String getKey(){
        return this.key;
    }

    public String getValue(){
        return this.value;
    }
}