package com.sample.jenkinsHelper.util;

public interface Operations {

    // Operations allowed

    // Pull first environment file to be removed
    public static final String PULL_NEXT_FILE_OLDER_THAN = "pull_previous_file";

    // Check if there is a single environment up
    public static final String CHECK_FILE_IS_SINGLE_ENVIRONMENT_UP = "is_single_env_up";

    // Add blue environment to DNS Zone and turn it green
    public static final String TURN_GREEN = "turn_green";
}