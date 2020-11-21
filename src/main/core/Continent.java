package main.core;

import java.awt.*;
import java.util.*;

public class Continent {

    private String name;

    private Set<Territory> territories;

    public Continent(String name){
        this.name = name;
        territories = new HashSet<>();
    }

    public String getContinentName(){
        return name;
    }

    public void addContinentTerritory(Territory territory){
        territories.add(territory);
    }

    public void removeContinentTerritory(Territory territory){
        territories.remove(territory);
    }
}
