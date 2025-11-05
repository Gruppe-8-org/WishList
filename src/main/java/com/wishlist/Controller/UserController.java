package com.wishlist.Controller;

import com.wishlist.Model.*;
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
        User newUser = new User();
        model.addAttribute("newUser", newUser);
        return "add-user";
    }

    @PostMapping("/create")
    public String addUser(@ModelAttribute User newUser) {
        userService.addUser(newUser);
        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String LoginUser(Model model) {
        User emptyUser = new User();
        model.addAttribute("user", emptyUser);
        return "login-user";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user) {
        int userID = userService.getUserByUsername(user.getUsername()).getID();
        userService.login(user.getUsername(), user.getPassword());
        return "redirect:/user/" + userID;
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
    public String updateUser(Model model, @PathVariable(value="uid") int id) {
        User updateUser = userService.getUserByID(id);

        User userDTO = new User();
        userDTO.setName(updateUser.getName());
        userDTO.setUsername(updateUser.getUsername());
        userDTO.setPassword(updateUser.getPassword());

        model.addAttribute("ID", id);
        model.addAttribute("userDTO", userDTO);
        return "update-user";
    }

    @PostMapping("/{uid}/update")
    public String updateUser(@ModelAttribute User userDTO, @PathVariable(value="uid") int id) {

        User updateUser = new User(id, userDTO.getName(), userDTO.getUsername(), userDTO.getPassword());
        userService.updateUser(updateUser);
        return "redirect:/user/login";
    }

    @PostMapping("/{uid}/delete")
    public String deleteUser(@PathVariable int uid) {
        userService.deleteUserByID(uid);
        return "redirect:/";
    }
}