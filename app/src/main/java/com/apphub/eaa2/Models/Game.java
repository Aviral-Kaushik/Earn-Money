package com.apphub.eaa2.Models;

public class Game {

    private int image;

    private String gameName;

    private String gameCoins;

    private String link;

    public Game(int image, String gameName, String gameCoins, String link) {
        this.image = image;
        this.gameName = gameName;
        this.gameCoins = gameCoins;
        this.link = link;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameCoins() {
        return gameCoins;
    }

    public void setGameCoins(String gameCoins) {
        this.gameCoins = gameCoins;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
