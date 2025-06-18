package ru.practicum.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserState {
    ACTIVE("active"),
    BLOCKED("blocked"),
    DELETED("deleted");

    private final String value;

    UserState(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UserState fromValue(String value) {
        for (UserState state : values()) {
            if (state.value.equalsIgnoreCase(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown state: " + value);
    }
}
