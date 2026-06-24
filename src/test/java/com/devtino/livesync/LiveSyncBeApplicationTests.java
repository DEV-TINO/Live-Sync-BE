package com.devtino.livesync;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "JWT_SECRET_KEY=test-secret-key-for-context-loads-at-least-32-bytes"
})
class LiveSyncBeApplicationTests {

    @Test
    void contextLoads() {
    }

}
