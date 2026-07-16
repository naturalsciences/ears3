package eu.eurofleets.ears3.service.ontology;

import eu.eurofleets.ears3.domain.ontology.ActionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionDefinitionRepository extends JpaRepository<ActionDefinition, Long> {
}
