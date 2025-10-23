package com.wishlist.Controller;


import com.wishlist.Model.Product;
import com.wishlist.Model.User;
import com.wishlist.Model.Wishlist;
import com.wishlist.Service.ProductService;
import com.wishlist.Service.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class WishlistController {
    private final WishlistService wishlistService;
    private final ProductService productService;

    public WishlistController(WishlistService wishlistService, ProductService productService) {
        this.wishlistService = wishlistService;
        this.productService = productService;
    }
    @GetMapping("/")
    public String showFrontPage() {
        return "frontpage";
    }
    //I postmappen har jeg kommenteret på sessions, det skal højst sandsynligt tilføjes til dem alle, for netop at knytte det til et ID
    @GetMapping("/wishlist/create")
    public String showCreateForm(Model model) {
        model.addAttribute("wishlist", new Wishlist());
        return "add-wishlist";
    }

    @PostMapping("/wishlist/create")
    public String addWishlist(@RequestParam String wishlistName) {
        Wishlist wishlist = new Wishlist();
        wishlist.setProducts(new ArrayList<>());

        // hardcoded user id for at teste om tingene virker
        int hardcodedUserId = 1;

        wishlist.setAuthor(hardcodedUserId);
        List<User> guests = new ArrayList<>(); // vi skal bruge den her senere, men lige nu er den bare tom
        wishlist.setTitle(wishlistName);
        wishlistService.addWishlist(wishlist, guests);

        return "redirect:/wishlist/" + wishlist.getID();
    }


    //Den nedenunder tror jeg skal bruges senere hen når vi tilføjer sessions.
    //Det tilføjer et ID-tjek på brugeren, så man ikke bare kan komme derhen vedhjælp af endpointet

//    public String addWishlist(@RequestParam String wishlistName, HttpSession session) {
////        Integer userId = (Integer) session.getAttribute("userId");
////        if (userId == null) {
////            return "redirect:/";
////        }
//
//        Wishlist wishlist = new Wishlist();
//        List<User> guests = new ArrayList<>();
//        wishlist.setTitle(wishlistName);
//
//        wishlist.setID(userId);
//
//        wishlistService.addWishlist(wishlist, guests);
//        return "redirect:/wishlist";
//    }

    //her skal vi også have tilføjet sessions.
    @GetMapping("/wishlist/{id}")
    public String viewWishlist(@PathVariable("id") int id, Model model) {
        Wishlist wishlist = wishlistService.getWishlistByID(id);

        List<Product> products = productService.getProductsByWishlistID(id);

        model.addAttribute("wishlist", wishlist);
        model.addAttribute("products", products);
        return "view-wishlist";
    }
    @PostMapping("/wishlist/delete/{id}")
    public String deleteWishlist(@PathVariable int id) {
        wishlistService.deleteWishlistByID(id);
        return "redirect:/wishlist";
    }
}
