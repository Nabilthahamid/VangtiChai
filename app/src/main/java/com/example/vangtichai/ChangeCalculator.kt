package com.example.vangtichai

data class NoteCount(val denomination: Int, val count: Int)

/** Integer-only change calculation; includes every denomination, even at zero. */
object ChangeCalculator {
    private val denominations = listOf(500, 100, 50, 20, 10, 5, 2, 1)

    fun calculate(amount: Int): List<NoteCount> {
        require(amount >= 0) { "Amount must be nonnegative" }
        var remaining = amount
        return denominations.map { denomination ->
            val count = remaining / denomination
            remaining %= denomination
            NoteCount(denomination, count)
        }
    }
}
