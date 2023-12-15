import java.io.File
import java.io.InputStream

private const val FILE_PATH = "C:\\Documents\\Uni\\7 Семестр\\СПО (7 вариант)\\Лаба 2\\code.txt"


fun main() {

    //Получаем данный из файла
    val code = readLinesFromFile(FILE_PATH)

    val trimmedCode = code.trimIndent()
    val lexicalAnalyzer = LexicalAnalyzer()
    lexicalAnalyzer.analyze(trimmedCode)
}

fun readLinesFromFile(filePath: String): String {
    val lines = mutableListOf<String>()
    File(filePath).forEachLine { word ->
        lines.add(word)
    }
    return lines.joinToString("\n")
}
