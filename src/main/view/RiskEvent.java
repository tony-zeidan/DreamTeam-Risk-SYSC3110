package main.view;

import java.util.EventObject;

/**
 * This class is used when handling the update of the model, specifying the type
 * of event that occurred and necessary information to process the event in each view.
 *
 * @author Kyler Verge
 * @author Ethan Chase
 * @author Anthony Dooley
 * @author Tony Zeidan
 */
public class RiskEvent extends EventObject {

    /**
     * The type of event that was thrown (type of this event).
     */
    private RiskEventType type;
    /**
     * The data that is sent along with the event.
     */
    private Object[] eventInfo;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @param type The type of event
     * @param eventInfo The data that will be sent along with this event
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
