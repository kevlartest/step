package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/delete-comment")
public class DeleteCommentServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {

    // Default to Bad Request
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.setContentType("text/xml");

    try {
        UserService userService = UserServiceFactory.getUserService();
        if(!userService.isUserLoggedIn()){
            System.err.println("User is not logged in!");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            System.err.println("User is not author or admin!");
            return;
        }

        datastore.delete(commentEntityKey);
        response.setStatus(HttpServletResponse.SC_OK); // Successfully deleted
      } catch(Exception e){
            System.err.println("There was an error deleting the comment!");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
  }
}
