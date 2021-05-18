package com.sample.jenkinsHelper.util;

import com.sample.jenkinsHelper.exceptions.InvalidInputException;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InputParserTest {

    @Test
    void shouldValidateParamPattern(){
        assertThrows(InvalidInputException.class, () -> InputParser.checkKeyValue(null, null));
        assertThrows(InvalidInputException.class, () -> InputParser.checkKeyValue(null, ""));
        assertDoesNotThrow(() -> InputParser.checkKeyValue("teste", "teste"));
    }

    @Test
    void shouldValidateEmptyInput(){
        assertThrows(InvalidInputException.class, () -> InputParser.checkEmptyInput(null, null));
        assertThrows(InvalidInputException.class, () -> InputParser.checkEmptyInput("", null));
        assertDoesNotThrow(() -> InputParser.checkEmptyInput("teste", "teste"));
    }

    @Test
    void shouldValidateIncorrectArgs() throws InvalidInputException {
        assertThrows(InvalidInputException.class, () -> InputParser.checkInputAndParse(null));
        assertThrows(InvalidInputException.class, () -> InputParser.checkInputAndParse(new String[]{}));

        String[] args1 = new String[]{
                "oper=turn_green",
                "tenant_id=42e96cd9-a217-4525-9fb8-e594b5aa08cb",
                "secret_key=6Ww.-IqBaUwSG~0RWM6W3O_~JFKAAvE748",
                "client_id=62fbf6f1-0f7a-4738-a227-c851cc30db06",
                "subscription_id=0c12862f-7622-4746-b689-f0e9488696b3",
                "resource_group_name=Sample",
                "dns_zone_name=sample.com.br",
                "a_name=1235",
                "ip_address=1.2.3.4",
                "ttl=30"};
        assertEquals(InputParser.checkInputAndParse(args1).size(), args1.length);

        String[] args2 = new String[]{"connection_string=ultracomplexconnectionstring",
                "oper=pull_previous_file",
                "container=tf-container-test",
                "file=tf_file_test.tfstate"};
        assertEquals(InputParser.checkInputAndParse(args2).size(), args2.length);
    }

    @Test
    void shouldValidateParamsPattern(){
        String[] args1 = new String[]{"1234",
                "oper=create_blob_file"};
        assertThrows(InvalidInputException.class, () -> InputParser.checkVariablesFollowPatternAndSplit(args1));

        String[] args2 = new String[]{"",
                "oper=create_blob_file"};
        assertThrows(InvalidInputException.class, () -> InputParser.checkVariablesFollowPatternAndSplit(args2));

        String[] args3 = new String[]{"paramTest",
                "oper=create_blob_file"};
        assertThrows(InvalidInputException.class, () -> InputParser.checkVariablesFollowPatternAndSplit(args3));

        String[] args4 = new String[]{"oper="};
        assertThrows(InvalidInputException.class, () -> InputParser.checkVariablesFollowPatternAndSplit(args4));

        String[] args5 = new String[]{"oper"};
        assertThrows(InvalidInputException.class, () -> InputParser.checkVariablesFollowPatternAndSplit(args5));
    }

    @Test
    void shouldValidateMandatoryParamsCovered(){
        HashMap<String, String> vars = new HashMap<>();
        vars.put(Params.OPERATION, null);
        assertDoesNotThrow(() -> InputParser.checkAllMandatoryVariablesCovered(vars));

        HashMap<String, String> vars2 = new HashMap<>();
        assertThrows(InvalidInputException.class, () -> InputParser.checkAllMandatoryVariablesCovered(vars2));

        HashMap<String, String> vars3 = new HashMap<>();
        vars3.put("teste", null);
        assertThrows(InvalidInputException.class, () -> InputParser.checkAllMandatoryVariablesCovered(vars3));

        HashMap<String, String> vars4 = new HashMap<>();
        Arrays.stream(InputParser.getMandatoryParameters()).forEach(s -> vars4.put(s, null));
        assertDoesNotThrow(() -> InputParser.checkAllMandatoryVariablesCovered(vars4));
    }

    @Test
    void shouldValidateAllowedOperations(){
        HashMap<String, String> vars = new HashMap<>();
        vars.put(Params.OPERATION, Operations.CHECK_FILE_IS_SINGLE_ENVIRONMENT_UP);
        vars.put("teste", "teste");
        assertDoesNotThrow(() -> InputParser.checkOperationIsAllowed(vars));

        HashMap<String, String> vars2 = new HashMap<>();
        vars2.put("teste", "teste");
        assertThrows(NullPointerException.class, () -> InputParser.checkOperationIsAllowed(vars2));

        HashMap<String, String> vars3 = new HashMap<>();
        vars3.put(Params.OPERATION, Operations.TURN_GREEN);
        assertDoesNotThrow(() -> InputParser.checkOperationIsAllowed(vars3));
    }

    @Test
    void shouldValidateEnvironmentVariable() throws InvalidInputException {
        assertThrows(InvalidInputException.class, () -> InputParser.checkValidEnvironment("test", "PARAM"));
        assertThrows(InvalidInputException.class, () -> InputParser.checkValidEnvironment("", "PARAM"));
        assertThrows(InvalidInputException.class, () -> InputParser.checkValidEnvironment(null, "PARAM"));
        assertThrows(InvalidInputException.class, () -> InputParser.checkValidEnvironment("DEV", "PARAM"));
        assertThrows(InvalidInputException.class, () -> InputParser.checkValidEnvironment("QA", "PARAM"));
        assertThrows(InvalidInputException.class, () -> InputParser.checkValidEnvironment("PRD", "PARAM"));
        assertDoesNotThrow(() -> InputParser.checkValidEnvironment("dev", "PARAM"));
        assertDoesNotThrow(() -> InputParser.checkValidEnvironment("qa", "PARAM"));
        assertDoesNotThrow(() -> InputParser.checkValidEnvironment("prd", "PARAM"));
    }

}
