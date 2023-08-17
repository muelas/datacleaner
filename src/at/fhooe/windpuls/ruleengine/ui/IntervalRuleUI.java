package at.fhooe.windpuls.ruleengine.ui;

import at.fhooe.windpuls.ruleengine.RuleUtil;
import at.fhooe.windpuls.ruleengine.operation.OperationBinary;
import at.fhooe.windpuls.ruleengine.operation.OperationTernary;
import at.fhooe.windpuls.ruleengine.operation.binary.GreaterEquals;
import at.fhooe.windpuls.ruleengine.operation.ternary.InInterval;
import at.fhooe.windpuls.ruleengine.operation.ternary.NotInInterval;
import at.fhooe.windpuls.ruleengine.rule.IntervalRule;
import at.fhooe.windpuls.ruleengine.rule.Rule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Logger;

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

        OperationTernary[] operations = RuleUtil.findAllClassesUsingReflectionsLibrary("at.fhooe.windpuls.ruleengine.operation.ternary", OperationTernary.class).stream().map(cls -> {
            try {
                return (OperationTernary) cls.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                return null;
            }
        }).toArray(OperationTernary[]::new);
        operationCb = new JComboBox<>(operations);
        operationCb.setPrototypeDisplayValue(new InInterval());

        NumberFormat format = DecimalFormat.getInstance();
        format.setMinimumFractionDigits(1);
        format.setMaximumFractionDigits(2);
        format.setParseIntegerOnly(false);
        format.setRoundingMode(RoundingMode.HALF_UP);
        fromTex = new JFormattedTextField(format);
        fromTex.setValue(0.0);
        fromTex.setColumns(10);
        toTex = new JFormattedTextField(format);
        toTex.setValue(0.0);
        toTex.setColumns(10);

        FocusListener fl = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                Component c = e.getComponent();
                if (c == fromTex) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            fromTex.selectAll();
                        }
                    });
                } else if (c == toTex) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            toTex.selectAll();
                        }
                    });
                }
            }
        };
        fromTex.addFocusListener(fl);
        toTex.addFocusListener(fl);
    }


    @Override
    public Rule getRule() {
        return new IntervalRule(columnCb.getSelectedItem().toString(), (double) fromTex.getValue(), (double) toTex.getValue(), (OperationTernary) operationCb.getSelectedItem());
    }

    @Override
    public JPanel getRulePanelInternal() {
        return this.intervalRulePanel;
    }
}
