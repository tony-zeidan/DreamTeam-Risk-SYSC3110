package com.dreamteam.view;

/**
 * This class is an interface for the different views to update their GUI from the model.
 *
 * @author Kyler Verge
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Tony Zeidan
 */
public interface RiskGameHandler {

    /**
     * Each handler for the risk model shall implement a handling of its updates.
     *
     * @param e The event that was triggered
     */
    void handleRiskUpdate(RiskEvent e);
}
