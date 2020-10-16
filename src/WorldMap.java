import java.util.ArrayList;
import java.util.HashMap;

public class WorldMap {
    private HashMap<String, Territory> territories;
    public WorldMap()
    {
        createTerritories();
    }
    public void createTerritories()
    {
        ArrayList<Territory>terrTemp = new ArrayList<>();
       String territoryNames ="Alaska,Northwest Territory,Greenland,Western Canada,Central Canada,Eastern Canada,Western United States,Eastern United States,Central America,Venezuela,Peru,Brazil,Argentina,North Africa,Egypt,East Africa,Congo,South Africa,Madagascar,Iceland,Scandinavia,Ukraine,Great Britain,Northern Europe,Southern Europe,Western Europe,Indonesia,New Guinea,Western Australia,Eastern Australia,Siam,India,China,Mongolia,Japan,Irkutsk,Yakutsk,Kamchatka,Siberia,Afghanistan,Ural,Middle East";
       String nieghbours = "Kamchatka,Northwest Territory,Western Canada\n" + "GreenLand,Central Canada,Alaska,Western Canada\n" + "Central Canada,Eastern Canada,Northwest Territory,Iceland\n" +
               "Central Canada,Northwest Territory,Alaska,Western United States\n" + "Western Canada,Eastern Canada,Northwest Territory,Greenland,Western United States,Eastern United States\n" +
               "Greenland,Central Canada,Eastern United States\n" + "Western Canada,Central Canada,Eastern United States,Central America\n" + "Central Canada,Western United States,Eastern Canada,Central America\n" +
               "Western United States,Eastern United States,Venezuela\n" + "Central America, Brazil,Peru\n" + "Venezuela,Brazil,Argentina\n" + "North Africa\n" +
               "Venezuela,Peru,Argentina\n" + "Peru,Brazil\n" + "Brazil,Western Europe,Southern Europe,Egypt,Congo,East Africa\n" + "Southern Europe,North Africa,East Africa,Middle East\n" +
               "Egypt,Middle East,North Africa,Congo,South Africa,Madagascar\n" + "North Africa,East Africa,South Africa\n" + "Madagascar,Congo,East Africa\n" +
               "East Africa,South Africa\n" + "Greenland,Scandinavia,Great Britain\n" + "Iceland,Ukraine,Northern Europe,Great Britain\n" + "Scandinavia,Northern Europe,Southern Europe,Middle East,Afghanistan,Ural\n" +
               "Iceland,Western Europe,Northern Europe,Scandinavia\n" + "Southern Europe, Western Europe,Great Britain,Scandinavia,Ukraine\n" + "Western Europe,Northern Europe,Ukraine,Middle East,Egypt,North Africa\n" + "Great Britain,Northern Europe,Southern Europe,North Africa\n" +
               "Siam,Western Australia,New Guinea\n" + "Indonesia,Western Australia,Eastern Australia\n" + "Indonesia,Eastern Australia,New Guinea\n" + "Western Australia,New Guinea\n" +
               "Indonesia,India,China\n" + "Middle East,Afghanistan,China,Siam\n" + "Siam,India,Afghanistan,Mongolia,Ural,Siberia\n" + "Irkutsk,Japan,China,Siberia,Kamchatka\n" +
               "Kamchatka,Mongolia\n" + "Mongolia,Siberia,Kamchatka,Yakutsk\n" + "Siberia,Yakutsk,Irkutsk\n" + "Japan,Yakutsk,Irkutsk,Mongolia,Alaska\n" + "Ural,China,Mongolia,Irkutsk,Yakutsk\n" +
               "Ukraine,Middle East,India,China,Ural\n" + "Ukraine,Afghanistan,China,Siberia\n" + "Afghanistan,Ukraine,India,Egypt,East Africa,Southern Europe";

       for (String name:territoryNames.split(","))
       {
           System.out.println(name);
           territories.put(name,new Territory(name));
           terrTemp.add(new Territory(name));
       }
       int i =0;
       for(String neighs: nieghbours.split("\n"))
       {
           Territory t = terrTemp.get(i);
           for (String terr: neighs.split(","))
           {
               t.addNeighbours(new Territory(terr));
           }
           i++;
       }
    }
    public void setUp(ArrayList<Player> players)
    {

    }
    public static void main(String[] args)
    {
        WorldMap map = new WorldMap();
    }
}