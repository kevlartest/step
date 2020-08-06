package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/list-comments")
public class ListCommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String amountParameter = request.getParameter("amount");
    final int amount =
        (amountParameter == null) ? 5 : Integer.parseInt(amountParameter); // Default to 5
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments =
        results.asList(FetchOptions.Builder.withLimit(amount)).stream()
            .map(Comment::new)
            .collect(Collectors.toList());

    response.setContentType("application/json;");
    new Gson().toJson(comments, response.getWriter());
  }
}
