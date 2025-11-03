package com.wishlist.Controller;

import com.wishlist.Model.User;
import com.wishlist.Model.Wishlist;
import com.wishlist.Service.UserService;
import com.wishlist.Service.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    UserService userService;
    WishlistService wishlistService;

    public UserController(UserService userService, WishlistService wishlistService) {
        this.userService = userService;
        this.wishlistService = wishlistService;
    }
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        User newUser = new User(0, null, null, null);
        model.addAttribute("newUser", newUser);
        return "add-user";
    }

    @PostMapping("/create")
    public String addUser(@ModelAttribute User newUser) {
        userService.addUser(newUser);
        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String LoginUser(Model model, @ModelAttribute int id) {
        User loggedUser = userService.getUserByID(id);
        model.addAttribute("loggedUser", loggedUser);
        return "login-user";
    }

    @PostMapping("/start")
    public String loginUser(@ModelAttribute User loggedUser) {
        userService.login(loggedUser.getID(), loggedUser.getPassword());
        return "redirect:/user/" + loggedUser.getID();
    }

    @GetMapping("/{id}")
    public String userPage(Model model, @PathVariable int id) {
        User userByID = userService.getUserByID(id);
        List<Wishlist> userWishlists = wishlistService.getAllWishlistsByUserWithID(id);

        model.addAttribute("userByID", userByID);
        model.addAttribute("userWishlists", userWishlists);
        return "userByID";
    }


    @GetMapping("/{id}/update")
    public String updateUser(Model model, @PathVariable int id) {
        User updateUser = userService.getUserByID(id);
        model.addAttribute("updateUser", updateUser);
        return "update-user";
    }

    @PostMapping("/{id}/update")
    public String updateUser(@ModelAttribute User updateUser) {
        userService.updateUser(updateUser);
        return "redirect:/user/login";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable int id) {
        userService.deleteUserByID(id);
        return "redirect:/";
    }
}