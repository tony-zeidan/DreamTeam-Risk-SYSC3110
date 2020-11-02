package main.core;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * main.core.WorldMap represents the entire map of that the main.core.Game contains.
 *
 * @author Anthony Dooley
 * @author Tony Zeidan
 */
public class WorldMap {

    /**
     * The name of the world.
     */
    private String name;

    /**
     * Container for players playing on the map.
     */
    private List<Player> players;

    /**
     * The map containing all territories (used to implement graph).
     */
    private HashMap<String, Territory> allTerritories;

    /**
     * The map containing the coordinates of each region.
     */
    private HashMap<Territory,Point> allCoordinates;

    private List<Territory[]> mapEdgeList;

    /**
     * Random variable for assigning territories in setup.
     */
    private static Random rand;

    /**'
     * Constructor for instances of main.core.WorldMap.
     * Creates a new World with the name given (hardcoded map).
     *
     * @param name The name of the World
     */
    public WorldMap(String name)
    {
        this.name = name;
        players = null;
        rand = new Random();
        this.players = players;
        allTerritories = new HashMap<>();
        allCoordinates = new HashMap<>();
        mapEdgeList = new ArrayList<>(); //test edge list
        readMap();
    }

    /**
     * Testing constructor for main.core.WorldMap.
     * {@link Game#Game(String)}
     */
    public WorldMap()
    {
        name = "TEST: Solar System";
        rand = new Random();
        Player p1 = new Player("Tony",Color.RED);
        Player p2 = new Player("Fred",Color.BLUE);
        allTerritories = new HashMap<>();
        allCoordinates = new HashMap<>();
        Territory mars = new Territory("Mars");
        Territory earth = new Territory("Earth");
        addTerritoryNeighbour(mars,earth);
        addPlayerOwned(p1,mars);
        addPlayerOwned(p2,mars);
        allTerritories.put("Earth",earth);
        allTerritories.put("Mars",mars);
        allCoordinates.put(earth,new Point(60,60));
        allCoordinates.put(mars,new Point(100,100));
        mars.setUnits(10);
        earth.setUnits(1);
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
        HashMap<Territory,String> neighbourStrings = new HashMap<>();

        //use a regex pattern to recognize each portion our map syntax in each line of the file
        Pattern valid = Pattern.compile("((\\w+\\s?)+)\\(((\\d+):(\\d+))\\)((,?((\\w+)\\s?)+)+)");

        //attempt to read the file
        try {
            File myObj = new File("src/resources/map.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                Matcher matcher = valid.matcher(data);

                String readName = "";

                //check if the line in the file matches our map syntax (pattern)
                if (matcher.matches()) {

                    //parse the information from the different groups of the line
                    readName = matcher.group(1);
                    int xCoord = Integer.parseInt(matcher.group(4));
                    int yCoord = Integer.parseInt(matcher.group(5));
                    Point readCoordinates = new Point(xCoord,yCoord);
                    String readNeighbours = matcher.group(6).substring(1);  //substring gets rid of first comma
                    Territory readTerritory = new Territory(readName);

                    //System.out.println(matcher.group(1));

                    //put the information in the correct spots
                    allTerritories.put(readName,readTerritory);
                    allCoordinates.put(readTerritory,readCoordinates);
                    neighbourStrings.put(readTerritory,readNeighbours);
                }
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
                    addTerritoryNeighbour(t,allTerritories.get(rt));
                }
            }
        }
    }

    public boolean areNeighbours(Territory first, Territory second) {
        for (Territory[] t : mapEdgeList) {
            if (t[0].equals(first)&&t[1].equals(second)) return true;
            if (t[0].equals(second)&&t[1].equals(first)) return true;
        }
        return false;
    }

    public void addPlayerOwned(Player player,Territory territory) {
        Player previousOwner = getTerritoryOwner(territory);
        removePlayerOwned(previousOwner,territory);
        player.addTerritory(territory);
    }

    public void removePlayerOwned(Player player,Territory territory) {
        if (player==null) return;
        player.removeTerritory(territory);
    }

    public void addTerritoryNeighbour(Territory first, Territory second) {
        if (areNeighbours(first,second)) return;
        mapEdgeList.add(new Territory[]{first,second});
    }

    public boolean ownsAllTerritoryNeighbours(Player player,Territory territory) {
        for (Territory t : getTerritoryNeighbours(territory)) {
            if (!player.ownsTerritory(t)) return false;
        }
        return true;
    }

    public List<Territory> getTerritoryNeighbours(Territory territory) {
        ArrayList<Territory> neighbours = new ArrayList<>();
        for (Territory[] t : mapEdgeList) {
            if (t[0].equals(territory)) {
                neighbours.add(t[1]);
            } else if (t[1].equals(territory)) {
                neighbours.add(t[0]);
            }
        }
        return neighbours;
    }

    /**
     * Retrieves a map of all the territories linked to their coordinates.
     *
     * @return A map collection of territories and points.
     */
    public Map<Territory,Point> getAllCoordinates() {
        return allCoordinates;
    }

    public void printTerritoryNeighbours(Territory territory) {
        for (Territory t : getTerritoryNeighbours(territory)) {
            System.out.print(String.format("%s (%s,Units: %s), ",t.getName(),getTerritoryOwner(t).getColour(),t.getUnits()));
        }
    }

    public Player getTerritoryOwner(Territory territory) {
        for (Player p : players) {
            if (p.ownsTerritory(territory)) return p;
        }
        return null;
    }

    /**
     * Sets up the map, by assigning territories and populating randomly territories.
     *
     * @param players the players playing the game
     */
    public void setUp(List<Player> players)
    {
        this.players=players;
        assignTerritories(players);
        //place remaining troops on each of the territories
        int max = 50;
        if (players.size() != 2)
            max = -5*players.size() +50;
        placeTroops(players,max);
    }

    /**
     * Assigns the territories to each player and puts one unit on it.
     *
     * @param players the ordered players
     */
    private void assignTerritories(List<Player> players) {
        ArrayList<Territory> allTerrs = new ArrayList<>(allTerritories.values());
        int playerInd = -1;
        while(allTerrs.size() != 0)
        {
            //rotate through players and randomly get a free territory
            playerInd = (playerInd +1)%players.size();

            Territory t = allTerrs.get(rand.nextInt(allTerrs.size()));
            //set current player to territory, add a unit and remove territory from free territories
            addPlayerOwned(players.get(playerInd),t);
            t.addUnits(1);
            allTerrs.remove(t);
        }
    }

    /**
     * Randomly places the troops for each player on each territory.
     *
     * @param players The list of players in the world
     * @param max The amount of units for each player
     */
    private void placeTroops(List<Player> players,int max)
    {
        for (Player player: players)
        {
            //numOfTroops depends on how many territories each player got, as there can be a 1 difference
            List<Territory> playerTerritories = player.getOwnedTerritories();
            int numOfTroops =playerTerritories.size();
            while(numOfTroops != max)
            {
                //get the player's arraylist of territories and randomly select one to then add 1 unit to
                (playerTerritories.get(rand.nextInt(playerTerritories.size()))).addUnits(1);
                numOfTroops++;
            }
        }
    }

    /**
     * Retrieves the territory with the given name (or null if nonexistent).
     *
     * @param name The name of the territory
     * @return The territory with the name
     */
    public Territory getTerritory(String name) {
        return allTerritories.getOrDefault(name,null);
    }

    /**
     * Determines if two territories are neighbours.
     */


    /**
     * Retrieves a list of territories within the map.
     *
     * @return A complete list of territories
     */
    public List<Territory> getTerritories() {
        return new ArrayList<>(allTerritories.values());
    }

    /**
     * Prints the map (through delegation of printing).
     */
    public void printMap() {
        for (Territory t: allTerritories.values()) {
            t.print();
        }
    }

    public static void main(String[] args) {
        WorldMap w1 = new WorldMap("test read");
        w1.readMap();
        w1.printMap();
    }

}