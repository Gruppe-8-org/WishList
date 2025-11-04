package com.wishlist.Controller;


import com.wishlist.Model.Product;
import com.wishlist.Model.User;
import com.wishlist.Model.Wishlist;
import com.wishlist.Model.WishlistProduct;
import com.wishlist.Service.ProductService;
import com.wishlist.Service.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user/{uid}/wishlist")
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
    public String showCreateForm(Model model, @PathVariable("uid") int id) {
       Wishlist newWishlist = new Wishlist(0, null, 0, null, null);
       List<Product> allProducts = productService.getAllProducts();
       model.addAttribute("newWishlist", newWishlist);
       model.addAttribute("allProducts", allProducts);
       return "wishlistcreateform";
    }

    @PostMapping("/create")
    public String addWishlist(@ModelAttribute Wishlist newWishlist, @RequestParam int id, @PathVariable int uid) {

        newWishlist.setProducts(new ArrayList<>());
        newWishlist.setAuthor(id);
        List<User> guests = new ArrayList<>();
        newWishlist.setTitle(newWishlist.getTitle());

        wishlistService.addWishlist(newWishlist, guests);

        return "redirect:/user/{id}/wishlist/" + newWishlist.getID();
    }

    @GetMapping("/{wid}/update")
    public String updateWishlist(Model model, @PathVariable int wid, @PathVariable(value="uid") int id) {
        Wishlist updateWishlist = wishlistService.getWishlistByID(wid);
        List<Product> allProducts = productService.getAllProducts();

        model.addAttribute("updateWishlist", updateWishlist);
        model.addAttribute("allProducts", allProducts);
        return "wishlistupdateform";
    }

    @PostMapping("/{wid}/update")
    public String updateWishlist(@ModelAttribute Wishlist updateWishlist, @PathVariable int wid, @PathVariable(value="uid") int id) {
        wishlistService.updateWishlist(updateWishlist, new ArrayList<>());

        return "redirect:/user/{id}";
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
