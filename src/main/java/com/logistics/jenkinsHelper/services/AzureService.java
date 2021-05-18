package com.logistics.jenkinsHelper.services;

import com.logistics.jenkinsHelper.exceptions.InvalidEnvCountException;
import com.logistics.jenkinsHelper.exceptions.InvalidInputException;
import com.logistics.jenkinsHelper.util.LoggerUtil;
import com.logistics.jenkinsHelper.util.Operations;
import com.logistics.jenkinsHelper.util.Params;

import java.util.HashMap;
import java.util.List;

public class AzureService {

    private AzureService() {}
    private static AzureService azureService;

    private BlobStorageService blobStorageService;
    private NetworkService networkService;

    private String connectionString;

    public static AzureService getInstance(){
        if(azureService == null)
            return azureService = new AzureService();
        return azureService;
    }

    public void execute(HashMap<String, String> params) throws InvalidInputException, InvalidEnvCountException {
        LoggerUtil.logInfo("Starting execute");

        String operation = params.get(Params.OPERATION);
        String containerName;
        String fileName;

        switch (operation){
            case Operations.PULL_NEXT_FILE_OLDER_THAN:
                setForStorage(params);

                containerName = params.get(Params.CONTAINER_NAME);
                fileName = params.get(Params.FILE_NAME);

                if(fileName == null || fileName.trim().equals(""))
                    pullPreviousFileForContainer(containerName);
                else
                    pullPreviousFileForContainerAndID(containerName, fileName);
                break;

            case Operations.CHECK_FILE_IS_SINGLE_ENVIRONMENT_UP:
                setForStorage(params);

                containerName = params.get(Params.CONTAINER_NAME);

                checkSingleEnvironmentIsUp(containerName);
                break;

            case Operations.TURN_GREEN:
                setForNetwork();

                String tenant = params.get(Params.TENANT_ID);
                String secret = params.get(Params.SECRET_KEY);
                String client = params.get(Params.CLIENT_ID);
                String subscription = params.get(Params.SUBSCRIPTION_ID);

                String environment = params.get(Params.ENVIRONMENT);
                String resourceGroupName = params.get(Params.RG_NAME);
                String DNSZoneName = params.get(Params.DNS_ZONE_NAME);
                String aname = params.get(Params.ANAME);
                String ipAddress = params.get(Params.IPADDRESS);
                String ttl = params.get(Params.TTL);

                turnEnvGreen(environment, subscription, client, secret, tenant, resourceGroupName, DNSZoneName, aname, ipAddress, ttl);
                break;

            default:
                throw new InvalidInputException("Invalid operation "+operation);
        }
    }

    private void setForNetwork() {
        this.networkService = new NetworkService();
    }

    private void setForStorage(HashMap<String, String> params) {
        this.connectionString = params.get(Params.CONN_STRING);
        this.blobStorageService = new BlobStorageService(this.connectionString);
    }

    private void turnEnvGreen(String environment, String subscription, String client, String secret, String tenant, String resourceGroupName, String dnsZoneName, String aname, String ipAddress, String ttl) throws InvalidInputException {
        LoggerUtil.logInfo("Starting turn green");

        this.networkService.turnGreen(environment, subscription, client, secret, tenant, resourceGroupName, dnsZoneName, aname, ipAddress, ttl);

        LoggerUtil.logInfo("Finished printing previous file");
    }

    private void pullPreviousFileForContainerAndID(String containerName, String currentFile) throws InvalidInputException{
        LoggerUtil.logInfo("Starting push next file");

        String previousFile = this.blobStorageService.pullPreviousFileForDeletion(containerName, currentFile);

        System.out.println(previousFile);

        LoggerUtil.logInfo("Finished printing previous file");
    }

    private void pullPreviousFileForContainer(String containerName) throws InvalidInputException{
        LoggerUtil.logInfo("Starting push next file");

        List<String> files = this.blobStorageService.listFilesForContainer(containerName);

        if(files != null && files.size() > 0)
            System.out.println(files.stream().findFirst().orElse(""));

        LoggerUtil.logInfo("Finished printing previous file");
    }

    private void checkSingleEnvironmentIsUp(String containerName) throws InvalidEnvCountException, InvalidInputException {
        LoggerUtil.logInfo("Starting list all files for container");

        List<String> files = this.blobStorageService.listFilesForContainer(containerName);

        if(files.size() == 0)
            System.out.println("OK");
        else
            throw new InvalidEnvCountException("Found more than zero environments up");

        LoggerUtil.logInfo("Finished listing all files");
    }
}