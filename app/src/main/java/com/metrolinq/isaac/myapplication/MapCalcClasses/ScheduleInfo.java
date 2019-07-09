package com.metrolinq.isaac.myapplication.MapCalcClasses;

import java.util.Date;

public class ScheduleInfo {

    private int Hour;
    private int Min;
    private double OriLat;
    private double OriLon;
    private double DesLat;
    private double DesLon;
    private int Fare;
    private String ClientName;
    private Date CurrentDate;
    private int Year, Month, Day;
    private String phoneNumber;
    private String PayType, AssignDriver, OriName, DesName;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOriName() {
        return OriName;
    }

    public void setOriName(String oriName) {
        OriName = oriName;
    }

    public String getDesName() {
        return DesName;
    }

    public void setDesName(String desName) {
        DesName = desName;
    }

    public String getAssignDriver() {
        return AssignDriver;
    }

    public void setAssignDriver(String assignDriver) {
        AssignDriver = assignDriver;
    }

    public Date getCurrentDate() {
        return CurrentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.CurrentDate = currentDate;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public int getMonth() {
        return Month;
    }

    public void setMonth(int month) {
        Month = month;
    }

    public int getDay() {
        return Day;
    }

    public void setDay(int day) {
        Day = day;
    }

    public String getPayType() {
        return PayType;
    }

    public void setPayType(String payType) {
        PayType = payType;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    public int getFare() {
        return Fare;
    }

    public void setFare(int fare) {
        this.Fare = fare;
    }

    public int getHour() {
        return Hour;
    }

    public void setHour(int hour) {
        this.Hour = hour;
    }

    public int getMin() {
        return Min;
    }

    public void setMin(int min) {
        this.Min = min;
    }

    public double getOriLat() {
        return OriLat;
    }

    public void setOriLat(double oriLat) {
        this.OriLat = oriLat;
    }

    public double getOriLon() {
        return OriLon;
    }

    public void setOriLon(double oriLon) {
        this.OriLon = oriLon;
    }

    public double getDesLat() {
        return DesLat;
    }

    public void setDesLat(double desLat) {
        this.DesLat = desLat;
    }

    public double getDesLon() {
        return DesLon;
    }

    public void setDesLon(double desLon) {
        this.DesLon = desLon;
    }

    public ScheduleInfo(Integer hour, Integer min, double oriLat, double oriLon, double desLat,
                        double desLon, int fare, String clientName, int year, int month, int day,
                        Date currentDate, String payType,String assignDriver, String oriName, String desName, String phoneNumber) {
        Hour = hour;
        Min = min;
        OriLat = oriLat;
        OriLon = oriLon;
        DesLat = desLat;
        DesLon = desLon;
        Fare = fare;
        ClientName = clientName;
        Year = year;
        Month = month;
        Day = day;
        CurrentDate = currentDate;
        PayType = payType;
        AssignDriver = assignDriver;
        OriName = oriName;
        DesName = desName;
        this.phoneNumber = phoneNumber;


    }
}
