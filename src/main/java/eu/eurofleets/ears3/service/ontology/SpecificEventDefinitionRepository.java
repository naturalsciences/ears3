package eu.eurofleets.ears3.service.ontology;

import eu.eurofleets.ears3.domain.ontology.ActionDefinition;
import eu.eurofleets.ears3.domain.ontology.ProcessDefinition;
import eu.eurofleets.ears3.domain.ontology.SpecificEventDefinition;
import eu.eurofleets.ears3.domain.ontology.ToolDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecificEventDefinitionRepository extends JpaRepository<SpecificEventDefinition, Long> {
    List<SpecificEventDefinition> findByTool(ToolDefinition tool);
    List<SpecificEventDefinition> findByProcess(ProcessDefinition process);
    List<SpecificEventDefinition> findByProcessAndAction(ProcessDefinition process, ActionDefinition action);
}
