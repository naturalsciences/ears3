package eu.eurofleets.ears3.domain;

import jakarta.xml.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "page")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class IEventPage<E> {

    @XmlElementWrapper(name = "events")
    @XmlElement(name = "event")
    private List<E> content = new ArrayList<>();

    private int number;
    private int size;
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
    private boolean first;
    private boolean last;
    private boolean empty;

    public IEventPage() {
    }

    public IEventPage(Page<E> page) {
        this.content = page.getContent();
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.numberOfElements = page.getNumberOfElements();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }

    public List<E> getContent() {
        return content;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isEmpty() {
        return empty;
    }
}