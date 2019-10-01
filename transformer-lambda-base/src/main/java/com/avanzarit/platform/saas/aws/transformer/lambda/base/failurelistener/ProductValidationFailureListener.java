package com.avanzarit.platform.saas.aws.transformer.lambda.base.failurelistener;

import com.avanzarit.platform.saas.aws.service.lateststate.States;
import com.avanzarit.platform.saas.aws.service.lateststate.UpdateLatestStateService;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerValidationFailureListener;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Listens for when the validation of a product fails in the lambda transformation logic. When it does, the latest state
 * of the product is updated to WARNING.
 */
public class ProductValidationFailureListener<I> implements TransformerValidationFailureListener<I> {

    private UpdateLatestStateService latestStateService;

    public ProductValidationFailureListener(UpdateLatestStateService latestStateService) {
        this.latestStateService = latestStateService;
    }

    @Override
    public void onValidationFailure(CmwContext cmwContext, UpdateInfo updateInfo, I validatedEntity,
                                    String message) {
        latestStateService.updateState(cmwContext, updateInfo, States.WARNING, message);
    }
}
