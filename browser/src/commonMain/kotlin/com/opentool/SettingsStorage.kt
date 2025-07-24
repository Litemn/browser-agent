package com.opentool

/**
 * Interface for platform-specific settings storage.
 */
interface SettingsStorage {
    /**
     * Saves the settings to persistent storage.
     *
     * @param settings The settings to save
     */
    fun saveSettings(settings: AppSettings)

    /**
     * Loads the settings from persistent storage.
     *
     * @return The loaded settings, or null if no settings were found
     */
    fun loadSettings(): AppSettings?
}

/**
 * Gets the platform-specific settings storage implementation.
 */
expect fun getSettingsStorage(): SettingsStorage