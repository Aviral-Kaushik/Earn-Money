package com.apphub.eaa2.Models;

import java.util.Calendar;

public class Options {

    private int optionImage;

    private String optionTitle;

    private String optionDescription;

    private double optionEarningAmount;

    public Options(int optionImage,
                   String optionTitle,
                   String optionDescription,
                   double optionEarningAmount) {
        this.optionImage = optionImage;
        this.optionTitle = optionTitle;
        this.optionDescription = optionDescription;
        this.optionEarningAmount = optionEarningAmount;
    }

    public int getOptionImage() {
        return optionImage;
    }

    public void setOptionImage(int optionImage) {
        this.optionImage = optionImage;
    }

    public String getOptionTitle() {
        return optionTitle;
    }

    public void setOptionTitle(String optionTitle) {
        this.optionTitle = optionTitle;
    }

    public String getOptionDescription() {
        return optionDescription;
    }

    public void setOptionDescription(String optionDescription) {
        this.optionDescription = optionDescription;
    }

    public double getOptionEarningAmount() {
        return optionEarningAmount;
    }

    public void setOptionEarningAmount(double optionEarningAmount) {
        this.optionEarningAmount = optionEarningAmount;
    }
}
