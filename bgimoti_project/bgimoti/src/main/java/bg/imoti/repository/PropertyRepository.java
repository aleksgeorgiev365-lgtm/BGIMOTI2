package bg.imoti.repository;

import bg.imoti.model.Property;
import bg.imoti.model.Property.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * Достъп до таблицата с имоти — поддържа търсене и филтриране
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    /** Всички активни обяви */
    List<Property> findByActiveTrueOrderByCreatedAtDesc();

    /** Филтриране по град */
    List<Property> findByCityAndActiveTrueOrderByCreatedAtDesc(String city);

    /** Филтриране по тип */
    List<Property> findByTypeAndActiveTrueOrderByCreatedAtDesc(PropertyType type);

    /** Пълно филтриране (JPQL) */
    @Query("""
        SELECT p FROM Property p
        WHERE p.active = true
          AND (:city IS NULL OR p.city = :city)
          AND (:type IS NULL OR p.type = :type)
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:minArea  IS NULL OR p.area  >= :minArea)
          AND (:maxArea  IS NULL OR p.area  <= :maxArea)
          AND (:rooms    IS NULL OR p.rooms = :rooms)
          AND (:keyword  IS NULL OR LOWER(p.title)    LIKE LOWER(CONCAT('%',:keyword,'%'))
                                 OR LOWER(p.district) LIKE LOWER(CONCAT('%',:keyword,'%'))
                                 OR LOWER(p.city)     LIKE LOWER(CONCAT('%',:keyword,'%')))
        ORDER BY p.createdAt DESC
    """)
    List<Property> search(
        @Param("city")     String city,
        @Param("type")     PropertyType type,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("minArea")  BigDecimal minArea,
        @Param("maxArea")  BigDecimal maxArea,
        @Param("rooms")    Integer rooms,
        @Param("keyword")  String keyword
    );

    /** Обяви на конкретен брокер */
    List<Property> findByBrokerIdAndActiveTrueOrderByCreatedAtDesc(Long brokerId);
}
