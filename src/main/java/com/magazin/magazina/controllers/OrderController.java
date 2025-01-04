package com.magazin.magazina.controllers;

import com.magazin.magazina.config.JwtService;
import com.magazin.magazina.models.*;
import com.magazin.magazina.repositories.ProductRepository;
import com.magazin.magazina.repositories.UserRepository;
import com.magazin.magazina.request.CustomerRequest;
import com.magazin.magazina.request.OrderItemRequest;
import com.magazin.magazina.request.OrderRequest;
import com.magazin.magazina.request.OrderWithCustomerRequest;
import com.magazin.magazina.services.OrderService;
import com.magazin.magazina.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ProductRepository productRepository;



    // Orders
    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        Optional <Order> order = orderService.getOrderById(id);
        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Order Items
    @GetMapping("/order-items")
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        return ResponseEntity.ok(orderService.getAllOrdersItem());
    }

    @GetMapping("/order-items/{id}")
    public ResponseEntity<?> getOrderItemById(@PathVariable Integer id) {
        Optional<OrderItem> orderItem = orderService.getOrderItemById(id);
        if (orderItem.isPresent()) {
            return ResponseEntity.ok(orderItem.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/create")
    public Order createOrder(@RequestBody OrderWithCustomerRequest request, @RequestHeader("Authorization") String authorizationHeader) {
        List<OrderItem> orderItems = new ArrayList<>();
        String token = authorizationHeader.replace("Bearer ", "");
        System.out.println("\n\ntoken: " + token);
        OrderRequest orderRequest = request.getOrderRequest();
        CustomerRequest customerRequest = request.getCustomerRequest();

        System.out.println("\n\n\n\n:::::\n\n\n");
        System.out.println(orderRequest);
        System.out.println(customerRequest);

        System.out.println("\n\nprinted:\n\n");

        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            // Find the product using productId from the request
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + itemRequest.getProductId()));

            // Create an OrderItem from the request
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product); // Map the product
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItems.add(orderItem);
        }

        return orderService.createOrder(token, orderItems, orderRequest.getPaymentMethod(), customerRequest);
    }


    // Endpoint to add an individual order item to an order
    @PostMapping("/add-item")
    public OrderItem addOrderItem(@RequestBody OrderItemRequest orderItemRequest) {
        Order order = new Order();
        order.setId(orderItemRequest.getOrderId());  // Ensure the Order is already created

        // Create the order item using the service
        return orderService.createOrderItem(order, orderItemRequest.getProductId(),
                orderItemRequest.getQuantity());
    }


}
