package com.magazin.magazina.controllers;
import com.magazin.magazina.models.Stock;
import com.magazin.magazina.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@RequestMapping("/api/v1/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping
    public Stock createStock(
            @RequestBody Stock newStock,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        System.out.println(token);
        System.out.println("\n\n\n\n\n\n  tee");
        // Create new stock and set createdBy using the decoded token
        return stockService.createStock(newStock, token);
    }


    // Get all stocks
    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }


    // Get stock by ID
    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Integer id) {
        Stock stock = stockService.getStockById(id);
        return ResponseEntity.ok(stock);
    }


    @PutMapping("/{id}")
    public Stock updateStock(
            @PathVariable Integer id,
            @RequestBody Stock updatedStock,
            @RequestHeader("Authorization") String authorizationHeader) {

        // Remove "Bearer " prefix from the Authorization header to get the token
        String token = authorizationHeader.replace("Bearer ", "");

        // Update stock and set updatedBy using the decoded token
        return stockService.updateStock(id, updatedStock, token);
    }



    // Delete a stock
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStock(@PathVariable Integer id) {
        stockService.deleteStock(id);
        return ResponseEntity.ok("Stock deleted successfully");
    }
}
