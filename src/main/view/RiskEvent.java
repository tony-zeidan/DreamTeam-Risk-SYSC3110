package main.view;

import java.util.EventObject;

public class RiskEvent extends EventObject {
    private RiskEventType type;
    private Object trigger;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public RiskEvent(Object source,Object trigger,RiskEventType type) {
        super(source);
        this.trigger = trigger;
        this.type = type;
    }
    public RiskEventType getType() {
        return type;
    }
    public Object getTrigger() {
        return trigger;
    }


}
