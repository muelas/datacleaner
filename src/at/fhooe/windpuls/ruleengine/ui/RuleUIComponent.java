package at.fhooe.windpuls.ruleengine.ui;

import at.fhooe.windpuls.ruleengine.rule.Rule;

import javax.swing.*;
import java.awt.*;

public abstract class RuleUIComponent {

    public abstract Rule getRule();

    public JPanel getRulePanel() {
        JPanel p = getRulePanelInternal();
        p.setPreferredSize(new Dimension(300, 65));
        return p;
    }

    protected abstract JPanel getRulePanelInternal();
}
