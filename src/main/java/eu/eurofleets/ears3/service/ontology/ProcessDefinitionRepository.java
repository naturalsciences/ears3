package eu.eurofleets.ears3.service.ontology;

import eu.eurofleets.ears3.domain.ontology.ProcessDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessDefinitionRepository extends JpaRepository<ProcessDefinition, Long> {
}
