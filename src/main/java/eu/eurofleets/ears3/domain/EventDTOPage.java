package eu.eurofleets.ears3.domain;

import eu.eurofleets.ears3.dto.EventDTO;
import jakarta.xml.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "page")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventDTOPage extends IEventPage<EventDTO> {

    @XmlElementWrapper(name = "events")
    @XmlElement(name = "event")
    private List<EventDTO> content = new ArrayList<>();

    public EventDTOPage() {
    }

    public EventDTOPage(Page<EventDTO> page) {
        super(page);
        this.content = page.getContent();
    }

    public List<EventDTO> getContent() {
        return content;
    }
}