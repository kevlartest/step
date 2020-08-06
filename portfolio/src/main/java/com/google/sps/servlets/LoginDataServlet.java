package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.LoginData;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/logindata")
public class LoginDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");

    UserService userService = UserServiceFactory.getUserService();

    final boolean loggedIn = userService.isUserLoggedIn();
    final boolean isUserAdmin = loggedIn && userService.isUserAdmin();
    final String userId = loggedIn ? userService.getCurrentUser().getUserId() : "";

    final LoginData loginData = new LoginData(loggedIn, userId, isUserAdmin);

    response.getWriter().println(loginData.toJSON());
  }
}
