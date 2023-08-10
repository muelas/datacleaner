package at.fhooe.windpuls.ruleengine.ui;

import at.fhooe.windpuls.ruleengine.RuleUtil;
import at.fhooe.windpuls.ruleengine.operation.OperationBinary;
import at.fhooe.windpuls.ruleengine.operation.binary.GreaterEquals;
import at.fhooe.windpuls.ruleengine.rule.RelationRule;
import at.fhooe.windpuls.ruleengine.rule.Rule;
import at.fhooe.windpuls.ruleengine.rule.SimpleRule;

import javax.swing.*;

public class RelationRuleUI extends RuleUIComponent {
    protected JPanel relationRulePanel;
    private JComboBox<String> column1Cb;
    private JComboBox<OperationBinary> operationCb;
    private JComboBox<String> column2Cb;

    private void createUIComponents() {
        String[] colData = RuleUtil.getColumnNames();
        if (colData == null)
            JOptionPane.showMessageDialog(relationRulePanel, "Error loading column data", "Error Loading Column Names", JOptionPane.ERROR_MESSAGE);
        else {
            column1Cb = new JComboBox<>(colData);
            column1Cb.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxx");
            column2Cb = new JComboBox<>(colData);
            column2Cb.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxx");
        }

        OperationBinary[] operations = (OperationBinary[]) RuleUtil.findAllClassesUsingReflectionsLibrary("at.fhooe.windpuls.ruleengine.operation.binary", OperationBinary.class).stream().map(cls -> {
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
        return new RelationRule(column1Cb.getSelectedItem().toString(), column2Cb.getSelectedItem().toString(), (OperationBinary) operationCb.getSelectedItem());
    }
    @Override
    public JPanel getRulePanelInternal() {
        return this.relationRulePanel;
    }
}
