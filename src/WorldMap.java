import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * WorldMap represents the entire map of that the Game contains.
 *
 * @author Anthony Dooley
 */
public class WorldMap {
    private HashMap<String, Territory> territories;
    private Random rand;
    public WorldMap()
    {
        rand = new Random();
        territories = new HashMap<>();
        createTerritories();
    }

    /**
     * instantiate territories and make nieghbours
     */
    private void createTerritories()
    {
        //territories and neighbours are hardcoded, but will be done later with reading a xml
        ArrayList<Territory>terrTemp = new ArrayList<>();
        String territoryNames ="Alaska,Northwest Territory,Greenland,Western Canada,Central Canada,Eastern Canada,Western United States,Eastern United States,Central America,Venezuela,Peru,Brazil,Argentina,North Africa,Egypt,East Africa,Congo,South Africa,Madagascar,Iceland,Scandinavia,Ukraine,Great Britain,Northern Europe,Southern Europe,Western Europe,Indonesia,New Guinea,Western Australia,Eastern Australia,Siam,India,China,Mongolia,Japan,Irkutsk,Yakutsk,Kamchatka,Siberia,Afghanistan,Ural,Middle East";
        String nieghbours = "Kamchatka,Northwest Territory,Western Canada\n" + "Greenland,Central Canada,Alaska,Western Canada\n" + "Central Canada,Eastern Canada,Northwest Territory,Iceland\n" +
               "Central Canada,Northwest Territory,Alaska,Western United States\n" + "Western Canada,Eastern Canada,Northwest Territory,Greenland,Western United States,Eastern United States\n" +
               "Greenland,Central Canada,Eastern United States\n" + "Western Canada,Central Canada,Eastern United States,Central America\n" + "Central Canada,Western United States,Eastern Canada,Central America\n" +
               "Western United States,Eastern United States,Venezuela\n" + "Central America,Brazil,Peru\n" + "Venezuela,Brazil,Argentina\n"+
               "Venezuela,Peru,Argentina\n" + "Peru,Brazil\n" + "Brazil,Western Europe,Southern Europe,Egypt,Congo,East Africa\n" + "Southern Europe,North Africa,East Africa,Middle East\n" +
               "Egypt,Middle East,North Africa,Congo,South Africa,Madagascar\n" + "North Africa,East Africa,South Africa\n" + "Madagascar,Congo,East Africa\n" +
               "East Africa,South Africa\n" + "Greenland,Scandinavia,Great Britain\n" + "Iceland,Ukraine,Northern Europe,Great Britain\n" + "Scandinavia,Northern Europe,Southern Europe,Middle East,Afghanistan,Ural\n" +
               "Iceland,Western Europe,Northern Europe,Scandinavia\n" + "Southern Europe,Western Europe,Great Britain,Scandinavia,Ukraine\n" + "Western Europe,Northern Europe,Ukraine,Middle East,Egypt,North Africa\n" + "Great Britain,Northern Europe,Southern Europe,North Africa\n" +
               "Siam,Western Australia,New Guinea\n" + "Indonesia,Western Australia,Eastern Australia\n" + "Indonesia,Eastern Australia,New Guinea\n" + "Western Australia,New Guinea\n" +
               "Indonesia,India,China\nMiddle East,Afghanistan,China,Siam\nSiam,India,Afghanistan,Mongolia,Ural,Siberia\n" + "Irkutsk,Japan,China,Siberia,Kamchatka\n" +
               "Kamchatka,Mongolia\nMongolia,Siberia,Kamchatka,Yakutsk\nSiberia,Yakutsk,Irkutsk\nJapan,Yakutsk,Irkutsk,Mongolia,Alaska\nUral,China,Mongolia,Irkutsk,Yakutsk\n" +
               "Ukraine,Middle East,India,China,Ural\nUkraine,Afghanistan,China,Siberia\nAfghanistan,Ukraine,India,Egypt,East Africa,Southern Europe";
        //create territories and put in territories map
       for (String name:territoryNames.split(","))
       {
           Territory t = new Territory(name);
           territories.put(name,t);
           terrTemp.add(t);
       }
       //make neighbours, format right now is \n specifies new territory and ',' a new neighbour
       int i =0;
       for(String neighs: nieghbours.split("\n"))
       {
           Territory t = terrTemp.get(i);
           for (String terr: neighs.split(","))
           {
               t.addNeighbour(territories.get(terr));
           }
           i++;
       }
    }

    /**
     * sets up the map, by assigning territories and populating randomly territories
     * @param players the players playing the game
     */
    public void setUp(List<Player> players)
    {
//        //ArrayList index specifying player, where each holds an arraylist of territories
//        //**** may need to add arraylist of territories as a parameter in player** may be better design wise
//        ArrayList<ArrayList<Territory>> playersTerritories = new ArrayList<>();
//        for(int i = 0; i<players.size();i++)
//        {
//            //an arraylist of territories for each player
//            playersTerritories.add(new ArrayList<>());
//        }

        assignTerritories(players);
        //place remaining troops on each of the territories
        int max = 50;
        if (players.size() != 2)
            max = -5*players.size() +50;
        placeTroops(players,max);
    }

    /**
     * assigns the territories to each player and puts one unit on it.
     * @param players the ordered players
     */
    private void assignTerritories(List<Player> players) {
        ArrayList<Territory> allTerritories = new ArrayList<>(territories.values());
        int playerInd = -1;
        while(allTerritories.size() != 0)
        {
            //rotate through players and randomly get a free territory
            playerInd = (playerInd +1)%players.size();

            Territory t = allTerritories.get(rand.nextInt(allTerritories.size()));
            //set current player to territory, add a unit and remove territory from free territories
            t.setOwner(players.get(playerInd));
            t.addUnits(1);
            allTerritories.remove(t);
            //add territory to each player
            players.get(playerInd).addTerritory(t);
        }
    }

    private void placeTroops(List<Player> players,int max)
    {
        for (Player player: players)
        {
            //numOfTroops depends on how many territories each player got, as there can be a 1 difference
            ArrayList<Territory> playerTerritories = player.getOwnedTerritories();
            int numOfTroops =playerTerritories.size();
            while(numOfTroops != max)
            {
                //get the player's arraylist of territories and randomly select one to then add 1 unit to
                (playerTerritories.get(rand.nextInt(playerTerritories.size()))).addUnits(1);
                numOfTroops++;
            }
        }
    }

    public Territory getTerritory(String name) {
        return territories.getOrDefault(name,null);
    }
    public ArrayList<Territory> getTerritories()
    {
        return new ArrayList<>(territories.values());
    }
    /**
     * prints the map
     */
    public void printMap()
    {
        for (Territory t: territories.values())
        {
            t.print();
        }
    }

}