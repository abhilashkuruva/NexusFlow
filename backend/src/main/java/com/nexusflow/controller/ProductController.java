package com.nexusflow.controller;

import com.nexusflow.entity.Product;
import com.nexusflow.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all product categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {
        List<Product> products = productService.searchProducts(q);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @Operation(summary = "Create new product")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product productDetails
    ) {
        Product updated = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/summary")
    @Operation(summary = "Get product statistics summary")
    public ResponseEntity<Map<String, Object>> getProductStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", productService.countActiveProducts());
        stats.put("categories", productService.getAllCategories());
        return ResponseEntity.ok(stats);
    }
}