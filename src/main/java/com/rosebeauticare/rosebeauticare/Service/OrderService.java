package com.rosebeauticare.rosebeauticare.Service;

import com.rosebeauticare.rosebeauticare.Model.Order;
import com.rosebeauticare.rosebeauticare.Model.Customer;
import com.rosebeauticare.rosebeauticare.Model.Staff;
import com.rosebeauticare.rosebeauticare.Repository.OrderRepository;
import com.rosebeauticare.rosebeauticare.Repository.CustomerRepository;
import com.rosebeauticare.rosebeauticare.Repository.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_CONTENT_TYPES = { "image/jpeg", "image/png" };

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private GridFsService gridFsService;

    public Order createOrder(Order order, List<MultipartFile> images) throws IOException {
        logger.info("Creating order for customer ID: {}", order.getCustomerId());

        // Validate customer
        Customer customer = customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        order.setCustomerName(customer.getName());

        // Validate staff
        Staff staff = staffRepository.findById(order.getStaffId())
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));
        order.setStaffName(staff.getName());

        // Validate dates
        if (order.getOrderDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Order date cannot be in the future");
        }
        if (order.getDueDate().isBefore(order.getOrderDate())) {
            throw new IllegalArgumentException("Due date must be on or after order date");
        }

        // Validate cloth items
        validateClothItems(order.getClothItems());

        // Handle images
        if (images != null && !images.isEmpty()) {
            if (images.size() > 5) {
                throw new IllegalArgumentException("Maximum 5 images allowed");
            }
            List<Order.Image> imageList = images.stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        validateFile(file);
                        try {
                            String fileId = gridFsService.storeFile(file, order.getId());
                            return new Order.Image(fileId, file.getContentType());
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to store image", e);
                        }
                    }).collect(Collectors.toList());
            order.setImages(imageList);
        }

        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully: {}", savedOrder.getId());
        return savedOrder;
    }

    public Order getOrderById(String id) {
        logger.debug("Fetching order by ID: {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public List<Order> getOrdersByCustomerId(String customerId) {
        logger.debug("Fetching orders for customer ID: {}", customerId);
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found");
        }
        return orderRepository.findByCustomerId(customerId);
    }

    private void validateClothItems(List<Order.ClothItem> clothItems) {
        if (clothItems == null || clothItems.isEmpty()) {
            throw new IllegalArgumentException("At least one cloth item is required");
        }
        if (clothItems.size() > 4) {
            throw new IllegalArgumentException("Maximum 4 cloth items allowed");
        }

        List<String> validServices = Arrays.asList("Tailoring", "Aari", "Embroidery", "SareeFleeting");
        List<String> validTailoringSubServices = Arrays.asList("Blouse", "Chudithar", "Frogs", "Leganga", "PattuPaavadaai-Set");

        for (Order.ClothItem item : clothItems) {
            if (!validServices.contains(item.getServiceType())) {
                throw new IllegalArgumentException("Invalid service type: " + item.getServiceType());
            }
            if (item.getServiceType().equals("Tailoring") && !validTailoringSubServices.contains(item.getSubService())) {
                throw new IllegalArgumentException("Invalid sub-service: " + item.getSubService());
            }
            validateCustomization(item.getCustomization(), item.getSubService());
        }
    }

    private void validateCustomization(Order.Customization customization, String subService) {
        if (customization == null || customization.getType() == null) {
            throw new IllegalArgumentException("Customization type is required");
        }
        Map<String, Object> details = customization.getDetails();
        switch (subService) {
            case "Blouse":
                validateBlouseCustomization(customization.getType(), details);
                break;
            case "Chudithar":
                validateChuditharCustomization(customization.getType(), details);
                break;
            case "Frogs":
            case "PattuPaavadaai-Set":
                validateFrogsOrPattuCustomization(customization.getType(), details);
                break;
            default:
                // Handle other sub-services (e.g., Leganga) as needed
        }
    }

    private void validateBlouseCustomization(String type, Map<String, Object> details) {
        List<String> validTypes = Arrays.asList("Normal", "Lining", "Pipping", "Princess", "Pattern");
        if (!validTypes.contains(type)) {
            throw new IllegalArgumentException("Invalid blouse type: " + type);
        }
        if (type.equals("Pipping")) {
            String pippingType = (String) details.get("pippingType");
            if (!Arrays.asList("Saree", "Contrast").contains(pippingType)) {
                throw new IllegalArgumentException("Invalid pipping type: " + pippingType);
            }
        } else if (type.equals("Princess")) {
            String princessType = (String) details.get("princessType");
            if (!Arrays.asList("Full", "Patti").contains(princessType)) {
                throw new IllegalArgumentException("Invalid princess type: " + princessType);
            }
        } else if (type.equals("Pattern")) {
            String neckShape = (String) details.get("neckShape");
            if (!Arrays.asList("U-Shape", "Round", "Diamond", "V", "Square", "Others").contains(neckShape)) {
                throw new IllegalArgumentException("Invalid neck shape: " + neckShape);
            }
            String handType = (String) details.get("handType");
            if (handType != null) {
                if (handType.equals("Short")) {
                    String shortStyle = (String) details.get("shortStyle");
                    if (!Arrays.asList("Puff", "Puff with Border", "Pattern").contains(shortStyle)) {
                        throw new IllegalArgumentException("Invalid short hand style: " + shortStyle);
                    }
                } else if (handType.equals("Elbow")) {
                    String elbowStyle = (String) details.get("elbowStyle");
                    if (!Arrays.asList("Top Puff", "Puff with Border", "Long Puff with Border", "V Puff", "Long V Puff", "Pattern").contains(elbowStyle)) {
                        throw new IllegalArgumentException("Invalid elbow hand style: " + elbowStyle);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid hand type: " + handType);
                }
            }
        }
    }

    private void validateChuditharCustomization(String type, Map<String, Object> details) {
        List<String> validTypes = Arrays.asList("Lining", "Pipping", "Pattern");
        if (!validTypes.contains(type)) {
            throw new IllegalArgumentException("Invalid chudithar type: " + type);
        }
        if (type.equals("Pipping")) {
            String pippingType = (String) details.get("pippingType");
            if (!Arrays.asList("Self", "Contrast").contains(pippingType)) {
                throw new IllegalArgumentException("Invalid pipping type: " + pippingType);
            }
        } else if (type.equals("Pattern")) {
            String neckShape = (String) details.get("neckShape");
            if (!Arrays.asList("U-Shape", "Round", "Diamond", "V", "Square", "Others").contains(neckShape)) {
                throw new IllegalArgumentException("Invalid neck shape: " + neckShape);
            }
            String handType = (String) details.get("handType");
            if (handType != null) {
                if (Arrays.asList("Short", "Elbow", "3/4", "Full").contains(handType)) {
                    String styleKey = handType.toLowerCase() + "Style";
                    String style = (String) details.get(styleKey);
                    List<String> validStyles = handType.equals("Short") ?
                            Arrays.asList("Puff", "Puff with Border", "Pattern") :
                            Arrays.asList("Top Puff", "Puff with Border", "Long Puff with Border", "V Puff", "Long V Puff", "Pattern");
                    if (!validStyles.contains(style)) {
                        throw new IllegalArgumentException("Invalid " + handType.toLowerCase() + " style: " + style);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid hand type: " + handType);
                }
            }
            String bottomType = (String) details.get("bottomType");
            if (!Arrays.asList("Normal", "Patiala", "StraightCut", "Palazzo", "Gathering").contains(bottomType)) {
                throw new IllegalArgumentException("Invalid bottom type: " + bottomType);
            }
            String pocket = (String) details.get("pocket");
            if (!Arrays.asList("Pocket", "WithoutPocket").contains(pocket)) {
                throw new IllegalArgumentException("Invalid pocket option: " + pocket);
            }
        }
    }

    private void validateFrogsOrPattuCustomization(String type, Map<String, Object> details) {
        String neck = (String) details.get("neck");
        if (!Arrays.asList("Round", "V Shape", "U Shape", "Boat").contains(neck)) {
            throw new IllegalArgumentException("Invalid neck type: " + neck);
        }
        String hand = (String) details.get("hand");
        if (!Arrays.asList("Top Puff", "Puff with Border", "Short Puff", "Short Pattern").contains(hand)) {
            throw new IllegalArgumentException("Invalid hand type: " + hand);
        }
        if (type.equals("PattuPaavadaai-Set")) {
            String frontDesign = (String) details.get("frontDesign");
            if (!Arrays.asList("Front Pleat", "Front Shape", "Front Border").contains(frontDesign)) {
                throw new IllegalArgumentException("Invalid front design: " + frontDesign);
            }
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Image size exceeds 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(ALLOWED_CONTENT_TYPES).contains(contentType)) {
            throw new IllegalArgumentException("Invalid image content type: " + contentType);
        }
    }
     public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}