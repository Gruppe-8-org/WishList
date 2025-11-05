package com.wishlist.Controller;

import com.wishlist.Model.*;
import com.wishlist.Service.UserService;
import com.wishlist.Service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    public String loginUser(@ModelAttribute User user, HttpSession session, Model model) {
        User dbUser = userService.getUserByUsername(user.getUsername());

        if (dbUser != null && dbUser.getPassword().equals(user.getPassword())) {
            session.setAttribute("userID", dbUser.getID());
            session.setMaxInactiveInterval(30 * 60);
            return "redirect:/user/" + dbUser.getID();
        } else {
            model.addAttribute("wrongCredentials", true);
            return "login-user";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request) {
        session.invalidate();
        request.getSession(true);
        return "redirect:/user/login";
    }



    @GetMapping("/{uid}")
    public String userPage(Model model, @PathVariable int uid, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userID");
        if (userId == null || !userId.equals(uid)) {
            return "redirect:/user/login";
        }


        User userByID = userService.getUserByID(uid);
        List<Wishlist> userWishlists = wishlistService.getAllWishlistsByUserWithID(uid);

        model.addAttribute("userByID", userByID);
        model.addAttribute("userWishlists", userWishlists);
        return "userByID";
    }


    @GetMapping("/{uid}/update")
    public String updateUser(Model model, @PathVariable int uid, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userID");
        if (userId == null || !userId.equals(uid)) {
            return "redirect:/user/login";
        }


        User updateUser = userService.getUserByID(uid);
        User userDTO = new User();
        userDTO.setName(updateUser.getName());
        userDTO.setUsername(updateUser.getUsername());
        userDTO.setPassword(updateUser.getPassword());

        model.addAttribute("ID", uid);
        model.addAttribute("userDTO", userDTO);
        return "update-user";
    }

    @PostMapping("/{uid}/update")
    public String updateUser(@ModelAttribute User userDTO, @PathVariable int uid, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userID");
        if (userId == null || !userId.equals(uid)) {
            return "redirect:/user/login";
        }


        User updateUser = new User(uid, userDTO.getName(), userDTO.getUsername(), userDTO.getPassword());
        userService.updateUser(updateUser);
        return "redirect:/user/" + uid;
    }

    @PostMapping("/{uid}/delete")
    public String deleteUser(@PathVariable int uid, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userID");
        if (userId == null || !userId.equals(uid)) {
            return "redirect:/user/login";
        }


        userService.deleteUserByID(uid);
        session.invalidate();
        return "redirect:/";
    }
}