package com.groovesquid.model;

public class Country {
    private String ID, CC1, CC2, CC3, CC4, DMA, iso, region, city, zip, IPR;
    
    public Country() {
        this.IPR = "1";
        this.ID = "223";
        this.CC1 = "0";
        this.CC2 = "0";
        this.CC3 = "0";
        this.CC4 = "2147483648";
    }

    public Country(String ID, String CC1, String CC2, String CC3, String CC4, String DMA, String iso, String region, String city, String zip, String IPR) {
        this.ID = ID;
        this.CC1 = CC1;
        this.CC2 = CC2;
        this.CC3 = CC3;
        this.CC4 = CC4;
        this.DMA = DMA;
        this.iso = iso;
        this.region = region;
        this.city = city;
        this.zip = zip;
        this.IPR = IPR;
    }
    
    @Override
    public String toString() {
        return "ID: " + ID + ", CC1: " + CC1 + ", CC2: " + CC2 + ", CC3: " + CC3 + ", CC4: " + CC4 + ", DMA: " + DMA + ", iso: " + iso + ", region: " + region + ", city: " + city + ", zip: " + zip + ", IPR: " + IPR;
    }
}
