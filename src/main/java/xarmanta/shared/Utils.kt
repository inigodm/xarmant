
package xarmanta.shared

import java.io.File
import java.util.function.Consumer
import java.util.stream.Stream

fun String.runCommand(workingDir: File,
                      consumer: Consumer<String> = Consumer { println(it) },
                      errorConsumer: Consumer<String> = Consumer { System.err.println(it) }) {
    consumer.accept(this)
    val builder = ProcessBuilder()
    builder.command("sh", "-c", this)
    builder.directory(workingDir)
    val process = builder.start()
    Stream.of(process.inputStream.bufferedReader())
            .forEach { it.forEachLine { i -> consumer.accept("$i\n") } }
    Stream.of(process.errorStream.bufferedReader())
            .forEach { it.forEachLine { i -> errorConsumer.accept("$i\n") } }

}
