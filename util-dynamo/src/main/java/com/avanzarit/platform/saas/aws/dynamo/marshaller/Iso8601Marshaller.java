package com.avanzarit.platform.saas.aws.dynamo.marshaller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.amazonaws.util.DateUtils;

import java.util.Date;

/**
 * Iso8601Marshaller is a {@link DynamoDBTypeConverter} class that converts a date to its ISO 8601 representation.
 */
public class Iso8601Marshaller implements DynamoDBTypeConverter<String,Date> {
    @Override
    public String convert(Date date) {
        return DateUtils.formatISO8601Date(date);
    }

    @Override
    public Date unconvert(String s) {
        return DateUtils.parseISO8601Date(s);
    }
}