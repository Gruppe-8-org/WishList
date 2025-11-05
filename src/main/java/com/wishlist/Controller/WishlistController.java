package com.wishlist.Controller;


import com.wishlist.Model.*;
import com.wishlist.Repository.UserRepository;
import com.wishlist.Service.ProductService;
import com.wishlist.Service.UserService;
import com.wishlist.Service.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user/{uid}/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    private final ProductService productService;
    private final UserRepository userRepository;
    private final UserService userService;

    public WishlistController(WishlistService wishlistService, ProductService productService, UserRepository userRepository, UserService userService) {
        this.wishlistService = wishlistService;
        this.productService = productService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/")
    public String showFrontPage() {
        return "index";
    }

    //I postmappen har jeg kommenteret på sessions, det skal højst sandsynligt tilføjes til dem alle, for netop at knytte det til et ID
    @GetMapping("/add")
    public String showCreateForm(Model model, @PathVariable("uid") int id) {
       List<Product> allProducts = productService.getAllProducts();
       List<User> allUsers = userService.getAllUsers();
       WishlistDTO wishlistDTO = new WishlistDTO();
       model.addAttribute("allUsers", allUsers);
       model.addAttribute("wishlistDTO", wishlistDTO);
       model.addAttribute("allProducts", allProducts);
       model.addAttribute("ID", id);
       return "wishlistcreateform";
    }

    @PostMapping("/add")
    public String addWishlist(@ModelAttribute WishlistDTO wishlistDTO, @PathVariable("uid") int authorID) {
        List<WishlistProduct> products = new ArrayList<>();

        wishlistDTO.setProductDescriptions(wishlistDTO.getProductDescriptions().stream().filter(s -> !s.isEmpty()).toList());

        for (int i = 0; i < wishlistDTO.getProductIDs().size(); i++) {
            products.add(new WishlistProduct(productService.getProductByID(wishlistDTO.getProductIDs().get(i)), 0, wishlistDTO.getProductDescriptions().get(i)));
        }

        List<User> guests = new ArrayList<>();

        for (Integer guestID : wishlistDTO.getGuestIDs()) {
            guests.add(userRepository.getUserByID(guestID));
        }

        Wishlist wishlist = new Wishlist(0, wishlistDTO.getWishlistTitle(), authorID, wishlistDTO.getWishlistHeldOn(), products);

        wishlistService.addWishlist(wishlist, guests);
        return "redirect:/user/{uid}";
    }

    @GetMapping("/{wid}/update")
    public String updateWishlist(Model model, @PathVariable int wid, @PathVariable(value="uid") int id) {
        Wishlist updateWishlist = wishlistService.getWishlistByID(wid);
        List<Product> allProducts = productService.getAllProducts();
        List<User> allUsers = userService.getAllUsers();

        WishlistDTO wishlistDTO = new WishlistDTO();
        wishlistDTO.setWishlistTitle(updateWishlist.getTitle());
        wishlistDTO.setWishlistHeldOn(updateWishlist.getHeldOn());
        wishlistDTO.setProductIDs(updateWishlist.getProducts().stream().map(p -> p.getProduct().getID()).toList());
        wishlistDTO.setGuestIDs(wishlistService.getAllWishlistGuests(updateWishlist.getID()));
        wishlistDTO.setProductDescriptions(updateWishlist.getProducts().stream().map(WishlistProduct::getDescription).toList());
        model.addAttribute("wishlistDTO", wishlistDTO);
        model.addAttribute("allProducts", allProducts);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("ID", id);
        return "wishlistupdateform";
    }

    @PostMapping("/{wid}/update")
    public String updateWishlist(@ModelAttribute WishlistDTO wishlistDTO, @PathVariable(value="wid") int wid, @PathVariable(value="uid") int uid) {
        List<WishlistProduct> products = new ArrayList<>();

        for (int i = 0; i < wishlistDTO.getProductIDs().size(); i++) {
            products.add(new WishlistProduct(productService.getProductByID(wishlistDTO.getProductIDs().get(i)), 0, wishlistDTO.getProductDescriptions().get(i)));
        }

        List<User> guests = new ArrayList<>();

        for (Integer guestID : wishlistDTO.getGuestIDs()) {
            guests.add(userRepository.getUserByID(guestID));
        }

        Wishlist updateWishlist = new Wishlist(wid, wishlistDTO.getWishlistTitle(), uid, wishlistDTO.getWishlistHeldOn(), products);
        wishlistService.updateWishlist(updateWishlist, guests);
        return "redirect:/user/{uid}";
    }

    /*
    public String addWishlist(@RequestParam String wishlistName, HttpSession session) {
           Integer userId = (Integer) session.getAttribute("userId");
           if (userId == null) {
               return "redirect:/";
           }

       Wishlist wishlist = new Wishlist();
       List<User> guests = new ArrayList<>();
       wishlist.setTitle(wishlistName);

       wishlist.setID(userId);

       wishlistService.addWishlist(wishlist, guests);
       return "redirect:/wishlist";
}*/

    //her skal vi også have tilføjet sessions.
    @GetMapping("/{wid}")
    public String viewWishlist(@PathVariable int wid, @PathVariable("uid") int id, Model model) {
        Wishlist wishlist = wishlistService.getWishlistByID(id);
        List<WishlistProduct> wishlistProducts = wishlist.getProducts();

        model.addAttribute("wishlist", wishlist);
        model.addAttribute("products", wishlistProducts);
        return "renderwishlist";
    }

    @GetMapping("/{wid}/delete")
    public String deleteWishList(Model model, @PathVariable int uid, @PathVariable int wid) {
        Wishlist deleteWishlist = wishlistService.getWishlistByID(wid);

        model.addAttribute("deleteWishlist", deleteWishlist);
        return "wishlistdeleteform";
    }

    @PostMapping("/{wid}/delete")
    public String deleteWishlist(@PathVariable int wid, @PathVariable int uid) {
        wishlistService.deleteWishlistByID(wid);
        return "redirect:/user/{id}";
    }
}
