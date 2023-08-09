package at.fhooe.windpuls.ruleengine.ui;

import at.fhooe.windpuls.ruleengine.rule.Rule;
import at.fhooe.windpuls.ruleengine.rule.TimedRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TimedRuleUI implements RuleUIComponent {
    protected static final Logger log = LogManager.getLogger();
    public JPanel timedRulePanel;
    private JLabel frameLab;
    private JTextField frameTex;
    private JButton addRuleBut;
    private JPanel rulesPanel;
    private JScrollPane rulesScroll;
    private final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
    private final ArrayList<RuleUIComponent> rules = new ArrayList<>();

    private void createUIComponents() {
        rulesPanel = new JPanel(new GridBagLayout());
//        gbc.gridheight = gbc.gridwidth = 1;
//        gbc.gridx = gbc.gridy = 0;
//        gbc.fill = GridBagConstraints.HORIZONTAL;

        rulesScroll = new JScrollPane(rulesPanel);
        rulesScroll.setPreferredSize(new Dimension(500, 200));

        addRuleBut = new JButton("Add Rule");
        addRuleBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ruleType = JOptionPane.showOptionDialog(timedRulePanel, "Select Type of Rule", "Rule Type", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Simple Rule", "Timed Rule"}, "Simple Rule");

                JPanel ruleUI;
                switch (ruleType) {
                    case 0 -> {  // Simple Rule
                        SimpleRuleUI rule = new SimpleRuleUI();
                        ruleUI = rule.simpleRulePanel;
                        rules.add(rule);
                    }
                    case 1 -> { // Timed Rule
                        TimedRuleUI rule = new TimedRuleUI();
                        ruleUI = rule.timedRulePanel;
                        rules.add(rule);
                    }
                    default -> {
                        log.warn("Unknown return type when adding new rule: {}", ruleType);
                        return;
                    }
                }
                rulesPanel.add(ruleUI, gbc);
                rulesPanel.revalidate();
                gbc.gridy++;
            }
        });
    }

    @Override
    public Rule getRule() {
        TimedRule rule = new TimedRule(rules.get(0).getRule(), Integer.parseInt(this.frameTex.getText()));
        return rule;
    }
}
