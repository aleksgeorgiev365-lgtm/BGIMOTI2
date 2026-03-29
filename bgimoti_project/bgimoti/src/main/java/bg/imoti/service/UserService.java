package bg.imoti.service;

import bg.imoti.model.User;
import bg.imoti.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Бизнес логика за потребителски акаунти:
 * - регистрация (с хеширане на паролата)
 * - вход (с проверка на хеша)
 * - деактивиране на акаунт
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрира нов потребител.
     *
     * @param fullName    име на потребителя
     * @param email       имейл
     * @param rawPassword парола в чист текст
     * @param roleName    CLIENT / BROKER / ADMIN
     * @param licenseNo   номер на лиценз, ако е брокер
     * @return записания потребител
     * @throws IllegalArgumentException ако имейлът вече съществува
     */
    public User register(String fullName, String email, String rawPassword,
                         String roleName, String licenseNo) {

        String normalizedEmail = email == null ? "" : email.toLowerCase().trim();

        // 1. Проверка за дублиран имейл
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Потребител с имейл \"" + normalizedEmail + "\" вече съществува."
            );
        }

        // 2. Определяне на роля
        User.Role role;
        try {
            role = User.Role.valueOf(roleName == null ? "CLIENT" : roleName.toUpperCase().trim());
        } catch (Exception e) {
            role = User.Role.CLIENT;
        }

        // 3. Изграждане и запис
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);

        if (role == User.Role.BROKER) {
            user.setLicenseNo(licenseNo);
        }

        // Ако в User имаш поле active, активираме акаунта по подразбиране
        user.setActive(true);

        return userRepository.save(user);
    }

    /**
     * Проверява имейл и парола.
     *
     * @param email       имейл
     * @param rawPassword парола в чист текст
     * @return Optional<User> — празен при грешни данни
     */
    public Optional<User> login(String email, String rawPassword) {
        String normalizedEmail = email == null ? "" : email.toLowerCase().trim();

        // 1. Търсене по имейл
        Optional<User> opt = userRepository.findByEmail(normalizedEmail);
        if (opt.isEmpty()) {
            return Optional.empty();
        }

        User user = opt.get();

        // 2. Проверка дали акаунтът е активен
        if (user.getActive() == null || !user.getActive()) {
            return Optional.empty();
        }

        // 3. Проверка на паролата
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    /**
     * Намира потребител по ID.
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Деактивира потребител.
     */
    public void deactivate(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }
}