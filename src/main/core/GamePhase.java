package main.core;

public enum GamePhase {
    START_GAME("start of game phase"),
    BONUS_TROUPE("bonus troupe phase"),
    ATTACK("attack phase"),
    MOVE_UNITS("move units phase");

    private String name;

    private GamePhase(String name) {
        this.name=name;
    }
}
