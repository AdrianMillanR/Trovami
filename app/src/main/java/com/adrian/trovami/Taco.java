package com.adrian.trovami;

public class Taco {
    private final Double latitude;
    private final Double longitude;
    private final String flavor;

    public Taco(Double latitude, Double longitude, String flavor) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.flavor = flavor;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getFlavor() {
        return flavor;
    }
}
