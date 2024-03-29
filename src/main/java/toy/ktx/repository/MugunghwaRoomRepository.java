package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;

import java.util.List;

public interface MugunghwaRoomRepository extends JpaRepository<MugunghwaRoom, Long> {

    @Query("select m from MugunghwaRoom m join fetch m.mugunghwaSeat where m.mugunghwa.id= :id")
    List<MugunghwaRoom> getMugunghwaRoomsToSeatByIdWithFetch(@Param("id") Long id);

    @Query("select m from MugunghwaRoom m join fetch m.mugunghwaSeat")
    List<MugunghwaRoom> getMugunghwaRoomsToSeatFetch();

    List<MugunghwaRoom> findAllByMugunghwa(Mugunghwa mugunghwa);
}
