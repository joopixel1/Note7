package com.pixie.note7.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.pixie.note7.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}