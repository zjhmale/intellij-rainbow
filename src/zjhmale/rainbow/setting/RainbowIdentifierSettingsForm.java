package zjhmale.rainbow.setting;

import javax.swing.*;

/**
 * Created by zjh on 16/2/22.
 */
public class RainbowIdentifierSettingsForm {
    private JPanel panel;
    private JPanel appearancePanel;
    private JCheckBox rainbowIdentifierCheckBox;

    private final RainbowIdentifierSettings settings;

    public RainbowIdentifierSettingsForm() {
        settings = RainbowIdentifierSettings.getInstance();
    }

    public JComponent getComponent() {
        return panel;
    }

    public boolean isRainbowIdentifier() {
        return rainbowIdentifierCheckBox.isSelected();
    }

    public boolean isModified() {
        final boolean isRainbowIdentifier = settings.isRainbowIdentifier;
        return (rainbowIdentifierCheckBox.isSelected() != isRainbowIdentifier);
    }

    public void reset() {
        rainbowIdentifierCheckBox.setSelected(settings.isRainbowIdentifier);
    }
}
