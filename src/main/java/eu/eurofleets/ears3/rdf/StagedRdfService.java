package eu.eurofleets.ears3.rdf;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLStreamException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Owns the stage -> activate workflow for the distributable ontology RDF file.
 * <p>
 * Neither staging a file nor generating one from the database ever touches the
 * "live" file directly - only activate() does that, and only after archiving
 * whatever was live before. This is deliberate: publishing/activating should always
 * be one conscious, explicit action, on both the master (after generating) and a
 * ship (after receiving a file by email/USB/etc).
 * <p>
 * On a PASSIVE (ship) instance, this is the ONLY thing that changes what SPARQL
 * queries see - the ontology database tables are never populated here, so there is
 * no way for the ship's live file to diverge from whatever was actually activated.
 */
@Service
public class StagedRdfService {

    @Value("${app.ontology.rdf.staged-path}")
    private String stagedPathStr;

    @Value("${app.ontology.rdf.archive-dir}")
    private String archiveDirStr;

    private Path staged() {
        return Paths.get(stagedPathStr);
    }

    private Path archiveDir() {
        return Paths.get(archiveDirStr);
    }

    private Path tmp(){
        return Paths.get(FileUtils.getTempDirectory().getAbsolutePath(),"earsv2-onto-vessel.rdf");
    }

    /**
     * Receiving a file - shore resetting from a known-good export, or a ship receiving shore's emailed file.
     */
    public void stage(InputStream rdfInput) throws IOException {
        Files.createDirectories(staged().getParent());
        Files.copy(rdfInput, staged(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Receiving a file - shore resetting from a known-good export, or a ship receiving shore's emailed file.
     */
    public void tmp(InputStream rdfInput) throws IOException {
        Files.copy(rdfInput, tmp(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Master-only: generate directly into staging from the current database state. Still requires activate().
     */
    public void stageFromBytes(byte[] rdfBytes) throws IOException {
        Files.createDirectories(staged().getParent());
        Files.write(staged(), rdfBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void archiveStaged() throws IOException {
        if (!Files.exists(staged())) {
            throw new IllegalStateException("Nothing staged - upload or generate a file before archiving.");
        }
        Files.createDirectories(archiveDir());

        String ts = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Files.copy(staged(), archiveDir().resolve("staged_" + ts + ".rdf"), StandardCopyOption.REPLACE_EXISTING);

//        Files.createDirectories(live().getParent());
//        Files.copy(staged(), live(), StandardCopyOption.REPLACE_EXISTING);
//        Files.deleteIfExists(staged());
    }


    public FileInfo stagedInfo() {
        return infoOf(staged());
    }

    public InputStream openStaged() throws IOException {
        return Files.newInputStream(staged());
    }

    public InputStream openTmp() throws IOException {
        return Files.newInputStream(tmp());
    }


    /**
     * Filename to use for Content-Disposition when serving the live file for download.
     */
    public String stagedFilename() {
        return staged().getFileName().toString();
    }

    /**
     * Lightweight header lookup (label, scope, dates) via OntologyHeaderReader -
     * does NOT build a full RDF model. Replaces the old getFileDate()'s reliance on
     * IOntologyModel.getStaticStuff().
     */
    public OntologyHeaderReader.OntologyHeader stagedHeader() throws IOException {
        try (InputStream in = openStaged()) {
            return OntologyHeaderReader.read(in);
        } catch (XMLStreamException e) {
            throw new IOException("Failed to parse the ontology header from the live file", e);
        }
    }

    private FileInfo infoOf(Path p) {
        try {
            if (!Files.exists(p)) {
                return null;
            }
            return new FileInfo(p.getFileName().toString(), Files.size(p),
                    Files.getLastModifiedTime(p).toInstant().toString());
        } catch (IOException e) {
            return null;
        }
    }

    public record FileInfo(String filename, long sizeBytes, String lastModified) {
    }
}
