package com.dreamteam.view;

/**
 * This class specifies the type of event being done
 *
 * @author Kyler Verge
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Tony Zeidan
 */
public enum RiskEventType {
    /**
     * Map update events are thrown to update displayable points on the map.
     */
    UPDATE_MAP,
    /**
     * Game began will only be thrown at the beginning of the game.
     */
    GAME_BEGAN,
    /**
     * Each time a die is rolled in the game.
     */
    DIE_ROLLED,
    /**
     * Each time the attack button needs to be updated.
     */
    UPDATE_ATTACKABLE,
    /**
     * An event that is thrown when the attacking user should be selecting die, this ignores
     * the AI player.
     */
    SELECT_ATTACK_DIE,
    /**
     * An event that is triggered when the user loads a map with incorrect data within it.
     */
    INVALID_MAP_LOAD,
    /**
     * An event that is thrown when the defending user should be selecting die, this ignores
     * the AI player.
     */
    SELECT_DEFEND_DIE,
    /**
     * An event that is thrown every time the phase of the game is changed,
     * used to update the map and GUI.
     */
    PHASE_CHANGE,
    /**
     * Each time an attack has just been commenced in the game.
     */
    ATTACK_COMMENCED,
    /**
     * Each time an attack has just ended in the game.
     */
    ATTACK_COMPLETED,
    /**
     * Each time a territory has been taken over in the game.
     */
    TERRITORY_DOMINATED,
    /**
     * Each time the defender drives of the attacker to the point where he/she
     * can no longer attack (1 unit) in the game.
     */
    TERRITORY_DEFENDED,
    /**
     * Each time units are moved in the game.
     */
    UNITS_MOVED,
    /**
     * Each time a turn has been started in the game.
     */
    TURN_BEGAN,
    /**
     * Each time a turn has ended in the game.
     */
    TURN_ENDED,
    /**
     * Only thrown when the game is over.
     */
    GAME_OVER
}
