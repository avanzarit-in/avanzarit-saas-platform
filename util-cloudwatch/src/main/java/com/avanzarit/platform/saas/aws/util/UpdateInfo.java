package com.avanzarit.platform.saas.aws.util;

/**
 * An object providing useful identification information for an update that is passing through the
 * system.
 */
public class UpdateInfo {
    private final String context;
    private final String objectId;
    private final String updateId;

    /**
     * Creates a new UpdateInfo object containing the given context, object id and update id.
     */
    public UpdateInfo(String context, String objectId, String updateId) {
        this.context = context;
        this.objectId = objectId;
        this.updateId = updateId;
    }

    public String getContext() {
        return context;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getUpdateId() {
        return updateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UpdateInfo that = (UpdateInfo) o;

        if (context != null ? !context.equals(that.context) : that.context != null) {
            return false;
        }
        if (objectId != null ? !objectId.equals(that.objectId) : that.objectId != null) {
            return false;
        }
        return !(updateId != null ? !updateId.equals(that.updateId) : that.updateId != null);
    }

    @Override
    public int hashCode() {
        int result = context != null ? context.hashCode() : 0;
        result = 31 * result + (objectId != null ? objectId.hashCode() : 0);
        result = 31 * result + (updateId != null ? updateId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UpdateInfo{"
                + "context='" + context + '\''
                + ", objectId='" + objectId + '\''
                + ", updateId='" + updateId + '\''
                + '}';
    }
}
