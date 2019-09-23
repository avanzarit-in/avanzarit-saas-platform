package com.avanzarit.platform.saas.aws.lambda.logstream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * The CloudWatchLogEvent is a Java object representation of a single logging event contained in the
 * {@link CloudWatchLogsContainer}.
 */
@JsonIgnoreProperties
public class CloudWatchLogEvent {

    private String id;
    private Date timestamp;
    private String message;

    /**
     * Empty constructor required by Jackson for instantiating this object.
     */
    public CloudWatchLogEvent() {
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CloudWatchLogEvent that = (CloudWatchLogEvent) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) {
            return false;
        }
        return !(message != null ? !message.equals(that.message) : that.message != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CloudWatchLogEvent{"
                + "id='" + id + '\''
                + ", timestamp=" + timestamp
                + ", message='" + message + '\''
                + '}';
    }
}
