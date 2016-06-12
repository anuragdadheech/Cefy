package com.cefy.cefy.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Anurag
 */
public class LinkedInPositions implements Serializable {
    @SerializedName("_total")
    public int total;
    public ArrayList<LinkedInValue> values;
}
