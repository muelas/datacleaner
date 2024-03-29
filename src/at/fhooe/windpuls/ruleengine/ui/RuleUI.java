package at.fhooe.windpuls.ruleengine.ui;

import at.fhooe.windpuls.ruleengine.rule.Rule;
import at.fhooe.windpuls.ruleengine.RuleUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RuleUI extends JFrame {
    protected static final Logger log = LogManager.getLogger();

    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JPanel rulesPanel;
    private final JScrollPane rulesPanelScroll;
    private final ArrayList<RuleUIComponent> rules = new ArrayList<>();

    private final JTextArea outputArea;
    private JDialog dia;

    private JRadioButton andRadio;
    private JRadioButton orRadio;

    private JTextField inPath;
    private JTextField outPath;

    public RuleUI() throws HeadlessException {
        super("RuleUI");
        this.setLayout(new BorderLayout());

        this.rulesPanel = new JPanel(new GridBagLayout());
        gbc.gridheight = gbc.gridwidth = 1;
        gbc.gridx = gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

//        rulesPanel.setPreferredSize(new Dimension(600, 100));
        rulesPanelScroll = new JScrollPane(rulesPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        rulesPanelScroll.setPreferredSize(new Dimension(610, 110));
        this.add(rulesPanelScroll, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout());
        outputArea = new JTextArea(50, 100);
        southPanel.add(getStartButton());
        southPanel.add(getAddRuleButton());
        southPanel.add(getOutputButton());
        southPanel.add(new JSeparator(JSeparator.VERTICAL));

        this.andRadio = new JRadioButton("AND", true);
        this.orRadio = new JRadioButton("OR", false);
        ButtonGroup bg = new ButtonGroup();
        bg.add(andRadio);
        bg.add(orRadio);
        southPanel.add(andRadio);
        southPanel.add(orRadio);

        this.add(southPanel, BorderLayout.SOUTH);

        // in/out path selections
        JPanel northPanel = new JPanel(new GridLayout(2, 1));
        JPanel inPanel = new JPanel(new BorderLayout());
        JLabel inLabel = new JLabel("Input File: ");
        inPanel.add(inLabel, BorderLayout.WEST);
        inPath = new JTextField("");
        inPath.setEditable(false);
        inPanel.add(inPath, BorderLayout.CENTER);
        JButton inButton = new JButton("Select");
        inButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogType(JFileChooser.OPEN_DIALOG);
            fc.setFileFilter(new FileNameExtensionFilter("Comma Separated Values", "csv"));
            if (fc.showOpenDialog(RuleUI.this) == JFileChooser.APPROVE_OPTION) {
                inPath.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });
        inPanel.add(inButton, BorderLayout.EAST);

        JPanel outPanel = new JPanel(new BorderLayout());
        JLabel outLabel = new JLabel("Output File: ");
        outPanel.add(outLabel, BorderLayout.WEST);
        outPath = new JTextField("");
        outPath.setEditable(false);
        outPanel.add(outPath, BorderLayout.CENTER);
        JButton outButton = new JButton("Select");
        outButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogType(JFileChooser.SAVE_DIALOG);
            fc.setFileFilter(new FileNameExtensionFilter("Text", "txt"));
            if (fc.showSaveDialog(RuleUI.this) == JFileChooser.APPROVE_OPTION) {
                outPath.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });
        outPanel.add(outButton, BorderLayout.EAST);

        northPanel.add(inPanel);
        northPanel.add(outPanel);
        this.add(northPanel, BorderLayout.NORTH);


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(700, 500));
//        this.setLocationRelativeTo(null);
        this.setLocation(250, 400);
        this.setVisible(true);
    }

    private JButton getAddRuleButton() {
        final JButton addRuleButton = new JButton("Add Rule");
        addRuleButton.addActionListener(e -> {

            int ruleType = JOptionPane.showOptionDialog(RuleUI.this, "Select Type of Rule", "Rule Type", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Simple Rule", "Timed Rule", "RelationRule", "IntervalRule"}, "Simple Rule");

            RuleUIComponent rule;
            switch (ruleType) {
                case 0 -> {  // Simple Rule
                    rule = new SimpleRuleUI();
                }
                case 1 -> { // Timed Rule
                    rule = new TimedRuleUI();
                }
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
        return addRuleButton;
    }


    @NotNull
    private JButton getOutputButton() {
        final JButton outputButton = new JButton("Show Output");
        outputButton.addActionListener(e -> {
            if (dia != null && dia.isVisible()) {
                dia.setVisible(false);
                outputButton.setText("Show Output");
            } else {
                outputButton.setText("Hide Output");
                if (dia == null) {
                    dia = new JDialog(RuleUI.this, "Output", false);
                    dia.add(new JScrollPane(outputArea));
                }
                dia.setSize(1300, 700);
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
            // Get selected rules
            Rule[] r = rules.stream().map(RuleUIComponent::getRule).toArray(Rule[]::new);

            // prepare Progress Dialog
            int lineCnt = RuleUtil.getLineCount();
            JDialog pd = new JDialog(this, true);
            pd.setLayout(new BorderLayout());
            pd.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            pd.setSize(200, 80);
            pd.setLocationRelativeTo(this);

            JProgressBar prog = new JProgressBar(JProgressBar.HORIZONTAL, 0, lineCnt);
            prog.setStringPainted(true);
            pd.add(prog, BorderLayout.CENTER);
            pd.add(new JLabel("Analyzing..."), BorderLayout.NORTH);

            // Start analysis
            Thread t = new Thread(() -> RuleUtil.analyze(r, andRadio.isSelected(), (inPath.getText().isEmpty() ? null : inPath.getText()), (outPath.getText().isEmpty() ? null : outPath.getText()), prog, pd));
            t.start();
            pd.setVisible(true);
            // Analysis done - writing output
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
