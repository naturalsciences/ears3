package eu.eurofleets.ears3.service.ontology;

import eu.eurofleets.ears3.domain.ontology.ActionDefinition;
import eu.eurofleets.ears3.domain.ontology.GenericEventDefinition;
import eu.eurofleets.ears3.domain.ontology.ProcessDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenericEventDefinitionRepository extends JpaRepository<GenericEventDefinition, Long> {
    List<GenericEventDefinition> findByProcess(ProcessDefinition process);
    List<GenericEventDefinition> findByProcessAndAction(ProcessDefinition process, ActionDefinition action);
}
