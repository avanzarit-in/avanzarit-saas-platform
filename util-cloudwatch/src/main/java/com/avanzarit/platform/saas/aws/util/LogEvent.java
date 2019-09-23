package com.avanzarit.platform.saas.aws.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Objects;

/** 
 * The LogEvent class contains all relevant data related to something happening within the system. 
 * <p> 
 * <ul> 
 * <li>prefix: The environment in which the log event occurred (e.g.: mw_develop)</li> 
 * <li>layer: The layer of the system in which the log event occurred (e.g.: canonical)</li> 
 * <li>component: The component of the system in which the log event occurred (e.g.: changedcache)</li> 
 * <li>objectId: The objectId of the entity within the system for which the event is relevant (e.g.: 
 * PRD-HC781342)</li> 
 * <li>context: The context (or locale) of the entity within the system for which the event is relevant (e.g.: 
 * en_AA)</li> 
 * <li>correlationId: The update id of the update that caused an event to occur within the system (e.g.: 
 * 2016-09-21T12:16:31.000Z_PRD-HC781342#en_AA_4909944)</li> 
 * <li>message: The message describing the event. (e.g.: 'The name of the product was changed')</li> 
 * <li>status: The status or severity of the event (usually one of: CONFIRMATION, DISCARDED, INFO, DEBUG, WARNING, 
 * ERROR)</li> 
 * <li>timestamp: The timestamp at which the event occurred</li> 
 * <li>awsRequestId: A unique identifier from AWS mostly used in AWS Lambda functions to identify a Lambda function 
 * invocation</li> 
 * </ul> 
 */ 
 
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEvent {

    private static final Logger LOGGER = LogManager.getLogger(LogEvent.class);

    /**
     * Created enum to distinguish between logType of Info, Warning, Error and Debug.
     */
    public enum LogType {
        LOGTYPE_CWL_SEARCH("cwlsearch"),
        LOGTYPE_CWL_CLOUDWATCH("cwlcloudwatch");

        private String name;

        LogType(String name) {
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return name;
        }

        /**
         * Method contains switch case to handle logType of Info, Warning, Error and Debug.
         */
        @JsonCreator
        public static LogType forValue(String name) {
            LogType type = null;
            switch (name) {
                case "cwlsearch":
                    type = LOGTYPE_CWL_SEARCH;
                    break;
                case "cwlcloudwatch":
                    type = LOGTYPE_CWL_CLOUDWATCH;
                    break;
                default:
                    LOGGER.debug("Invalid logType ");
            }
            return type;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
 
    private LogType logType; 
    private String prefix; 
    private String layer; 
    private String component; 
    private String objectId; 
    private String context; 
    private String correlationId; 
    private String message; 
    private String status; 
    private Date timestamp; 
    private String awsRequestId; 
 
    /** 
     * Empty constructor used by Jackson to instantiate this class. 
     */ 
    public LogEvent() { 
        this.logType = LogType.LOGTYPE_CWL_SEARCH; 
    } 
 
    public LogEvent(LogType logType) { 
        this.logType = logType; 
    } 
 
    /** 
     * Creates a new LogEvent instance using the given details. 
     */ 
    public LogEvent(String prefix, String layer, String component, String objectId, String context, 
                    String correlationId, String message, String status, Date timestamp, String awsRequestId) { 
        this(); 
 
        this.prefix = prefix; 
        this.layer = layer; 
        this.component = component; 
        this.objectId = objectId; 
        this.context = context; 
        this.correlationId = correlationId; 
        this.message = message; 
        this.status = status; 
        this.timestamp = timestamp; 
        this.awsRequestId = awsRequestId; 
    } 
 
    public LogEvent(String prefix, String layer, String component, String objectId, String context, 
                    String correlationId, String message, String status, Date timestamp, String awsRequestId, 
                    LogType logType) { 
        this(logType); 
 
        this.prefix = prefix; 
        this.layer = layer; 
        this.component = component; 
        this.objectId = objectId; 
        this.context = context; 
        this.correlationId = correlationId; 
        this.message = message; 
        this.status = status; 
        this.timestamp = timestamp; 
        this.awsRequestId = awsRequestId; 
    } 
 
    @JsonProperty("prefix") 
    public String getPrefix() { 
        return prefix; 
    } 
 
    @JsonProperty("layer") 
    public String getLayer() { 
        return layer; 
    } 
 
    @JsonProperty("component") 
    public String getComponent() { 
        return component; 
    } 
 
    @JsonProperty("logtype") 
    public LogType getLogType() { 
        return logType; 
    } 
 
    @JsonProperty("objectid") 
    public String getObjectId() { 
        return objectId; 
    } 
 
    @JsonProperty("context") 
    public String getContext() { 
        return context; 
    } 
 
    @JsonProperty("correlationid") 
    public String getCorrelationId() { 
        return correlationId; 
    } 
 
    @JsonProperty("message") 
    public String getMessage() { 
        return message; 
    } 
 
    @JsonProperty("status") 
    public String getStatus() { 
        return status; 
    } 
 
    @JsonProperty("timestamp") 
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC") 
    public Date getTimestamp() { 
        return timestamp; 
    } 
 
    @JsonProperty("awsrequestid") 
    public String getAwsRequestId() { 
        return awsRequestId; 
    } 
 
    @Override 
    public boolean equals(Object o) { 
        if (this == o) { 
            return true; 
        } 
        if (o == null || getClass() != o.getClass()) { 
            return false; 
        } 
        LogEvent logEvent = (LogEvent) o; 
        return Objects.equals(prefix, logEvent.prefix) 
                && Objects.equals(layer, logEvent.layer) 
                && Objects.equals(component, logEvent.component) 
                && Objects.equals(objectId, logEvent.objectId) 
                && Objects.equals(context, logEvent.context) 
                && Objects.equals(correlationId, logEvent.correlationId) 
                && Objects.equals(message, logEvent.message) 
                && Objects.equals(status, logEvent.status) 
                && Objects.equals(timestamp, logEvent.timestamp)
                && Objects.equals(awsRequestId, logEvent.awsRequestId)
                && Objects.equals(logType, logEvent.logType);
    }

    @Override
    public String toString() { 
        return "LogEvent{" 
                + "logType='" + logType + '\'' 
                + ", layer='" + layer + '\'' 
                + ", component='" + component + '\'' 
                + ", objectId='" + objectId + '\'' 
                + ", context='" + context + '\'' 
                + ", correlationId='" + correlationId + '\'' 
                + ", message='" + message + '\'' 
                + ", status='" + status + '\'' 
                + ", timestamp=" + timestamp 
                + ", awsRequestId='" + awsRequestId + '\'' 
                + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(logType, prefix, layer, component, objectId, context,
                correlationId, message, status, timestamp, awsRequestId
        );
    }
}
 