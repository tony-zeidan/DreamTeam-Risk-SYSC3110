package main.view;

import main.core.Player;
import main.core.Territory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RiskEventPane extends JPanel {

    private DefaultTableModel infoModel;
    private DefaultListModel<String> eventModel;
    private JTextArea instructionsText;

    public RiskEventPane() {
        super(new GridLayout(3,1));

        JPanel top = new JPanel(new BorderLayout());
        JPanel middle = new JPanel(new BorderLayout());
        JPanel bottom = new JPanel(new BorderLayout());

        infoModel = new DefaultTableModel();
        eventModel = new DefaultListModel<>();

        JTable infoTable = new JTable(infoModel);
        infoModel.addColumn("Info");
        infoModel.addColumn("Value");
        top.add(BorderLayout.NORTH,new JLabel("Selected Territory Information"));
        top.add(BorderLayout.CENTER,infoTable);

        JList eventList = new JList(eventModel);
        middle.add(BorderLayout.NORTH,new JLabel("In-Game Events"));
        JScrollPane gameEventScroller = new JScrollPane(eventList);
        gameEventScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        gameEventScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        middle.add(BorderLayout.CENTER,gameEventScroller);

        instructionsText = new JTextArea();
        instructionsText.setEditable(false);
        instructionsText.setLineWrap(true);
        instructionsText.setWrapStyleWord(true);
        bottom.add(BorderLayout.NORTH,new JLabel("In-Game Instructor"));
        bottom.add(BorderLayout.CENTER,instructionsText);

        Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        top.setBorder(raisedEtched);
        middle.setBorder(raisedEtched);
        bottom.setBorder(raisedEtched);
        this.add(top);
        this.add(middle);
        this.add(bottom);
        //set event pane size
        setPreferredSize(new Dimension(200,800));
    }

    public void setInfoDisplay(Player player, Territory territory) {
        infoModel.addRow(new Object[]{"Name",territory.getName()});
        infoModel.addRow(new Object[]{"Owner",player.getName()});
        infoModel.addRow(new String[]{"Colour", player.getColour().getName()});
        infoModel.addRow(new Object[]{"Units",territory.getUnits()});
    }

    public void addEvent(String event) {
        eventModel.addElement(event);
    }

    public void setCurrentInstruction(String instruction) {
        instructionsText.setText(instruction);
    }
    public void clearSelectedTerritoryDisplay()
    {
        if (infoModel.getRowCount() > 0) {
            for (int i = infoModel.getRowCount() - 1; i > -1; i--) {
                infoModel.removeRow(i);
            }
        }
    }
}
