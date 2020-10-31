import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class JFortifyInputDialog extends JDialog implements ChangeListener {

    /**
     * The initial amount of units stored in the moving territory.
     */
    private int initialUnits1;

    /**
     * The initial amount of units stored in the destination territory.
     */
    private int initialUnits2;

    /**
     * Label for containing the text of units in moving territory (after move).
     */
    private JLabel territoryUnits1;

    /**
     * Label for containing the text of units in destination territory (after move).
     */
    private JLabel territoryUnits2;

    /**
     * Text field for shortcutting slider.
     */
    private JTextField shortcutUnits;

    /**
     * Slider model for the JSlider representing moved units.
     */
    private BoundedRangeModel sliderModel;

    /**
     * Constructor for instances of JFortifyInputDialog.
     * Creates a new dialog corresponding to the two given territories.
     *
     * @param frame The parent frame
     * @param moving The territory that is moving
     * @param destination The territory that will be receiving units
     * @param minMove The minimum amount of units the player can move
     */
    public JFortifyInputDialog(JFrame frame, Territory moving, Territory destination, int minMove) {
        super(frame, String.format("FORTIFY: %s moving to %s",moving.getName(),destination.getName()));

        //initialize other fields
        initialUnits1 = moving.getUnits();
        initialUnits2 = destination.getUnits();
        territoryUnits1 = new JLabel(String.valueOf(initialUnits1-minMove),SwingConstants.CENTER);
        territoryUnits2 = new JLabel(String.valueOf(initialUnits2),SwingConstants.CENTER);

        //initialize slider model
        sliderModel = new DefaultBoundedRangeModel(minMove,1,minMove,initialUnits1);
        sliderModel.addChangeListener(this);

        //initialize and set preferences for slider
        JSlider unitsSlider = new JSlider(sliderModel);
        unitsSlider.setMajorTickSpacing(findMajorTick(minMove,initialUnits1));
        unitsSlider.setPaintTicks(true);
        unitsSlider.setPaintLabels(true);

        //initialize shortcut text field
        shortcutUnits = new JTextField(minMove);
        shortcutUnits.setToolTipText("input number of units to move");
        shortcutUnits.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int num = Integer.parseInt(shortcutUnits.getText());
                    if (num<minMove) {
                        sliderModel.setValue(minMove);
                    } else if (num>initialUnits1) {
                        sliderModel.setValue(initialUnits1);
                    } else {
                        sliderModel.setValue(num);
                    }
                } catch (NumberFormatException n) {
                }
            }
        });

        /*
        Middle panel of the border layout.
        This panel contains:
            1) a label at the top "moving:"
            2) a JSlider concerning the amount of units to move
            3) a button at the bottom "Move Units"
         */
                JPanel middlePanel = new JPanel(new BorderLayout());
        JButton move = new JButton("Move Units");
        move.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();    //TODO: this should communicate with the controller
            }
        });
        middlePanel.add(BorderLayout.NORTH,new JLabel("moving:",SwingConstants.CENTER));
        middlePanel.add(BorderLayout.CENTER,unitsSlider);
        middlePanel.add(BorderLayout.SOUTH,move);

        /*
        Left panel of the border layout.
        This panel contains:
            1) a label containing the name of the first territory
            2) a label containing how many units were in the territory before this move
            3) a label containing the amount of units will be left after the move
         */
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(BorderLayout.NORTH,new JLabel(moving.getName()+"'s Units: "));
        JPanel leftSubPanel = new JPanel(new GridLayout(4,1));
        leftSubPanel.add(new JLabel("    before move: "));
        leftSubPanel.add(new JLabel(String.valueOf(initialUnits1),SwingConstants.CENTER));
        leftSubPanel.add(new JLabel("    after move: "));
        leftSubPanel.add(territoryUnits1);
        leftPanel.add(BorderLayout.CENTER,leftSubPanel);
        leftPanel.add(BorderLayout.SOUTH,shortcutUnits);

        /*
        Right panel of the border layout.
        This panel contains:
            1) a label containing the name of the second territory
            2) a label containing how many units were in the territory before this move
            3) a label containing the amount of units will be left after the move
         */
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(BorderLayout.NORTH,new JLabel(destination.getName()+"'s Units: "));
        JPanel rightSubPanel = new JPanel(new GridLayout(4,1));
        rightSubPanel.add(new JLabel("    before move: "));
        rightSubPanel.add(new JLabel(String.valueOf(initialUnits2),SwingConstants.CENTER));
        rightSubPanel.add(new JLabel("    after move: "));
        rightSubPanel.add(territoryUnits2);
        rightPanel.add(BorderLayout.CENTER,rightSubPanel);

        //border for customization
        Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        leftPanel.setBorder(raisedEtched);
        rightPanel.setBorder(raisedEtched);

        //add items to content pane
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        content.add(BorderLayout.WEST,leftPanel);
        content.add(BorderLayout.CENTER,middlePanel);
        content.add(BorderLayout.EAST,rightPanel);

        //dialog preferences
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    /**
     * Calculates the space that should be in between each major tick.
     *
     * @param start The start of the tick range
     * @param finish The end of the tick range
     * @return The spacing of each major tick from the start
     */
    private int findMajorTick(int start, int finish) {
        int t = finish-start;
        for (int i = t-1; i>= 0; i--) {
            if (t%i==0) return i;
        }
        return t;
    }

    /**
     * Retrieves the current value of the slider model.
     *
     * @return The slider model value
     */
    public int getSelectedUnits() {
        return sliderModel.getValue();
    }

    /**
     * StateChangeListener implementation (only used for slider).
     * Sets the text of units in each territory according to the current slider value;
     *
     * @param e The event that was triggered
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        DefaultBoundedRangeModel source = (DefaultBoundedRangeModel) e.getSource();
        int value = getSelectedUnits();
        territoryUnits1.setText(String.valueOf(initialUnits1-value));
        territoryUnits2.setText(String.valueOf(initialUnits2+value));
        /*if (!source.getValueIsAdjusting()) {
            int value = getSelectedUnits();
            territoryUnits1.setText(String.valueOf(initialUnits1-value));
            territoryUnits2.setText(String.valueOf(initialUnits2+value));
        }*/
    }

    /**
     * Disposes of the dialog.
     */
    private void close() {
        this.dispose();
    }

    /*
    Testing
     */
    public static void main(String[] args) {
        Territory t1 = new Territory("EARTH");
        t1.setUnits(50);
        Territory t2 = new Territory("MARS");
        t2.setUnits(10);
        JFortifyInputDialog f1 = new JFortifyInputDialog(null,t1,t2,0);
    }


}
