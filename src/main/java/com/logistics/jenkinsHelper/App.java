package com.logistics.jenkinsHelper;

import com.logistics.jenkinsHelper.services.AzureService;
import com.logistics.jenkinsHelper.util.InputParser;
import com.logistics.jenkinsHelper.util.LoggerUtil;
import com.logistics.jenkinsHelper.util.Params;

import java.util.HashMap;

public class App {

    public static void main (String[] args) {
        try {
            HashMap<String, String> vars = InputParser.checkInputAndParse(args);

            LoggerUtil.setEnableConsoleLogs(vars.get(Params.ENABLE_CONSOLE_LOGS));

            LoggerUtil.logInfo("---------------------------------------------------------");
            LoggerUtil.logInfo("Starting Jenkins Helper");
            LoggerUtil.logInfo("---------------------------------------------------------");

            AzureService.getInstance().execute(vars);

            LoggerUtil.logInfo("Finished Jenkins Helper");
            System.exit(0);
        }catch(Exception e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}