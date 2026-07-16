package eu.eurofleets.ears3.service.ontology;

import eu.eurofleets.ears3.domain.ontology.ActionDefinition;
import eu.eurofleets.ears3.domain.ontology.EarsTermDefinitionBase;
import eu.eurofleets.ears3.domain.ontology.GenericEventDefinition;
import eu.eurofleets.ears3.domain.ontology.ProcessDefinition;
import eu.eurofleets.ears3.domain.ontology.PropertyDefinition;
import eu.eurofleets.ears3.domain.ontology.SpecificEventDefinition;
import eu.eurofleets.ears3.domain.ontology.ToolCategoryDefinition;
import eu.eurofleets.ears3.domain.ontology.ToolDefinition;
import eu.eurofleets.ears3.dto.ontology.CreateChildRequest;
import eu.eurofleets.ears3.dto.ontology.HierarchyRef;
import eu.eurofleets.ears3.dto.ontology.NodeDTO;
import eu.eurofleets.ears3.dto.ontology.UpdatePropertiesRequest;
import eu.eurofleets.ears3.service.ontology.ActionDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.GenericEventDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.ProcessDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.PropertyDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.SpecificEventDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.ToolCategoryDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.ToolDefinitionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Ports the tree-building logic that lived in AsConceptChildFactory.createKeys() /
 * AsConcept.getChildren(ConceptHierarchy) / ConceptHierarchy.isGeneric() in the
 * NetBeans app, onto a lazy-loading REST API consumed by jsTree.
 *
 * IMPORTANT: children are NOT a static parent-child edge list. Each level is computed
 * by joining GenericEventDefinition/SpecificEventDefinition against the ancestor
 * context (HierarchyRef), so the same Process/Action/Property row can legitimately
 * appear under multiple different Tools with different children each time. This is
 * why every lookup takes a full HierarchyRef, not just a single parent id.
 */
@Service
public class OntologyTreeService {

    private final ToolCategoryDefinitionRepository toolCategoryRepo;
    private final ToolDefinitionRepository toolRepo;
    private final ProcessDefinitionRepository processRepo;
    private final ActionDefinitionRepository actionRepo;
    private final PropertyDefinitionRepository propertyRepo;
    private final GenericEventDefinitionRepository gevRepo;
    private final SpecificEventDefinitionRepository sevRepo;

    public OntologyTreeService(ToolCategoryDefinitionRepository toolCategoryRepo,
                                ToolDefinitionRepository toolRepo,
                                ProcessDefinitionRepository processRepo,
                                ActionDefinitionRepository actionRepo,
                                PropertyDefinitionRepository propertyRepo,
                                GenericEventDefinitionRepository gevRepo,
                                SpecificEventDefinitionRepository sevRepo) {
        this.toolCategoryRepo = toolCategoryRepo;
        this.toolRepo = toolRepo;
        this.processRepo = processRepo;
        this.actionRepo = actionRepo;
        this.propertyRepo = propertyRepo;
        this.gevRepo = gevRepo;
        this.sevRepo = sevRepo;
    }

    // ------------------------------------------------------------------
    // Reading the tree
    // ------------------------------------------------------------------

    /** Root level: all ToolCategoryDefinitions. Equivalent of FakeConcept "Root" -> getToolCategoryCollection(). */
    public List<NodeDTO> getRoots() {
        return toolCategoryRepo.findAll().stream()
                .filter(this::isActive)
                .sorted(Comparator.comparing(EarsTermDefinitionBase::getPrefLabel))
                .map(tc -> toDTO(tc, "toolcategory",
                        new HierarchyRef(tc.getId(), null, null, null, null, null)))
                .toList();
    }

    public List<NodeDTO> getChildren(HierarchyRef ref) {
        if (ref.propertyId() != null) {
            return List.of(); // Property.getChildren() is always empty - a leaf
        }
        if (ref.actionId() != null) {
            return getPropertyChildren(ref);
        }
        if (ref.processId() != null) {
            return getActionChildren(ref);
        }
        if (ref.hostedToolId() != null || ref.toolId() != null) {
            return getProcessAndHostedToolChildren(ref);
        }
        if (ref.toolCategoryId() != null) {
            return getToolChildren(ref);
        }
        return getRoots();
    }

    /** ToolCategory.getChildren() -> getToolCollection() */
    private List<NodeDTO> getToolChildren(HierarchyRef ref) {
        ToolCategoryDefinition tc = toolCategoryRepo.getReferenceById(ref.toolCategoryId());
        return tc.getTools().stream()
                .filter(this::isActive)
                .sorted(Comparator.comparing(EarsTermDefinitionBase::getPrefLabel))
                .map(t -> toDTO(t, "tool",
                        new HierarchyRef(ref.toolCategoryId(), t.getId(), null, null, null, null)))
                .toList();
    }

    /** Tool.getChildren() -> getProcessCollection(toolCategory) UNION hostedCollection */
    private List<NodeDTO> getProcessAndHostedToolChildren(HierarchyRef ref) {
        Long effectiveToolId = ref.hostedToolId() != null ? ref.hostedToolId() : ref.toolId();
        ToolDefinition tool = toolRepo.getReferenceById(effectiveToolId);
        ToolCategoryDefinition tc = ref.toolCategoryId() != null
                ? toolCategoryRepo.getReferenceById(ref.toolCategoryId()) : null;

        List<NodeDTO> result = new ArrayList<>();

        Set<ProcessDefinition> processes = new TreeSet<>(Comparator.comparing(EarsTermDefinitionBase::getPrefLabel));
        if (tc != null) {
            tc.getGenericEventDefinitions().stream()
                    .map(GenericEventDefinition::getProcess)
                    .forEach(processes::add);
        }
        sevRepo.findByTool(tool).stream().map(SpecificEventDefinition::getProcess).forEach(processes::add);

        for (ProcessDefinition p : processes) {
            result.add(toDTO(p, "process", new HierarchyRef(
                    ref.toolCategoryId(), ref.toolId(), ref.hostedToolId(), p.getId(), null, null)));
        }

        // hostedCollection - nested tools. Only one level deep: ConceptHierarchy has a single
        // hostedTool slot with nothing beneath it, so nesting stops here structurally.
        if (ref.hostedToolId() == null) {
            for (ToolDefinition hosted : tool.getHostedTools()) {
                if (!isActive(hosted)) {
                    continue;
                }
                result.add(toDTO(hosted, "tool", new HierarchyRef(
                        ref.toolCategoryId(), ref.toolId(), hosted.getId(), null, null, null)));
            }
        }
        return result;
    }

    /** Process.getChildren() -> getActionCollection(tool), filtered by SEV.tool==tool or GEV.toolCategory in tool.categories */
    private List<NodeDTO> getActionChildren(HierarchyRef ref) {
        ProcessDefinition process = processRepo.getReferenceById(ref.processId());
        Long effectiveToolId = ref.hostedToolId() != null ? ref.hostedToolId() : ref.toolId();
        ToolDefinition tool = effectiveToolId != null ? toolRepo.getReferenceById(effectiveToolId) : null;

        Set<ActionDefinition> actions = new TreeSet<>(Comparator.comparing(EarsTermDefinitionBase::getPrefLabel));
        for (SpecificEventDefinition sev : sevRepo.findByProcess(process)) {
            if (tool == null || tool.equals(sev.getTool())) {
                actions.add(sev.getAction());
            }
        }
        for (GenericEventDefinition gev : gevRepo.findByProcess(process)) {
            if (tool == null || tool.getToolCategories().contains(gev.getToolCategory())) {
                actions.add(gev.getAction());
            }
        }
        actions.removeIf(a -> "DEPRECATED".equals(a.getStatus()));

        return actions.stream().map(a -> toDTO(a, "action", new HierarchyRef(
                ref.toolCategoryId(), ref.toolId(), ref.hostedToolId(), ref.processId(), a.getId(), null))).toList();
    }

    /** Action.getChildren() -> properties from whichever EventDefinition matches (tool, process, action) */
    private List<NodeDTO> getPropertyChildren(HierarchyRef ref) {
        ActionDefinition action = actionRepo.getReferenceById(ref.actionId());
        ProcessDefinition process = processRepo.getReferenceById(ref.processId());
        Long effectiveToolId = ref.hostedToolId() != null ? ref.hostedToolId() : ref.toolId();
        ToolDefinition tool = effectiveToolId != null ? toolRepo.getReferenceById(effectiveToolId) : null;

        Set<PropertyDefinition> props = new TreeSet<>(Comparator.comparing(EarsTermDefinitionBase::getPrefLabel));
        for (SpecificEventDefinition sev : sevRepo.findByProcessAndAction(process, action)) {
            if (tool == null || tool.equals(sev.getTool())) {
                props.addAll(sev.getProperties());
            }
        }
        for (GenericEventDefinition gev : gevRepo.findByProcessAndAction(process, action)) {
            if (tool == null || tool.getToolCategories().contains(gev.getToolCategory())) {
                props.addAll(gev.getProperties());
            }
        }

        return props.stream().map(p -> toDTO(p, "property", new HierarchyRef(
                ref.toolCategoryId(), ref.toolId(), ref.hostedToolId(), ref.processId(), ref.actionId(), p.getId()))).toList();
    }

    /**
     * ConceptHierarchy.isGeneric() - true only if the Process at this point in the
     * hierarchy is reachable via a GenericEventDefinition on the ToolCategory ancestor.
     * Note this is computed at the Process level and, in the original code, implicitly
     * applies to everything deeper (Action/Property) in the same branch, since
     * "process" stays populated as you descend.
     */
    public boolean isGeneric(HierarchyRef ref) {
        if (ref.toolCategoryId() == null || ref.processId() == null) {
            return false;
        }
        ToolCategoryDefinition tc = toolCategoryRepo.getReferenceById(ref.toolCategoryId());
        ProcessDefinition process = processRepo.getReferenceById(ref.processId());
        return tc.getGenericEventDefinitions().stream().anyMatch(gev -> process.equals(gev.getProcess()));
    }

    private boolean isActive(EarsTermDefinitionBase t) {
        return !"DEPRECATED".equals(t.getStatus());
    }

    private NodeDTO toDTO(EarsTermDefinitionBase concept, String type, HierarchyRef ref) {
        boolean generic = isGeneric(ref);
        String text = buildDisplayName(concept);
        boolean hasChildren = !"property".equals(type);
        Map<String, Object> data = new HashMap<>();
        data.put("conceptId", concept.getId());
        data.put("identifier", concept.getIdentifier());
        data.put("isGeneric", generic);
        data.put("status", concept.getStatus());
        // Ownership/ "current vessel" gating deliberately left as always-true at POC stage;
        // wire in a real check here once vessel context is available.
        data.put("editable", true);
        data.put("hierarchyRef", ref);
        return new NodeDTO(nodeId(ref), text, type, hasChildren, data);
    }

    /** Mirrors AsConceptNode.getDisplayName()'s Tool-specific suffix/parent annotation. */
    private String buildDisplayName(EarsTermDefinitionBase concept) {
        String label = concept.getPrefLabel();
        if (concept instanceof ToolDefinition tool) {
            String specific = java.util.stream.Stream.of(tool.getSerialNumber(), tool.getToolIdentifier())
                    .filter(s -> s != null && !s.isBlank())
                    .reduce((a, b) -> a + "," + b).orElse("");
            for (ToolDefinition host : tool.getHostTools()) {
                label += " \u2208 " + host.getPrefLabel();
            }
            if (!specific.isBlank() && !specific.equals(label)) {
                label += " (" + specific + ")";
            }
        }
        return label;
    }

    private String nodeId(HierarchyRef r) {
        StringBuilder sb = new StringBuilder("n");
        if (r.toolCategoryId() != null) {
            sb.append("_ctg").append(r.toolCategoryId());
        }
        if (r.toolId() != null) {
            sb.append("_dev").append(r.toolId());
        }
        if (r.hostedToolId() != null) {
            sb.append("_hdev").append(r.hostedToolId());
        }
        if (r.processId() != null) {
            sb.append("_pro").append(r.processId());
        }
        if (r.actionId() != null) {
            sb.append("_act").append(r.actionId());
        }
        if (r.propertyId() != null) {
            sb.append("_pry").append(r.propertyId());
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Writing the tree (only reachable when editing-enabled == true, enforced by the controller)
    // ------------------------------------------------------------------

    /**
     * CreateChildNodeAction equivalent. At POC stage, restricted to ToolCategory ->
     * new Tool, exactly matching CreateChildNodeAction.enable()'s check in the
     * original app ("only allow creating new tools").
     */
    public NodeDTO createChild(CreateChildRequest req) {
        if (!"toolcategory".equals(req.parentType())) {
            throw new IllegalArgumentException("Children can currently only be created under a Tool Category.");
        }
        ToolCategoryDefinition tc = toolCategoryRepo.getReferenceById(req.parentRef().toolCategoryId());

        ToolDefinition tool = new ToolDefinition();
        tool.setIdentifier(UUID.randomUUID().toString());
        tool.setPrefLabel(req.newPrefLabel());
        tool.setStatus("WAIT_FOR_APPROVAL");
        tool.getToolCategories().add(tc);
        tool = toolRepo.save(tool);
        tc.getTools().add(tool);

        HierarchyRef newRef = new HierarchyRef(tc.getId(), tool.getId(), null, null, null, null);
        return toDTO(tool, "tool", newRef);
    }

    /**
     * DeleteNodeAction equivalent. Enable rule ported directly: !isRoot && !isGeneric.
     * For Process/Action/Property nodes (which can be shared across multiple tools/
     * categories) this removes only the association relevant to this branch, not
     * necessarily the underlying shared definition row - a best-effort approximation
     * since the original concept.delete(ConceptHierarchy) implementation from the
     * base ontology library was not available to port from directly.
     */
    public void delete(HierarchyRef ref, String type) {
        if (isGeneric(ref)) {
            throw new IllegalStateException(
                    "Cannot delete a node inherited from a generic (tool-category-level) definition.");
        }
        switch (type) {
            case "toolcategory" -> toolCategoryRepo.deleteById(ref.toolCategoryId());
            case "tool" -> deleteTool(ref);
            case "process" -> deleteProcessAssociation(ref);
            case "action" -> deleteActionAssociation(ref);
            case "property" -> deletePropertyAssociation(ref);
            default -> throw new IllegalArgumentException("Unknown node type: " + type);
        }
    }

    private void deleteTool(HierarchyRef ref) {
        if (ref.hostedToolId() != null) {
            // Unhost it rather than delete the tool definition outright, in case it's a shared model.
            ToolDefinition host = toolRepo.getReferenceById(ref.toolId());
            ToolDefinition hosted = toolRepo.getReferenceById(ref.hostedToolId());
            host.getHostedTools().remove(hosted);
        } else {
            toolRepo.deleteById(ref.toolId());
        }
    }

    private void deleteProcessAssociation(HierarchyRef ref) {
        Long effectiveToolId = ref.hostedToolId() != null ? ref.hostedToolId() : ref.toolId();
        ToolDefinition tool = toolRepo.getReferenceById(effectiveToolId);
        ProcessDefinition process = processRepo.getReferenceById(ref.processId());
        sevRepo.findByTool(tool).stream()
                .filter(sev -> process.equals(sev.getProcess()))
                .forEach(sevRepo::delete);
    }

    private void deleteActionAssociation(HierarchyRef ref) {
        Long effectiveToolId = ref.hostedToolId() != null ? ref.hostedToolId() : ref.toolId();
        ToolDefinition tool = toolRepo.getReferenceById(effectiveToolId);
        ProcessDefinition process = processRepo.getReferenceById(ref.processId());
        ActionDefinition action = actionRepo.getReferenceById(ref.actionId());
        sevRepo.findByProcessAndAction(process, action).stream()
                .filter(sev -> tool.equals(sev.getTool()))
                .forEach(sevRepo::delete);
    }

    private void deletePropertyAssociation(HierarchyRef ref) {
        Long effectiveToolId = ref.hostedToolId() != null ? ref.hostedToolId() : ref.toolId();
        ToolDefinition tool = toolRepo.getReferenceById(effectiveToolId);
        ProcessDefinition process = processRepo.getReferenceById(ref.processId());
        ActionDefinition action = actionRepo.getReferenceById(ref.actionId());
        PropertyDefinition property = propertyRepo.getReferenceById(ref.propertyId());
        sevRepo.findByProcessAndAction(process, action).stream()
                .filter(sev -> tool.equals(sev.getTool()))
                .forEach(sev -> sev.getProperties().remove(property));
    }

    public NodeDTO updateProperties(UpdatePropertiesRequest req) {
        EarsTermDefinitionBase concept = resolveConcept(req.ref(), req.type());
        if (req.prefLabel() != null) {
            concept.setPrefLabel(req.prefLabel());
        }
        if (req.altLabel() != null) {
            concept.setAltLabel(req.altLabel());
        }
        if (req.definition() != null) {
            concept.setDefinition(req.definition());
        }
        if (concept instanceof ToolDefinition tool) {
            if (req.toolIdentifier() != null) {
                tool.setToolIdentifier(req.toolIdentifier());
            }
            if (req.serialNumber() != null) {
                tool.setSerialNumber(req.serialNumber());
            }
        }
        if (concept instanceof PropertyDefinition prop) {
            if (req.mandatory() != null) {
                prop.setMandatory(req.mandatory());
            }
            if (req.multiple() != null) {
                prop.setMultiple(req.multiple());
            }
        }
        return toDTO(concept, req.type(), req.ref());
    }

    private EarsTermDefinitionBase resolveConcept(HierarchyRef ref, String type) {
        return switch (type) {
            case "toolcategory" -> toolCategoryRepo.getReferenceById(ref.toolCategoryId());
            case "tool" -> toolRepo.getReferenceById(ref.hostedToolId() != null ? ref.hostedToolId() : ref.toolId());
            case "process" -> processRepo.getReferenceById(ref.processId());
            case "action" -> actionRepo.getReferenceById(ref.actionId());
            case "property" -> propertyRepo.getReferenceById(ref.propertyId());
            default -> throw new IllegalArgumentException("Unknown node type: " + type);
        };
    }
}
