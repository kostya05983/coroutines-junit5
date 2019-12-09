package org.it.zoo.junit5.coroutines

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

/**
 * @author Konstantin Volivach
 */
class CoroutinesExtension : ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback,
    BeforeEachCallback, AfterEachCallback, AfterAllCallback {
    private companion object {
        const val DEFAULT_CONTEXT_LIST_KEY = "context_list"
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun afterTestExecution(context: ExtensionContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun beforeEach(context: ExtensionContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun afterEach(context: ExtensionContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun afterAll(context: ExtensionContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun joinActiveTestContext(context: ExtensionContext) {
        if (context.executionException.isPresent) return


    }
}