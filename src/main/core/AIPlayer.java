package main.core;

import main.view.RiskEvent;
import main.view.RiskEventType;

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
    public void doAiTurn(GameSingleton model)
    {

        //placeUnits()
        Territory[] territories = territoryToAttack();
        while(territories[0] != null)
        {
            //notify
            territories = territoryToAttack();
            //model.notifyHandlers(new RiskEvent(this, RiskEventType.AI_ATTACK, territories));
            model.performBattle(territories[0],territories[1]);
        }
        //moveTroops
    }
    public void placeUnits(int numUnits){
        while(numUnits >0)
        {
            Territory terrToAddUnit = null;
            double highestUtility = -1;
            for(Territory territory: getOwnedTerritories())
            {
                double utility = placeUnitsUtilityFunction(territory);
                if (utility>highestUtility)
                {
                    highestUtility = utility;
                    terrToAddUnit = territory;
                }
            }
            terrToAddUnit.addUnits(1);
            numUnits--;
        }
    }
    public double placeUnitsUtilityFunction(Territory territory){
        //arbitrary percentages, just want to put more emphasis on troop difference
        //25 percent how many neighbouring are owned terr, if all neighbour owned make that part 0 percent
        int friendlyNeighbouringTerrs = numOwnedNeighbouringTerritories(territory);
        if(friendlyNeighbouringTerrs == territory.getNeighbours().size())
        {
            return 0;
        }
        double neighbouringPercentage = .25*(numOwnedNeighbouringTerritories(territory))/(territory.getNeighbours().size()-1);
        //75 percent difference of surrounding troops there are to its troops
        int numEnemyTroops = numNeighbouringEnemyTroops(territory);
        int numTroops = territory.getUnits();
        double lessTroopsPercentage;
        if(numTroops-numEnemyTroops > 5)
            lessTroopsPercentage = 0;
        else if(numTroops-numEnemyTroops < -5)
            lessTroopsPercentage = .75;
        else
             lessTroopsPercentage = .75*(0.5 -(numTroops-numEnemyTroops)/10.0);
        return neighbouringPercentage + lessTroopsPercentage ;
    }
    public Territory[] territoryToAttack()
    {
        Territory attacking = null;
        Territory defending = null;
        double mostUtility = 0;
        for (Territory attackingTerr: getOwnedTerritories())
        {
            for (Territory defendingTerr: attackingTerr.getNeighbours())
            {
                if (defendingTerr.getOwner() != this)
                {
                    double utility = attackUtilityFunction(attackingTerr.getUnits(),defendingTerr.getUnits());
                    if (utility > mostUtility)
                    {
                        mostUtility = utility;
                        attacking = attackingTerr;
                        defending = defendingTerr;
                    }
                }
            }
        }
        return new Territory[]{attacking, defending};
    }
    public double attackUtilityFunction(int attackers, int defenders)
    {
        int numAttackersDice = (attackers>4)?3:attackers-1;
        double utility=0;
        if (numAttackersDice==0)
            return 0;
        if (defenders == 1)
        {
            double probabilityOf1Loss= Math.pow(1,numAttackersDice)+Math.pow(2,numAttackersDice)+Math.pow(3,numAttackersDice)+Math.pow(4,numAttackersDice)+Math.pow(5,numAttackersDice)+Math.pow(6,numAttackersDice);
            double probabilityOf1Win = 1- probabilityOf1Loss;
            utility = 1*probabilityOf1Win - 1*probabilityOf1Loss;
        }
        else
        {
            if (numAttackersDice == 1)
            {
                utility = 1*.2546 - 1*74.54;
            }
            else if(numAttackersDice == 2)
            {
                utility = 2*0.2276 + 1*0.3241 - 1*0.3241- 2*0.4483;
            }
            else
            {
                utility = 2*0.3717 + 1*0.3358 - 1*0.3358- 2*0.2926;
            }
        }
        return utility;
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
}
