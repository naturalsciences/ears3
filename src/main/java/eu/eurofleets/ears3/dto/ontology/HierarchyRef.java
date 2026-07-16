package eu.eurofleets.ears3.dto.ontology;

/**
 * Mirrors be.naturalsciences.bmdc.ontology.ConceptHierarchy's fixed slots exactly:
 * toolCategory, tool, hostedTool, process, action, property. The jsTree node id and
 * every children-lookup call are built from this.
 *
 * Only null fields are populated up to the "current" depth in the tree - e.g. a Tool
 * node's ref has toolCategoryId + toolId set, everything deeper is null.
 */
public record HierarchyRef(
        Long toolCategoryId,
        Long toolId,
        Long hostedToolId,
        Long processId,
        Long actionId,
        Long propertyId
) {
    public static final HierarchyRef ROOT = new HierarchyRef(null, null, null, null, null, null);
}
