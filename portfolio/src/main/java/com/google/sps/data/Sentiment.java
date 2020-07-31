package com.google.sps.data;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;

import java.io.IOException;

public class Sentiment {
    private final float score;
    private final float magnitude;

    public Sentiment(float score, float magnitude) {
        this.score = score;
        this.magnitude = magnitude;
    }

    public float getScore() {
        return score;
    }

    public float getMagnitude() {
        return magnitude;
    }

    /**
     * Calculates sentiment for the given piece of text
     * @param text The piece of text to apply sentiment analysis on
     * @return A pair of floats, with score,magnitude
     */
    public static Sentiment getSentiment(final String text) throws IOException, NullPointerException {
        final Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        final LanguageServiceClient languageService = LanguageServiceClient.create();
        final com.google.cloud.language.v1.Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
        final float score = sentiment.getScore();
        final float magnitude = sentiment.getMagnitude();
        languageService.close();
        return new Sentiment(score,magnitude);
    }
}
