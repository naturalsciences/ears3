/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.ontology.storage;

/**
 *
 * @author thomas
 */
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    public static final String ONTOLOGY_DIR = "/var/www/ears2/";

    public static final String VESSEL_ONTOLOGY_FILE_NAME = "earsv2-onto-vessel.rdf";

    public static final String VESSEL_ONTOLOGY_FILE_LOCATION = ONTOLOGY_DIR + VESSEL_ONTOLOGY_FILE_NAME;


}
