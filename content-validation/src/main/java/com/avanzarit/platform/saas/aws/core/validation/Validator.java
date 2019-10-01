package com.avanzarit.platform.saas.aws.core.validation;

import com.avanzarit.platform.saas.aws.core.validation.impl.jsonschema.JsonValidationException;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

public interface Validator {
    ValidationReport validate(CmwContext cmwContext, UpdateInfo updateInfo, Object toBeValidated) throws JsonValidationException;
}
