package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.model.ProjectGenerationTask;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;

@Service
public class SupplierService {

    @Autowired
    private FileService fileService;

    @Autowired
    private TaskService taskService;

    public void executeTask(String taskId) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");
        if (url == null) {
            throw new InternalException("Zip file not found");
        }
        try {
            ProjectGenerationTask projectGenerationTask = taskService.getTask(taskId);
            File outputFile = File.createTempFile(taskId, ".zip");
            fileService.storeResult(projectGenerationTask, outputFile, url);
        } catch (Exception e) {
            throw new InternalException(e);
        }
    }

    public ResponseEntity<FileSystemResource> getTaskResult(String taskId) {
        ProjectGenerationTask projectGenerationTask = taskService.getTask(taskId);
        File inputFile = new File(projectGenerationTask.getStorageLocation());

        if (!inputFile.exists()) {
            throw new InternalException("File not generated yet");
        }

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentDispositionFormData("attachment", "challenge.zip");

        return new ResponseEntity<>(new FileSystemResource(inputFile), respHeaders, HttpStatus.OK);
    }
}
