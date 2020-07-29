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
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/nickname")
public class NicknameServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");

        final String userId = request.getParameter("userId");
        String nickname = (userId != null) ? getUserNickname(userId) : "UNDEFINED";

        response.getWriter().println("{ \"nickname\": \" " + nickname + "\" }");
    }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setStatus(400); // Default to bad request

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
        response.setStatus(401); // Unauthenticated
        return;
    }

    try {
        final String nickname = request.getParameter("nickname");
        final String userId = userService.getCurrentUser().getUserId();

        if(nickname == null){
            System.err.println("Nickname is null for userId: " + userId);
            return;
        }
        if(userId == null){
            System.err.println("UserId is null");
            return;
        }

        nickname.trim(); // Make sure it's not blank
        if(nickname.isEmpty() || nickname.length() < 3 || nickname.length() > 20){
            System.err.println("Nickname length too short or long!");
            return;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity entity = new Entity("UserInfo", userId);
        entity.setProperty("id", userId);
        entity.setProperty("nickname", nickname);
        datastore.put(entity);

        response.sendRedirect("/");
    } catch (Exception e){
        System.err.println("There was an error setting a nickname");
    }
  }

  /**
   * Returns the nickname of the user with id, or empty String if the user has not set a nickname.
   */
  public static String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("UserInfo").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return "";
    }
    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }
}