package com.kbratkovic.investmentgoldportfolio.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.ui.fragments.SettingsFragment

class SettingsActivity : AppCompatActivity() {

    private val mSettingsFragment = SettingsFragment()
    private lateinit var mToolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initializeAndSetToolbar()
        setFragment()
        addOnBackPressedCallback()
        setSharedPreferences()

    } // onCreate


    private fun initializeAndSetToolbar() {
        mToolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.settings_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


    private fun setFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.settings_container, mSettingsFragment)
            addToBackStack(null)
            commit()
        }
    }


    private fun addOnBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }


    private fun setSharedPreferences() {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

}