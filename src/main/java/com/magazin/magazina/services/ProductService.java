package com.magazin.magazina.services;


import com.magazin.magazina.models.Product;
import com.magazin.magazina.models.ProductCategory;
import com.magazin.magazina.models.ProductImage;
import com.magazin.magazina.models.Supplier;
import com.magazin.magazina.repositories.ProductCategoryRepository;
import com.magazin.magazina.repositories.ProductImageRepository;
import com.magazin.magazina.repositories.ProductRepository;
import com.magazin.magazina.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public List <Product> getAllProducts(){
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElseThrow();
    }

    public String updateProduct(
            Integer productId,
            String name, Double price, Integer productCategory, String productCategoryName,
            String description, Integer supplierId, Double weight, Double length,
            Double height, Double width, MultipartFile file) throws IOException {

        // Fetch the product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update product details
        if (name != null) product.setName(name);
        if (price != null) product.setPrice(price);
        if (description != null) product.setDescription(description);
        if (weight != null) product.setWeight(weight);
        if (length != null) product.setLength(length);
        if (height != null) product.setHeight(height);
        if (width != null) product.setWidth(width);

        // Update supplier if provided
        if (supplierId != null) {
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            product.setSupplier(supplier);
        }

        // Update product category if provided
        if (productCategory != null) {
            ProductCategory category = productCategoryRepository.findById(productCategory)
                    .orElseThrow(() -> new RuntimeException("Invalid product category ID."));
            product.setProductCategory(category);
        }

        // Save product after details update
        product = productRepository.save(product);

        // Delete old product images from database and file storage
        List<ProductImage> existingImages = productImageRepository.findByProduct(product);
        if (existingImages != null && !existingImages.isEmpty()) {
            // Delete images from the database
            for (ProductImage oldImage : existingImages) {
                productImageRepository.delete(oldImage);
                // Also delete from the file system
                Path filePath = Paths.get(oldImage.getImageUrl());
                Files.deleteIfExists(filePath);
            }
        }

        // Upload new image if provided
        if (file != null && !file.isEmpty()) {
            String baseUploadDir = "uploads/";
            String categoryUploadDir = productCategoryName != null ? baseUploadDir + productCategoryName : baseUploadDir;

            // Create the directory if it doesn't exist
            File directory = new File(categoryUploadDir);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Failed to create upload directory.");
            }

            // Save the new image
            String filename = file.getOriginalFilename();
            Path filePath = Paths.get(categoryUploadDir, filename);
            Files.write(filePath, file.getBytes());

            // Save the new image in the database
            ProductImage newImage = new ProductImage();
            newImage.setImageUrl(filePath.toString());
            newImage.setProduct(product);

            productImageRepository.save(newImage);
        }

        return "Product updated successfully";
    }


}
