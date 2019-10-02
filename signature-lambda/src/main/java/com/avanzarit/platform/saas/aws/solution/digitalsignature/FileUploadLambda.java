package com.avanzarit.platform.saas.aws.solution.digitalsignature;

import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.model.S3Object;
import com.avanzarit.platform.saas.aws.core.validation.impl.jsonschema.SchemaValidator;
import com.avanzarit.platform.saas.aws.dynamo.DynamoConstants;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableNameParser;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.impl.S3FileEventHandler;
import com.avanzarit.platform.saas.aws.lambda.requesthandler.S3EventHandlingLambda;
import com.avanzarit.platform.saas.aws.s3.S3Item;
import com.avanzarit.platform.saas.aws.solution.digitalsignature.model.CreditNoteEntity;
import com.avanzarit.platform.saas.aws.solution.digitalsignature.model.dao.CreditNoteRepository;
import com.avanzarit.platform.saas.aws.solution.digitalsignature.transformer.FileToCreditNoteEntityTransformer;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannel;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannelbuilder.DynamoDbTransformationTriggerOutputChannelBuilder;
import com.avanzarit.platform.saas.aws.util.CmwContext;

public class FileUploadLambda extends S3EventHandlingLambda<S3FileEventHandler> {

    @Override
    public void registerTriggers(Region region, CmwContext cmwContext) {
        TransformationTriggerOutputChannel<S3Item<S3Object>, CreditNoteEntity> outputChannel =
                new DynamoDbTransformationTriggerOutputChannelBuilder<S3Item<S3Object>, CreditNoteEntity>()
                        .withTableNameParser(new StructuredTableNameParser())
                        .withOutputRepository(new CreditNoteRepository(DynamoConstants.DYNAMO_CLIENT))
                        .withName("CreditNote")
                        .withTransformer(new FileToCreditNoteEntityTransformer())
                        .withValidator(new SchemaValidator())
                        .build();
    }

    @Override
    protected S3FileEventHandler getS3EventHandler() {
        return new S3FileEventHandler();
    }

    @Override
    public void onInvoke(CmwContext cmwContext) {
    }

    @Override
    public void onFinish(CmwContext cmwContext) {
    }
}
