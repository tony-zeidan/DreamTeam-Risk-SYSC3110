package com.dreamteam.core;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * WorldMap represents the entire map of that the Game contains.
 *
 * @author Anthony Dooley
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Kyler Verge
 */
public class WorldMap {

    /**
     * The name of the world.
     */
    private String name;

    /**
     * The map containing all territories (used to implement graph).
     */
    private Map<String, Territory> allTerritories;

    /**
     * The map containing the coordinates of each region.
     */
    private Map<Territory, Point> allCoordinates;

    /**
     * A set of continents on the map.
     */
    private Map<String, Continent> continents;

    /**
     * Random variable for assigning territories in setup.
     */
    private static Random rand;
    /**
     * regex pattern for continent
     */
    private static final Pattern CONTINENT_PATTERN = Pattern.compile("((\\w+\\s?)+)\\((\\d+)\\)\\|((((\\w+\\s?)+),?)+)");
    /**
     * regex pattern for territories
     */
    private static final Pattern TERRITORY_PATTERN = Pattern.compile("((\\w+\\s?)+)\\((\\d+):(\\d+)\\)\\|((((\\w+\\s?)+),?)+)");

    /**
     * Constructor for instances of WorldMap.
     * Creates a new World with the name given (hardcoded map).
     *
     * @param name The name of the World
     */
    public WorldMap(String name) {
        this.name = name;
        rand = new Random();
        allTerritories = new HashMap<>();
        allCoordinates = new HashMap<>();
        continents = new HashMap<>();
        readMap();
    }

    /**
     * Retrieves the name of the world.
     *
     * @return The world's name
     */
    public String getName() {
        return name;
    }

    /**
     * Reads in the map from the map.txt file (for now)
     */
    public void readMap() {

        //contains a temporary list of neighbours (in the form of strings)
        //corresponding to each territory (this is a result of reading the text file)
        HashMap<Territory, String> neighbourStrings = new HashMap<>();


        //attempt to read the file
        try {
            File territoryData = new File("src/resources/map_packages/main_package/countries.txt");
            File continentData = new File("src/resources/map_packages/main_package/continents.txt");
            Scanner myReader = new Scanner(continentData);
            while (myReader.hasNextLine()) {
                readContinentLine(myReader.nextLine());
            }
            myReader.close();
            myReader = new Scanner(territoryData);
            while (myReader.hasNextLine()) {
                readTerritoryLine(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        /*
        We now have a map of all territories, but no neighbours have been added to them.
        We must loop through the string representation of all neighbours and add them to the
        corresponding territories.
         */
        for (Territory t : neighbourStrings.keySet()) {
            for (String rt : neighbourStrings.get(t).split(",")) {
                if (allTerritories.containsKey(rt)) {
                    t.addNeighbour(allTerritories.get(rt));
                }
            }
        }

        writeXML();
    }

    /**
     * reads the String line that holds the continent, the territories they hold, and bonus troops
     *
     * @param line The line to read
     */
    private void readContinentLine(String line) {
        Matcher matcher = CONTINENT_PATTERN.matcher(line);
        if (matcher.matches()) {
            String readName = matcher.group(1);
            int bonusUnits = 0;
            try {
                bonusUnits = Integer.parseInt(matcher.group(3));
            } catch (NumberFormatException e) {
                System.out.println("line incorrectly formatted");
                return;
            }
            Continent continent = new Continent(readName, bonusUnits);
            continents.put(readName, continent);
            String territoriesString = matcher.group(4);
            List<String> territoriesWithin = Arrays.asList(territoriesString.split(","));
            for (String s : territoriesWithin) {
                if (!allTerritories.containsKey(s)) {
                    allTerritories.put(s, new Territory(s));
                }
                continent.addContinentTerritory(allTerritories.get(s));
            }
        }
    }

    /**
     * reads a String of the territory text file to determine, territory and neighbours
     *
     * @param line The line to read
     */
    private void readTerritoryLine(String line) {
        Matcher matcher = TERRITORY_PATTERN.matcher(line);
        if (matcher.matches()) {
            String readName = matcher.group(1);
            if (!allTerritories.containsKey(readName)) {
                allTerritories.put(readName, new Territory(readName));
            }
            Territory territory = allTerritories.get(readName);
            int xCord = 0;
            int yCord = 0;
            try {
                xCord = Integer.parseInt(matcher.group(3));
                yCord = Integer.parseInt(matcher.group(4));


            } catch (NumberFormatException e) {
                System.out.println("line was formatted incorrectly.");
                return;
            }
            Point readCoordinates = new Point(xCord, yCord);
            allCoordinates.put(territory, readCoordinates);

            String neighString = matcher.group(5);
            List<String> neighbourList = Arrays.asList(neighString.split(","));
            for (String s : neighbourList) {
                if (!allTerritories.containsKey(s)) {
                    allTerritories.put(s, new Territory(s));
                }
                territory.addNeighbour(allTerritories.get(s));
            }
        }
    }

    /**
     * Gets the neighbouring territories that the current player owns
     * when attacking a territory.
     *
     * @param player    The player in question
     * @param territory The territory that was selected to be attacked
     * @return The map with only the neighbouring territories owned
     */
    public Map<Territory, Point> getNeighbourNodesOwned(Player player, Territory territory) {
        HashMap<Territory, Point> neighbours = new HashMap<>();
        for (Territory terr : territory.getNeighbours()) {
            if (player.ownsTerritory(terr)) {
                neighbours.put(terr, allCoordinates.get(terr));
            }
        }
        return neighbours;
    }

    /**
     * Retrieves a map of all the territories linked to their coordinates.
     *
     * @return A map collection of territories and points.
     */
    public Map<Territory, Point> getAllCoordinates() {
        return allCoordinates;
    }

    /**
     * Sets up the map, by assigning territories and populating randomly territories.
     *
     * @param players the players playing the game
     */
    public void setUp(List<Player> players) {
        assignTerritories(players);
        //place remaining troops on each of the territories
        int max = 50;
        if (players.size() != 2)
            max = -5 * players.size() + 50;
        placeTroops(players, max);
        updateContinentRulers();
    }

    /**
     * Assigns the territories to each player and puts one unit on it.
     *
     * @param players the ordered players
     */
    private void assignTerritories(List<Player> players) {
        ArrayList<Territory> allTerrs = new ArrayList<>(allTerritories.values());
        int playerInd = -1;
        while (allTerrs.size() != 0) {
            //rotate through players and randomly get a free territory
            playerInd = (playerInd + 1) % players.size();

            Territory t = allTerrs.get(rand.nextInt(allTerrs.size()));
            //set current player to territory, add a unit and remove territory from free territories
            t.setOwner(players.get(playerInd));
            t.addUnits(1);
            allTerrs.remove(t);
        }
    }

    /**
     * Randomly places the troops for each player on each territory.
     *
     * @param players The list of players in the world
     * @param max     The amount of units for each player
     */
    private void placeTroops(List<Player> players, int max) {
        for (Player player : players) {
            //numOfTroops depends on how many territories each player got, as there can be a 1 difference
            Set<Territory> playerTerritories = player.getOwnedTerritories();
            int numOfTroops = playerTerritories.size();
            while (numOfTroops != max) {
                int territoryInd = rand.nextInt(playerTerritories.size());
                int counter = 0;
                //iterate to a randomly selected territory and add one unit to it
                for (Territory terr : playerTerritories) {
                    if (counter == territoryInd) {
                        terr.addUnits(1);
                        break;
                    }
                    counter++;
                }
                numOfTroops++;
            }
        }
    }

    /**
     * returns set of continents owned by given player
     *
     * @param player to determine continents it owns
     * @return Set of continents the player owns
     */
    public Set<Continent> getRuled(Player player) {
        Set<Continent> ruled = new HashSet<>();
        for (Continent c : continents.values()) {
            if (player == c.getRuler()) {
                ruled.add(c);
            }
        }
        return ruled;
    }

    /**
     * updates the owner of the continent
     */
    public void updateContinentRulers() {
        for (Continent c : continents.values()) c.updateRuler();
    }

    /**
     * Please ignore this method for now.
     * Testing map xml generation.
     *
     * @deprecated
     */
    private void writeXML() {
        /*Document doc = new Document("world");
        Attribute docName = new Attribute("id",getName());
        int i = 1;
        for (Territory t : allCoordinates.keySet()) {
            Point p = allCoordinates.get(t);
            Element terrElem = new Element("territory");
            Attribute terrElemId = new Attribute("id",i+"");
            terrElem.addAttribute(terrElemId);
            ValuedElement terrElemName = new ValuedElement("name",t.getName());
            terrElem.addChild(terrElemName);
            ValuedElement terrElemPoint = new ValuedElement("point",p.x + "," + p.y);
            terrElem.addChild(terrElemPoint);
            ValuedElement terrElemUnits = new ValuedElement("units","1");
            terrElem.addChild(terrElemUnits);
            ValuedElement terrElemOwner = new ValuedElement("owner","null");
            terrElem.addChild(terrElemOwner);
            Element terrElemNeighbours = new Element("neighbours");
            for (Territory n : t.getNeighbours()) {
                ValuedElement neighboursElemName = new ValuedElement("name",n.getName());
                terrElemNeighbours.addChild(neighboursElemName);
            }
            terrElem.addChild(terrElemNeighbours);
            doc.addChild(terrElem);
            i ++;
        }
        System.out.println(doc.toString());*/
    }

}