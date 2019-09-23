package com.avanzarit.platform.saas.aws.util.matchers;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Map;

/**
 * Matcher class that verifies whether an {@link UpdateItemRequest} complies with certain criteria.
 */
public class UpdateItemRequestMatcher extends BaseMatcher<UpdateItemRequest> {
    private String table;
    private String keyName;
    private String keyValue;
    private String expressionAttributeName;
    private String expressionAttributeValue;

    public UpdateItemRequestMatcher(String table, String keyName, String keyValue, String expressionAttributeName,
                                    String expressionAttributeValue) {
        this.table = table;
        this.keyName = keyName;
        this.keyValue = keyValue;
        this.expressionAttributeName = expressionAttributeName;
        this.expressionAttributeValue = expressionAttributeValue;
    }

    @Override
    public boolean matches(Object o) {
        if (o instanceof UpdateItemRequest) {
            UpdateItemRequest request = (UpdateItemRequest) o;

            if (request.getTableName().equals(table)
                    && hasKeyValue(request, keyName, keyValue)
                    && hasExpressionAttributeValue(request, expressionAttributeName, expressionAttributeValue)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasKeyValue(UpdateItemRequest request, String keyName, String keyValue) {
        for (Map.Entry<String, AttributeValue> entry : request.getKey().entrySet()) {
            if (keyName.equals(entry.getKey()) && new AttributeValue().withS(keyValue).equals(entry.getValue())) {
                return true;
            }
        }

        return false;
    }

    private boolean hasExpressionAttributeValue(UpdateItemRequest request, String expressionAttributeName,
                                                String expressionAttributeValue) {
        for (Map.Entry<String, AttributeValue> entry : request.getExpressionAttributeValues().entrySet()) {
            if (expressionAttributeName.equals(entry.getKey())
                    && new AttributeValue().withS(expressionAttributeValue).equals(entry.getValue())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(
                "An UpdateItemRequest with table " + table
                        + ", key name " + keyName
                        + ", key value " + keyValue
                        + ", expression attribute name " + expressionAttributeName
                        + ", expression attribute value " + expressionAttributeValue
        );
    }
}
