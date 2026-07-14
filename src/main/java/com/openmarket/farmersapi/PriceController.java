package com.openmarket.farmersapi;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/price")
@CrossOrigin("*")
public class PriceController {

    @Autowired
    private MandiPriceService mandiService;

    @GetMapping("/real")
    public ResponseEntity<?> getRealPrice(
            @RequestParam String crop, 
            @RequestParam String state,
            @RequestParam String season, 
			@RequestParam String quality, @RequestParam(required = false) String district,

			@RequestParam(required = false) String market) {
        
        System.out.println("🌐 [CONTROLLER] Request for: " + crop + " in " + state);

		Map<String, Object> rawData = mandiService.getRealTimePrice(state, crop, season, quality, district, market);

        // If the service failed or API is down
        if (rawData == null) {
            return ResponseEntity.status(503).body("Government API is currently unreachable.");
        }

        List<?> records = (List<?>) rawData.get("records");

        // If no records found, return empty list with 200 OK
        // This allows the frontend to trigger its "fallback" logic gracefully
        if (records == null || records.isEmpty()) {
            return ResponseEntity.ok(List.of()); 
        }

        // Returns the list of market records
        return ResponseEntity.ok(records);
    }
}