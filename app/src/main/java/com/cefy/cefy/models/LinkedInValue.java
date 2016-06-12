package com.cefy.cefy.models;

import java.io.Serializable;

/**
 * @author Anurag
 */
public class LinkedInValue implements Serializable {
    public String title;
    public String summary;
    public int id;
    public boolean isCurrent;
    public LinkedInCompany company;
    public LinkedInStartDate startDate;
}
