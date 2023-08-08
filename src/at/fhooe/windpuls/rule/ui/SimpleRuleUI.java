package at.fhooe.windpuls.rule.ui;

import at.fhooe.windpuls.rule.operation.OperationBinary;
import at.fhooe.windpuls.rule.RuleUtil;

import javax.swing.*;

public class SimpleRuleUI {
    private JComboBox<String> columnCb;
    private JLabel simpleRuleLabel;
    private JComboBox<OperationBinary> operationCb;
    private JTextField valueTex;
    public JPanel simpleRulePanel;

    private void createUIComponents() {
        String[] colData = RuleUtil.loadColumnNames();
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
        }).toArray();
        operationCb = new JComboBox<>(operations);
    }
}
