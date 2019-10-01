package com.avanzarit.platform.saas.aws.service.deltafiltering;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.core.model.TsunamiMetadata;
import com.avanzarit.platform.saas.aws.util.CmwContext;

import java.util.Objects;

public class DeltaDetectionService {
    private TsunamiDetectionService tsunamiDetectionService;
    private ObjectChangeService objectChangeService;

    public DeltaDetectionService(ObjectChangeService objectChangeService, TsunamiDetectionService tsunamiDetectionService) {
        this.objectChangeService = objectChangeService;
        this.tsunamiDetectionService = tsunamiDetectionService;
    }

    public <T extends CoreEntity> boolean detectsDelta(CmwContext cmwContext, T oldEntity, T newEntity) {
        if (oldEntity == null) {
            return true;
        }

        if (!Objects.equals(oldEntity.getRetryUpdate(), newEntity.getRetryUpdate())) {
            return true;
        }

        return detectsDeltaInternal(cmwContext, oldEntity.getEntityBody(), newEntity.getEntityBody(), newEntity.getTsunamiMetadata());
    }

    public boolean detectsDeltaIgnoringRetry(CmwContext cmwContext, Object oldObject, Object newObject, TsunamiMetadata tsunamiMetadata) {
        if (oldObject == null) {
            return true;
        }

        return detectsDeltaInternal(cmwContext, oldObject, newObject, tsunamiMetadata);
    }

    private boolean detectsDeltaInternal(CmwContext cmwContext, Object oldObject, Object newObject, TsunamiMetadata tsunamiMetadata) {
        if (tsunamiDetectionService.isTsunamiDetected(cmwContext.getLayer(), tsunamiMetadata)) {
            return true;
        }

        return objectChangeService.objectChanged(oldObject, newObject);
    }

    public boolean detectsDeltaBetweenObjects(Object oldObject, Object newObject) {
        return objectChangeService.objectChanged(oldObject, newObject);
    }
}
