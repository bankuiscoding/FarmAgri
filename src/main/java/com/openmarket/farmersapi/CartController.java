package com.openmarket.farmersapi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    @Autowired
    private CartItemRepository repo;

    @PostMapping("/add")
    public String addItem(@RequestBody CartItem item) {
        repo.addItem(item);
        return "Item added to cart";
    }

    @GetMapping
    public List<CartItem> getCart() {
        return repo.getAllItems();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        repo.deleteItem(id);
        return "Item removed";
    }

    @PutMapping("/{id}/{qty}")
    public String updateQty(@PathVariable int id, @PathVariable int qty) {
        repo.updateQuantity(id, qty);
        return "Quantity updated";
    }

    @DeleteMapping("/clear")
    public String clear() {
        repo.clearCart();
        return "Cart cleared";
    }
}