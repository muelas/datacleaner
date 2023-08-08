package at.fhooe.windpuls.rule.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimedRuleUI {
    protected static final Logger log = LogManager.getLogger();
    public JPanel timedRulePanel;
    private JLabel timedRuleLabel;
    private JLabel frameLab;
    private JTextField frameTex;
    private JButton addRuleBut;
    private JPanel rulesPanel;
    final GridBagConstraints gbc = new GridBagConstraints();

    private void createUIComponents() {
        rulesPanel = new JPanel(new GridBagLayout());
        gbc.gridheight = gbc.gridwidth = 1;
        gbc.gridx = gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addRuleBut = new JButton("Add Rule");
        addRuleBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ruleType = JOptionPane.showOptionDialog(timedRulePanel, "Select Type of Rule", "Rule Type", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Simple Rule", "Timed Rule"}, "Simple Rule");

                JPanel rule;
                switch (ruleType) {
                    case 0: // Simple Rule
                        rule = new SimpleRuleUI().simpleRulePanel;
                        break;
                    case 1: // Timed Rule
                        rule = new TimedRuleUI().timedRulePanel;
                        break;
                    default:
                        log.warn("Unknown return type when adding new rule: {}", ruleType);
                        return;
                }
                rulesPanel.add(rule);
            }
        });
    }
}
