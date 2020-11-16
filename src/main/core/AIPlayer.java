package main.core;

public class AIPlayer extends Player {

    /**
     * Constructor for instances of Player class with name.
     *
     * @param name The name of the player
     */
    public AIPlayer(String name) {
        super(name);
    }

    /**
     * Constructor for instances of main.core.Player class with name and colour.
     *
     * @param name   The name of the player
     * @param colour The colour of the units that the player owns
     */
    public AIPlayer(String name, RiskColour colour) {
        super(name, colour);
    }
}
