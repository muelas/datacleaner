package at.fhooe.windpuls.rule.ui;

import at.fhooe.windpuls.rule.RuleUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class RuleUI extends JFrame {
    protected static final Logger log = LogManager.getLogger();

    public RuleUI() throws HeadlessException {
        super("RuleUI");
    }


}
