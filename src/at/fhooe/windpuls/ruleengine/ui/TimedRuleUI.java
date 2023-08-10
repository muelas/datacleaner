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

public class TimedRuleUI extends RuleUIComponent {
    protected static final Logger log = LogManager.getLogger();
    public JPanel timedRulePanel;
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
//        rulesScroll.setPreferredSize(new Dimension(500, 200));

        addRuleBut = new JButton("Add Rule");
        addRuleBut.addActionListener(e -> {
            int ruleType = JOptionPane.showOptionDialog(timedRulePanel, "Select Type of Rule", "Rule Type", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Simple Rule", "RelationRule", "IntervalRule"}, "Simple Rule");

            RuleUIComponent rule;
            switch (ruleType) {
                case 0 -> {  // Simple Rule
                    rule = new SimpleRuleUI();
                }
//                    case 1 -> { // Timed Rule
//                        // not applicable here
//                        rule = new TimedRuleUI();
//                    }
                case 2 -> { // Relation Rule
                    rule = new RelationRuleUI();
                }
                case 3 -> { //Interval Rule
                    rule = new IntervalRuleUI();
                }
                default -> {
                    log.warn("Unknown return type when adding new rule: {}", ruleType);
                    return;
                }
            }
            rules.add(rule);
            rulesPanel.add(rule.getRulePanel(), gbc);
            rulesPanel.revalidate();
            gbc.gridy++;
        });

        this.timedRulePanel = new JPanel(new BorderLayout());
        this.timedRulePanel.setPreferredSize(new Dimension(500, 100));
    }

    @Override
    public Rule getRule() {
        TimedRule rule = new TimedRule(rules.get(0).getRule(), Integer.parseInt(this.frameTex.getText()));
        return rule;
    }

    @Override
    public JPanel getRulePanelInternal() {
        return this.timedRulePanel;
    }

    @Override
    public JPanel getRulePanel() {
        JPanel p = getRulePanelInternal();
        p.setPreferredSize(new Dimension(500, 300));
        return p;
    }

}
