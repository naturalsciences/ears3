package eu.eurofleets.ears3.service.ontology;

import eu.eurofleets.ears3.domain.ontology.ActionDefinition;
import eu.eurofleets.ears3.domain.ontology.ProcessActionDefinition;
import eu.eurofleets.ears3.domain.ontology.ProcessDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessActionDefinitionRepository extends JpaRepository<ProcessActionDefinition, Long> {
    List<ProcessActionDefinition> findByProcess(ProcessDefinition process);
    List<ProcessActionDefinition> findByAction(ActionDefinition action);
}
