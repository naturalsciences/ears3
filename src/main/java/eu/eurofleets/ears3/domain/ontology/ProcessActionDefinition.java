package eu.eurofleets.ears3.domain.ontology;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * involvesStep - which Actions generically belong to which Process, independent of
 * any specific tool/event context. Mirrors EARSOntologyCreator.saveProcessActions().
 */
@Entity
@Table(name = "ontology_process_action",
        uniqueConstraints = @UniqueConstraint(columnNames = {"process_id", "action_id"}))
public class ProcessActionDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ProcessDefinition process;

    @ManyToOne(optional = false)
    private ActionDefinition action;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessDefinition getProcess() {
        return process;
    }

    public void setProcess(ProcessDefinition process) {
        this.process = process;
    }

    public ActionDefinition getAction() {
        return action;
    }

    public void setAction(ActionDefinition action) {
        this.action = action;
    }
}
