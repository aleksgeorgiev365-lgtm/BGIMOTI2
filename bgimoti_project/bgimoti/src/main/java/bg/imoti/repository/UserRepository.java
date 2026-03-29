package bg.imoti.repository;

import bg.imoti.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Достъп до таблицата с потребители
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Намери потребител по имейл (за вход) */
    Optional<User> findByEmail(String email);

    /** Проверка дали имейлът вече съществува */
    boolean existsByEmail(String email);
}
