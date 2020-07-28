package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/delete-comment")
public class DeleteCommentServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType("text/xml");

      try {
            long id = Long.parseLong(request.getParameter("id"));

            Key taskEntityKey = KeyFactory.createKey("Comment", id);
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.delete(taskEntityKey);

            response.setStatus(200); // Successfully deleted
      } catch(Exception e){
            System.err.println("There was an error deleting the comment!");
            e.printStackTrace();
            response.setStatus(500); // Internal server error
      }



  }
}