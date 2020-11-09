package main.view;

import main.core.GameSingleton;
import main.core.Player;
import main.core.Territory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RiskEventPane extends JPanel implements RiskGameHandler {

    private static final int EVENT_HISTORY_CAPACITY = 25;
    public static final String DEFAULT_INSTRUCTION = "Please select a territory or end your turn.";

    private DefaultTableModel infoModel;
    private DefaultListModel<String> eventModel;
    private JTextArea instructionsText;
    private JList eventList;
    private JTable infoTable;
    private JScrollPane gameEventScroller;

    public RiskEventPane() {
        super(new GridLayout(3, 1));

        JPanel top = new JPanel(new BorderLayout());
        JPanel middle = new JPanel(new BorderLayout());
        JPanel bottom = new JPanel(new BorderLayout());

        infoModel = new DefaultTableModel();
        eventModel = new DefaultListModel<>();

        infoTable = new JTable(infoModel);
        infoModel.addColumn("Info");
        infoModel.addColumn("Value");
        top.add(BorderLayout.NORTH, new JLabel("Selected Territory Information"));
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
     * Shows the current territory selected
     * @param player
     * @param territory
     */
    public void setInfoDisplay(Player player, Territory territory) {
        infoModel.addRow(new Object[]{"Name", territory.getName()});
        infoModel.addRow(new Object[]{"Owner", player.getName()});
        infoModel.addRow(new String[]{"Colour", player.getColour().getName()});
        infoModel.addRow(new Object[]{"Units", territory.getUnits()});
    }

    /**
     * Adds the event description to the DefaultListModel
     * @param event
     */
    public void addEvent(String event) {
        eventModel.addElement(event);
    }

    /**
     * Sets the instruction for the user
     * @param instruction
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
     * @param e
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
                System.out.println("Displayed");
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
