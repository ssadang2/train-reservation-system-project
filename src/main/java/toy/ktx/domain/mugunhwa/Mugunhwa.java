package toy.ktx.domain.mugunhwa;

import lombok.Data;
import toy.ktx.domain.Train;
import toy.ktx.domain.ktx.KtxRoom;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mugunhwa")
public class Mugunhwa extends Train {

    @OneToMany(mappedBy = "mugunhwa")
    private List<MugunhwaRoom> mugunhwaRoomList = new ArrayList<>();
}
