import kotlin.random.Random

fun dieRoll(sides: Int): Int {
    require(sides in setOf(4, 6, 8, 10, 12, 20)) {
        "Invalid number of die sides: $sides"
    }
    return Random.nextInt(sides) + 1
}
