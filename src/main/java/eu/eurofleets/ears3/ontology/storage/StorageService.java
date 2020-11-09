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
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Stream;

import java.nio.file.Path;

public interface StorageService {

    void init() throws IOException;

    void store(MultipartFile file, String name) throws IOException;

    Stream<Path> loadAll() throws IOException;

    Path load(String filename);

    Resource loadAsResource(String filename) throws IOException;

    void deleteAll();

}
