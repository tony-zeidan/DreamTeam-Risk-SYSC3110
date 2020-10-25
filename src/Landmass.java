import java.util.ArrayList;
import java.util.List;

/**
 * Landmass represents a region in which smaller territories are contained (ex. Continents)
 *
 * @author Tony Zeidan
 * @deprecated
 */
public class Landmass {

    /**
     * Name of the region that this instance represents.
     */
    private String name;

    /**
     * List of territories within the region that this instance represents.
     */
    private List<Territory> territories;

    /**
     * Constructor for instances of Landmass. Creates a new (empty) region with
     * the given name.
     *
     * @param name The name of the region.
     */
    public Landmass(String name) {
        this.name=name;
        territories = new ArrayList<>();
    }

    /**
     *  (For future use)
     *  Determines whether a single Player owns all territories within the region.
     *  (For bonus unit placement)
     *
     * @return The player who owns all territories (or null)
     */
    public Player getConqueror() {
        Player conqueror = territories.get(0).getOwner();
        for (int i = 1; i < territories.size(); i++) {
            if (territories.get(i).getOwner()!=territories.get(i-1).getOwner()) return null;
        }
        return conqueror;
    }

    public String toString() {
        return String.format("Landmass of %s containing %s territories",name,territories.size());
    }

    /**
     * Have a pretty version of the Landmass and its contained territories printed.
     *
     * @param tabs A string representing tabulation (\t)
     */
    public void print(String tabs) {
        System.out.println(tabs + "Landmass of " + name + ": ");
        for (Territory t : territories) {
            t.print(tabs+"\t");
        }
    }
}
