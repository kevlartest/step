package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/delete-comment")
public class DeleteCommentServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Default to Bad Request
    response.setStatus(400);
    response.setContentType("text/xml");

    try {
        UserService userService = UserServiceFactory.getUserService();
        if(!userService.isUserLoggedIn()){
            System.err.println("User is not logged in!");
            return;
        }

        final long id = Long.parseLong(request.getParameter("id"));

        final Key commentEntityKey = KeyFactory.createKey("Comment", id);
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Entity commentEntity;

        commentEntity = datastore.get(commentEntityKey);

        // Only delete comment if user is author or is admin
        final String commentUserId = (String) commentEntity.getProperty("userId");
        final boolean isUserAdmin = userService.isUserAdmin();
        final String userId = userService.getCurrentUser().getUserId();

        if(!isUserAdmin && !commentUserId.equalsIgnoreCase(userId)){
            System.err.println("User is not author or admin!");
            return;
        }

        datastore.delete(commentEntityKey);
        response.setStatus(200); // Successfully deleted
      } catch(Exception e){
            System.err.println("There was an error deleting the comment!");
            e.printStackTrace();
            response.setStatus(500); // Internal server error
      }
  }
}