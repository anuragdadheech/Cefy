package com.cefy.cefy.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Anurag
 */
public class Count implements Serializable{
    @SerializedName("total_count")
    public int TotalCount;
}
