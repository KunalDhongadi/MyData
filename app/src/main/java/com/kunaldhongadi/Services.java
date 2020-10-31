package com.kunaldhongadi;

public class Services {

    private String sServiceName;
    private String sDate;

    public Services(String sServiceName, String sDate) {
        this.sServiceName = sServiceName;
        this.sDate = sDate;
    }

    public String getsServiceName() {
        return sServiceName;
    }

    public void setsServiceName(String sServiceName) {
        this.sServiceName = sServiceName;
    }

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }
}
