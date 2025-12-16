package com.example.bankapplicationproject;

public class MusteriModel {


    Integer tc;
    String isim;
    String soyisim;
    Integer telno;
    String iban;
    Double bakiye;

    public MusteriModel(Integer tc, String isim, String soyisim, Integer telno, String iban, Double bakiye) {
        this.tc = tc;
        this.isim = isim;
        this.soyisim = soyisim;
        this.telno = telno;
        this.iban = iban;
        this.bakiye = bakiye;
    }

    public Integer getTc() {
        return tc;
    }

    public void setTc(Integer tc) {
        this.tc = tc;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getSoyisim() {
        return soyisim;
    }

    public void setSoyisim(String soyisim) {
        this.soyisim = soyisim;
    }

    public Integer getTelno() {
        return telno;
    }

    public void setTelno(Integer telno) {
        this.telno = telno;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Double getBakiye() {
        return bakiye;
    }

    public void setBakiye(Double bakiye) {
        this.bakiye = bakiye;
    }
}
