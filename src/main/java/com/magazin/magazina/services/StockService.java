package com.magazin.magazina.services;
import com.magazin.magazina.config.JwtService;
import com.magazin.magazina.models.Product;
import com.magazin.magazina.models.Stock;
import com.magazin.magazina.models.User;
import com.magazin.magazina.repositories.ProductRepository;
import com.magazin.magazina.repositories.StockRepository;
import com.magazin.magazina.repositories.UserRepository;
//import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;


    @Autowired
    private JwtService jwtService; // Assuming JwtUtils is a service for decoding the token

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Stock updateStock(Integer id, Stock updatedStock, String token) {
        // Extract the user ID from the token (used for createdBy/updatedBy)
        String userId = jwtService.getUserIdFromToken(token);
        System.out.println(userId);
        Optional<Stock> existingStockOptional = stockRepository.findById(id);

        if (existingStockOptional.isPresent()) {
            Stock existingStock = existingStockOptional.get();

            // Adjust stock quantity based on movementType
            String movementType = updatedStock.getMovementType().toString();
            int quantityChange = updatedStock.getQuantity();

            switch (movementType) {
                case "DEPARTURE":
                    existingStock.setQuantity(existingStock.getQuantity() - quantityChange);
                    break;
                case "TRANSFERRED":
                    existingStock.setQuantity(existingStock.getQuantity() - quantityChange);
                    break;
                case "ARRIVAL":
                    existingStock.setQuantity(existingStock.getQuantity() + quantityChange);
                    break;
                default:
                    throw new RuntimeException("Invalid movement type: " + movementType);
            }

            // Set additional fields
            existingStock.setMovementType(updatedStock.getMovementType().toString());
            existingStock.setMovementReason(updatedStock.getMovementReason());
            existingStock.setSku(updatedStock.getSku());
            existingStock.setReorderLevel(updatedStock.getReorderLevel());
            existingStock.setStatus(updatedStock.getStatus());
            existingStock.setProduct(updatedStock.getProduct());

            return stockRepository.save(existingStock); // Save the updated stock
        } else {
            throw new RuntimeException("Stock with ID " + id + " not found.");
        }
    }

    public Stock createStock(Stock newStock, String token) {
        String userId = jwtService.getUserIdFromToken(token);
        Integer userIdx = Integer.parseInt(userId);

        System.out.println(userIdx);
        System.out.println("\n\n\nuserIdx");

        // Fetch the Product entity based on the productId passed in the request
        Product product = productRepository.findById(newStock.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + newStock.getProduct().getId()));

        // Fetch the user entity from the database
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userIdx));

        Stock newStock1 = new Stock();

        // Set fields for the new stock
        newStock1.setCreatedBy(user);
        newStock1.setCreatedAt(LocalDateTime.now());
        newStock1.setQuantity(newStock.getQuantity());
        newStock1.setReorderLevel(newStock.getReorderLevel());
        newStock1.setProduct(product);
        newStock1.setSku(newStock.getSku());
        newStock1.setStatus(newStock.getStatus());
        newStock1.setMovementType(newStock.getMovementType().toString());
        newStock1.setMovementReason(newStock.getMovementReason());

        // Adjust quantity based on movementType
        String movementType = newStock.getMovementType().toString();
        System.out.println("\n\nMovement type" + movementType);
        int quantityChange = newStock.getQuantity();

        switch (movementType) {
            case "DEPARTURE":
                newStock1.setQuantity(newStock1.getQuantity() - quantityChange);
                break;
            case "TRANSFERRED":
                newStock1.setQuantity(newStock1.getQuantity() - quantityChange);
                break;
            case "ARRIVAL":
                newStock1.setQuantity(newStock1.getQuantity() + quantityChange);
                break;
            default:
                throw new RuntimeException("Invalid movement type: " + movementType);
        }

        return stockRepository.save(newStock1); // Save the new stock
    }




    // Get all stocks
    public List<Stock> getAllStocks() {
        return stockRepository.findAll(Sort.by(Sort.Direction.ASC, "quantity"));
    }

    // Get a stock by its ID
    public Stock getStockById(Integer id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock with ID " + id + " not found."));
    }

    // Delete a stock
    public void deleteStock(Integer id) {
        Optional<Stock> existingStockOptional = stockRepository.findById(id);
        if (existingStockOptional.isPresent()) {
            stockRepository.deleteById(id);
        } else {
            throw new RuntimeException("Stock with ID " + id + " not found.");
        }
    }
}
