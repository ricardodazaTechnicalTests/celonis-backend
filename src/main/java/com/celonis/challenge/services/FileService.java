package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.ProjectGenerationTaskRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;

@Service
public class FileService {

    @Autowired
    private ProjectGenerationTaskRepository projectGenerationTaskRepository;

    public FileService(){

    }

    public void storeResult(ProjectGenerationTask projectGenerationTask, File outputFile, URL url) throws IOException {
        outputFile.deleteOnExit();
        projectGenerationTask.setStorageLocation(outputFile.getAbsolutePath());
        projectGenerationTaskRepository.save(projectGenerationTask);
        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        }
    }
}
