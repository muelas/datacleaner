package at.fhooe.windpuls.ruleengine.ui;

import at.fhooe.windpuls.ruleengine.RuleUtil;
import at.fhooe.windpuls.ruleengine.operation.OperationBinary;
import at.fhooe.windpuls.ruleengine.operation.OperationTernary;
import at.fhooe.windpuls.ruleengine.operation.binary.GreaterEquals;
import at.fhooe.windpuls.ruleengine.operation.ternary.NotInInterval;
import at.fhooe.windpuls.ruleengine.rule.Rule;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class IntervalRuleUI extends RuleUIComponent {
    private JComboBox<String> columnCb;
    protected JPanel intervalRulePanel;
    private JComboBox<OperationTernary> operationCb;
    private JFormattedTextField fromTex;
    private JFormattedTextField toTex;

    private void createUIComponents() {
        String[] colData = RuleUtil.getColumnNames();
        if (colData == null)
            JOptionPane.showMessageDialog(intervalRulePanel, "Error loading column data", "Error Loading Column Names", JOptionPane.ERROR_MESSAGE);
        else {
            columnCb = new JComboBox<>(colData);
            columnCb.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxx");
        }

        OperationTernary[] operations = RuleUtil.findAllClassesUsingReflectionsLibrary("at.fhooe.windpuls.ruleengine.operation.binary", OperationTernary.class).stream().map(cls -> {
            try {
                return (OperationTernary) cls.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                return null;
            }
        }).toArray(OperationTernary[]::new);
        operationCb = new JComboBox<>(operations);
        operationCb.setPrototypeDisplayValue(new NotInInterval());

        fromTex = new JFormattedTextField(DecimalFormat.getInstance());
        fromTex.setColumns(10);
        toTex = new JFormattedTextField(DecimalFormat.getInstance());
        toTex.setColumns(10);
    }

    @Override
    public Rule getRule() {
        return null;
    }

    @Override
    public JPanel getRulePanelInternal() {
        return this.intervalRulePanel;
    }
}
