package com.wishlist.Controller;


import com.wishlist.Model.Product;
import com.wishlist.Model.User;
import com.wishlist.Model.Wishlist;
import com.wishlist.Service.ProductService;
import com.wishlist.Service.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user/{id}/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    private final ProductService productService;

    public WishlistController(WishlistService wishlistService, ProductService productService) {
        this.wishlistService = wishlistService;
        this.productService = productService;
    }
    @GetMapping("/")
    public String showFrontPage() {
        return "index";
    }
    //I postmappen har jeg kommenteret på sessions, det skal højst sandsynligt tilføjes til dem alle, for netop at knytte det til et ID
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("wishlist", new Wishlist());
        return "wishlistcreateform";
    }

    @PostMapping("/create")
    public String addWishlist(@RequestParam String wishlistName) {
        Wishlist wishlist = new Wishlist();
        wishlist.setProducts(new ArrayList<>());

        // hardcoded user id for at teste om tingene virker
        int hardcodedUserId = 1;

        wishlist.setAuthor(hardcodedUserId);
        List<User> guests = new ArrayList<>(); // vi skal bruge den her senere, men lige nu er den bare tom
        wishlist.setTitle(wishlistName);
        wishlistService.addWishlist(wishlist, guests);

        return "redirect:/user/{id}/wishlist/" + wishlist.getID();
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
    @GetMapping("/{id}")
    public String viewWishlist(@PathVariable("id") int id, Model model) {
        Wishlist wishlist = wishlistService.getWishlistByID(id);

        List<Product> products = productService.getProductsByWishlistID(id);

        model.addAttribute("wishlist", wishlist);
        model.addAttribute("products", products);
        return "renderwishlist";
    }
    @PostMapping("/{id}/delete")
    public String deleteWishlist(@PathVariable int id) {
        wishlistService.deleteWishlistByID(id);
        return "redirect:/wishlist";
    }

    //Vi mangler en update wishlist.
}
