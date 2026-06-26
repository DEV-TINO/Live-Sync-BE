package com.devtino.livesync;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LiveSyncBeApplicationTests {

    @Test
    void applicationCanBeConstructed() {
        assertDoesNotThrow(LiveSyncBeApplication::new);
    }
}
