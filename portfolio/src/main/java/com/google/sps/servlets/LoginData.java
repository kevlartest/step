package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

@WebServlet("/logindata")
public class LoginData extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");

    UserService userService = UserServiceFactory.getUserService();

    final boolean isLoggedIn = userService.isUserLoggedIn();
    final boolean isUserAdmin = isLoggedIn ? userService.isUserAdmin() : false;
    final String userId = isLoggedIn ? userService.getCurrentUser().getUserId() : "";

    response.getWriter().println("{ \"loggedIn\": " + isLoggedIn + ", \"userId\": \"" + userId + "\", \"isUserAdmin\": " + isUserAdmin + " }");
  }
}
