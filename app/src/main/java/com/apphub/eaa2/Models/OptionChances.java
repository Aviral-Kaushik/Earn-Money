package com.apphub.eaa2.Models;

public class OptionChances {

    private int moneyBagChances;

    private int surpriseGiftChances;

    private int dailyBonusChances;

    private int earnRewardChances;

    private int goldCoinChances;

    private int walletMoneyChances;

    public OptionChances(int moneyBagChances,
                         int surpriseGiftChances,
                         int dailyBonusChances,
                         int earnRewardChances,
                         int goldCoinChances,
                         int walletMoneyChances) {
        this.moneyBagChances = moneyBagChances;
        this.surpriseGiftChances = surpriseGiftChances;
        this.dailyBonusChances = dailyBonusChances;
        this.earnRewardChances = earnRewardChances;
        this.goldCoinChances = goldCoinChances;
        this.walletMoneyChances = walletMoneyChances;
    }

    public int getMoneyBagChances() {
        return moneyBagChances;
    }

    public void setMoneyBagChances(int moneyBagChances) {
        this.moneyBagChances = moneyBagChances;
    }

    public int getSurpriseGiftChances() {
        return surpriseGiftChances;
    }

    public void setSurpriseGiftChances(int surpriseGiftChances) {
        this.surpriseGiftChances = surpriseGiftChances;
    }

    public int getDailyBonusChances() {
        return dailyBonusChances;
    }

    public void setDailyBonusChances(int dailyBonusChances) {
        this.dailyBonusChances = dailyBonusChances;
    }

    public int getEarnRewardChances() {
        return earnRewardChances;
    }

    public void setEarnRewardChances(int earnRewardChances) {
        this.earnRewardChances = earnRewardChances;
    }

    public int getGoldCoinChances() {
        return goldCoinChances;
    }

    public void setGoldCoinChances(int goldCoinChances) {
        this.goldCoinChances = goldCoinChances;
    }

    public int getWalletMoneyChances() {
        return walletMoneyChances;
    }

    public void setWalletMoneyChances(int walletMoneyChances) {
        this.walletMoneyChances = walletMoneyChances;
    }
}
