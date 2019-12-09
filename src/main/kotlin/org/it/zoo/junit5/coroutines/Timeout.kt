package org.it.zoo.junit5.coroutines

import java.util.concurrent.TimeUnit

annotation class Timeout(
    val value: Int,
    val timeUnit: TimeUnit = TimeUnit.MILLISECONDS
)