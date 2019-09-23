package com.avanzarit.platform.saas.aws.util.delta;

/**
 * DeltaValue is an enum type that has four possible values that are related to the delta
 * filtering functionality of the system.
 * <p>
 * NEW: A new entity was created in the system and passed delta filtering.<br/>
 * SAME: An entity was updated in the system, but the main content remained the same.<br/>
 * UPDATED: An entity was updated in the system and the actual content was changed.<br/>
 * DELETED: An entity was deleted from the system.
 */
public enum DeltaValue {
    NEW("delta_new"), SAME("delta_same"), UPDATED("delta_updated"), DELETED("delta_deleted");

    private String value;

    DeltaValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
