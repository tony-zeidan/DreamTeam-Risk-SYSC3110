package com.dreamteam.view;

import com.dreamteam.core.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * This class is the JPanel that displays the event descriptions and is a part of the RiskFrame. RiskEventPane is
 * also a com.dreamteam.view that updates the event descriptions after certain events are done in the model.
 *
 * @author Kyler Verge
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Tony Zeidan
 */
public class RiskEventPane extends JPanel implements RiskGameHandler {

    /**
     * The constant for how many events can be displayed at a given time.
     */
    private static final int EVENT_HISTORY_CAPACITY = 25;
    /**
     * The constant for the default instruction to be displayed.
     */
    public static final String DEFAULT_INSTRUCTION = "Please select a territory or end your turn.";
    /**
     * Model for the selected territory info display.
     */
    private DefaultTableModel infoModel;
    /**
     * Model for the events occurring in-game.
     */
    private DefaultListModel<String> eventModel;
    /**
     * Text field for the instructions display.
     */
    private JTextArea instructionsText;
    /**
     * Actual list component for in-game events.
     */
    private JList eventList;
    /**
     * Actual table component for selected territory display.
     */
    private JTable infoTable;
    /**
     * Actual component for scrolling in the selected territory display component.
     */
    private JScrollPane gameEventScroller;
    /**
     * Actual component for displaying selected territory.
     */
    private JLabel selectedTerritoryInformation;

    /**
     * Creates a Jpanel with the events descriptions and adds it as a com.dreamteam.view in the model
     * to update model events.
     */
    public RiskEventPane() {
        super(new GridLayout(3, 1));

        JPanel top = new JPanel(new BorderLayout());
        JPanel middle = new JPanel(new BorderLayout());
        JPanel bottom = new JPanel(new BorderLayout());
        selectedTerritoryInformation = new JLabel("Selected Territory Info");

        infoModel = new DefaultTableModel();
        eventModel = new DefaultListModel<>();

        infoTable = new JTable(infoModel);
        infoModel.addColumn("Info");
        infoModel.addColumn("Value");
        top.add(BorderLayout.NORTH, selectedTerritoryInformation);
        top.add(BorderLayout.CENTER, infoTable);

        eventList = new JList(eventModel);
        eventList.setVisibleRowCount(0);
        middle.add(BorderLayout.NORTH, new JLabel("In-Game Events"));
        gameEventScroller = new JScrollPane(eventList);
        gameEventScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        gameEventScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        middle.add(BorderLayout.CENTER, gameEventScroller);

        instructionsText = new JTextArea();
        instructionsText.setEditable(false);
        instructionsText.setLineWrap(true);
        instructionsText.setWrapStyleWord(true);
        bottom.add(BorderLayout.NORTH, new JLabel("In-Game Instructor"));
        bottom.add(BorderLayout.CENTER, instructionsText);

        Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        top.setBorder(raisedEtched);
        middle.setBorder(raisedEtched);
        bottom.setBorder(raisedEtched);
        this.add(top);
        this.add(middle);
        this.add(bottom);
        //set event pane size
        setPreferredSize(new Dimension(200, 800));
    }

    /**
     * Shows the current territory selected.
     *
     * @param player    The player this territory belongs to
     * @param territory The territory that is being displayed
     */
    public void setInfoDisplay(Player player, Territory territory) {
        //law of demeter not broken here
        infoModel.addRow(new Object[]{"Name", territory.getName()});
        infoModel.addRow(new Object[]{"Owner", player.getName()});
        infoModel.addRow(new String[]{"Colour", player.getColour().getName()});
        infoModel.addRow(new Object[]{"Units", territory.getUnits()});
        selectedTerritoryInformation.setIcon(player.getAvatar());
    }

    /**
     * Adds the event description to the DefaultListModel.
     *
     * @param event The string representation of the event to display
     */
    public void addEvent(String event) {
        eventModel.addElement(event);
        //Keeps scrollPane always scrolled to the bottom
        this.validate();
        JScrollBar vertical = gameEventScroller.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum() + 1);
    }

    /**
     * Sets the instruction for the user.
     *
     * @param instruction The string representation of the instruction to display
     */
    public void setCurrentInstruction(String instruction) {
        instructionsText.setText(instruction);
    }

    /**
     * clears the selected territory
     */
    public void clearSelectedTerritoryDisplay() {
        if (infoModel.getRowCount() > 0) {
            for (int i = infoModel.getRowCount() - 1; i > -1; i--) {
                infoModel.removeRow(i);
            }
        }
    }

    /**
     * highlights the newest event description
     */
    private void setEventHighlight() {
        eventList.setSelectedIndex(eventModel.getSize() - 1);
    }

    /**
     * clears event description list if greater EVENT_HISTORY_CAPACITY
     */
    private void checkCapacityExceeded() {
        if (eventModel.getSize() > EVENT_HISTORY_CAPACITY) eventModel.clear();
    }

    /**
     * handles event updates from the model, when certain events are triggered.
     *
     * @param e The event that was triggered
     */
    @Override
    public void handleRiskUpdate(RiskEvent e) {
        GameSingleton model = (GameSingleton) e.getSource();
        RiskEventType type = e.getType();
        Object[] info = e.getEventInfo();

        checkCapacityExceeded();
        switch (type) {
            case GAME_BEGAN:
                addEvent("The game has began! Welcome to the world of " + info[0] + "!");
                break;
            case TURN_BEGAN:
                Player beganPlayer = (Player) info[0];
                addEvent(String.format("%s's turn has began", beganPlayer.getName()));
                setCurrentInstruction(beganPlayer.getName() + ", please select a territory or end your turn.");
                break;
            case TURN_ENDED:
                clearSelectedTerritoryDisplay();
                Player endedPlayer = (Player) info[0];
                addEvent(String.format("%s's turn has ended", endedPlayer.getName()));
                break;
            case ATTACK_COMMENCED:
                Player attacker = (Player) info[0];
                Player defender = (Player) info[1];
                addEvent(String.format("A battle has broken out between %s and %s!", attacker.getName(), defender.getName()));
                break;
            case ATTACK_COMPLETED:
                attacker = (Player) info[0];
                defender = (Player) info[1];
                addEvent(String.format("The battle has ended between %s and %s!", attacker.getName(), defender.getName()));
                setCurrentInstruction(attacker.getName() + ", please select a territory or end your turn.");
                System.out.println("Displayed");
                break;
            case DIE_ROLLED:
                int[] die = (int[]) info[0];
                String rolled = "Rolled: ";
                for (int roll : die) {
                    rolled += roll + ",";
                }
                addEvent(rolled.substring(0, rolled.length() - 1));
                break;
            case TERRITORY_DOMINATED:
                attacker = (Player) info[0];
                defender = (Player) info[1];
                addEvent(String.format("%s dominated %s in battle!", attacker.getName(), defender.getName()));
                break;
            case TERRITORY_DEFENDED:
                attacker = (Player) info[0];
                defender = (Player) info[1];
                addEvent(String.format("%s defended his territory against %s!", attacker.getName(), defender.getName()));
                break;
            case UNITS_MOVED:
                Territory initialT = (Territory) info[0];
                Territory finalT = (Territory) info[1];
                int num = (int) info[2];
                addEvent(String.format("%s units have been moved from %s to %s!", num, initialT.getName(), finalT.getName()));
                break;
            case GAME_OVER:
                gameEventScroller.setEnabled(false);
                instructionsText.setEnabled(false);
                infoTable.setEnabled(false);
                eventList.setEnabled(false);
                break;
        }

        setEventHighlight();
    }
}
