package com.dreamteam.core;

import com.github.cliftonlabs.json_simple.*;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Queue;
import java.util.*;

/**
 * WorldMap represents the entire map of that the Game contains.
 *
 * @author Anthony Dooley
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Kyler Verge
 */
public class WorldMap implements Jsonable {

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
    public void readMap(InputStream is) throws Exception {
        try {
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            JsonObject parser = (JsonObject) Jsoner.deserialize(buf);
            JsonObject map = (JsonObject)parser.get("map");
            name = (String)map.get("name");
            JsonArray territories =(JsonArray)((JsonObject)map).get("territories");
            readCountries(territories);
            JsonArray continents = (JsonArray)map.get("continents");
            readContinents(continents);
        } catch (JsonException e) {
            e.printStackTrace();
        }
        if (!validMap())
        {
            System.out.println("invalid");
            throw new Exception("Please input valid map");
        }
        is.close();
    }
    private boolean validMap()
    {
        int numVisited = 0;
        Queue<Territory> territories = new LinkedList<>();
        Set<Territory> visited = new HashSet<>();
        Territory start = (new ArrayList<> (allTerritories.values())).get(0);
        territories.add(start);
        visited.add(start);
        while (!territories.isEmpty()) {
            Territory terr = territories.remove();
            for (Territory adjTerr : terr.getNeighbours()) {
                if (!visited.contains(adjTerr)) {
                    territories.add(adjTerr);
                    visited.add(adjTerr);
                }
            }
            numVisited += 1;
        }
        return (numVisited == allTerritories.values().size());
    }
    private void readCountries(JsonArray territories)
    {
        for(Object terr: territories)
        {
            String readName = (String) ((JsonObject)terr).get("name");
            if (!allTerritories.containsKey(readName)) {
                allTerritories.put(readName, new Territory(readName));
            }
            Territory territory = allTerritories.get(readName);
            try {
                String[] coord = ((String)((JsonObject)terr).get("coordinates")).split(",");
                System.out.println(coord[0]);
                System.out.println(coord[1]);
                int xCord = (int)Double.parseDouble(coord[0]);
                int yCord =(int)Double.parseDouble(coord[1]);
                Point readCoordinates = new Point(xCord, yCord);
                allCoordinates.put(territory, readCoordinates);
                List<String> neighbourList = (List<String>) ((JsonObject)terr).get("neighbours");;
                for (String s : neighbourList) {
                    if (!allTerritories.containsKey(s)) {
                        allTerritories.put(s, new Territory(s));
                    }
                    territory.addNeighbour(allTerritories.get(s));
                }

            } catch (NumberFormatException e) {
                System.out.println("line was formatted incorrectly.");
                return;
            }
        }
    }
    /**
     * reads the String line that holds the continent, the territories they hold, and bonus troops
     *
     */
    private void readContinents(JsonArray listContinents) {
        for(Object cont: listContinents) {
            String readName = (String) ((JsonObject)cont).get("name");

            try {
                int bonusUnits = Integer.parseInt((String)((JsonObject)cont).get("value"));
                Continent continent = new Continent(readName, bonusUnits);
                continents.put(readName, continent);
                List<String> territoriesWithin = (List<String>) ((JsonObject)cont).get("territories");;
                for (String s : territoriesWithin) {
                    if (!allTerritories.containsKey(s)) {
                        allTerritories.put(s, new Territory(s));
                    }
                    continent.addContinentTerritory(allTerritories.get(s));
                }
            } catch (NumberFormatException e) {
                System.out.println("line incorrectly formatted");
                return;
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
    public void setUp(List<Player> players, InputStream mapData) {
        try{
            readMap(mapData);
        } catch(Exception e)
        {
            System.out.println(e);
        }

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

    /**
     * Serialize to a JSON formatted string.
     *
     * @return a string, formatted in JSON, that represents the Jsonable.
     */
    @Override
    public String toJson() {
        JsonObject json = new JsonObject();
        json.put("name", name);
        JsonArray continentsJson = new JsonArray();
        continentsJson.addAll(continents.values());
        json.put("continents", continentsJson);
        JsonArray territoriesJson = new JsonArray();
        ArrayList<JsonObject> territoriesJsonList = new ArrayList<>();
        for (Territory terr: allTerritories.values())
        {
            JsonObject jsonTerr = new JsonObject();
            jsonTerr.put("name", terr.getName());
            jsonTerr.put("coordinates", getCoordinatesString(terr));
            jsonTerr.put("neighbours", terr.toJsonBuildMap());
            territoriesJsonList.add(jsonTerr);
        }
        territoriesJson.addAll(territoriesJsonList);
        json.put("territories",territoriesJson);
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

    }
    public String getCoordinatesString(Territory terr)
    {
        Point terrPoint = allCoordinates.get(terr);
        return terrPoint.getX() +","+terrPoint.getY();
    }
    public static void main(String[] args) throws Exception {
        WorldMap w = new WorldMap("world");
        w.readMap(new FileInputStream(new File("C:/Users/Anthony/Desktop/game.json")));
    }
}