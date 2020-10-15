import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class Game implements the main functionality of the RISK game.
 *
 * @author Tony Zeidan
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Kyler Verge
 *
 * @since 1.00
 * @version 1.00
 */
public class Game {

    private List<Player> players;
    private WorldMap map;

    public Game() {
        players = new ArrayList<>(6);

        //player configs...
        map.setUp(players);

        //fix mapping
        map = null;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public static void main(String[] args) {
        System.out.println("Works");
    }

}
