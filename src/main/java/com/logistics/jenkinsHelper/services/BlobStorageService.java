package com.logistics.jenkinsHelper.services;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.logistics.jenkinsHelper.exceptions.InvalidInputException;
import com.logistics.jenkinsHelper.util.InputParser;
import com.logistics.jenkinsHelper.util.Params;
import com.logistics.jenkinsHelper.util.TerraformFileName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BlobStorageService {

    private String connectionString;
    private BlobServiceClient blobServiceClient;

    public BlobStorageService(String connectionString){
        this.connectionString = connectionString;
        this.blobServiceClient = new BlobServiceClientBuilder().connectionString(this.connectionString).buildClient();
    }

    public boolean fileExists(String containerName, String fileName){
        BlobContainerClient blobContainerClient = getOrCreateContainer(containerName);

        return blobContainerClient.getBlobClient(fileName).exists();
    }

    private BlobContainerClient getOrCreateContainer(String containerName) {
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        if(!blobContainerClient.exists())
            blobContainerClient = blobServiceClient.createBlobContainer(containerName);

        return blobContainerClient;
    }

    public List<String> listFilesForContainer(String containerName) throws InvalidInputException{
        InputParser.checkEmptyInput(containerName, Params.CONTAINER_NAME);

        PagedIterable<BlobItem> blobs = getOrCreateContainer(containerName).listBlobs();

        return blobs.streamByPage()
                .map(blobItemPagedResponse -> blobItemPagedResponse.getValue().stream()
                                                    .map(BlobItem::getName)
                                                    .collect(Collectors.toList()))
                .reduce((strings, strings2) -> {
                    strings.addAll(strings2);
                    return strings;
                }).orElse(new ArrayList<>());
    }

    public String pullPreviousFileForDeletion(String containerName, String currentFileName) throws InvalidInputException{
        checkVariableInputs(containerName, currentFileName);

        if(!fileExists(containerName, currentFileName))
            return "";

        List<String> files = listFilesForContainer(containerName);
        if(files == null || files.size() < 1)
            return "";

        List<TerraformFileName> tfFiles = new ArrayList<>();

        for(int i = 0; i < files.size(); i++)
            tfFiles.add(TerraformFileName.validateFileName(files.get(i)));

        if(tfFiles.size() < 1)
            return "";

        TerraformFileName currentFile = TerraformFileName.validateFileName(currentFileName);
        tfFiles.sort(Comparator.comparing(TerraformFileName::getBuildId).reversed());

        TerraformFileName firstFile = tfFiles.stream().max(Comparator.comparing(terraformFileName -> terraformFileName.getBuildId() < currentFile.getBuildId())).orElse(null);
        if(firstFile != null && firstFile.getBuildId() < currentFile.getBuildId())
            return firstFile.toString();

        return "";
    }

    private void checkVariableInputs(String containerName, String fileName) throws InvalidInputException{
        InputParser.checkEmptyInput(containerName, Params.CONTAINER_NAME);
        InputParser.checkEmptyInput(fileName, Params.FILE_NAME);

        checkValidFileNameStructure(fileName);
    }

    private void checkValidFileNameStructure(String fileName) throws InvalidInputException {
        TerraformFileName.validateFileName(fileName);
    }
}