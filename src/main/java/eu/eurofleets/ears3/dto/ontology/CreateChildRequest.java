package eu.eurofleets.ears3.dto.ontology;

/** Only ToolCategory -> Tool creation is supported at POC stage, matching CreateChildNodeAction.enable() in the original NetBeans app. */
public record CreateChildRequest(HierarchyRef parentRef, String parentType, String newPrefLabel) {
}
