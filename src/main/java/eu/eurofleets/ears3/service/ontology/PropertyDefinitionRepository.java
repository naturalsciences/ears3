package eu.eurofleets.ears3.service.ontology;

import eu.eurofleets.ears3.domain.ontology.PropertyDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyDefinitionRepository extends JpaRepository<PropertyDefinition, Long> {
}
