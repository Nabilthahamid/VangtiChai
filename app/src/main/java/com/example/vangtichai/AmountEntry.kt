package com.example.vangtichai

/** Pure digit-entry rules, independent of Android views. */
object AmountEntry {
    const val MAX_AMOUNT = 999_999_999

    fun appendDigit(amount: Int, digit: Int): Int {
        require(amount in 0..MAX_AMOUNT)
        require(digit in 0..9)
        // Check before multiplying, so the calculation cannot overflow.
        return if (amount > (MAX_AMOUNT - digit) / 10) amount else amount * 10 + digit
    }
}
