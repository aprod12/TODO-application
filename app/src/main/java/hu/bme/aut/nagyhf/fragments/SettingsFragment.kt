package hu.bme.aut.nagyhf.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import hu.bme.aut.nagyhf.R

class SettingsFragment() : PreferenceFragmentCompat() {


    interface SettingsListener{
        fun onNightModeChanged()
    }

    private lateinit var listener : SettingsListener

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings,rootKey)

    }

}