package com.wishlist.Controller;

import com.wishlist.Model.Product;
import com.wishlist.Model.User;
import com.wishlist.Model.Wishlist;
import com.wishlist.Model.WishlistProduct;
import com.wishlist.Service.ProductService;
import com.wishlist.Service.WishlistService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/user")
@ComponentScan("com.wishlist.Service")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String productPage (Model model) {
        List<Product> products = productService.getAllProducts();

        model.addAttribute("products", products);
        return "productpage";
    }

    @GetMapping("/products/{id}")
    public String productPageByID (@PathVariable int id, Model model) {
        Product productByID = productService.getProductByID(id);

        model.addAttribute("productByID", productByID);
        return "showproduct";
    }

    @GetMapping("/products/add")
    public String addProduct (Model model) {
        Product newProduct = new Product(0, null, null, null, 0);
        model.addAttribute("newProduct", newProduct);
        return "add-product";
    }

    @PostMapping("/products/add")
    public String addProduct (@ModelAttribute Product newProduct) {
        productService.addProduct(newProduct);
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/update")
    public String updateProduct (Model model, @PathVariable int id) {
        Product updateProduct = productService.getProductByID(id);

        Product productDTO = new Product();
        productDTO.setTitle(updateProduct.getTitle());
        productDTO.setManufacturer(updateProduct.getManufacturer());
        productDTO.setPrice(updateProduct.getPrice());

        model.addAttribute("ID", id);
        model.addAttribute("productDTO", productDTO);
        return "update-product";
    }

    @PostMapping("/products/{id}/update")
    public String updateProduct (@ModelAttribute Product productDTO, @PathVariable int id) {
        Product updateProduct = new Product(id, productDTO.getTitle(), productDTO.getManufacturer(), productDTO.getPathToImage(), productDTO.getPrice());
        productService.updateProduct(updateProduct);
        return "redirect:/user/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct (@PathVariable int id) {
        productService.deleteProductByID(id);
        return "redirect:/user/products";
    }







}