package main.core;

public class mainClass {

    public static void main(String[] args) {
        GameSingleton g1 = GameSingleton.getGameInstance();
        g1.runGame();
    }

}
