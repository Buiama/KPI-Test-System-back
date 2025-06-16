package com.kpi.authservice.exceptions;

public class GroupNotFoundException extends IllegalStateException{
    private static final String message = "Group is not valid";
    public GroupNotFoundException() {
        super(message);
    }
    public GroupNotFoundException(String message) {
        super(message);
    }
}
