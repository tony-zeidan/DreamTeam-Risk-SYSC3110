package main.core;

import java.awt.*;
import java.util.*;

public class Continent {

    private String name;

    private int bonusAmount;

    private Set<Territory> territories;

    public Continent(String name, int bonusAmount){
        this.name = name;
        this.bonusAmount = bonusAmount;
        territories = new HashSet<>();
    }

    public String getContinentName(){
        return name;
    }

    public int getBonusRulerAmount(){
        return bonusAmount;
    }

    public void addContinentTerritory(Territory territory){
        territories.add(territory);
    }

    public void removeContinentTerritory(Territory territory){
        territories.remove(territory);
    }

    public Player getRuler(){
        Iterator<Territory> it = territories.iterator();
        Player firstOwner = it.next().getOwner();
        while(it.hasNext()){
            Player nextOwner = it.next().getOwner();
            if(firstOwner != nextOwner){ return null;}
        }
        return firstOwner;
    }

}
