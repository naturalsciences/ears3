package eu.eurofleets.ears3.controller.ontology;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Instance-wide kill-switches for the ontology editor, driven by environment
 * variables (an .env file, or however your deployment injects Spring properties).
 *
 * ears.ontology.mode:
 *   ACTIVE  - master/shore instance. DB is authoritative; interactive tree editing
 *             and "generate RDF from DB" are meaningful here.
 *   PASSIVE - ship/slave instance (the default). The ontology database tables are
 *             not populated or used at all; the RDF file is only staged and
 *             activated, then served as-is for GET/SPARQL. This prevents any
 *             possibility of the ship's copy diverging from what shore published.
 *
 * ears.ontology.editing-enabled:
 *   Gates interactive tree edits (create/delete/update) AND "generate from DB".
 *   Defaults to false. Should only be true on the master.
 *
 * ears.ontology.import-enabled:
 *   Gates staging + activating an RDF file. Safe to leave true everywhere -
 *   receiving a file is never destructive to anything except the staging slot,
 *   and activation is a single explicit, confirmed action.
 */
@Component
public class OntologyEditingGuard {

    public enum Mode { ACTIVE, PASSIVE }

    @Value("${ears.ontology.mode:PASSIVE}")
    private Mode mode;

    @Value("${ears.ontology.editing-enabled:false}")
    private boolean editingEnabled;

    @Value("${ears.ontology.import-enabled:true}")
    private boolean importEnabled;

    public Mode getMode() {
        return mode;
    }

    public boolean isEditingEnabled() {
        return editingEnabled;
    }

    public boolean isImportEnabled() {
        return importEnabled;
    }

    public void assertEditingAllowed() {
        if (!editingEnabled) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Interactive editing is disabled on this instance. If this is a slave/ship copy, "
                    + "changes must be made on the master and brought over as a published RDF file.");
        }
    }

    public void assertImportAllowed() {
        if (!importEnabled) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ontology file import is disabled on this instance.");
        }
    }
}
