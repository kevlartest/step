package com.google.sps.data;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;

import java.io.IOException;

public class Sentiment {
  private float score = 0;
  private float magnitude = 0;

  private void setSentiment(float score, float magnitude) {
    this.score = score;
    this.magnitude = magnitude;
  }

  public Sentiment(float score, float magnitude) {
    setSentiment(score, magnitude);
  }

  /**
   * Calculates sentiment for the given piece of text
   * This consists of a score and a magnitude
   *
   * @param text The piece of text to apply sentiment analysis on
   */
  public Sentiment(final String text) throws IOException, NullPointerException {
    final Document doc =
            Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
    final LanguageServiceClient languageService = LanguageServiceClient.create();
    final com.google.cloud.language.v1.Sentiment sentiment =
            languageService.analyzeSentiment(doc).getDocumentSentiment();
    final float score = sentiment.getScore();
    final float magnitude = sentiment.getMagnitude();
    languageService.close();
    setSentiment(score,magnitude);
  }

  public float getScore() {
    return score;
  }

  public float getMagnitude() {
    return magnitude;
  }
}
