package com.opentool

import com.russhwolf.settings.PreferencesSettings

/**
 * Gets the JVM-specific settings storage implementation using multiplatform-settings.
 */
actual fun getSettingsStorage(): SettingsStorage {
    // Create a JVM-specific Settings implementation using PreferencesSettings
    val settings = PreferencesSettings.Factory().create("browser-agent-settings")
    return MultiplatformSettingsStorage(settings)
}
