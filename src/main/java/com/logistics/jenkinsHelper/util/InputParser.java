package com.logistics.jenkinsHelper.util;

import com.logistics.jenkinsHelper.exceptions.InvalidInputException;
import com.logistics.jenkinsHelper.services.AzureService;

import java.util.Arrays;
import java.util.HashMap;

public class InputParser {

    private static String ENVIRONMENT_DEV = "dev";
    private static String ENVIRONMENT_QA = "qa";
    private static String ENVIRONMENT_PRD = "prd";

    private static String[] mandatoryParameters = {Params.OPERATION};
    private static String[] validOperations = { Operations.PULL_NEXT_FILE_OLDER_THAN,
                                                Operations.CHECK_FILE_IS_SINGLE_ENVIRONMENT_UP,
                                                Operations.TURN_GREEN};

    private InputParser(){}

    public static HashMap<String, String> checkInputAndParse(String[] args) throws InvalidInputException {
        if(args == null || args.length < 1)
            throw new InvalidInputException("No input parameters were given. You should follow the example: \"java -jar <jarfile> var1=foo var2=bar\"");

        Arrays.sort(args);

        HashMap<String, String> mappedVariables = checkVariablesFollowPatternAndSplit(args);
        checkAllMandatoryVariablesCovered(mappedVariables);
        checkOperationIsAllowed(mappedVariables);

        return mappedVariables;
    }

    public static void checkOperationIsAllowed(HashMap<String, String> mappedVariables) throws InvalidInputException{
        Arrays.sort(validOperations);
        String requestedOperation = mappedVariables.get(Params.OPERATION);
        if(Arrays.binarySearch(validOperations, requestedOperation) < 0)
            throw new InvalidInputException("Operation "+requestedOperation+" is not allowed. Check your input");
    }

    public static void checkAllMandatoryVariablesCovered(HashMap<String, String> mappedVariables) throws InvalidInputException {
        String[] mappedKeys = mappedVariables.keySet().toArray(new String[mappedVariables.size()]);
        Arrays.sort(mappedKeys);

        for(int i = 0; i < mandatoryParameters.length; i++){
            if(Arrays.binarySearch(mappedKeys, mandatoryParameters[i]) < 0)
                throw new InvalidInputException("Param "+mandatoryParameters[i]+" is required but was not found");
        }
    }

    public static HashMap<String, String> checkVariablesFollowPatternAndSplit(String[] args) throws InvalidInputException {
        HashMap<String, String> mapRet = new HashMap<>();

        for(int i = 0; i < args.length; i++){
            int equalIndex = args[i].indexOf("=");
            if(equalIndex == -1)
                throw new InvalidInputException("Param "+ args[i]+" does not follow the pattern var1=foo");

            String key = args[i].substring(0, equalIndex);
            String value = args[i].substring(equalIndex+1);

            checkKeyValue(args[i], key);
            checkKeyValue(args[i], value);

            mapRet.put(key, value);
        }

        return mapRet;
    }

    public static void checkKeyValue(String arg, String key) throws InvalidInputException {
        if(key == null || key.trim().length() == 0)
            throw new InvalidInputException("Param '"+ arg+"' does not follow the pattern var1=foo");
    }

    public static void checkEmptyInput(String value, String variableNameIfError) throws InvalidInputException{
        if(value == null || value.trim().equals(""))
            throw new InvalidInputException("Parameter '"+variableNameIfError+"' cannot be empty");
    }

    public static void checkValidEnvironment(String value, String variableNameIfError) throws InvalidInputException {
        checkEmptyInput(value,variableNameIfError);
        if(!value.equals(ENVIRONMENT_DEV) && !value.equals(ENVIRONMENT_QA) && !value.equals(ENVIRONMENT_PRD))
            throw new InvalidInputException("Parameter "+variableNameIfError+" must be either one of: " + ENVIRONMENT_DEV + " or " + ENVIRONMENT_QA + " or " + ENVIRONMENT_PRD);
    }

    public static String[] getMandatoryParameters() {
        return mandatoryParameters;
    }
}
