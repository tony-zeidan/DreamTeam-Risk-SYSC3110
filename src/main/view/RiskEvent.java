package main.view;

import java.util.EventObject;

public class RiskEvent extends EventObject {
    private String eventDescription;
    private String info;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public RiskEvent(Object source, String description,String info) {
        super(source);
        eventDescription =description;
        this.info = info;
    }
    public String getDescription() {
        return eventDescription;
    }
    public String getInfo() {
        return info;
    }
}
