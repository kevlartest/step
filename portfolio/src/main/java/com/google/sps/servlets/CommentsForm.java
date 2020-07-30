package com.google.sps.servlets;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
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

    // Inner class to help send variables as Json
    // null variables are ignored by Gson so only the set variables get sent
    private static class GsonHelper {  
        String loginURL;
        String logoutURL;
        String nickname;
        String email;
    }  

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final UserService userService = UserServiceFactory.getUserService();
        final CommentsForm.GsonHelper helperClass = new CommentsForm.GsonHelper();

        if (!userService.isUserLoggedIn()) {
            // Send login URL to display in login form
            helperClass.loginURL = userService.createLoginURL("/");
        } else {
            // They're logged in, send logoutURL
            helperClass.logoutURL = userService.createLogoutURL("/");

            final User user = userService.getCurrentUser();
            final String nickname = NicknameServlet.getUserNickname(user.getUserId());

            if(nickname.isEmpty()){
                // They don't have a nickname, send email to display in nickname form
                helperClass.email = user.getEmail();
            } else {
                // They have a nickname, send over to display in comment form
                helperClass.nickname = nickname;
            }
        }

        response.setContentType("application/json;");
        new Gson().toJson(helperClass, response.getWriter());
    }
}
