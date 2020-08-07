// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Comment;
import com.google.sps.data.Sentiment;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  private static final int MIN_COMMENT_LENGTH = 15;
  private static final int MAX_COMMENT_LENGTH = 2000;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {

    // Bad request by default
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

    final UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      System.err.println("User is not logged in!");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    String userId = userService.getCurrentUser().getUserId();
    String body = request.getParameter("body");

    if (userId == null) {
      System.err.println("Comment userId is null");
      return;
    }

    if (body == null) {
      System.err.println("Comment body is null");
      return;
    }

    // Trim strings to prevent submitting effectively empty fields
    userId = userId.trim();
    body = body.trim();

    // Don't store a blank comment, or one where body isn't > 15 and < 2000 characters
    if (userId.isEmpty()
            || body.isEmpty()
            || body.length() < MIN_COMMENT_LENGTH
            || body.length() > MAX_COMMENT_LENGTH) {
      System.err.println("Comment userId or body are effectively empty!");
      return;
    }

    final Instant timestamp = Instant.now();
    final Sentiment sentiment;
    Comment comment = null;

    try {
      sentiment = new Sentiment(body);
      // Comment instance here is only being used to convert the data using toDatastoreEntity()
      comment = new Comment(userId, body, timestamp, sentiment);
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
      System.err.println("Unable to get Sentiment!");
      e.printStackTrace();
    }

    if (comment == null) {
      System.err.println("Comment object is null!");
      return;
    }

    // If sentiment analysis fails, the Sentiment values will be set to 0;
    // proceed to store the comment anyway.

    try {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(comment.toDatastoreEntity());
      response.setStatus(HttpServletResponse.SC_OK);
      response.sendRedirect("/index.html");
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      System.err.println("There was an error storing the comment!");
      e.printStackTrace();
    }
  }
}
