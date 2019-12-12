package org.it.zoo.junit5.coroutines

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class CoroutinesTestContext {
    private companion object {
        const val DEFAULT_CHECKPOINT_AMOUNT = 1
    }

    private val releaseLatch: CountDownLatch = CountDownLatch(DEFAULT_CHECKPOINT_AMOUNT)
    private lateinit var throwableReference: Throwable

    /**
     * Complete the context immediately, making corresponding test pass.
     */
    fun completeNow() {
        releaseLatch.countDown()
    }

    /**
     * Make the context fail immediately, making the corresponding test fail.
     *
     * @param ex the cause of failure
     */
    fun failNow(ex: Throwable) {
        throwableReference = ex
        releaseLatch.countDown()
    }

    /**
     * Check if the context has been marked has failed or not.
     */
    fun failed(): Boolean {
        return ::throwableReference.isInitialized
    }

    /**
     * @return the cause of faulure, or null if test context hasn't failed
     */
    fun causeOfFailure(): Throwable? {
        return throwableReference
    }

    fun awaitCompetition(timeout: Long, unit: TimeUnit): Boolean {
        return failed() && releaseLatch.await(timeout, unit)
    }
}