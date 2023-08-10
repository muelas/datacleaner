package at.fhooe.windpuls.ruleengine.ui;

import at.fhooe.windpuls.ruleengine.operation.binary.GreaterEquals;
import at.fhooe.windpuls.ruleengine.rule.Rule;
import at.fhooe.windpuls.ruleengine.rule.SimpleRule;
import at.fhooe.windpuls.ruleengine.operation.OperationBinary;
import at.fhooe.windpuls.ruleengine.RuleUtil;

import javax.swing.*;

public class SimpleRuleUI extends RuleUIComponent {
    private JComboBox<String> columnCb;
    private JComboBox<OperationBinary> operationCb;
    private JTextField valueTex;
    public JPanel simpleRulePanel;

    private void createUIComponents() {
        String[] colData = RuleUtil.getColumnNames();
        if (colData == null)
            JOptionPane.showMessageDialog(simpleRulePanel, "Error loading column data", "Error Loading Column Names", JOptionPane.ERROR_MESSAGE);
        else {
            columnCb = new JComboBox<>(colData);
            columnCb.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxx");
        }

        OperationBinary[] operations = RuleUtil.findAllClassesUsingReflectionsLibrary("at.fhooe.windpuls.ruleengine.operation.binary", OperationBinary.class).stream().map(cls -> {
            try {
                return (OperationBinary) cls.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                return null;
            }
        }).toArray(OperationBinary[]::new);
        operationCb = new JComboBox<>(operations);
        operationCb.setPrototypeDisplayValue(new GreaterEquals());
    }

    @Override
    public Rule getRule() {
        return new SimpleRule(columnCb.getSelectedItem().toString(), Double.parseDouble(valueTex.getText()), (OperationBinary) operationCb.getSelectedItem());
    }
    @Override
    public JPanel getRulePanelInternal() {
        return this.simpleRulePanel;
    }
}
