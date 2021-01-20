package xarmanta.shared

import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.function.Consumer
import java.util.function.Supplier

class KotlinAsyncRunner() : CoroutineScope {

    private val job = Job() // or SupervisorJob()
    override val coroutineContext = job + Dispatchers.Default

    fun <T> runAsync(supplier: Supplier<T>, consumer: Consumer<T>, delayms: Long = 10000) {
        val job = launch {
            consumer.accept(supplier.get())
        }
        launch {
            cancelCommandOnTimeout(job, delayms)
        }
    }

    fun runAsync(background: Runnable, delayms: Long = 10000) {
        val job = launch {
            background.run()
        }
        launch {
            cancelCommandOnTimeout(job, delayms)
        }
    }

    fun runAsyncReThrowable(background: Runnable, shouldRedo: Supplier<Boolean>, delayms: Long = 10000) {
        val job = launch {
            background.run()
        }
        launch {
            cancelCommandOnTimeout(job, delayms)
        }
        if (job.isCancelled && shouldRedo.get()){
            runAsyncReThrowable(background, shouldRedo, delayms * 2)
        }
    }

    private suspend fun cancelCommandOnTimeout(job: Job, delayMiliseconds: Long) {
        println("Waiting for $job")
        delay(delayMiliseconds)
        println("After 10 seconds")
        if (!job.isCompleted) {
            println("Cancel $job")
            job.cancel()
            job.join()
        }
        println("Canceled job ${job.isCancelled}")
    }
}
