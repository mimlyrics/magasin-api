package com.magazin.magazina.controllers;

import com.magazin.magazina.models.*;
import com.magazin.magazina.repositories.*;
import com.magazin.magazina.services.ProductService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(
        origins = {
                "https://magazina.onrender.com",
                "http://localhost:3000"
        }, // Multiple allowed origins
        allowedHeaders = "*", // Allow all headers
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.OPTIONS}, // Allowed methods
        allowCredentials = "true" // Allow credentials like cookies
)
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;
    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;
    //private static final String UPLOAD_DIR = "uploads/";

    @GetMapping("category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategoryName(@PathVariable String category) {
        try {
            System.out.println("\n\n\nI'm here");
            System.out.println("category " + category);
            // Fetch products by category name
            List<Product> products = productRepository.findByProductCategory_Name(category);

            if (products.isEmpty()) {
                // Return 404 if no products are found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Return the list of products with an OK status
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();

            // Return 500 Internal Server Error in case of an exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createProduct(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "productCategory", required = false) Integer productCategory,
            @RequestParam(value = "productCategoryName", required = false) String productCategoryName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "supplierId", required = false) Integer supplierId,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "length", required = false) Double length,
            @RequestParam(value = "height", required = false) Double height,
            @RequestParam(value = "width", required = false) Double width,
            @RequestParam(value = "files", required = false) MultipartFile[] files)
    {
        String baseUploadDir = "uploads/";
        String categoryUploadDir = baseUploadDir;
        try {
            // Determine the upload directory
            if (productCategory != null) {
                categoryUploadDir = baseUploadDir + productCategoryName;
            }

            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            // Create the directory if it doesn't exist
            File directory = new File(categoryUploadDir);
            if (!directory.exists() && !directory.mkdirs()) {
                return ResponseEntity.status(500).body("Failed to create upload directory.");
            }

            // Save product
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setLength(length);
            product.setHeight(height);
            product.setWeight(weight);
            product.setWidth(width);
            product.setSupplier(supplier);
            
            System.out.println(productCategory);
            // Fetch the ProductCategory from the database
            Optional <ProductCategory> categoryOptional = productCategoryRepository.findById(productCategory);

            if (categoryOptional.isPresent()) {
                ProductCategory category = categoryOptional.get();
                product.setProductCategory(category);
            } else {
                return ResponseEntity.status(400).body("Invalid product category ID.");
            }

            product = productRepository.save(product);

            // Save images
            List<String> imagePaths = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String filename = file.getOriginalFilename();
                    Path filePath = Paths.get(categoryUploadDir, filename);
                    Files.write(filePath, file.getBytes());
                    imagePaths.add(filePath.toString());
                    ProductImage newImage = new ProductImage();
                    newImage.setImageUrl(filePath.toString());
                    newImage.setProduct(product);

                    productImageRepository.save(newImage);
                    System.out.println(filePath);
                    System.out.println(imagePaths);
                }
            }

            return ResponseEntity.ok("Product created successfully with images: " + imagePaths);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload files: " + e.getMessage());
        }
    }
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct2(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "productCategory", required = false) Integer productCategory,
            @RequestParam(value = "productCategoryName", required = false) String productCategoryName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "supplierId", required = false) Integer supplierId,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "length", required = false) Double length,
            @RequestParam(value = "height", required = false) Double height,
            @RequestParam(value = "width", required = false) Double width,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        String baseUploadDir = "uploads/";
        String categoryUploadDir = baseUploadDir;
        try {
            // Determine the upload directory
            if (productCategory != null) {
                categoryUploadDir = baseUploadDir + productCategoryName;
            }

            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            // Create the directory if it doesn't exist
            File directory = new File(categoryUploadDir);
            if (!directory.exists() && !directory.mkdirs()) {
                return ResponseEntity.status(500).body("Failed to create upload directory.");
            }

            // Save product
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setLength(length);
            product.setHeight(height);
            product.setWeight(weight);
            product.setWidth(width);
            product.setSupplier(supplier);

            // Fetch the ProductCategory from the database
            Optional<ProductCategory> categoryOptional = productCategoryRepository.findById(productCategory);

            if (categoryOptional.isPresent()) {
                ProductCategory category = categoryOptional.get();
                product.setProductCategory(category);
            } else {
                return ResponseEntity.status(400).body("Invalid product category ID.");
            }

            product = productRepository.save(product);

            // Save images
            if (files != null) {
                List<String> imagePaths = new ArrayList<>();
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String filename = file.getOriginalFilename();
                        Path filePath = Paths.get(categoryUploadDir, filename);
                        Files.write(filePath, file.getBytes());
                        imagePaths.add(filePath.toString());

                        ProductImage newImage = new ProductImage();
                        newImage.setImageUrl(filePath.toString());
                        newImage.setProduct(product);

                        productImageRepository.save(newImage);
                    }
                }
            }

            return ResponseEntity.ok(product); // Return the saved product
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload files: " + e.getMessage());
        }
    }


    @GetMapping
    public List <Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer productId) {
        try {
            // Fetch the product by ID
            Optional<Product> productOptional = productRepository.findById(productId);

            if (productOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Product not found.");
            }

            Product product = productOptional.get();

            System.out.println(product);

            // Delete associated images
            List<ProductImage> images = productImageRepository.findByProduct(product);

            for (ProductImage image : images) {
                File file = new File(image.getImageUrl());
                if (file.exists() && file.delete()) {
                    System.out.println("Deleted file: " + image.getImageUrl());
                } else {
                    System.out.println("Failed to delete file: " + image.getImageUrl());
                }

                // Delete the image record from the database
                productImageRepository.delete(image);
            }

            // Delete the product record
            productRepository.delete(product);

            return ResponseEntity.ok("Product and associated images deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting product: " + e.getMessage());
        }
    }


    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProduct(
            @PathVariable Integer productId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "productCategory", required = false) Integer productCategory,
            @RequestParam(value = "productCategoryName", required = false) String productCategoryName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "supplierId", required = false) Integer supplierId,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "length", required = false) Double length,
            @RequestParam(value = "height", required = false) Double height,
            @RequestParam(value = "width", required = false) Double width,
            @RequestParam(value = "file", required = false) MultipartFile file) {  // Accept only one file

        try {
            String response = productService.updateProduct(
                    productId, name, price, productCategory, productCategoryName,
                    description, supplierId, weight, length, height, width, file
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body("Error: " + ex.getMessage());
        } catch (IOException ex) {
            return ResponseEntity.status(500).body("Failed to update product: " + ex.getMessage());
        }
    }



}
