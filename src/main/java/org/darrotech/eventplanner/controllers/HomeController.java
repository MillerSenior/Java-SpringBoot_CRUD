package org.darrotech.eventplanner.controllers;

//import org.launchcode.hellospring.models.HelloSpringModel;
import org.darrotech.eventplanner.data.UserRepository;
import org.darrotech.eventplanner.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LaunchCode
 */
@Controller
public class HomeController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private AuthenticationController authenticationController;
    //for complete authentication

    @GetMapping
    public String index(Model model, HttpSession session) {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
        dateFormat.applyPattern("E-MM-dd-yyyy");
        User user = authenticationController.getUserFromSession(session);
        model.addAttribute("user", "Hello " + user.getUsername().toUpperCase() + "!");
        model.addAttribute("date","Today is: "+dateFormat.format(today));
        return "index";
    }
}
