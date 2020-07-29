package com.google.sps.data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import com.google.appengine.api.datastore.Entity;

public class Comment {
    private final long id;
    private final String userId;
    private final String body;
    private final Instant timestamp;

    public Comment(long id, String userId, String body, Instant timestamp){
        this.id = id;
        this.userId = userId;
        this.body = body;
        this.timestamp = timestamp;
    }

    /**
     * Constructor used when entity has not been persisted yet, and thus has no ID
     */
    public Comment(String userId, String body, Instant timestamp){
        this.id = 0L;
        this.userId = userId;
        this.body = body;
        this.timestamp = timestamp;
    }

    public long getId(){
        return id;
    }
    public String getUserId(){
        return userId;
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

    public Entity toDatastoreEntity(){
        Entity entity = new Entity("Comment");
        entity.setProperty("userId", userId);
        entity.setProperty("body", body);
        entity.setProperty("timestamp", timestamp.toString());

        return entity;
    }
}