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
import com.google.sps.data.Comment;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  private Map<String,List<Comment>> comments;

    @Override
    public void init() {
        comments = new HashMap<>();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Trim strings to prevent submitting effectively empty fields
        final String email = request.getParameter("email").trim();
        final String body = request.getParameter("body").trim();

        // Don't store a blank comment, or one where body < 15 characters
        if(email != null && body != null && !email.isEmpty() && !body.isEmpty() && body.length() >= 15) {

            final Instant timestamp = Instant.now();

            // The id will be assigned by the database so we set it to 0 here
            final Comment comment = new Comment(0L,email,body,timestamp);

            System.out.println("Timestamp: " + timestamp + " Email: " + email + " Comment: " + body);

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(comment.toDatastoreEntity());

        }
        response.sendRedirect("/index.html");
    }
}
