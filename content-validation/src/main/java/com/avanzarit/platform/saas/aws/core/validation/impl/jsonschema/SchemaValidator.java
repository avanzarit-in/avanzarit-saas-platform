package com.avanzarit.platform.saas.aws.core.validation.impl.jsonschema;

import com.avanzarit.platform.saas.aws.core.validation.ValidationReport;
import com.avanzarit.platform.saas.aws.core.validation.Validator;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.DateTimeUtils;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SchemaValidator implements Validator {
    private Map<String, JsonSchema> schemaCache = new HashMap<>();

    public ValidationReport validate(CmwContext cmwContext, UpdateInfo updateInfo, Object toBeValidated) throws JsonValidationException {
        String schemaPath = getJsonSchemaResourcePath(toBeValidated);
        JsonSchema schema = loadSchema(schemaPath);
        ProcessingReport report = getJsonSchemaProcessingReport(updateInfo, cmwContext, toBeValidated, schema);
        ArrayList<ProcessingMessage> processingMessages = Lists.newArrayList(report);

        if (report.isSuccess()) {
            return new ValidationReport(true, "");
        } else {
            String reportMessage = processingMessages.stream().map(ProcessingMessage::toString).collect(Collectors.joining(", "));
            return new ValidationReport(false, reportMessage);
        }
    }

    private String getJsonSchemaResourcePath(Object toBeValidated) throws JsonValidationException {
        if (toBeValidated.getClass().isAnnotationPresent(JsonSchemaValidation.class)) {
            Annotation annotation = toBeValidated.getClass().getAnnotation(JsonSchemaValidation.class);
            JsonSchemaValidation jsonSchemaValidation = (JsonSchemaValidation) annotation;
            return jsonSchemaValidation.schema();
        } else {
            throw new JsonValidationException("Object type has no JsonSchemaValidation annotation.");
        }
    }

    private JsonSchema loadSchema(String schema) {
        if (schemaCache.containsKey(schema)) {
            return schemaCache.get(schema);
        }

        try {
            Enumeration<URL> schemaResources = Thread.currentThread().getContextClassLoader().getResources(schema);
            URL schemaResource = schemaResources.nextElement();

            if (schemaResources.hasMoreElements()) {
                throw new JsonValidationException("Duplicate schema's found, unable to validate entity: " + schemaResources);
            } else {
                if (schemaResource != null) {
                    try {
                        URI uri = schemaResource.toURI();
                        try {
                            JsonSchema jsonSchema = JsonSchemaFactory.byDefault().getJsonSchema(uri.toString());
                            schemaCache.put(schema, jsonSchema);
                            return jsonSchema;
                        } catch (ProcessingException e) {
                            throw new JsonValidationException("Error loading json schema", e);
                        }
                    } catch (URISyntaxException e) {
                        throw new JsonValidationException("Error parsing json schema location", e);
                    }
                } else {
                    throw new JsonValidationException("Error validating json, no schema resource given.");
                }
            }
        } catch (IOException e) {
            throw new JsonValidationException("Failed to load JSON schema's", e);
        }
    }

    private ProcessingReport getJsonSchemaProcessingReport(UpdateInfo updateInfo, CmwContext cmwContext, Object toBeValidated, JsonSchema schema) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(DateTimeUtils.getDateFormat(DateTimeUtils.TIMESTAMP_WITH_MILLISECONDS));
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            String content = mapper.writeValueAsString(toBeValidated);
            ProcessingReport report = schema.validate(mapper.readTree(content));

            if (!report.isSuccess()) {
                cmwContext.logWarning(updateInfo, "JSON Validation failed for JSON content:\n" + content);
            }

            return report;
        } catch (ProcessingException | IOException e) {
            throw new JsonValidationException("Error comparing input object to json schema", e);
        }
    }
}


