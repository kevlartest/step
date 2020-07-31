package com.google.sps.data;

import com.google.appengine.api.datastore.Entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Comment {
    private final long id;
    private final String userId;
    private final String body;
    private final Instant timestamp;
    private final Sentiment sentiment;

    public Comment(long id, String userId, String body, Instant timestamp, Sentiment sentiment) {
        this.id = id;
        this.userId = userId;
        this.body = body;
        this.timestamp = timestamp;
        this.sentiment = sentiment;
    }

    /**
     * Constructor used when entity has not been persisted yet, and thus has no ID
     */
    public Comment(String userId, String body, Instant timestamp, Sentiment sentiment){
        this(0,userId,body,timestamp,sentiment);
    }

    /**
     * Create comment object from datastore Entity
     */
    public Comment(Entity entity){
        id = entity.getKey().getId();
        userId = (String) entity.getProperty("userId");
        body = (String) entity.getProperty("body");
        timestamp = Instant.parse((String) entity.getProperty("timestamp"));
        final float sentimentScore = (float) entity.getProperty("sentimentScore");
        final float sentimentMagnitude = (float) entity.getProperty("sentimentMagnitude");
        sentiment = new Sentiment(sentimentScore,sentimentMagnitude);
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
    public Sentiment getSentiment() {
        return sentiment;
    }

    public Entity toDatastoreEntity(){
        Entity entity = new Entity("Comment");
        entity.setProperty("userId", userId);
        entity.setProperty("body", body);
        entity.setProperty("timestamp", timestamp.toString());
        entity.setProperty("sentimentScore", sentiment.getScore());
        entity.setProperty("sentimentMagnitude", sentiment.getMagnitude());

        return entity;
    }
}
