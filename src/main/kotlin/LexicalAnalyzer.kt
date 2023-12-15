import Lexeme.*

class LexicalAnalyzer {

    private enum class State {
        START, // Лексема закончена
        IDENTIFIER,
        VALUE,
        ASSIGN,
        COMMENT
    }

    private var currentState = State.START
    private var lexemeBuffer: String = ""
    private val results = mutableListOf<Lexeme>()

    fun analyze(text: String) {
        text.forEachIndexed { index, char ->
            when (currentState) {
                State.START -> {
                    clearBuffer()

                    when {
                        char in listOf(' ', '\n', '\t', '\r') -> {}

                        char.isRomanDigit() -> {
                            addToBuffer(char)
                            currentState = State.VALUE
                        }

                        char.isLetter() -> {
                            addToBuffer(char)
                            currentState = State.IDENTIFIER
                        }

                        char == '{' -> {
                            if (text.substring(startIndex = index).contains('}')) {
                                currentState = State.COMMENT
                            } else {
                                reportError(index, "Незакрытый комментарий")
                            }
                        }

                        char == '(' -> {
                            if (text.substring(startIndex = index).contains(')')) {
                                currentState = State.START
                            } else {
                                reportError(index, "Незакрытая скобка")
                            }
                        }

                        char == ':' -> {
                            addToBuffer(char)
                            currentState = State.ASSIGN
                        }

                        else -> {
                            addToBuffer(char)
                            val type = KNOWN_LEXEMES.find { it.value == lexemeBuffer }?.type ?: Type.UNKNOWN
                            val value = lexemeBuffer
                            addLexeme(index, value, type)
                            currentState = State.START
                        }
                    }
                }

                State.IDENTIFIER -> {
                    if (char.isLetterOrDigit()) {
                        addToBuffer(char)
                    } else {
                        addLexeme(
                            index = index,
                            value = lexemeBuffer,
                            type = KNOWN_LEXEMES.find { it.value == lexemeBuffer }?.type ?: Type.IDENTIFIER
                        )
                        if (!char.isWhitespace()) {
                            addLexeme(
                                index = index,
                                value = char.toString(),
                                type = KNOWN_LEXEMES.find { it.value == char.toString() }?.type ?: Type.UNKNOWN
                            )
                        }
                        currentState = State.START
                    }
                }

                State.VALUE -> {
                    if (char.isRomanDigit()) {
                        addToBuffer(char)
                    } else {
                        addLexeme(index = index, value = lexemeBuffer.removeSuffix("."), type = Type.CONSTANT)
                        if (!char.isWhitespace()) {
                            addLexeme(
                                index = index,
                                value = char.toString(),
                                type = KNOWN_LEXEMES.find { it.value == char.toString() }?.type ?: Type.UNKNOWN
                            )
                        }
                        currentState = State.START
                    }
                }

                State.ASSIGN -> {
                    if (char == '=') {
                        addToBuffer(char)
                        addLexeme(index, lexemeBuffer, Type.ASSIGN_SIGN)
                    } else {
                        addLexeme(index, lexemeBuffer, Type.DELIMITER)
                        addLexeme(index, char.toString(), Type.DELIMITER)
                    }
                    currentState = State.START
                }

                State.COMMENT -> {
                    if (char == '}')
                        currentState = State.START
                }
            }
        }
        print("Исходный код:\n$text\n\n\n")
        results
            .mapIndexed { index, lexeme ->
                checkResult(lexeme, index)
            }
            .forEach { lexeme ->
                when (lexeme.type) {
                    Type.UNKNOWN -> println("\u001b[31m$lexeme")
                    else -> println("\u001b[0m$lexeme")
                }
            }
    }

    private fun checkResult(result: Lexeme, index: Int): Lexeme {
        val lexeme = result as? Lexeme ?: return result;
        return if (lexeme.type == Type.UNKNOWN || lexeme.type == Type.DELIMITER && KNOWN_LEXEMES.none { it.value == lexeme.value }) {
            Lexeme(
                position = lexeme.position ?: 0,
                value = lexeme.value,
                type = Type.UNKNOWN
            )
        } else lexeme
    }

    private fun clearBuffer() {
        lexemeBuffer = ""
    }

    private fun addToBuffer(char: Char) {
        lexemeBuffer += char
    }

    private fun addLexeme(index: Int, value: String, type: Type) {
        val value1 =
            if (type == Type.UNKNOWN) "Недопустимый символ '$value'"
            else value
        results.add(Lexeme(index, value1, type))
    }

    private fun reportError(position: Int, value: String) {
        results.add(Lexeme(position, value, Type.UNKNOWN))
    }

    private fun Char.isRomanDigit(): Boolean {
        return when (this) {
            'I', 'V', 'X', 'L', 'C', 'D', 'M' -> true
            else -> false
        }
    }

    companion object {
        val KNOWN_LEXEMES = listOf(
            Lexeme(value = "if", type = Type.CONDITIONAL_OPERATOR),
            Lexeme(value = "then", type = Type.CONDITIONAL_OPERATOR),
            Lexeme(value = "else", type = Type.CONDITIONAL_OPERATOR),
            Lexeme(value = ";", type = Type.DELIMITER),
            Lexeme(value = "+", type = Type.OPERATORS_SIGN),
            Lexeme(value = "-", type = Type.OPERATORS_SIGN),
            Lexeme(value = "*", type = Type.OPERATORS_SIGN),
            Lexeme(value = "/", type = Type.OPERATORS_SIGN),
            Lexeme(value = "<", type = Type.COMPARISON_SIGN),
            Lexeme(value = ">", type = Type.COMPARISON_SIGN),
            Lexeme(value = "=", type = Type.COMPARISON_SIGN),
            Lexeme(value = ":=", type = Type.ASSIGN_SIGN),
            Lexeme(value = "for", type = Type.LOOP_START),
            Lexeme(value = "do", type = Type.LOOP_END),
            Lexeme(value = "(", type = Type.PARENTHESIS),
            Lexeme(value = ")", type = Type.PARENTHESIS),
        )
    }
}