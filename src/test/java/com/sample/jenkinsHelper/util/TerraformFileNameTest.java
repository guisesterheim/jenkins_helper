package com.sample.jenkinsHelper.util;

import com.sample.jenkinsHelper.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TerraformFileNameTest {

    @Test
    void shouldValidateFileNames() throws InvalidInputException {
        assertThrows(InvalidInputException.class, () -> TerraformFileName.validateFileName("teste"));
        assertThrows(InvalidInputException.class, () -> TerraformFileName.validateFileName("1234"));
        assertThrows(InvalidInputException.class, () -> TerraformFileName.validateFileName("platform-company--1234.tfstate"));
        assertThrows(InvalidInputException.class, () -> TerraformFileName.validateFileName("platform-company-dev-1234.tfstate"));
        assertThrows(InvalidInputException.class, () -> TerraformFileName.validateFileName("platform-company-dev-1234."));
        assertThrows(InvalidInputException.class, () -> TerraformFileName.validateFileName("platform-company-dev-1234"));
        assertThrows(InvalidInputException.class, () -> TerraformFileName.validateFileName("platform-company-tf_file-dev-abc.tfstate"));
        assertDoesNotThrow(() -> TerraformFileName.validateFileName("platform-company-tf_file-dev-1234.tfstate"));
    }
}
