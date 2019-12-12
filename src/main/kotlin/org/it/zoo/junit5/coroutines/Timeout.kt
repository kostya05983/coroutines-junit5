package org.it.zoo.junit5.coroutines

import java.util.concurrent.TimeUnit

annotation class Timeout(
    val value: Long,
    val timeUnit: TimeUnit = TimeUnit.MILLISECONDS
)