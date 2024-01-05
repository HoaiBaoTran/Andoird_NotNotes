package com.example.notnotes.appservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.notnotes.R
import com.example.notnotes.databinding.ActivitySettingBinding

open class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val ENGLISH_LANGUAGE_CODE = "en"
    private val VIETNAME_LANGUAGE_CODE = "vi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSaveSetting.setOnClickListener {
            applyLanguage()
        }

    }

    private fun getLanguageCode() : String? {
        val selectedButton = binding.radioGroup.checkedRadioButtonId
        if (selectedButton == binding.rbEnglish.id) {
            return ENGLISH_LANGUAGE_CODE
        } else if (selectedButton == binding.rbVn.id) {
            return VIETNAME_LANGUAGE_CODE
        }
        return null
    }

    protected open fun applyLanguage() {
        val languageCode = getLanguageCode() // Implement this to get the desired language code
        LocaleManager.setLocale(this, languageCode!!)
        recreate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}