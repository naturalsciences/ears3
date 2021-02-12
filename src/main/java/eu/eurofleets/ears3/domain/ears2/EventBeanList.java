package eu.eurofleets.ears3.domain.ears2;

import eu.eurofleets.ears3.domain.Event;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "events")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventBeanList {

    public EventBeanList() {
    }

    public EventBeanList(List<EventBean> events) {
        this.events = events;
    }

    public EventBeanList(List<Event> events, boolean fake) {
        List<EventBean> res = new ArrayList<>();
        for (Event event : events) {
            EventBean e = new EventBean(event);
            res.add(e);
        }
        this.events = res;
    }

    @XmlElement(name = "event")
    private List<EventBean> events = null;

    public List<EventBean> getEvents() {
        return this.events;
    }

    public void setEvents(List<EventBean> events) {
        this.events = events;
    }
}
