package eu.eurofleets.ears3.dto.ontology;

import java.util.Map;

/**
 * Shape consumed directly by jsTree's core.data callback.
 * "type" drives icon + contextmenu behavior via jsTree's types plugin.
 * "data" carries everything the frontend needs without a further round trip:
 * conceptId, identifier, isGeneric, status, editable, and the HierarchyRef needed
 * to ask for this node's own children.
 */
public record NodeDTO(
        String id,
        String text,
        String type,
        boolean children,
        Map<String, Object> data
) {
}
