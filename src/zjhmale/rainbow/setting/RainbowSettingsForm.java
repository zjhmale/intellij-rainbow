package zjhmale.rainbow.setting;

import javax.swing.*;

/**
 * Created by zjh on 16/2/22.
 */
public class RainbowSettingsForm {
    private JPanel panel;
    private JPanel appearancePanel;
    private JCheckBox rainbowIdentifierCheckBox;
    private JCheckBox rainbowDelimiterCheckBox;

    private final RainbowSettings settings;

    public RainbowSettingsForm() {
        rainbowIdentifierCheckBox.setSelected(true);
        rainbowDelimiterCheckBox.setSelected(true);

        settings = RainbowSettings.getInstance();
    }

    public JComponent getComponent() {
        return panel;
    }

    public boolean isRainbowIdentifier() {
        return rainbowIdentifierCheckBox.isSelected();
    }

    public boolean isRainbowDelimiter() {
        return rainbowDelimiterCheckBox.isSelected();
    }

    public boolean isModified() {
        return rainbowIdentifierCheckBox.isSelected() != settings.isRainbowIdentifier
                || rainbowDelimiterCheckBox.isSelected() != settings.isRainbowDelimiter;
    }

    public void reset() {
        rainbowIdentifierCheckBox.setSelected(settings.isRainbowIdentifier);
        rainbowDelimiterCheckBox.setSelected(settings.isRainbowDelimiter);
    }
}
