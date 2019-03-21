package com.metrolinq.isaac.myapplication;

import java.io.Serializable;

class ClientInfo implements  Serializable {

    String firstName;
    String lastName;
    String fullName;
    String phNum;

    String paytype;

    public ClientInfo(String firstName, String lastName, String fullName, String phNum, String paytype) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.phNum = phNum;
        this.paytype = paytype;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhNum() {
        return phNum;
    }

    public void setPhNum(String phNum) {
        this.phNum = phNum;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

}
