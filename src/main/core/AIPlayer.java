package main.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class AIPlayer extends Player {

    /**
     * Constructor for instances of Player class with name.
     *
     * @param name The name of the player
     */
    public AIPlayer(String name) {
        super(name);
    }

    /**
     * Constructor for instances of main.core.Player class with name and colour.
     *
     * @param name   The name of the player
     * @param colour The colour of the units that the player owns
     */
    public AIPlayer(String name, RiskColour colour) {
        super(name, colour);
    }
    public void doAiTurn()
    {
        //place units, no human input needed.
        //attacking human input might be needed
        //placeUnits
        //while(percent)
            //territoryToAttack
        //moveTroops
    }
    public void placeUnits(int numUnits){

    }
    public double placeUnitsUtilityFunction(Territory territory){
        //40 percent how many neighbouring are owned terr, if all neighbour owned make that part 0 percent
        //60 percent how little troops there are
        return 0;
    }
    public Territory territoryToAttack()
    {
        return null;
    }
    public void attackUtilityFunction()
    {

    }
    public void moveTroops()
    {
        //territoryMovingUnitsAway null if no owned territory with no neighbouring enemy territories, and units more than 1
        Territory terrUnitsMoveAwayFrom = territoryMovingUnitsAway();
        if (terrUnitsMoveAwayFrom != null)
        {
            Territory terrUnitsMoveTo = territoryMovingUnitsTo(terrUnitsMoveAwayFrom);
            int unitsToMove = terrUnitsMoveAwayFrom.getUnits() -1;
            terrUnitsMoveAwayFrom.removeUnits(unitsToMove);
            terrUnitsMoveTo.addUnits(unitsToMove);
        }
    }
    public Territory territoryMovingUnitsAway()
    {
        //no neighbouring enemy territories
        //most units, must have more than 1 unit to move unit(s)
        int mostTroops = 1;
        Territory terrUnitsMoveAwayFrom = null;
        Set<Territory> territories = getOwnedTerritories();
        for(Territory territory:getOwnedTerritories())
        {
            if (ownsAllNeighbouringTerritories(territory))
            {
                int terrTroops = territory.getUnits();
                if (terrTroops > mostTroops)
                {
                    mostTroops = terrTroops;
                    terrUnitsMoveAwayFrom = territory;
                }
            }
        }
        return terrUnitsMoveAwayFrom;
    }
    public Territory territoryMovingUnitsTo(Territory territory)
    {
        //pick the territory with the most enemy units
        //firstbreadth search
        int mostEnemyTroops = 0;
        Territory terrUnitsMoveTo = territory;
        Queue<Territory> territories = new LinkedList<>();
        territories.add(territory);
        while (!territories.isEmpty())
        {
            Territory terr = territories.remove();
            for(Territory adjTerr: terr.getNeighbours())
            {
                if (adjTerr.getOwner() == this && adjTerr.getVisited() != true)
                {
                    territories.add(adjTerr);
                }
            }
            terr.setVisited(true);
            int numEnemyTroops = numNeighbouringEnemyTroops(terr);
            if (numEnemyTroops > mostEnemyTroops)
            {
                 mostEnemyTroops = numEnemyTroops;
                 terrUnitsMoveTo = terr;
            }
        }
        for(Territory terr:this.getOwnedTerritories())
        {
            terr.setVisited(false);
        }
        return terrUnitsMoveTo;
    }
    public int numOwnedNeighbouringTerritories(Territory territory)
    {
        int counter = 0;
        for (Territory neighbourTerr:territory.getNeighbours())
        {
            if (this == neighbourTerr.getOwner())
            {
                counter++;
            }
        }
        return counter;
    }
    public boolean ownsAllNeighbouringTerritories(Territory territory)
    {
        return(numOwnedNeighbouringTerritories(territory) == territory.getNeighbours().size());
    }
    public int numNeighbouringEnemyTroops(Territory territory)
    {
        int numUnits =0;
        for(Territory terr: territory.getNeighbours())
        {
            if(terr.getOwner() != this)
            {
                numUnits+= terr.getUnits();
            }
        }
        return numUnits;
    }
    public static void main(String[] args)
    {

    }
}
