package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.mugunhwa.MugunghwaSeat;

@SpringBootTest
@Transactional
class MugunghwaSeatServiceTest {

    @Autowired
    MugunghwaSeatService mugunghwaSeatService;

    @Test
    void save() {
    }
}