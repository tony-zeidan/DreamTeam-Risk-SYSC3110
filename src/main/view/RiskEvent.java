package main.view;

import java.util.EventObject;

public class RiskEvent extends EventObject {
    private RiskEventType type;
    private Object[] eventInfo;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public RiskEvent(Object source,RiskEventType type,Object... eventInfo) {
        super(source);
        this.eventInfo = eventInfo;
        this.type = type;
    }

    /**
     * getter for the type of event passed
     * @return RiskEventType
     */
    public RiskEventType getType() {
        return type;
    }

    /**
     * getter for additional information of
     * @return Object[]
     */
    public Object[] getEventInfo() {
        return eventInfo;
    }
}
