package eu.eurofleets.ears3.controller.ontology;


import eu.eurofleets.ears3.dto.ontology.CreateChildRequest;
import eu.eurofleets.ears3.dto.ontology.HierarchyRef;
import eu.eurofleets.ears3.dto.ontology.NodeDTO;
import eu.eurofleets.ears3.dto.ontology.UpdatePropertiesRequest;
import eu.eurofleets.ears3.service.ontology.OntologyTreeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ontology/tree")
public class OntologyTreeController {

    private final OntologyTreeService treeService;
    private final OntologyEditingGuard guard;

    public OntologyTreeController(OntologyTreeService treeService, OntologyEditingGuard guard) {
        this.treeService = treeService;
        this.guard = guard;
    }

    @GetMapping("/roots")
    public List<NodeDTO> roots() {
        return treeService.getRoots();
    }

    @PostMapping("/children")
    public List<NodeDTO> children(@RequestBody HierarchyRef ref) {
        return treeService.getChildren(ref);
    }

    @PostMapping("/nodes")
    public NodeDTO createChild(@RequestBody CreateChildRequest req) {
        guard.assertEditingAllowed();
        return treeService.createChild(req);
    }

    @DeleteMapping("/nodes")
    public void deleteNode(@RequestParam String type, @RequestBody HierarchyRef ref) {
        guard.assertEditingAllowed();
        treeService.delete(ref, type);
    }

    @PutMapping("/nodes/properties")
    public NodeDTO updateProperties(@RequestBody UpdatePropertiesRequest req) {
        guard.assertEditingAllowed();
        return treeService.updateProperties(req);
    }
}
