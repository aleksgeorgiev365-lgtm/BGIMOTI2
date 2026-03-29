package bg.imoti.controller;

import bg.imoti.model.User;
import bg.imoti.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST контролер за автентикация
 *
 * POST /api/auth/login    — вход
 * POST /api/auth/register — регистрация
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")   // Позволява заявки от HTML frontend-а
public class AuthController {

    @Autowired
    private UserService userService;

    // ─────────────────────────────────────────────────────────────
    //  POST /api/auth/login
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody @Valid LoginRequest req) {

        Map<String, Object> body = new HashMap<>();

        Optional<User> result = userService.login(req.getEmail(), req.getPassword());

        if (result.isEmpty()) {
            // ── ГРЕШНИ ДАННИ — върни 401 с ясно съобщение ──────
            body.put("success", false);
            body.put("message", "Невалиден имейл или парола. Моля опитайте отново.");
            return ResponseEntity.status(401).body(body);
        }

        // ── УСПЕШЕН ВХОД ─────────────────────────────────────────
        User user = result.get();
        body.put("success",  true);
        body.put("message",  "Добре дошли, " + user.getFullName() + "!");
        body.put("userId",   user.getId());
        body.put("name",     user.getFullName());
        body.put("email",    user.getEmail());
        body.put("role",     user.getRole().name());

        return ResponseEntity.ok(body);
    }

    // ─────────────────────────────────────────────────────────────
    //  POST /api/auth/register
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody @Valid RegisterRequest req) {

        Map<String, Object> body = new HashMap<>();

        try {
            User user = userService.register(
                req.getFullName(),
                req.getEmail(),
                req.getPassword(),
                req.getRole() != null ? req.getRole() : "CLIENT",
                req.getLicenseNo()
            );

            body.put("success",  true);
            body.put("message",  "Профилът е създаден успешно! Добре дошли, " + user.getFullName() + "!");
            body.put("userId",   user.getId());
            body.put("name",     user.getFullName());
            body.put("email",    user.getEmail());
            body.put("role",     user.getRole().name());

            return ResponseEntity.status(201).body(body);

        } catch (IllegalArgumentException e) {
            // ── ДУБЛИРАН ИМЕЙЛ ───────────────────────────────────
            body.put("success", false);
            body.put("message", e.getMessage());
            return ResponseEntity.status(409).body(body);  // 409 Conflict
        }
    }

    // ── Inner DTO classes ─────────────────────────────────────────

    static class LoginRequest {
        @NotBlank(message = "Имейлът е задължителен")
        @Email(message = "Невалиден имейл адрес")
        private String email;

        @NotBlank(message = "Паролата е задължителна")
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String e) { this.email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
    }

    static class RegisterRequest {
        @NotBlank @Size(min = 2, max = 120)
        private String fullName;

        @NotBlank @Email
        private String email;

        @NotBlank @Size(min = 8, message = "Паролата трябва да е поне 8 символа")
        private String password;

        private String role;
        private String licenseNo;

        public String getFullName() { return fullName; }
        public void setFullName(String n) { this.fullName = n; }
        public String getEmail() { return email; }
        public void setEmail(String e) { this.email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
        public String getRole() { return role; }
        public void setRole(String r) { this.role = r; }
        public String getLicenseNo() { return licenseNo; }
        public void setLicenseNo(String l) { this.licenseNo = l; }
    }
}
