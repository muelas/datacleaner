package at.fhooe.windpuls.ruleengine.ui;

import at.fhooe.windpuls.ruleengine.rule.Rule;
import at.fhooe.windpuls.ruleengine.rule.SimpleRule;
import at.fhooe.windpuls.ruleengine.operation.OperationBinary;
import at.fhooe.windpuls.ruleengine.RuleUtil;

import javax.swing.*;

public class SimpleRuleUI  implements RuleUIComponent {
    private JComboBox<String> columnCb;
    private JComboBox<OperationBinary> operationCb;
    private JTextField valueTex;
    public JPanel simpleRulePanel;

    private void createUIComponents() {
        String[] colData = RuleUtil.getColumnNames();
        if (colData == null)
            JOptionPane.showMessageDialog(simpleRulePanel, "Error loading column data", "Error Loading Column Names", JOptionPane.ERROR_MESSAGE);
        else
            columnCb = new JComboBox<>(colData);

        OperationBinary[] operations = (OperationBinary[]) RuleUtil.findAllClassesUsingReflectionsLibrary("at.fhooe.windpuls.rule.operation.binary", OperationBinary.class).stream().map(cls -> {
            try {
                return (OperationBinary) cls.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                return null;
            }
        }).toArray(OperationBinary[]::new);
        operationCb = new JComboBox<>(operations);
    }

    @Override
    public Rule getRule() {
        return new SimpleRule(columnCb.getSelectedItem().toString(),Double.parseDouble(valueTex.getText()), (OperationBinary) operationCb.getSelectedItem());
    }
}
