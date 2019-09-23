package com.avanzarit.platform.saas.aws.util.matchers;

import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;

import static org.mockito.Matchers.argThat;

/**
 * Builder class that allows to build an {@link UpdateItemRequestMatcher} using a small DSL.
 */
public class UpdateItemRequestMatcherBuilder {
    private String tableName;
    private String keyName;
    private String keyValue;
    private String expressionAttributeName;
    private String expressionAttributeValue;

    /**
     * Sets the table name of the UpdateItemRequest.
     *
     * @param tableName The expected table name.
     */
    public UpdateItemRequestMatcherBuilder withTable(String tableName) {
        this.tableName = tableName;

        return this;
    }

    /**
     * Sets the key that needs to be defined in the UpdateItemRequest.
     *
     * @param keyName  The name of the key that is expected.
     * @param keyValue The value of the key that is expected.
     */
    public UpdateItemRequestMatcherBuilder withKey(String keyName, String keyValue) {
        this.keyName = keyName;
        this.keyValue = keyValue;

        return this;
    }

    /**
     * Sets the expression attribute value that is expected to exist in the UpdateItemRequest.
     *
     * @param expressionAttributeName  The name of the expression attribute value.
     * @param expressionAttributeValue The value of the expression attribute value.
     */
    public UpdateItemRequestMatcherBuilder withExpressionAttributeValue(String expressionAttributeName,
                                                                        String expressionAttributeValue) {
        this.expressionAttributeName = expressionAttributeName;
        this.expressionAttributeValue = expressionAttributeValue;

        return this;
    }

    /**
     * Creates the UpdateItemRequest matcher.
     */
    public UpdateItemRequest build() {
        UpdateItemRequestMatcher matcher = new UpdateItemRequestMatcher(
                tableName, keyName, keyValue, expressionAttributeName, expressionAttributeValue
        );

        return argThat(matcher);
    }

    /**
     * Initiates a new {@link UpdateItemRequestMatcherBuilder}.
     */
    public static UpdateItemRequestMatcherBuilder updateItemRequest() {
        return new UpdateItemRequestMatcherBuilder();
    }
}
