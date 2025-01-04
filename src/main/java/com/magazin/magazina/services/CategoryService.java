package com.magazin.magazina.services;

import com.magazin.magazina.models.Category;
import com.magazin.magazina.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    private static final String UPLOAD_DIR = "uploads/";

    public Category createCategoryvv(String name, MultipartFile file) throws IOException {
        Category category = new Category();

        // Check if the file is not null and not empty
        if (file != null && !file.isEmpty()) {
            System.out.println("not null");
            // Define a base directory for category images
            String baseDirectoryPath = UPLOAD_DIR + "/categories/";

            // Create a safe folder path for the category name
            String categoryDirectoryPath = baseDirectoryPath + name;

            // Ensure the category directory exists
            File categoryDirectory = new File(categoryDirectoryPath);
            if (!categoryDirectory.exists()) {
                categoryDirectory.mkdirs();  // Create directory if it doesn't exist
            }

            // Sanitize the file name to avoid problematic characters
            String sanitizedFileName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");

            // Create the final file path for storing the uploaded file
            String filePath = categoryDirectoryPath + "/" + sanitizedFileName;

            // Save the file to the specified path
            File targetFile = new File(filePath);
            file.transferTo(targetFile);

            // Set the image URL or file path for the category (adjust according to how you want the path to be saved)
            category.setImageUrl(filePath);  // Set the relative or absolute file path depending on your app setup
            System.out.println("Category Directory Path: " + categoryDirectoryPath);
            System.out.println("Sanitized File Name: " + sanitizedFileName);
            System.out.println("Final File Path: " + filePath);

        }


        // Set category properties
        category.setName(name);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        System.out.println(category);



        // Save the category in the database
        return categoryRepository.save(category);
    }


    public Category updateCategory(Integer id, Category updatedCategory, MultipartFile file) throws IOException {
        Optional<Category> existingCategoryOptional = categoryRepository.findById(id);

        if (existingCategoryOptional.isPresent()) {
            Category existingCategory = existingCategoryOptional.get();

            existingCategory.setName(updatedCategory.getName());
            existingCategory.setUpdatedAt(LocalDateTime.now());

            if (file != null && !file.isEmpty()) {
                String directoryPath = UPLOAD_DIR + updatedCategory.getName();
                File directory = new File(directoryPath);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String filePath = directoryPath + "/" + file.getOriginalFilename();
                file.transferTo(new File(filePath));

                existingCategory.setImageUrl(filePath);
            }

            return categoryRepository.save(existingCategory);
        } else {
            throw new RuntimeException("Category not found with id " + id);
        }
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}
