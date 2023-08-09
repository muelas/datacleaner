package at.fhooe.windpuls.ruleengine.ui;

import at.fhooe.windpuls.ruleengine.rule.Rule;
import at.fhooe.windpuls.ruleengine.RuleUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RuleUI extends JFrame {
    protected static final Logger log = LogManager.getLogger();

    private final GridBagConstraints gbc = new GridBagConstraints();
    private JPanel rulePanel;
    private final ArrayList<RuleUIComponent> rules = new ArrayList<>();

    private final JTextArea outputArea;
    private JDialog dia;

    public RuleUI() throws HeadlessException {
        super("RuleUI");
        this.setLayout(new BorderLayout());

        this.rulePanel = new JPanel(new GridBagLayout());
        gbc.gridheight = gbc.gridwidth = 1;
        gbc.gridx = gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = gbc.weighty = 1;

        TimedRuleUI trUI = new TimedRuleUI();
        JPanel trPanel = trUI.timedRulePanel;
        rules.add(trUI);
        rulePanel.add(trPanel, gbc);
        this.add(rulePanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout());
        outputArea = new JTextArea(50, 100);
        southPanel.add(getStartButton());
        southPanel.add(getOutputButton());
        this.add(southPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
//        this.setLocationRelativeTo(null);
        this.setLocation(200,400);
        this.setVisible(true);
    }

    @NotNull
    private JButton getOutputButton() {
        final JButton outputButton = new JButton("Show Output");
        outputButton.addActionListener(e -> {
            if (dia!=null && dia.isVisible()) {
                dia.setVisible(false);
                outputButton.setText("Show Output");
            } else {
                outputButton.setText("Hide Output");
                if (dia == null) {
                    dia = new JDialog(RuleUI.this, "Output", false);
                    dia.add(outputArea);
                }
                dia.setSize(1300, 500);
                JFrame p = RuleUI.this;
                dia.setLocation(p.getX() + p.getWidth(), p.getY());
                dia.setVisible(true);
                p.requestFocus();
            }
        });
        return outputButton;
    }

    @NotNull
    private JButton getStartButton() {
        JButton startButton = new JButton("Start Analysis");
        startButton.addActionListener(e -> {
            Rule[] r = rules.stream().map(RuleUIComponent::getRule).toArray(Rule[]::new);
            RuleUtil.analyze(r);
            try {
                BufferedReader br = new BufferedReader(new FileReader(RuleUtil.OUT_FILE));
                outputArea.setText("");
                outputArea.read(br, null);
                br.close();
            } catch (IOException ex) {
                log.error("Error reading output file", ex);
            }
        });
        return startButton;
    }

    public static void main(String[] args) {
        new RuleUI();
    }
}
