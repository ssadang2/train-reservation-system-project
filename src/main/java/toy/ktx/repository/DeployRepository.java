package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.Deploy;

import java.time.LocalDateTime;
import java.util.List;

public interface DeployRepository extends JpaRepository<Deploy, Long> {

//    List<Deploy> findByDeparturePlaceAndArrivalPlaceAndDepartureTimeAfter(
//            String departurePlace, String arrivalPlace, LocalDateTime departureTime, LocalDateTime arrivalTime
//    );

    @Query("select d from Deploy d where d.departurePlace = :departurePlace and d.arrivalPlace = :arrivalPlace" +
            " and d.departureTime >= :departureTime")
    List<Deploy> searchDeploy(@Param("departurePlace") String departurePlace, @Param("arrivalPlace") String arrivalPlace,
                                     @Param("departureTime") LocalDateTime departureTime);
}