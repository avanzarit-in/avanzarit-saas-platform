package com.avanzarit.platform.saas.aws.solution.digitalsignature.transformer;

import com.amazonaws.services.s3.model.S3Object;
import com.avanzarit.platform.saas.aws.s3.S3Item;
import com.avanzarit.platform.saas.aws.solution.digitalsignature.model.CreditNoteEntity;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.AbstractTransformerBase;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.Transformer;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

public class FileToCreditNoteEntityTransformer extends AbstractTransformerBase<S3Item<S3Object>, CreditNoteEntity>
        implements Transformer<S3Item<S3Object>, CreditNoteEntity> {

    @Override
    public CreditNoteEntity transform(CmwContext cmwContext, UpdateInfo updateInfo, S3Item<S3Object> inputEntity) {
        return null;
    }
}
