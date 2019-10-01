package com.avanzarit.platform.saas.aws.lambda.model;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * A LambdaRouterRecord is the Java object representation of the message that is being put on a Router Kinesis stream.
 */
public class LambdaRouterRecord {

    private String eventSourceArn;
    private String eventId;
    private String eventSource;
    private String eventName;
    private Map<String, AttributeValue> oldImage;
    private Map<String, AttributeValue> newImage;

    @JsonProperty("EventSourceARN")
    public String getEventSourceArn() {
        return eventSourceArn;
    }

    public void setEventSourceArn(String eventSourceArn) {
        this.eventSourceArn = eventSourceArn;
    }

    @JsonProperty("EventID")
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @JsonProperty("EventSource")
    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    @JsonProperty("EventName")
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @JsonProperty("OldImage")
    public Map<String, AttributeValue> getOldImage() {
        return oldImage;
    }

    public void setOldImage(Map<String, AttributeValue> oldImage) {
        this.oldImage = oldImage;
    }

    @JsonProperty("NewImage")
    public Map<String, AttributeValue> getNewImage() {
        return newImage;
    }

    public void setNewImage(Map<String, AttributeValue> newImage) {
        this.newImage = newImage;
    }

    @Override
    public String toString() {
        return "LambdaRouterRecord [eventSourceArn=" + eventSourceArn + ", eventId=" + eventId + ", eventSource="
                + eventSource + ", eventName=" + eventName + ", oldImage=" + oldImage + ", newImage=" + newImage + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
        result = prime * result + ((eventName == null) ? 0 : eventName.hashCode());
        result = prime * result + ((eventSource == null) ? 0 : eventSource.hashCode());
        result = prime * result + ((eventSourceArn == null) ? 0 : eventSourceArn.hashCode());
        result = prime * result + ((newImage == null) ? 0 : newImage.hashCode());
        result = prime * result + ((oldImage == null) ? 0 : oldImage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LambdaRouterRecord other = (LambdaRouterRecord) obj;
        if (eventId == null) {
            if (other.eventId != null) {
                return false;
            }
        } else if (!eventId.equals(other.eventId)) {
            return false;
        }
        if (eventName == null) {
            if (other.eventName != null) {
                return false;
            }
        } else if (!eventName.equals(other.eventName)) {
            return false;
        }
        if (eventSource == null) {
            if (other.eventSource != null) {
                return false;
            }
        } else if (!eventSource.equals(other.eventSource)) {
            return false;
        }
        if (eventSourceArn == null) {
            if (other.eventSourceArn != null) {
                return false;
            }
        } else if (!eventSourceArn.equals(other.eventSourceArn)) {
            return false;
        }
        if (newImage == null) {
            if (other.newImage != null) {
                return false;
            }
        } else if (!newImage.equals(other.newImage)) {
            return false;
        }
        if (oldImage == null) {
            if (other.oldImage != null) {
                return false;
            }
        } else if (!oldImage.equals(other.oldImage)) {
            return false;
        }
        return true;
    }
}
