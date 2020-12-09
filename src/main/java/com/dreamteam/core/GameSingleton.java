package com.dreamteam.core;

import com.dreamteam.view.RiskEvent;
import com.dreamteam.view.RiskEventType;
import com.dreamteam.view.RiskGameHandler;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsonable;
import com.github.cliftonlabs.json_simple.Jsoner;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * This class represents the model for the program, notifies
 * the com.dreamteam.view of changes made to the game, is responsible
 * for the com.dreamteam.core functionality of the game.
 * <p>
 * The model now works with phases and as it progresses through the phases,
 * it notifies any views. The phases are:
 * Bonus Troupe Phase, Attack Phase, Move Units Phase
 *
 * @author Kyler Verge
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Tony Zeidan
 */
public class GameSingleton implements Jsonable {

    /**
     * Single instance of the game itself
     */
    private static GameSingleton gameInstance;
    /**
     * The list of players that may or may not be active throughout the game.
     *
     * @see Player
     */
    private List<Player> players;
    /**
     * The world that the players will be playing on.
     *
     * @see WorldMap
     */
    private WorldMap world;
    /**
     * Contains the current number of active players.
     */
    private int numActivePlayer;
    /**
     * Stores the location of the current player in the list of players.
     */
    private static int currentPlayerInd;
    /**
     * Stores the current phase of the game.
     */
    private static GamePhase gamePhase;
    /**
     * A list of all handlers that listen to this model.
     */
    private List<RiskGameHandler> riskHandlers;

    private int bonusTroops;
    /**
     * Default constructor for instances of main.com.dreamteam.core.Game class.
     * (For now) Creates a new game with the hardcoded map and the players that
     * the user inputs.
     */
    private GameSingleton() {
        //initialize map, player list, and scanner
        players = new ArrayList<>();
        world = new WorldMap();
        currentPlayerInd = 0;
        gamePhase = null;
        riskHandlers = new ArrayList<>();
        bonusTroops =0;
    }

    /**
     * Gets an instance of the GameSingleton class (Game)
     *
     * @return GameSingleton, the model of the program
     */
    public static GameSingleton getGameInstance() {
        //if an instance doesn't exist, create only one instance
        if (gameInstance == null) {
            gameInstance = new GameSingleton();
        }
        return gameInstance;
    }

    /**
     * Sets the list of players to be assigned to the current game.
     * TODO: Someone double-check javadoc here
     *
     * @param players The list of players in the game.
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Start a new game from contents in a saved zipfile.
     * TODO: Someone double-check javadoc here
     *
     * @param zf Zipfile containing contents to start new game
     * @throws Exception TODO: Not sure what to put here
     */
    public void newGame(ZipFile zf) throws Exception {
            //six random colors for players
            List<RiskColour> randomColors = new LinkedList<>();
            randomColors.add(RiskColour.RED);
            randomColors.add(RiskColour.GRAY);
            randomColors.add(RiskColour.BLUE);
            randomColors.add(RiskColour.YELLOW);
            randomColors.add(RiskColour.BLACK);
            randomColors.add(RiskColour.GREEN);

            Random rand = new Random();

            /*We must get all player names and generate colours.
            Loop through players and obtain names through user input.
            Randomly assign colours.
            */
            for (Player p : players) {
                //get this players name

                int randIndex = rand.nextInt(randomColors.size());
                //generate and assign random colours
                RiskColour colour = randomColors.get(randIndex);
                p.setColour(colour);
                randomColors.remove(randIndex);
            }

        ZipEntry mapData = zf.getEntry("map.json");
        InputStream mapStream = zf.getInputStream(mapData);
        try {
            world.assignNewMap(players, mapStream);
        } catch (RiskGameException e) {
            e.printStackTrace();
            notifyHandlers(new RiskEvent(this,RiskEventType.INVALID_MAP_LOAD));
        }
        mapStream.close();
        zf.close();



        //set the initial amount of active players accordingly
        setNumActivePlayer(players.size());

        //shuffle the order of the players
        shufflePlayers();
        //gamePhase = GamePhase.START_GAME;

        notifyHandlers(new RiskEvent(this, RiskEventType.GAME_BEGAN,
                world.getName()));

        gamePhase = GamePhase.START_GAME;

        notifyHandlers(new RiskEvent(this,
                RiskEventType.TURN_BEGAN, getCurrentPlayer(), getBonusUnits(getCurrentPlayer())));

        nextPhase();    //beginning should be bonus troupe
    }

    /**
     * Imports the contents of a saved game and updates the worldmap and game
     * accordingly.
     *
     * @param zf The zipfile containing the contents of the game to be imported
     * @throws Exception TODO: Not sure what to put here
     */
    public void importGame(ZipFile zf) throws Exception {
        ZipEntry mapData = zf.getEntry("map.json");
        InputStream mapStream = zf.getInputStream(mapData);
        ZipEntry gameData = zf.getEntry("game.json");
        InputStream gameStream = zf.getInputStream(gameData);
        try {
            world.readMap(mapStream);
        } catch (RiskGameException e) {
            e.printStackTrace();
            notifyHandlers(new RiskEvent(this,RiskEventType.INVALID_MAP_LOAD));
        }
        readGame(gameStream);
        mapStream.close();
        gameStream.close();
    }

    /**
     * TODO: Somebody who understands what is going on here needs to finish this javadoc
     * @param gameStream TODO: See line 184
     */
    private void readGame(InputStream gameStream)
    {
        try {
            BufferedReader buf = new BufferedReader(new InputStreamReader(gameStream));
            JsonObject parser = (JsonObject) Jsoner.deserialize(buf);
            gamePhase = GamePhase.valueOf((String)(parser).get("phase"));
            numActivePlayer=Integer.parseInt((String)(parser).get("activeNum"));
            if (gamePhase == GamePhase.BONUS_TROUPE)
                bonusTroops = Integer.parseInt((String)(parser).get("bonusTroops"));
            JsonArray players = (JsonArray) (parser).get("players");
            System.out.println(players);
            for (Object player:players)
            {
                JsonObject playerInfo= (JsonObject)((JsonObject)player).get("player");
                RiskColour colour = RiskColour.valueOf((String)(playerInfo).get("colour"));
                String name = (String) (playerInfo).get("name");
                String isAI = (String) (playerInfo).get("isAI");
                Player playerObj = (isAI.equals("true"))? new AIPlayer(name,colour):new Player(name,colour);
                this.players.add(playerObj);
                int dieRoll =Integer.parseInt((String)(playerInfo.get("selectedDie")));
                playerObj.setDiceRoll(dieRoll);
                JsonArray territories = (JsonArray)(playerInfo.get("owned"));
                for(Object terr:territories)
                {
                    String terrName = (String)((JsonObject)terr).get("name");
                    int numUnits = Integer.parseInt((String)((JsonObject)terr).get("units"));
                    Territory territory = world.getTerritory(terrName);
                    territory.setOwner(playerObj);
                    territory.setUnits(numUnits);
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        notifyHandlers(new RiskEvent(this, RiskEventType.GAME_BEGAN,world.getName()));
        notifyHandlers(new RiskEvent(this, RiskEventType.PHASE_CHANGE, gamePhase));
        if (gamePhase == GamePhase.BONUS_TROUPE)
        {
            notifyHandlers(new RiskEvent(this, RiskEventType.TURN_BEGAN, getCurrentPlayer(),bonusTroops));
        }
        else
        {
            notifyHandlers(new RiskEvent(this, RiskEventType.TURN_BEGAN, getCurrentPlayer(),0));
        }
        notifyMapUpdateAllCoordinates();
    }
    /**
     * Exports the file to our custom save game format (.save).
     * This format is actually a ZIP folder containing a JSON and map image.
     *
     * @param file The file to export to (new or not)
     * @param mapImage The image representing the map
     */
    public void export(File file,Image mapImage,int bonus) {
        if (file!=null) {
            bonusTroops = bonus;
            try {
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));

                zos.putNextEntry(new ZipEntry("game.json"));
                zos.write(toJson().getBytes());
                zos.closeEntry();
                zos.putNextEntry(new ZipEntry("map.json"));
                zos.write(world.toJson().getBytes());
                zos.closeEntry();
                if(mapImage!=null){
                    zos.putNextEntry(new ZipEntry("map.png"));
                    ImageIO.write((RenderedImage) mapImage,"png",zos);
                    zos.closeEntry();
                }
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the number of currently active players
     *
     * @param numActivePlayer Number of active players
     */
    public void setNumActivePlayer(int numActivePlayer) {
        this.numActivePlayer = numActivePlayer;
    }

    /**
     * Notify all views to reset displayable coordinates/territories.
     */
    public void notifyMapUpdateAllCoordinates() {
        notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_MAP, world.getAllCoordinates()));
    }

    /**
     * Notify all views to set displayable coordinates/territories.
     *
     * @param territory The territory that determines the other displayable territories
     */
    public void notifyMapUpdateAttackingNeighbourCoordinates(Territory territory) {
        notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_MAP, getValidAttackNeighboursOwned(getCurrentPlayer(), territory)));
    }

    /**
     * notify all views to set the displayable coordinates/territories to be only those
     * owned by the current player
     */
    public void notifyMapUpdateOwnedCoordinates() {
        notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_MAP, getAllOwnedNodes(getCurrentPlayer())));
    }

    /**
     * notify all com.dreamteam.view to set the displayable coordinates/territories to be only those
     * that are in the same path as the given territory
     *
     * @param territory the territory that the other owned territories must be connected to
     */
    public void notifyMapUpdateTroupeMoveCoordinate(Territory territory) {
        notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_MAP, getValidTroupeMovementTerritories(territory)));
    }

    /**
     * Add a listener to this model.
     *
     * @param rgv The handler to add
     */
    public void addHandler(RiskGameHandler rgv) {
        riskHandlers.add(rgv);
    }

    /**
     * Remove a listener from this model.
     *
     * @param rgv The handler to remove
     */
    public void removeHandler(RiskGameHandler rgv) {
        riskHandlers.remove(rgv);
    }

    /**
     * Generates a random order for the players.
     */
    private void shufflePlayers() {
        //need to make a single random field in game class
        Random rand = new Random();

        for (int i = players.size(); i > 0; i--) {
            Player holder = players.get(players.size() - i);
            int chosen = rand.nextInt(i);
            players.set(players.size() - i, players.get(chosen));
            players.set(chosen, holder);
        }
    }

    /**
     * Switches the current phase of the game.
     * Goes through phases in a cyclical fashion.
     */
    public void nextPhase() {
        Player currentPlayer = getCurrentPlayer();
        switch (gamePhase) {
            case START_GAME:
            case MOVE_UNITS:
                this.gamePhase = GamePhase.BONUS_TROUPE;
                //notifyMapUpdateOwnedCoordinates();
                notifyMapUpdateAllCoordinates();
                if (currentPlayer instanceof AIPlayer) {
                    int numTroopsToPlace = getBonusUnits(currentPlayer);
                    ((AIPlayer) currentPlayer).placeUnits(numTroopsToPlace, this);
                    nextPhase();
                }
                break;
            case BONUS_TROUPE:
                this.gamePhase = GamePhase.ATTACK;
                notifyMapUpdateAllCoordinates();
                if (currentPlayer instanceof AIPlayer) {
                    ((AIPlayer) currentPlayer).doAttack(this);
                    nextPhase();
                }
                break;
            case ATTACK:
                this.gamePhase = GamePhase.MOVE_UNITS;
                notifyMapUpdateOwnedCoordinates();
                if (currentPlayer instanceof AIPlayer) {
                    ((AIPlayer) currentPlayer).moveTroops();
                    nextPlayer();
                }
                break;
        }
        notifyHandlers(new RiskEvent(this, RiskEventType.PHASE_CHANGE, gamePhase));
    }

    /**
     * Get the next player who has not yet been eliminated from the game.
     */
    public void nextPlayer() {
        notifyHandlers(new RiskEvent(this,
                RiskEventType.TURN_ENDED, getCurrentPlayer()));

        currentPlayerInd = (currentPlayerInd + 1) % players.size();
        while (!(players.get(currentPlayerInd).isActive())) {
            currentPlayerInd = (currentPlayerInd + 1) % players.size();
        }

        notifyHandlers(new RiskEvent(this,
                RiskEventType.TURN_BEGAN, getCurrentPlayer(), getBonusUnits(getCurrentPlayer())));
        System.out.println("New Player: " + getCurrentPlayer().getName());

        nextPhase();
    }

    /**
     * Get the player who is currently on their turn.
     *
     * @return the player who is currently on their turn
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerInd);
    }

    /**
     * Game has finished and determine the player who won.
     */
    private void endGame() {
        Player winner = null;

        for (Player p : players) {
            if (p.isActive()) winner = p;
        }

        //notify all views that the game is over
        notifyHandlers(new RiskEvent(this,
                RiskEventType.GAME_OVER, winner, world.getName()));
    }

    /**
     * Gets the number of bonus units a player receives at the start of their turn
     *
     * @param current The current Player
     * @return The amount of bonus units that the player receives
     */
    public int getBonusUnits(Player current) {
        int territoryBonus = current.getOwnedTerritories().size() / 3;
        int continentBonus = 0;
        Set<Continent> ruled = world.getRuled(current);
        for (Continent c : ruled) {
            continentBonus += c.getBonusRulerAmount();
        }

        return (Math.max(territoryBonus, 3)) + continentBonus;
    }

    /**
     * gets all territories/coordinates the that player owns
     *
     * @param player that nodes should be gotten
     * @return mapping of the territory to its coordinate
     */
    public Map<Territory, Point> getAllOwnedNodes(Player player) {
        Map<Territory, Point> owned = new HashMap<>();
        for (Territory t : player.getOwnedTerritories()) {
            Point p = world.getAllCoordinates().get(t);
            owned.put(t, p);
        }
        return owned;
    }

    /**
     * Retrieves a map of all the neighbours around the given territory that can
     * attack the given territory.
     *
     * @param attacker  The player who is currently attacking
     * @param defending The territory being defended
     * @return A map of valid attacking territories owned by the attacker
     */
    public Map<Territory, Point> getValidAttackNeighboursOwned(Player attacker, Territory defending) {

        if (attacker.ownsTerritory(defending)) {
            notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_ATTACKABLE, false));
            return null;
        }
        Map<Territory, Point> neighboursOwned = world.getNeighbourNodesOwned(attacker, defending);
        List<Territory> invalid = new ArrayList<>();
        for (Territory t : neighboursOwned.keySet()) {
            if (t.getUnits() == 1) invalid.add(t);
        }
        for (Territory t : invalid) {
            neighboursOwned.remove(t);
        }
        if (neighboursOwned.size() == 0) {
            notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_ATTACKABLE, false));
            return null;
        } else {
            notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_ATTACKABLE, true));
            return neighboursOwned;
        }
    }

    /**
     * Determines the territories/coordinates that are connected to the provided territory and same owner.
     *
     * @param initial The inital territory that the end of turn move starts at
     * @return mapping of territories and coordinates.
     */
    public Map<Territory, Point> getValidTroupeMovementTerritories(Territory initial) {
        List<Territory> queue = new LinkedList<>();
        Map<Territory, Point> visited = new HashMap<>();
        queue.add(initial);
        while (!queue.isEmpty()) {
            Territory current = queue.remove(0);
            Map<Territory, Point> validNeighbours = world.getNeighbourNodesOwned(initial.getOwner(), current);

            visited.put(current, world.getAllCoordinates().get(current));
            // Or you can store a set of visited vertices somewhere
            for (Territory t : validNeighbours.keySet()) {
                if (!visited.containsKey(t)) {
                    queue.add(t);
                }
            }
        }
        visited.remove(initial);
        List<Territory> invalidTerritories = new LinkedList<>();
        for (Territory t : visited.keySet()) {
            if (t.getUnits() == 1) invalidTerritories.add(t);
        }
        for (Territory t : invalidTerritories) {
            visited.remove(t);
        }

        if (visited.size() == 0) {
            notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_ATTACKABLE, false));
            return null;
        } else {
            notifyHandlers(new RiskEvent(this, RiskEventType.UPDATE_ATTACKABLE, true));
            return visited;
        }
    }

    /**
     * performs the battle, notifying views for user input if needed
     * for the attacking and defending of the two territories.
     *
     * @param attacking the attacking territory
     * @param defending the defending territory
     * @return true if the attacking territory has eliminated all troops in defending territory, otherwise false.
     */
    public boolean performBattle(Territory attacking, Territory defending) {

        Player attacker = attacking.getOwner();
        Player defender = defending.getOwner();

        int attDice = 0;
        int defDice = 0;

        if (attacker instanceof AIPlayer) {
            attDice = getMaxBattleDie(attacking.getUnits(), true);
        } else {
            int maxAttack = getMaxBattleDie(attacking.getUnits(), true);
            notifyHandlers(new RiskEvent(this, RiskEventType.SELECT_ATTACK_DIE, attacking, defending, maxAttack));
            attDice = attacker.getDiceRoll();
        }

        if (defender instanceof AIPlayer) {
            defDice = getMaxBattleDie(defending.getUnits(), false);
        } else {
            int maxDefend = getMaxBattleDie(defending.getUnits(), false);
            notifyHandlers(new RiskEvent(this, RiskEventType.SELECT_DEFEND_DIE, attacking, defending, maxDefend));
            defDice = defender.getDiceRoll();
        }
        boolean battleWon = battle(attacking, defending, attDice, defDice);
        notifyMapUpdateAllCoordinates();
        return battleWon;
    }

    /**
     * Represents a battle sequence between a territory owned by the current player and
     * an adjacent territory owned by another player.
     * <p>
     * Completes a single attack and removes units from the territories according to the result of
     * the attack. If, after the attack, the territory owned by a current player loses all but one
     * unit, then declare the attack a victory for the defender and return true. If, after the
     * attack, the territory being attacked contains no more units, then declare the attack a victory
     * for the attacker and return true.
     *
     * @param attacking The territory that supplies the attacking units
     * @param defending The territory that is being attacked
     * @param attackDie The number of dice that the attacker has chosen to roll
     * @param defendDie The number of dice that the defender has chosen to roll
     * @return true if the attacker dominated the defender
     */
    public boolean battle(Territory attacking, Territory defending, int attackDie, int defendDie) {

        Player attacker = attacking.getOwner();
        Player defender = defending.getOwner();

        notifyHandlers(new RiskEvent(this, RiskEventType.ATTACK_COMMENCED,
                attacker, defender));
        //TODO: we could use this in order to get input of die, and check if AIs were present

        int[] lost = attack(attackDie, defendDie);
        attacking.removeUnits(lost[0]);
        defending.removeUnits(lost[1]);

        notifyHandlers(new RiskEvent(this, RiskEventType.ATTACK_COMPLETED,
                attacker, defender, lost));

        if (attacking.getUnits() == 1) {
            notifyHandlers(new RiskEvent(this, RiskEventType.TERRITORY_DEFENDED,
                    attacker, defender, lost));

        } else if (defending.getUnits() == 0) {
            notifyHandlers(new RiskEvent(this, RiskEventType.TERRITORY_DOMINATED,
                    attacker, defender));
            notifyMapUpdateAllCoordinates();
            return true;
        }
        return false;
    }

    /**
     * Represents one smaller conflict between two territories.
     * This method represents the rolling of dice on both parties and the outcome of those rolls.
     *
     * @param attackRolls The number of dice the attacker is using for this attack
     * @param defendRolls The number of dice the defender is using for this defence
     * @return A pair of integers (position 0: how many units attacker lost, position 1: how many units defender lost)
     */
    public int[] attack(int attackRolls, int defendRolls) {

        //Random Acts as a Die
        Random rand = new Random();

        //Two Primitive Integer Arrays to Store Random Rolls

        //Roll Dice (Random Integer) For Both Parties And Display Simultaneously
        int[] attackDice = rollDice(attackRolls);
        notifyHandlers(new RiskEvent(this, RiskEventType.DIE_ROLLED,
                attackDice));
        int[] defendDice = rollDice(defendRolls);
        notifyHandlers(new RiskEvent(this, RiskEventType.DIE_ROLLED,
                defendDice));

        //Sort Both Rolls in Descending Order
        Arrays.sort(attackDice);
        Arrays.sort(defendDice);

        //Set Counter Variables for Lost Units in the Attack
        int attackLost = 0;
        int defendLost = 0;

        /*
        Logic:
        If both attacking rolls are greater than both defending rolls, then defender loses two units.
        If the top defender roll is equal/greater than the top attacking roll while the second defending roll
            is less than the second attacking roll, then both players lose one unit.
        If both defender rolls are equal to or greater than both attacking rolls, then the attacker loses two
            units.
        If the attacker rolls one dice, then check if that roll is greater than the top defender roll or not
            and remove unit accordingly.
         */
        for (int i = attackRolls - 1; i >= 0; i--) {
            for (int j = defendRolls - (attackRolls - i); j >= 0; j--) {
                if (attackDice[i] > defendDice[j]) {
                    defendLost += 1;
                    break;
                } else {
                    attackLost += 1;
                    break;
                }
            }
        }
        //Return the result of the attack via units lost
        return new int[]{attackLost, defendLost};
    }

    /**
     * Simulates the rolling of a given amount of die.
     *
     * @param rolls The amount of die to roll
     * @return The results of each roll
     */
    public static int[] rollDice(int rolls) {

        Random rand = new Random();

        int[] rollers = new int[rolls];

        for (int i = 0; i < rolls; i++) {
            rollers[i] = rand.nextInt(6) + 1;
        }

        return rollers;
    }

    /**
     * Gets the max amount of dice the attacker/defender can roll
     *
     * @param numUnits  The number of units on the territory
     * @param attacking Whether the player is attacking or defending
     * @return The max number of dice the player can roll
     */
    public int getMaxBattleDie(int numUnits, boolean attacking) {
        //The Player is Attacking
        if (attacking) {
            //Determines Number of Die by the Number of Units on Attacking Territory
            switch (numUnits) {
                case (1):
                    return 0;
                case (2):
                    return 1;
                case (3):
                    return 2;
                default:
                    return 3;
            }
            //The Player is Defending
        } else {
            //Determines Number of Die by the Number of Units on Defending Territory
            switch (numUnits) {
                case (0):
                    return 0;
                case (1):
                    return 1;
                default:
                    return 2;
            }
        }
    }

    /**
     * Move units into one territory from an adjacent territory, such
     * that the current player owns both territories after the movement sequence. At least one unit must
     * be left behind in the initial territory. If the owners of the two territories are different for
     * the movement sequence, then it is occurring after a victory for the attacker in a battle sequence.
     * If the owners of the two territories are the same, then the movement sequence is occurring just before
     * the end of the current player's turn.
     *
     * @param initialT The territory that will move units out
     * @param finalT   The territory that will add units
     * @param numUnits The number of units that the attacker/current player wants to move
     */
    public void moveUnits(Territory initialT, Territory finalT, int numUnits) {

        //Move the units from the fortifying territory to the fortified territory
        initialT.removeUnits(numUnits);
        finalT.addUnits(numUnits);

        //Check if the movement of units occurs after a battle sequence as a result of a victory for the attacker
        if (initialT.getOwner() != finalT.getOwner()) {
            Player attacker = initialT.getOwner();
            Player defender = finalT.getOwner();

            //Gives the victor the claimed territory
            finalT.setOwner(attacker);
            //attacker.addTerritory(finalT);
            defender.removeTerritory(finalT);
            //Print a message to confirm the fortify after an attack
            notifyHandlers(new RiskEvent(this, RiskEventType.UNITS_MOVED,
                    initialT, finalT, numUnits));
            world.updateContinentRulers();
            //Check to see if their is only one player remaining
            updateNumActivePlayers();
            if (this.getNumActivePlayer() == 1) {
                endGame();
            }
        } else {
            //Print a message to confirm the movement of units before end of current player's turn
            notifyHandlers(new RiskEvent(this, RiskEventType.UNITS_MOVED,
                    initialT, finalT, numUnits));
        }
        notifyMapUpdateAllCoordinates();
    }

    /**
     * Moves exactly one unit from the players bonus supply.
     *
     * @param bonusTerritory The territory the units will be moved to
     */
    public void moveBonus(Territory bonusTerritory) {
        Player mover = bonusTerritory.getOwner();
        Territory tempTerritory = new Territory("bonus");
        tempTerritory.setOwner(mover);
        tempTerritory.setUnits(1);
        moveUnits(tempTerritory, bonusTerritory, 1);
        mover.removeTerritory(tempTerritory);
        notifyMapUpdateAllCoordinates();
    }

    /**
     * Update the number of players active.
     */
    public void updateNumActivePlayers() {
        //Check to see if each player has at least one territory of their own, if not they are removed from the game
        int numActive = 0;
        for (Player player : players) {
            if (player.isActive()) {
                if (player.getOwnedTerritories().size() > 0) {
                    numActive += 1;
                } else {
                    //update
                    player.setActive(false);
                }
            }
        }
        numActivePlayer = numActive;
    }

    /**
     * Returns the number of players who have not yet been eliminated.
     *
     * @return The number of active players
     */
    public int getNumActivePlayer() {
        return numActivePlayer;
    }

    /**
     * Notifies the event handlers
     *
     * @param e RiskEvent
     */
    private void notifyHandlers(RiskEvent e) {
        for (RiskGameHandler rgv : riskHandlers) {
            rgv.handleRiskUpdate(e);
        }
    }

    /**
     * Retrieves the world map of the current game.
     *
     * @return The map associated to this current game
     */
    public WorldMap getWorld() {
        return world;
    }

    /**
     * Serialize to a JSON formatted string.
     *
     * @return a string, formatted in JSON, that represents the Jsonable.
     */
    @Override
    public String toJson() {
        JsonObject json = new JsonObject();
        JsonArray playersJson = new JsonArray();
        ArrayList<JsonObject> playersJsonList = new ArrayList<>();
        System.out.println(players.get(0));
        int i = 0;
        while (i<players.size())
        {
            int currentIndex = (currentPlayerInd+i)%players.size();
            JsonObject playerObj = new JsonObject();
            playerObj.put("player", players.get(currentIndex));
            playersJsonList.add(playerObj);
            i++;
        }
        playersJson.addAll(playersJsonList);
        json.put("players",playersJson);
        json.put("activeNum",numActivePlayer+"");
        if (gamePhase == GamePhase.BONUS_TROUPE)
            json.put("bonusTroops",bonusTroops+"");
        json.put("phase",gamePhase.name());
        return json.toJson();
    }

    /**
     * Serialize to a JSON formatted stream.
     *
     * @param writable where the resulting JSON text should be sent.
     * @throws IOException when the writable encounters an I/O error.
     */
    @Override
    public void toJson(Writer writable) throws IOException {
        try {
            writable.write(this.toJson());
        } catch (Exception ignored) {
        }
    }

    /**
     * Resets the information/status of the current game.
     */
    public void clean() {
        players.clear();
        world.clean();
        currentPlayerInd = 0;
        numActivePlayer = 0;
        gamePhase = null;
        riskHandlers.clear();
        bonusTroops = 0;
    }

    public static void main(String[] args) throws Exception {
        GameSingleton g =GameSingleton.getGameInstance();
        g.readGame(new FileInputStream("C:/Users/Anthony/Desktop/game.json"));
        //.out.println(g.toJson());
    }
}
