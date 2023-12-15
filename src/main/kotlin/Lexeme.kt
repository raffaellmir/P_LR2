class Lexeme(
    val position: Int? = null,
    val value: String,
    val type: Type
) {

    enum class Type(val description: String) {
        CONDITIONAL_OPERATOR("Оператор условия"), // [if, else, then]
        DELIMITER("Разделитель"), // ;
        IDENTIFIER("Идентификатор"),
        COMPARISON_SIGN("Знак сравнения"), // [>, <, =]
        OPERATORS_SIGN("Знак операции"), // [+, -, *, /]
        CONSTANT("Константа"),
        LOOP_START("Ключесвое слово цикла"), // for
        LOOP_END("Ключесвое слово окончания инициализации цикла"), // do
        PARENTHESIS("Скобка"), // [(, )]
        ASSIGN_SIGN("Знак присваивания"), // :=
        UNKNOWN("Лексическая ошибка")
    }

    override fun toString() =
        if (type != Type.UNKNOWN)
            "$value - ${type.description}"
        else
            "${type.description} - $value (позиция ошибки: $position"
}
