package com.zosh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zosh.model.Food;
import com.zosh.model.Restaurant;
import com.zosh.repository.RestaurantRepository;
import com.zosh.repository.foodRepository;
import com.zosh.response.AiChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiChatServiceImplementation implements AiChatService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private foodRepository foodRepository;

    @Value("${grok.api.key:}")
    private String grokApiKey;

    @Value("${grok.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String grokApiUrl;

    @Value("${grok.model:openai/gpt-oss-120b}")
    private String grokModel;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public AiChatResponse chatWithAi(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return new AiChatResponse("Please ask a question about our restaurants, dishes, or prices!", true);
        }

        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<Food> foods = foodRepository.findAll();

        // If Grok API key is configured, call Grok AI API
        if (grokApiKey != null && !grokApiKey.trim().isEmpty() && !grokApiKey.contains("your_")) {
            try {
                String systemPrompt = buildSystemCatalogPrompt(restaurants, foods);
                String aiReply = callGrokApi(systemPrompt, userMessage);
                if (aiReply != null && !aiReply.trim().isEmpty()) {
                    return new AiChatResponse(aiReply, true);
                }
            } catch (Exception e) {
                System.err.println("Grok API call failed, falling back to smart catalog search: " + e.getMessage());
            }
        }

        // Fallback intelligent natural language matcher based on live DB catalog
        String smartReply = generateSmartCatalogReply(userMessage, restaurants, foods);
        return new AiChatResponse(smartReply, true);
    }

    private String buildSystemCatalogPrompt(List<Restaurant> restaurants, List<Food> foods) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are 'QuickBite AI', an intelligent, polite, and enthusiastic food assistant for the Zosh Food / QuickBite platform.\n");
        sb.append("Your job is to answer customer questions about restaurants, dishes, cuisines, prices (in ₹), vegetarian/non-vegetarian options, and availability based exclusively on the current live catalog provided below.\n");
        sb.append("Always specify the exact restaurant name and food price (e.g. ₹250) whenever asked about dishes.\n");
        sb.append("If the customer writes in Hindi or Hinglish, answer in friendly Hinglish/Hindi. If in English, answer in English.\n\n");

        sb.append("--- LIVE RESTAURANTS CATALOG ---\n");
        if (restaurants.isEmpty()) {
            sb.append("Currently no restaurants are registered.\n");
        } else {
            for (Restaurant r : restaurants) {
                String city = (r.getAddress() != null && r.getAddress().getCity() != null) ? r.getAddress().getCity() : "All Cities";
                sb.append(String.format("- Restaurant: %s | Cuisine: %s | City: %s | Hours: %s | Status: %s\n",
                        r.getName(), r.getCuisineType(), city, r.getOpeningHours(), r.isOpen() ? "Open" : "Closed"));
            }
        }

        sb.append("\n--- LIVE FOOD / MENU DISHES CATALOG ---\n");
        if (foods.isEmpty()) {
            sb.append("Currently no dishes added to the menu.\n");
        } else {
            for (Food f : foods) {
                String restName = (f.getRestaurant() != null) ? f.getRestaurant().getName() : "Unknown Restaurant";
                String cat = (f.getFoodCategory() != null) ? f.getFoodCategory().getName() : "General";
                sb.append(String.format("- Dish: %s | Restaurant: %s | Price: ₹%d | Category: %s | Type: %s | Available: %s | Description: %s\n",
                        f.getName(), restName, f.getPrice(), cat, f.isVegetarian() ? "Veg" : "Non-Veg", f.isAvailable() ? "Yes" : "No", f.getDescription()));
            }
        }

        return sb.toString();
    }

    private String callGrokApi(String systemPrompt, String userMessage) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", grokModel);
        requestBody.put("temperature", 0.4);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", systemPrompt);
        messages.add(sysMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        requestBody.put("messages", messages);

        String jsonPayload = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(grokApiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + grokApiKey.trim())
                .timeout(Duration.ofSeconds(20))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode choices = rootNode.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
        } else {
            throw new RuntimeException("Grok API error: HTTP " + response.statusCode() + " - " + response.body());
        }

        return null;
    }

    private String generateSmartCatalogReply(String query, List<Restaurant> restaurants, List<Food> foods) {
        String lowerQuery = query.toLowerCase().trim();

        // Greeting
        if (lowerQuery.matches("^(hi|hello|hey|namaste|hlo|helo).*") && lowerQuery.length() < 15) {
            return "Namaste! 🙏 Main **QuickBite AI Assistant** hoon. Aap mujhse kisi bhi restaurant, dish, ya price ke baare me pooch sakte hain.\n\n" +
                    "Jaise:\n" +
                    "• *'Mujhe pizza khana hai, kahan milega aur kitna price hai?'*\n" +
                    "• *'Best veg dishes kaunse restaurants me hain?'*\n" +
                    "• *'₹200 ke andar kaunsi dishes available hain?'*";
        }

        // Price budget query (e.g. under 200, below 300, 200 me)
        java.util.regex.Matcher priceMatcher = java.util.regex.Pattern.compile("(under|below|less than|sasta|cheap|andar|budget)\\s*₹?\\s*(\\d+)").matcher(lowerQuery);
        if (priceMatcher.find()) {
            long maxPrice = Long.parseLong(priceMatcher.group(2));
            List<Food> budgetFoods = foods.stream()
                    .filter(f -> f.getPrice() != null && f.getPrice() <= maxPrice)
                    .limit(6)
                    .collect(Collectors.toList());

            if (!budgetFoods.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("₹%d ke budget me ye tasty dishes available hain:\n\n", maxPrice));
                for (Food f : budgetFoods) {
                    String restName = (f.getRestaurant() != null) ? f.getRestaurant().getName() : "Restaurant";
                    sb.append(String.format("• **%s** — **₹%d** (%s) at **%s**\n",
                            f.getName(), f.getPrice(), f.isVegetarian() ? "Veg 🌱" : "Non-Veg 🍗", restName));
                }
                return sb.toString();
            }
        }

        // Vegetarian filter
        boolean vegRequested = lowerQuery.contains("veg") && !lowerQuery.contains("non-veg") && !lowerQuery.contains("nonveg");

        // Search matching foods
        List<Food> matchedFoods = new ArrayList<>();
        for (Food f : foods) {
            String foodName = (f.getName() != null) ? f.getName().toLowerCase() : "";
            String category = (f.getFoodCategory() != null && f.getFoodCategory().getName() != null) ? f.getFoodCategory().getName().toLowerCase() : "";
            String desc = (f.getDescription() != null) ? f.getDescription().toLowerCase() : "";

            boolean nameMatches = lowerQuery.contains(foodName) || foodName.contains(lowerQuery);
            boolean wordMatches = Arrays.stream(lowerQuery.split("\\s+"))
                    .filter(w -> w.length() > 3)
                    .anyMatch(w -> foodName.contains(w) || category.contains(w) || desc.contains(w));

            if (nameMatches || wordMatches) {
                if (!vegRequested || f.isVegetarian()) {
                    matchedFoods.add(f);
                }
            }
        }

        if (!matchedFoods.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Aapki search ke anusaar ye dishes available hain:\n\n");
            for (Food f : matchedFoods.stream().limit(6).collect(Collectors.toList())) {
                String restName = (f.getRestaurant() != null) ? f.getRestaurant().getName() : "Partner Restaurant";
                String city = (f.getRestaurant() != null && f.getRestaurant().getAddress() != null && f.getRestaurant().getAddress().getCity() != null)
                        ? " (" + f.getRestaurant().getAddress().getCity() + ")" : "";
                String vegBadge = f.isVegetarian() ? "Veg 🌱" : "Non-Veg 🍗";
                String hours = (f.getRestaurant() != null && f.getRestaurant().getOpeningHours() != null) ? f.getRestaurant().getOpeningHours() : "9:00 AM - 10:00 PM";

                sb.append(String.format("🍽️ **%s**\n", f.getName()));
                sb.append(String.format("   • **Restaurant:** %s%s\n", restName, city));
                sb.append(String.format("   • **Price:** ₹%d | %s\n", f.getPrice(), vegBadge));
                sb.append(String.format("   • **Timing:** %s\n", hours));
                if (f.getDescription() != null && !f.getDescription().trim().isEmpty()) {
                    sb.append(String.format("   • *Description:* %s\n", f.getDescription()));
                }
                sb.append("\n");
            }
            return sb.toString().trim();
        }

        // Search matching restaurant
        List<Restaurant> matchedRestaurants = restaurants.stream()
                .filter(r -> r.getName() != null && (lowerQuery.contains(r.getName().toLowerCase()) || r.getName().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());

        if (!matchedRestaurants.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Restaurant r : matchedRestaurants) {
                sb.append(String.format("🏪 **%s**\n", r.getName()));
                sb.append(String.format("   • **Cuisine:** %s\n", r.getCuisineType() != null ? r.getCuisineType() : "Multi-Cuisine"));
                sb.append(String.format("   • **Timing:** %s (%s)\n", r.getOpeningHours() != null ? r.getOpeningHours() : "Open", r.isOpen() ? "Currently Open 🟢" : "Closed 🔴"));
                if (r.getAddress() != null && r.getAddress().getCity() != null) {
                    sb.append(String.format("   • **Location:** %s, %s\n", r.getAddress().getStreetAddress() != null ? r.getAddress().getStreetAddress() : "", r.getAddress().getCity()));
                }

                // Show top foods of this restaurant
                List<Food> restFoods = foods.stream()
                        .filter(f -> f.getRestaurant() != null && f.getRestaurant().getId().equals(r.getId()))
                        .limit(5)
                        .collect(Collectors.toList());

                if (!restFoods.isEmpty()) {
                    sb.append("   • **Special Dishes:**\n");
                    for (Food rf : restFoods) {
                        sb.append(String.format("     - %s: ₹%d (%s)\n", rf.getName(), rf.getPrice(), rf.isVegetarian() ? "Veg" : "Non-Veg"));
                    }
                }
                sb.append("\n");
            }
            return sb.toString().trim();
        }

        // Generic catalog summary
        if (restaurants.isEmpty()) {
            return "Filhal system mein koi restaurant registered nahi hai. Restaurant Owner dashboard se restaurant add hone ke baad main aapko sabhi dishes aur prices batane ke liye taiyar hoon!";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Mujhe aapki exact dish nahi mili, lekin humare platform par ye popular restaurants available hain:\n\n");
        for (Restaurant r : restaurants.stream().limit(4).collect(Collectors.toList())) {
            sb.append(String.format("• **%s** (%s) — Timing: %s\n",
                    r.getName(), r.getCuisineType() != null ? r.getCuisineType() : "Special Food", r.getOpeningHours() != null ? r.getOpeningHours() : "Available"));
        }
        sb.append("\nAap kisi specific dish (jaise Biryani, Pizza, Burger, Paneer) ka naam pooch sakte hain aur main aapko restaurant aur exact price bata dunga!");
        return sb.toString();
    }
}
