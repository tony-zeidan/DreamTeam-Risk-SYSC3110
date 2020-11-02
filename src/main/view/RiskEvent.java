package main.view;

import java.util.EventObject;

public class RiskEvent extends EventObject {
    private String eventDescription;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public RiskEvent(Object source, String description) {
        super(source);
        eventDescription =description;
    }
    public String getDescription() {
        return eventDescription;
    }
}
