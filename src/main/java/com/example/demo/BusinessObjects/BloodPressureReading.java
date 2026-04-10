package com.example.demo.BusinessObjects;

import java.time.LocalDate;

public class BloodPressureReading {
    private int id;
    private int systolic;
    private int diastolic;
    private int heartRate;
    private LocalDate date;

    public BloodPressureReading() {}
    
    public BloodPressureReading(int id, int systolic, int diastolic, int heartRate, LocalDate date) {
        this.id = id;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.heartRate = heartRate;
        this.date = date;
    }

    public int getSystolic() {
        return systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public void setSystolic(int systolic) {
        if (systolic < 0 || systolic > 300)
            throw new IllegalArgumentException("Invalid systolic value");
        this.systolic = systolic;
    }

    public void setDiastolic(int diastolic) {
        if (diastolic < 0 || diastolic > 200)
            throw new IllegalArgumentException("Invalid diastolic value");
        this.diastolic = diastolic;
    }

    public void setHeartRate(int heartRate) {
        if (heartRate < 0 || heartRate > 300)
            throw new IllegalArgumentException("Invalid heart rate value");
        this.heartRate = heartRate;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
