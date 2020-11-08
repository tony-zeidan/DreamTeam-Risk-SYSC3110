package main.core;

import jdk.jfr.ValueDescriptor;
import org.w3c.dom.Attr;

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
    private Map<String, Territory> allTerritories;

    /**
     * The map containing the coordinates of each region.
     */
    private Map<Territory,Point> allCoordinates;

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

        //use a regex pattern to recognize each portion our map syntax in each line of the file
        Pattern valid = Pattern.compile("((\\w+\\s?)+)\\(((\\d+):(\\d+))\\)((,?((\\w+)\\s?)+)+)");

        //attempt to read the file
        try {
            File myObj = new File("src/resources/map_packages/main_package/map.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                Matcher matcher = valid.matcher(data);

                String readName = "";

                //check if the line in the file matches our map syntax (pattern)
                if (matcher.matches()) {

                    //parse the information from the different groups of the line
                    readName = matcher.group(1);
                    int xCord = Integer.parseInt(matcher.group(4));
                    int yCord = Integer.parseInt(matcher.group(5));
                    Point readCoordinates = new Point(xCord, yCord);
                    String readNeighbours = matcher.group(6).substring(1);  //substring gets rid of first comma
                    Territory readTerritory = new Territory(readName);

                    //System.out.println(matcher.group(1));

                    //put the information in the correct spots
                    allTerritories.put(readName, readTerritory);
                    allCoordinates.put(readTerritory, readCoordinates);
                    neighbourStrings.put(readTerritory, readNeighbours);
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
                    t.addNeighbour(allTerritories.get(rt));
                }
            }
        }

        writeXML();
    }

    public void removePlayerOwned(Player player,Territory territory) {
        if (player==null) return;
        player.removeTerritory(territory);
    }

    /**
     * Gets the neighbouring territories that the current player owns
     * when attacking a territory
     *
     * @param player The player in question
     * @param territory The territory that was selected to be attacked
     * @return The map with only the neighbouring territories owned
     */
    public Map<Territory,Point> getNeighbourNodesOwned(Player player, Territory territory) {
        HashMap<Territory,Point> neighbours = new HashMap<>();
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
    public Map<Territory,Point> getAllCoordinates() {
        return allCoordinates;
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
            t.setOwner(players.get(playerInd));
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