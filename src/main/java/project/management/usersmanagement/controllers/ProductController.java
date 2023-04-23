package project.management.usersmanagement.controllers;

import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.management.usersmanagement.entities.Product;
import project.management.usersmanagement.security.services.ProductService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = productService.listProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return new ResponseEntity<>(new ApiResponse(), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable("id") Integer id, @RequestBody @Valid Product product) {
        product.setId(id);
        productService.updateProduct(id, product);
        return new ResponseEntity<>(new ApiResponse(), HttpStatus.OK);
    }


}
