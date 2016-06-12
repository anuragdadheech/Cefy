package com.cefy.cefy.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Anurag
 */
public class LinkedInUser implements Serializable {
    @SerializedName("emailAddress")
    public String email;
    public String firstName;
    public String lastName;
    public String headline;
    public String id;
    public String industry;
    public String pictureUrl;
    public String summary;
    public LinkedInPositions positions;

}
