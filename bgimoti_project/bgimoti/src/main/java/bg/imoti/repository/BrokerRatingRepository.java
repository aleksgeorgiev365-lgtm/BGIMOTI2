package bg.imoti.repository;

import bg.imoti.model.BrokerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BrokerRatingRepository extends JpaRepository<BrokerRating, Long> {

    /** Всички оценки за даден брокер */
    List<BrokerRating> findByBrokerId(Long brokerId);

    /** Средна оценка за брокер */
    @Query("SELECT AVG(r.rating) FROM BrokerRating r WHERE r.broker.id = :brokerId")
    Double avgRatingByBroker(@Param("brokerId") Long brokerId);

    /** Брой оценки за брокер */
    long countByBrokerId(Long brokerId);
}
