package com.avanzarit.platform.saas.aws.lambda.logstream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The CloudWatchLogsContainer is a Java object representation of the event that is being sent to AWS Kinesis when a set
 * of log events filtered out by a subscription filter is ready for processing.
 */
@JsonIgnoreProperties
public class CloudWatchLogsContainer {
    private String owner;
    private String logGroup;
    private String logStream;
    private List<String> subscriptionFilters;
    private String messageType;
    private List<CloudWatchLogEvent> logEvents;

    /**
     * Empty constructor required by Jackson for instantiating this object.
     */
    public CloudWatchLogsContainer() {
    }

    @JsonProperty("owner")
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @JsonProperty("logGroup")
    public String getLogGroup() {
        return logGroup;
    }

    public void setLogGroup(String logGroup) {
        this.logGroup = logGroup;
    }

    @JsonProperty("logStream")
    public String getLogStream() {
        return logStream;
    }

    public void setLogStream(String logStream) {
        this.logStream = logStream;
    }

    @JsonProperty("subscriptionFilters")
    public List<String> getSubscriptionFilters() {
        return subscriptionFilters;
    }

    public void setSubscriptionFilters(List<String> subscriptionFilters) {
        this.subscriptionFilters = subscriptionFilters;
    }

    @JsonProperty("messageType")
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @JsonProperty("logEvents")
    public List<CloudWatchLogEvent> getLogEvents() {
        return logEvents;
    }

    public void setLogEvents(List<CloudWatchLogEvent> logEvents) {
        this.logEvents = logEvents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CloudWatchLogsContainer that = (CloudWatchLogsContainer) o;

        if (owner != null ? !owner.equals(that.owner) : that.owner != null) {
            return false;
        }
        if (logGroup != null ? !logGroup.equals(that.logGroup) : that.logGroup != null) {
            return false;
        }
        if (logStream != null ? !logStream.equals(that.logStream) : that.logStream != null) {
            return false;
        }
        if (subscriptionFilters != null ? !subscriptionFilters.equals(that.subscriptionFilters) :
                that.subscriptionFilters != null) {
            return false;
        }
        if (messageType != null ? !messageType.equals(that.messageType) : that.messageType != null) {
            return false;
        }
        return !(logEvents != null ? !logEvents.equals(that.logEvents) : that.logEvents != null);
    }

    @Override
    public int hashCode() {
        int result = owner != null ? owner.hashCode() : 0;
        result = 31 * result + (logGroup != null ? logGroup.hashCode() : 0);
        result = 31 * result + (logStream != null ? logStream.hashCode() : 0);
        result = 31 * result + (subscriptionFilters != null ? subscriptionFilters.hashCode() : 0);
        result = 31 * result + (messageType != null ? messageType.hashCode() : 0);
        result = 31 * result + (logEvents != null ? logEvents.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CloudWatchLogsContainer{"
                + "owner='" + owner + '\''
                + ", logGroup='" + logGroup + '\''
                + ", logStream='" + logStream + '\''
                + ", subscriptionFilters=" + subscriptionFilters
                + ", messageType='" + messageType + '\''
                + ", logEvents=" + logEvents
                + '}';
    }
}
