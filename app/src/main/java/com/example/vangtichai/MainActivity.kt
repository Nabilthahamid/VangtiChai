package com.example.vangtichai

import android.os.Bundle
import android.content.res.Configuration
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.edit
import com.example.vangtichai.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var amount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        delegate.localNightMode = preferences.getInt(
            THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isDark = resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        binding.buttonTheme.setImageResource(
            if (isDark) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        )
        binding.buttonTheme.contentDescription = getString(
            if (isDark) R.string.switch_to_light else R.string.switch_to_dark
        )
        binding.buttonTheme.setOnClickListener {
            val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_NO
                else AppCompatDelegate.MODE_NIGHT_YES
            preferences.edit { putInt(THEME_MODE, mode) }
            // AppCompat recreates the Activity; saved instance state keeps the amount.
            delegate.localNightMode = mode
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(binding.main)

        amount = (savedInstanceState?.getInt(STATE_AMOUNT) ?: 0)
            .coerceIn(0, AmountEntry.MAX_AMOUNT)

        val digitButtons = listOf(
            binding.button0, binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6,
            binding.button7, binding.button8, binding.button9
        )
        digitButtons.forEachIndexed { digit, button ->
            button.setOnClickListener {
                amount = AmountEntry.appendDigit(amount, digit)
                renderAmount()
            }
        }
        binding.buttonClear.setOnClickListener {
            amount = 0
            renderAmount()
        }
        renderAmount()
    }

    private fun renderAmount() {
        binding.amountValue.text = getString(R.string.note_count_value, amount)
        val countViews = listOf(
            binding.count500, binding.count100, binding.count50, binding.count20,
            binding.count10, binding.count5, binding.count2, binding.count1
        )
        ChangeCalculator.calculate(amount).zip(countViews).forEach { (note, view) ->
            view.text = getString(R.string.note_count_value, note.count)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_AMOUNT, amount)
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val STATE_AMOUNT = "entered_amount"
        private const val THEME_PREFERENCES = "appearance"
        private const val THEME_MODE = "night_mode"
    }
}
