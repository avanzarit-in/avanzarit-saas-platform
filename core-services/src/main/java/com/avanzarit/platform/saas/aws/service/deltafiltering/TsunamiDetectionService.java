package com.avanzarit.platform.saas.aws.service.deltafiltering;

import com.avanzarit.platform.saas.aws.core.model.TsunamiMetadata;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TsunamiDetectionService {
    private static final String SERIALIZER_PREFIX = "ser_";

    public boolean isTsunamiDetected(String layer, TsunamiMetadata tsunamiMetadata) {
        boolean tsunamiDetected = false;

        if (isTsunamiMetadataAvailable(tsunamiMetadata)) {
            if (isSerializerLayer(layer)) {
                return isSerializerInterestedInTsunami(layer, tsunamiMetadata);
            } else {
                tsunamiDetected = true;
            }
        }

        return tsunamiDetected;
    }

    private boolean isTsunamiMetadataAvailable(TsunamiMetadata tsunamiMetadata) {
        return tsunamiMetadata != null && StringUtils.isNotBlank(tsunamiMetadata.getId());
    }

    private boolean isSerializerLayer(String layer) {
        return layer.startsWith(SERIALIZER_PREFIX);
    }

    private boolean isSerializerInterestedInTsunami(String serializerName, TsunamiMetadata tsunamiMetadata) {
        List<String> serializersInterestedInTsunami = tsunamiMetadata.getSerializers();

        return serializersInterestedInTsunami != null
                && serializersInterestedInTsunami.contains(serializerName);
    }
}
