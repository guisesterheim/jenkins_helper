package com.sample.jenkinsHelper.util;

import com.sample.jenkinsHelper.exceptions.InvalidInputException;

public class TerraformFileName {

    private static String MESSAGE_FILE_PATTERN = "The file name pattern should be <platform>-<service>-tf_file-<env>-<timestamp>.tfstate";

    private String platform;
    private String service;
    private String fileRadical;
    private String environment;
    private int buildId;

    public TerraformFileName(String platform, String service, String environment, String fileRadical, int buildId){
        this.platform = platform;
        this.service = service;
        this.environment = environment;
        this.fileRadical = fileRadical;
        this.buildId = buildId;
    }

    public static TerraformFileName validateFileName(String fileName) throws InvalidInputException {
        if(fileName == null || fileName.trim().equals(""))
            throw new InvalidInputException("Invalid file name found: "+fileName+". "+MESSAGE_FILE_PATTERN);

        if(fileName.lastIndexOf(".") == -1)
            throw new InvalidInputException("Invalid file name found: "+fileName+". "+MESSAGE_FILE_PATTERN);

        String removedExtensionFileName = fileName.substring(0, fileName.lastIndexOf("."));

        String[] fileNameStructure = removedExtensionFileName.split("-");
        if(fileNameStructure == null || fileNameStructure.length != 5)
            throw new InvalidInputException("Invalid file name found: "+fileName+". "+MESSAGE_FILE_PATTERN);

        if(!fileNameStructure[3].equals("dev") &&
                !fileNameStructure[3].equals("qa") &&
                !fileNameStructure[3].equals("qas") &&
                !fileNameStructure[3].equals("hlg") &&
                !fileNameStructure[3].equals("prd"))
            throw new InvalidInputException(MESSAGE_FILE_PATTERN + "The allowed strings for <env> are dev, qa, qas, hlg, prd");

        try{
            Integer.parseInt(fileNameStructure[4]);
        }catch(Exception e){
            throw new InvalidInputException("Invalid file name found: "+fileName+". "+MESSAGE_FILE_PATTERN);
        }
        return new TerraformFileName(fileNameStructure[0], fileNameStructure[1], fileNameStructure[2],
                                        fileNameStructure[3], Integer.parseInt(fileNameStructure[4]));
    }

    public String toString(){
        return getPlatform() + "-" + getService() + "-" + getEnvironment() + "-" + getFileRadical() + "-" + getBuildId() + ".tfstate";
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getFileRadical() {
        return fileRadical;
    }

    public void setFileRadical(String fileRadical) {
        this.fileRadical = fileRadical;
    }

    public int getBuildId() {
        return buildId;
    }

    public void setBuildId(int buildId) {
        this.buildId = buildId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
