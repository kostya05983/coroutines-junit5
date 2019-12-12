package org.it.zoo.junit5.coroutines

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.lang.AssertionError
import java.lang.Exception
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author Konstantin Volivach
 */
class CoroutinesExtension : ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback,
    BeforeEachCallback, AfterEachCallback, AfterAllCallback {
    private companion object {
        const val DEFAULT_CONTEXT_LIST_KEY = "context_list"
        const val DEFAULT_TIMEOUT_DURATION = 10000
        val DEFAULT_TIMEOUT_UNIT = TimeUnit.MILLISECONDS
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext?): Boolean {
        return parameterContext.parameter.type == CoroutinesTestContext::class.java
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        val coroutinesTestContext = CoroutinesTestContext()
        val store = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL)
        val contexts = store.getOrComputeIfAbsent(
            DEFAULT_CONTEXT_LIST_KEY,
            { mutableListOf<CoroutinesTestContext>() }) as MutableList<CoroutinesTestContext>
        contexts.add(coroutinesTestContext)
        return coroutinesTestContext
    }

    override fun beforeTestExecution(context: ExtensionContext) {
        joinActiveTestContext(context)
    }

    override fun afterTestExecution(context: ExtensionContext) {
        joinActiveTestContext(context)
    }

    override fun beforeEach(context: ExtensionContext) {
        joinActiveTestContext(context)
    }

    override fun afterEach(context: ExtensionContext) {
        joinActiveTestContext(context)
    }

    override fun afterAll(context: ExtensionContext) {
        joinActiveTestContext(context)
    }

    private fun joinActiveTestContext(extensionContext: ExtensionContext) {
        if (extensionContext.executionException.isPresent) return

        val currentContexts = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).remove(DEFAULT_CONTEXT_LIST_KEY) as MutableList<CoroutinesTestContext>?
        if (currentContexts != null) {
            for (context in currentContexts) {
                val (timeoutDuration, timeoutUnit) = getTimeoutParams(extensionContext)

                if (context.awaitCompetition(timeoutDuration, timeoutUnit)) {
                    if (context.failed()) {
                        val throwable = context.causeOfFailure()
                        if (throwable is Exception) {
                            throw  throwable
                        } else {
                            throw AssertionError(throwable)
                        }
                    }
                } else {
                    throw TimeoutException("The test execution timed out. Make sure your asynchronous code "
                        + "includes calls to either CoroutinesTestContext.completeNow(), CoroutinesTestContext.failNow() "
                        + "or Checkpoint#flag()");
                }
            }
        }
    }

    private fun getTimeoutParams(extensionContext: ExtensionContext): Pair<Int, TimeUnit> {
        val testMethod = extensionContext.testMethod
        return if (testMethod.isPresent && testMethod.get().isAnnotationPresent(Timeout::class.java)) {
            val timeout = testMethod.get().getAnnotation(Timeout::class.java)
            Pair(timeout.value, timeout.timeUnit)
        } else if (extensionContext.requiredTestClass.isAnnotationPresent(Timeout::class.java)) {
            val timeout = extensionContext.requiredTestClass.getAnnotation(Timeout::class.java)
            Pair(timeout.value, timeout.timeUnit)
        } else {
            Pair(DEFAULT_TIMEOUT_DURATION, DEFAULT_TIMEOUT_UNIT)
        }
    }
}