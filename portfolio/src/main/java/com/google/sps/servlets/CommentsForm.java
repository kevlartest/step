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

@WebServlet("/login")
public class CommentsForm extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    response.setContentType("text/html;");
    PrintWriter out = response.getWriter();

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {

        String email = userService.getCurrentUser().getEmail();
        request.setAttribute("email", email);

        String logoutUrl = userService.createLogoutURL("/");

        out.println("<form id=\"comment-form\" action=\"/comments\" method=\"POST\">");
        out.println("<h3>Add a comment or <a href=\"" + logoutUrl + "\">logout</a>:</h3>");
        out.println("<label for=\"email\">Email:</label>");
        out.println("<input type=\"email\" id=\"email\" name=\"email\" readonly=\"readonly\" value=\"" + email + "\">");
        out.println("<br><br>");
        out.println("<label for=\"body\">Comment:</label>");
        out.println("<input type=\"text\" id=\"body\" name=\"body\" onkeyup=\"validateComment()\">");
        out.println("<br><br>");
        out.println("<input id=\"submit-button\" type=\"submit\" disabled=\"true\"/>");
        out.println("</form>");

        

    } else {
        String loginUrl = userService.createLoginURL("/");
        out.println("<p>Please <a href=\"" + loginUrl + "\">login</a> to comment.</p>");
    }
  }
}