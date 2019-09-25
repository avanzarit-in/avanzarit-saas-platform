package com.avanzarit.platform.saas.aws.service.deltafiltering;

import com.bazaarvoice.jolt.ArrayOrderObliviousDiffy;

public class ObjectChangeService {

    public boolean objectChanged(Object oldObject, Object newObject) {
        ArrayOrderObliviousDiffy.Result result = new ArrayOrderObliviousDiffy().diff(oldObject, newObject);

        return !result.isEmpty();
    }
}
