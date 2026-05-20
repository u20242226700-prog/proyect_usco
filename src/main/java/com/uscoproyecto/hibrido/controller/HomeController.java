package com.uscoproyecto.hibrido.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/portal";
        }
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/portal";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/portal";
        }
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroPage(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/portal";
        }
        return "auth/registro";
    }
}
