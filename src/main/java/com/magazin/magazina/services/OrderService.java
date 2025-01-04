package com.magazin.magazina.services;

import com.magazin.magazina.config.JwtService;
import com.magazin.magazina.models.*;
import com.magazin.magazina.repositories.*;
import com.magazin.magazina.request.CustomerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;
    // Method to create an order

    @Autowired
    private StockRepository stockRepository;

    public List <Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Integer id) {
        return Optional.ofNullable(orderRepository.findById(id).orElse(null));
    }


    public List <OrderItem> getAllOrdersItem() {
        return orderItemRepository.findAll();
    }

    public Optional <OrderItem> getOrderItemById(Integer id) {
        return Optional.ofNullable(orderItemRepository.findById(id).orElse(null));
    }

    public Order createOrder(String token, List<OrderItem> orderItems, String paymentMethod, CustomerRequest customerDetails) {
        // Calculate total cost for the order

        String userId = jwtService.getUserIdFromToken(token);
        Integer customerId;

        try {
            customerId = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid userId format in token", e);
        }

        // Fetch the user by ID
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User was not found"));

        boolean hasPlacedFirstOrder = orderRepository.existsByCustomerId(customerId);

        if (!hasPlacedFirstOrder) {
            createCustomer(customer, customerDetails);
        }

        double totalCost = orderItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        // Create new Order
        Order newOrder = new Order();
        newOrder.setCustomer(customer);
        newOrder.setTotalCost(totalCost);
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setUpdatedAt(LocalDateTime.now());

        // Save the order (without order items for now)
        Order savedOrder = orderRepository.save(newOrder);

        // Now, set the order reference for each order item, update stock, and save them
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();

            // Fetch the Stock entry associated with this product
            Stock stock = stockRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new RuntimeException("Stock not found for product ID: " + product.getId()));

            // Subtract the quantity ordered from the stock
            int newStockQuantity = stock.getQuantity() - item.getQuantity();
            if (newStockQuantity < 0) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }
            stock.setQuantity(newStockQuantity);

            // Save the updated stock
            stockRepository.save(stock);

            // Set order reference and other details
            item.setOrder(savedOrder);
            item.setCreatedAt(LocalDateTime.now());
            item.setUpdatedAt(LocalDateTime.now());
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    // Method to create order item (for individual item creation)
    public OrderItem createOrderItem(Order order, Integer product, Integer quantity) {
        // Assuming you already have a product from product repository
        var product1 = productRepository.findById(product)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        OrderItem newItem = new OrderItem();
        newItem.setProduct(product1);
        newItem.setQuantity(quantity);
        //newItem.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));  // Convert String to Enum
        newItem.setCreatedAt(LocalDateTime.now());
        newItem.setUpdatedAt(LocalDateTime.now());

        // Set the order reference
        newItem.setOrder(order);

        // Save and return the new OrderItem
        return orderItemRepository.save(newItem);
    }

    private void createCustomer(User user, CustomerRequest customerDetails) {
        // Check if the customer already has a Customer record
        Customer customer = customerRepository.findByUser(user)
                .orElseGet(() -> new Customer()); // Create a new customer if not found

        // Set the location and other details for the first time
        if (customerDetails.getLatitude() != null && customerDetails.getLongitude() != null) {
            customer.setLatitude(customerDetails.getLatitude());
            customer.setLongitude(customerDetails.getLongitude());
            customer.setAddress(customerDetails.getAddress());
            customer.setCity(customerDetails.getCity());
            customer.setState(customerDetails.getState());
            customer.setCountry(customerDetails.getCountry());
            customer.setPostalCode(customerDetails.getPostalCode());
        }

        // Set timestamps
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        // Associate the customer with the user (the relationship is stored in user field)
        customer.setUser(user);

        // Save the customer information to the database
        customerRepository.save(customer);
    }


}
