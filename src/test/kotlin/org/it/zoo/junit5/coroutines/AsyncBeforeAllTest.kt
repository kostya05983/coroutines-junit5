package org.it.zoo.junit5.coroutines

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AsyncBeforeAllTest {
    private var counter = 0

    @BeforeAll
    fun before(context: CoroutinesTestContext) {
        Thread {
            Thread.sleep(20)
            counter += 1
            context.completeNow()
        }.start()
    }

    @Test
    fun checkCounter() {
        assertEquals(1, counter)
    }
}