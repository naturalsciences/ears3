package eu.eurofleets.ears3.dto.ontology;

public record UpdatePropertiesRequest(
        HierarchyRef ref,
        String type,
        String prefLabel,
        String altLabel,
        String definition,
        String toolIdentifier,
        String serialNumber,
        Boolean mandatory,
        Boolean multiple
) {
}
