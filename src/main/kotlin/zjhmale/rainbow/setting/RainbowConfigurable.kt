package zjhmale.rainbow.setting

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Created by zjh on 16/3/22.
 */
class RainbowConfigurable : Configurable {
    private var settingsForm: RainbowSettingsForm? = null

    override fun createComponent(): JComponent? {
        settingsForm = settingsForm ?: RainbowSettingsForm()
        return settingsForm?.component
    }

    override fun isModified(): Boolean {
        return settingsForm?.isModified ?: return false
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val settings = RainbowSettings.getInstance()
        settings.isRainbowIdentifier = settingsForm?.isRainbowIdentifier ?: true
        settings.isRainbowDelimiter = settingsForm?.isRainbowDelimiter ?: true
    }

    override fun reset() {
        settingsForm?.reset()
    }

    override fun disposeUIResources() {
        settingsForm = null
    }

    @Nls
    override fun getDisplayName() = "Rainbow"

    override fun getHelpTopic() = null
}