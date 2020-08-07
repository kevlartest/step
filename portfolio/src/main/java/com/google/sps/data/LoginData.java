package com.google.sps.data;

import com.google.gson.Gson;

/** Data class for serialising to JSON the LoginData to send to frontend */
public class LoginData {

  private final boolean loggedIn;
  private final String userId;
  private final boolean isUserAdmin;

  public LoginData(boolean loggedIn, String userId, boolean isUserAdmin) {
    this.loggedIn = loggedIn;
    this.userId = userId;
    this.isUserAdmin = isUserAdmin;
  }

  public boolean getLoggedIn() {
    return loggedIn;
  }

  public String getUserId() {
    return userId;
  }

  public boolean isUserAdmin() {
    return isUserAdmin;
  }

  public String toJSON() {
    return new Gson().toJson(this);
  }
}
