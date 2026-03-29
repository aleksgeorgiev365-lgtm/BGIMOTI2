package bg.imoti.controller;

import bg.imoti.model.Property;
import bg.imoti.model.Property.PropertyType;
import bg.imoti.model.User;
import bg.imoti.repository.BrokerRatingRepository;
import bg.imoti.repository.PropertyRepository;
import bg.imoti.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * REST контролер за имоти
 *
 * GET  /api/properties            — всички активни обяви
 * GET  /api/properties/search     — търсене с филтри
 * GET  /api/properties/{id}       — детайли за конкретна обява
 * POST /api/properties            — публикуване на нова обява (брокери/admin)
 * DELETE /api/properties/{id}     — изтриване (admin)
 * POST /api/properties/{id}/rate  — оценяване на брокер
 */
@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    @Autowired private PropertyRepository propRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private BrokerRatingRepository ratingRepo;

    // ── GET всички обяви ─────────────────────────────────────────
    @GetMapping
    public List<Map<String, Object>> getAll() {
        return propRepo.findByActiveTrueOrderByCreatedAtDesc()
                       .stream().map(this::toMap).toList();
    }

    // ── GET търсене с филтри ──────────────────────────────────────
    @GetMapping("/search")
    public List<Map<String, Object>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minArea,
            @RequestParam(required = false) BigDecimal maxArea,
            @RequestParam(required = false) Integer rooms) {

        PropertyType pType = null;
        if (type != null && !type.isBlank()) {
            try { pType = PropertyType.valueOf(type.toUpperCase()); }
            catch (IllegalArgumentException ignored) {}
        }

        return propRepo.search(
            (city != null && city.isBlank()) ? null : city,
            pType, minPrice, maxPrice, minArea, maxArea,
            rooms,
            (keyword != null && keyword.isBlank()) ? null : keyword
        ).stream().map(this::toMap).toList();
    }

    // ── GET детайли ───────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOne(@PathVariable Long id) {
        return propRepo.findById(id)
            .map(p -> ResponseEntity.ok(toMap(p)))
            .orElse(ResponseEntity.notFound().build());
    }

    // ── POST нова обява ───────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @RequestBody Map<String, Object> body) {

        Map<String, Object> resp = new HashMap<>();
        try {
            Long brokerId = Long.valueOf(body.get("brokerId").toString());
            User broker = userRepo.findById(brokerId)
                .orElseThrow(() -> new IllegalArgumentException("Брокерът не е намерен"));

            Property p = new Property();
            p.setTitle(body.get("title").toString());
            p.setType(PropertyType.valueOf(body.get("type").toString().toUpperCase()));
            p.setPrice(new BigDecimal(body.get("price").toString()));
            p.setArea(new BigDecimal(body.get("area").toString()));
            p.setCity(body.get("city").toString());
            p.setDistrict(body.getOrDefault("district","").toString());
            p.setDescription(body.getOrDefault("description","").toString());
            if (body.get("rooms") != null)
                p.setRooms(Integer.valueOf(body.get("rooms").toString()));
            p.setBroker(broker);
            p.setBadge("new");

            propRepo.save(p);
            resp.put("success", true);
            resp.put("message", "Обявата е публикувана успешно!");
            resp.put("id", p.getId());
            return ResponseEntity.status(201).body(resp);

        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "Грешка при публикуване: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // ── DELETE изтриване ──────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> resp = new HashMap<>();
        propRepo.findById(id).ifPresentOrElse(p -> {
            p.setActive(false);
            propRepo.save(p);
            resp.put("success", true);
            resp.put("message", "Обявата е изтрита.");
        }, () -> {
            resp.put("success", false);
            resp.put("message", "Обявата не е намерена.");
        });
        return ResponseEntity.ok(resp);
    }

    // ── POST оценка на брокер ─────────────────────────────────────
    @PostMapping("/{id}/rate")
    public ResponseEntity<Map<String, Object>> rate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        Map<String, Object> resp = new HashMap<>();
        int rating = Integer.parseInt(body.get("rating").toString());
        if (rating < 1 || rating > 5) {
            resp.put("success", false);
            resp.put("message", "Оценката трябва да е между 1 и 5.");
            return ResponseEntity.badRequest().body(resp);
        }
        resp.put("success", true);
        resp.put("message", "Оценката е записана! Благодарим ви.");
        return ResponseEntity.ok(resp);
    }

    // ── Helper ────────────────────────────────────────────────────
    private Map<String, Object> toMap(Property p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",          p.getId());
        m.put("title",       p.getTitle());
        m.put("type",        p.getType().name().toLowerCase());
        m.put("transaction", p.getTransaction().name().toLowerCase());
        m.put("price",       p.getPrice());
        m.put("area",        p.getArea());
        m.put("rooms",       p.getRooms());
        m.put("floor",       p.getFloor());
        m.put("totalFloors", p.getTotalFloors());
        m.put("city",        p.getCity());
        m.put("district",    p.getDistrict());
        m.put("description", p.getDescription());
        m.put("features",    p.getFeatures());
        m.put("badge",       p.getBadge());
        m.put("createdAt",   p.getCreatedAt());

        User broker = p.getBroker();
        if (broker != null) {
            m.put("brokerName",    broker.getFullName());
            m.put("brokerInitial", broker.getFullName().substring(0,1).toUpperCase());
            m.put("brokerId",      broker.getId());
            Double avg = ratingRepo.avgRatingByBroker(broker.getId());
            m.put("rating",   avg != null ? Math.round(avg * 10.0) / 10.0 : 5.0);
            m.put("reviews",  ratingRepo.countByBrokerId(broker.getId()));
        }
        return m;
    }
}
