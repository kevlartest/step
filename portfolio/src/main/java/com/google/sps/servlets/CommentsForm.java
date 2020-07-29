package com.google.sps.servlets;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.servlets.NicknameServlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

@WebServlet("/login")
public class CommentsForm extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    response.setContentType("text/html;");
    PrintWriter out = response.getWriter();

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
        String loginUrl = userService.createLoginURL("/");
        out.println("<p>Please <a href=\"" + loginUrl + "\">login</a> to comment.</p>");
        return;
    }

    final String logoutUrl = userService.createLogoutURL("/");
    final User user = userService.getCurrentUser();
    final String nickname = NicknameServlet.getUserNickname(user.getUserId());

    if (nickname.isEmpty()) {
        out.println("<p>Hello, " + user.getEmail() + " (<a href =\"" + logoutUrl + "\">Logout</a>)</p>");
        out.println("<p>Please set a nickname to comment:</p>");
        out.println("<form id=\"nickname-form\" method=\"POST\" action=\"/nickname\">");
        out.println("<input name=\"nickname\" id=\"nickname\" onkeyup=\"validateNickname()\"/>");
        out.println("<br><br>");
        out.println("<input id=\"nickname-submit-button\" type=\"submit\" disabled=\"true\"/>");
        out.println("</form>");

        return;
    }

    String userId = user.getUserId();

    // Show comment form
    out.println("<h3>Hello " + nickname + "!</h3>");
    out.println("<form id=\"comment-form\" action=\"/comments\" method=\"POST\">");
    out.println("<h3>Add a comment or <a href=\"" + logoutUrl + "\">logout</a>:</h3>");
    out.println("<label for=\"body\">Comment:</label>");
    out.println("<input type=\"text\" id=\"body\" name=\"body\" onkeyup=\"validateComment()\">");
    out.println("<br><br>");
    out.println("<input id=\"submit-button\" type=\"submit\" disabled=\"true\"/>");
    out.println("</form>");

    }
}