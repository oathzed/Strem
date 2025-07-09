package com.strem.app.ui.settings

import android.os.Bundle
import androidx.leanback.preference.LeanbackPreferenceFragment
import androidx.leanback.preference.LeanbackSettingsFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragment
import androidx.preference.PreferenceScreen
import com.strem.app.R

/**
 * Fragment for app settings.
 */
class SettingsFragment : LeanbackSettingsFragment() {

    override fun onPreferenceStartInitialScreen() {
        startPreferenceFragment(MainPreferenceFragment())
    }

    override fun onPreferenceStartFragment(
        preferenceFragment: PreferenceFragment,
        preference: Preference
    ): Boolean {
        val args = preference.extras
        val fragment = childFragmentManager.fragmentFactory.instantiate(
            requireActivity().classLoader,
            preference.fragment!!
        )
        fragment.arguments = args
        startPreferenceFragment(fragment)
        return true
    }

    override fun onPreferenceStartScreen(
        preferenceFragment: PreferenceFragment,
        preferenceScreen: PreferenceScreen
    ): Boolean {
        val fragment = PreferenceFragment()
        startPreferenceFragment(fragment)
        return true
    }

    /**
     * Main preference fragment containing all settings categories.
     */
    class MainPreferenceFragment : LeanbackPreferenceFragment() {
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            
            // Setup Trakt login preference
            val traktLoginPref = findPreference<Preference>("trakt_login")
            traktLoginPref?.setOnPreferenceClickListener {
                // TODO: Implement Trakt login
                true
            }
            
            // Setup Stremio addon preference
            val addAddonPref = findPreference<Preference>("add_addon")
            addAddonPref?.setOnPreferenceClickListener {
                // TODO: Implement Stremio addon installation
                true
            }
        }
    }
}