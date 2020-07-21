package com.google.sps.data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Comment {
    private final String email, body;
    private final Instant timestamp;

    public Comment(String email, String body, Instant timestamp){
        this.email = email;
        this.body = body;
        this.timestamp = timestamp;
    }

    public String getEmail(){
        return email;
    }
    public String getBody(){
        return body;
    }
    public Instant getTimestamp(){
        return timestamp;
    }

    public String getFormattedTimestamp(){
        return timestamp.truncatedTo(ChronoUnit.SECONDS).toString();
    }
}