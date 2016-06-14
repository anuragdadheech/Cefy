package com.cefy.cefy.models;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Anurag
 */
public class SMSData implements Serializable {
    public SMSData(String id, String address, String msg, int type, Date time) {
        this.id = id;
        this.address = address;
        this.msg = msg;
        this.type = type;
        this.time = time.toString();
    }

    public String id;
    public String address;
    public String msg;
    public int type;
    public String time;
}
