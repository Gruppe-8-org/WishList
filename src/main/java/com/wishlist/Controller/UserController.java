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

    @GetMapping("/{uid}")
    public String userPage(Model model, @PathVariable int uid) {
        User userByID = userService.getUserByID(uid);
        List<Wishlist> userWishlists = wishlistService.getAllWishlistsByUserWithID(uid);

        model.addAttribute("userByID", userByID);
        model.addAttribute("userWishlists", userWishlists);
        return "userByID";
    }


    @GetMapping("/{uid}/update")
    public String updateUser(Model model, @PathVariable int uid) {
        User updateUser = userService.getUserByID(uid);
        model.addAttribute("updateUser", updateUser);
        return "update-user";
    }

    @PostMapping("/{uid}/update")
    public String updateUser(@ModelAttribute User updateUser, @PathVariable int uid) {
        userService.updateUser(updateUser);
        return "redirect:/user/login";
    }

    @PostMapping("/{uid}/delete")
    public String deleteUser(@PathVariable int uid) {
        userService.deleteUserByID(uid);
        return "redirect:/";
    }
}