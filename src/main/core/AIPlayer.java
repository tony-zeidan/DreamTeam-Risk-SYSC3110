package main.core;

import main.view.RiskEvent;
import main.view.RiskEventType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * A subclass of Player that has the AIs parameters as well as functions to calculate the
 * placing of units, attacking and movement of troops.
 *
 * @author Anthony
 * @author Eathan Chase
 * @author Kyler Verge
 * @author Tony Zeidan
 */
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

    /**
     * places the number of units specified by the model, on the territories of the AI.
     *
     * @param numUnits The number of units to be added to the territories of the AI
     * @param model The GameSingleton that contains more general game logic
     */
    public void placeUnits(int numUnits, GameSingleton model){
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
            model.moveBonus(terrToAddUnit);
            numUnits--;
        }
    }

    /**
     * A utilty function to determine at that specific call what the most optimal territory to add a troops to.
     *
     * @param territory the Territory that's utility is being determined.
     * @return double the utility of placing a unit on that territory.
     */
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

    /**
     * performs all the attacks that the AI computes as optimal, with randomness caused by dice.
     *
     * @param model The GameSingleton that contains general game logic.
     */
    public void doAttack(GameSingleton model)
    {
        Territory[] territories = territoryToAttack();
        while(territories[0] != null)
        {
            boolean won = model.performBattle(territories[0],territories[1]);
            if (won) {
                int numTroopsToMove = territories[0].getUnits()-1;
                model.moveUnits(territories[0],territories[1],numTroopsToMove);
            }
            territories = territoryToAttack();
        }
    }

    /**
     * determines the most optimal territory to attack with and where to attack to.
     * @return Territory[] where at index 0 is the attacker territory and index 1 is the defending territory. Null is returned
     * when no attack is good enough for the AI.
     */
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

    /**
     * Retrieves the utility of an amount of units attacking another amount of units.
     * @param attackers int the amount of units on the attacking territory.
     * @param defenders in the amount of units on the defending territory.
     * @return The expected utility for a specific attack
     */
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

    /**
     * performs the end of turn move for the AI.
     */
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

    /**
     * determines the best territory to move its units away from
     * @return Territory where the end of turn move will start at
     */
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

    /**
     * Determines the best territory to move the units of another territory to.
     * @param territory The territory that the end of turn starts at.
     * @return Territory that the units will be moved to.
     */
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

    /**
     * Determines the number of owned territories that neighbour that territory
     * @param territory the Territory that is finding out the number of owned neighbours
     * @return int of the number of neighbouring territories it owns.
     */
    private int numOwnedNeighbouringTerritories(Territory territory)
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

    /**
     * Determines if the territory owns all the territories it neighbours
     * @param territory the Territory finding out if it owns all its neighbouring territories
     * @return boolean if it owns all the neighbouring territories returns true, else false.
     */
    private boolean ownsAllNeighbouringTerritories(Territory territory)
    {
        return(numOwnedNeighbouringTerritories(territory) == territory.getNeighbours().size());
    }

    /**
     * determines the number of enemy troops that border the territory.
     * @param territory The territory that finds the number of enemy units bordering it.
     * @return int the number of enemy troops bordering it.
     */
    private int numNeighbouringEnemyTroops(Territory territory)
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
