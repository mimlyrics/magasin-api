package com.magazin.magazina.controllers;

import com.magazin.magazina.models.Category;
import com.magazin.magazina.models.ProductCategory;
import com.magazin.magazina.repositories.CategoryRepository;
import com.magazin.magazina.repositories.ProductCategoryRepository;
import com.magazin.magazina.services.ProductCategoryService;
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
import java.time.LocalDateTime;
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
@RequestMapping("/api/v1/product_categories")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private CategoryRepository catRepository;

    @Autowired
    private ProductCategoryService categoryService;



    @GetMapping("/subcategories")
    public ResponseEntity<List<Category>> getSubcategoriesByCategoryName(@RequestParam String categoryName) {
        try {
            System.out.println("\n\n\n\n\nCategory" + categoryName );
            // Fetch subcategories
            List<Category> subcategories = catRepository.findByName(categoryName);

            if (subcategories.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // No subcategories found
            }

            return ResponseEntity.ok(subcategories);
        } catch (Exception e) {
            // Handle any unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductCategory> createCategory(
            @RequestParam("name") String name,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId) {

        String UPLOAD_DIR = "uploads/";
        System.out.println("Received name: " + name);
        System.out.println("Received file: " + file);
        System.out.println("Received parentCategoryId: " + parentCategoryId);

        try {
            Category parentCategory = null;

            // If parentCategoryId is provided, fetch the parent category from the database
            if (parentCategoryId != null) {
                parentCategory = catRepository.findById(parentCategoryId)
                        .orElseThrow(() -> new RuntimeException("Parent category not found with ID: " + parentCategoryId));
            }

            String imageUrl = null;

            // If file is provided, handle file upload
            if (file != null && !file.isEmpty()) {
                // Define the directory to store the uploaded file
                String directoryPath = UPLOAD_DIR + "categories/" + name;
                File directory = new File(directoryPath);

                // Create parent directories if they do not exist
                if (!directory.exists() && !directory.mkdirs()) {
                    System.out.println("Failed to create directory: " + directoryPath);
                    return ResponseEntity.status(500).body(null); // Return error if directory creation fails
                }

                // Ensure the directory exists
                Path categoryPath = Paths.get(directoryPath);
                if (!Files.exists(categoryPath)) {
                    Files.createDirectories(categoryPath); // Creates the directory and any nonexistent parent directories
                    System.out.println("Directory created successfully: " + categoryPath);
                }

                // Get original file name and sanitize it
                String filename = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
                System.out.println("Sanitized file name: " + filename);

                // Construct the file path
                Path filePath = categoryPath.resolve(filename);

                // Write the file to the directory
                Files.write(filePath, file.getBytes());
                System.out.println("File uploaded successfully: " + filePath);

                // Set the image URL
                imageUrl = filePath.toString();
            }

            // Create a new ProductCategory object
            ProductCategory category = new ProductCategory();
            category.setName(name);
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            category.setImageUrl(imageUrl);

            // Set the parent category if available
            if (parentCategory != null) {
                category.setCategory(parentCategory);
            }

            // Save the category in the database
            categoryRepository.save(category);

            // Return the created category in the response
            return ResponseEntity.ok(category);

        } catch (IOException e) {
            // Handle file upload failure
            System.out.println("Error uploading file: " + e.getMessage());
            return ResponseEntity.status(500).body(null); // Return 500 server error
        } catch (RuntimeException e) {
            // Handle errors related to parent category lookup
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(400).body(null); // Return 400 bad request for invalid parent category
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id, @RequestBody Category category) {
        // Find category by name
        /*Optional<Category> categoryOptional = categoryRepository.findByName(name);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Category not found");
        }

        Category category = categoryOptional.get();
        String imageUrl = category.getImageUrl();*/

        try {
            // Delete the image file if it exists
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                Path imagePath = Paths.get(category.getImageUrl());
                Files.deleteIfExists(imagePath);
            }

            // Delete the category record from the database
            categoryRepository.deleteById(id);

            return ResponseEntity.ok("Category deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error deleting category: " + e.getMessage());
        }
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductCategory> editCategory(
            @PathVariable("id") Integer id, // Changed from @RequestParam to @PathVariable
            @RequestParam("name") String name,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        // Find the category by ID
        Optional<ProductCategory> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        ProductCategory category = categoryOptional.get();
        String oldImageUrl = category.getImageUrl();

        try {
            // Handle file replacement if a new file is provided
            if (file != null && !file.isEmpty()) {
                // Delete the old file if it exists
                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                    Path oldImagePath = Paths.get(oldImageUrl);
                    Files.deleteIfExists(oldImagePath);
                }

                // Save the new file
                String UPLOAD_DIR = "uploads/";
                String directoryPath = UPLOAD_DIR + "categories/" + name;
                Path categoryPath = Paths.get(directoryPath);
                if (!Files.exists(categoryPath)) {
                    Files.createDirectories(categoryPath);
                }

                String newFileName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
                Path newFilePath = categoryPath.resolve(newFileName);
                Files.write(newFilePath, file.getBytes());

                // Update the category's image URL
                category.setImageUrl(newFilePath.toString());
            }

            // Update category details
            category.setName(name);
            category.setUpdatedAt(LocalDateTime.now());

            // Save the updated category in the database
            categoryRepository.save(category);

            return ResponseEntity.ok(category);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*@GetMapping("/{id}")
    public ProductCategory getProductCategoryById(@PathVariable Integer id) {
        return productCategoryService.getProductCategoryById(id);
    }

    @PostMapping
    public ProductCategory createProductCategory(@RequestBody ProductCategory productCategory) {
        System.out.println(productCategory);
        return productCategoryService.createProductCategory(productCategory);
    }

    @GetMapping
    public List <ProductCategory> getAllProductCategories() {
        return productCategoryService.getAllProductCategories();
    }

    @DeleteMapping("/{id}")
    public void deleteProductCategory(@PathVariable Integer id) {
        productCategoryService.deleteProductCategory(id);
    }

    @PutMapping("/{id}")
    public ProductCategory updateProductCategory(@PathVariable Integer id, @RequestBody ProductCategory productCategory) {
        return productCategoryService.updateProductCategory(id, productCategory);
    }*/


}
