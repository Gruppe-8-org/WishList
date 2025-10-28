package com.wishlist.Controller;

import com.wishlist.Model.User;
import com.wishlist.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping("/create")
    public String addUser(@ModelAttribute User user) {
        userService.addUser(user);
        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String LoginUser(Model model) {
        model.addAttribute("user", new User());
        return "login-user";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user) {
        userService.getUserByID(user.getID());
        return "redirect:/user/" + user.getID();
    }

    //Hvordan ved man hvilken bruger man vil opdaterer?
    @GetMapping("/{id}/update")
    public String updateUser(Model model, @PathVariable int id) {
        model.addAttribute("user", new User());
        return "update-user";
    }

    @PostMapping("/{id}/update")
    public String updateUser(@ModelAttribute User user, @PathVariable int id) {
        userService.updateUser(user);
        return "redirect:/user/login";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable int id) {
        userService.deleteUserByID(id);
        return "redirect:/";
    }
}
