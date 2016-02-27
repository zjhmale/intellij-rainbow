package zjhmale.rainbow.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by zjh on 2016/2/16.
 */
public class RainbowIdentifierConfigurable implements Configurable {
    private RainbowIdentifierSettingsForm settingsForm;

    public RainbowIdentifierConfigurable() {
        super();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (settingsForm == null) {
            settingsForm = new RainbowIdentifierSettingsForm();
        }
        return settingsForm.getComponent();
    }

    @Override
    public boolean isModified() {
        return settingsForm.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        RainbowIdentifierSettings settings = RainbowIdentifierSettings.getInstance();
        settings.isRainbowIdentifier = settingsForm.isRainbowIdentifier();
    }

    @Override
    public void reset() {
        if (settingsForm != null) {
            settingsForm.reset();
        }
    }

    @Override
    public void disposeUIResources() {
        settingsForm = null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Rainbow Identifier";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }
}
