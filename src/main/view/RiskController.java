package main.view;

import main.core.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RiskController implements ActionListener {
    private Game riskModel;
    public RiskController(Game rm)
    {
        riskModel = rm;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
