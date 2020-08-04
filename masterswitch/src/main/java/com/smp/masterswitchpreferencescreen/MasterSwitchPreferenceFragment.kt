package com.smp.masterswitchpreferencescreen

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView

@Keep
open class MasterSwitchPreferenceFragment : PreferenceFragmentCompat() {

    companion object {
        fun newInstance(attrs: MasterSwitchPreferenceAttrs): MasterSwitchPreferenceFragment {
            val frag = MasterSwitchPreferenceFragment()
            with(Bundle()) {
                putParcelable("MasterSwitchAttrs", attrs)
                frag.arguments = this
            }
            return frag
        }
    }

    val attrs: MasterSwitchPreferenceAttrs by lazy {
        requireArguments().getParcelable<MasterSwitchPreferenceAttrs>("MasterSwitchAttrs")!!
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.blank_preference_screen)
        val masterSwitchPreference = MasterSwitchSwitchPreference(context)
        masterSwitchPreference.key = attrs.key
        masterSwitchPreference.setDefaultValue(attrs.defaultValue)
        preferenceScreen.addPreference(masterSwitchPreference)
        addPreferencesFromResource(R.xml.explanation_preference_screen)
        val explanationText = findPreference<Preference>("com_smp_explanation_key")!!
        attrs.includedPrefScreenRes?.let {
            addPreferencesFromResource(it)
        }
        val includedPrefs = mutableListOf<Preference>()

        for (i in 2 until preferenceScreen.preferenceCount) {
            val pref = preferenceScreen.getPreference(i)
            includedPrefs.add(pref)
        }

        attrs.notIncludedPrefScreenRes?.let {
            addPreferencesFromResource(it)
        }

        setupMasterSwitch(masterSwitchPreference, explanationText, includedPrefs)
        setupExplanationText(explanationText)
    }

    private fun setupExplanationText(explanationText: Preference) {
        explanationText.apply {
            summary = attrs.explanationText
            attrs.explanationIcon?.let { iconRes ->
                setIcon(iconRes)
            }
        }
    }

    override fun onCreateRecyclerView(inflater: LayoutInflater?, parent: ViewGroup?, state: Bundle?): RecyclerView? {
        return super.onCreateRecyclerView(inflater, parent, state).apply {
            itemAnimator = null
            layoutAnimation = null
        }
    }


    private fun setupMasterSwitch(masterSwitch: MasterSwitchSwitchPreference, explanationText: Preference, includedPrefs: List<Preference>) {

        fun handleExplanationText(on: Boolean) {
            if (attrs.hideExplanation) explanationText.isVisible = !on
            includedPrefs.forEach {
                it.isVisible = on
            }
        }

        fun titleValue(on: Boolean): String =
                if (on) {
                    attrs.switchTextOn
                } else {
                    attrs.switchTextOff
                }

        masterSwitch.apply {
            fun setBackgroundColor(on: Boolean) {
                if (on) {
                    applySwitchOnBackgroundColor()
                } else {
                    applySwitchOffBackgroundColor()
                }
            }
            attrs = this@MasterSwitchPreferenceFragment.attrs
            attrs.key.let {
                key = it
                setDefaultValue(attrs.defaultValue)
            }
            title = titleValue(isChecked)
            handleExplanationText(isChecked)
            setOnPreferenceChangeListener { _, newValue ->
                title = titleValue(newValue as Boolean)
                handleExplanationText(newValue)
                setBackgroundColor(newValue)
                true
            }
        }
    }
}

internal fun dpToPixels(context: Context, dipValue: Float): Float {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
}