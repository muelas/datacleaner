package at.fhooe.windpuls.ruleengine;

import at.fhooe.windpuls.ruleengine.operation.binary.GreaterEquals;
import at.fhooe.windpuls.ruleengine.operation.binary.LessThan;
import at.fhooe.windpuls.ruleengine.operation.ternary.InInterval;
import at.fhooe.windpuls.ruleengine.rule.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RuleUtil {
    // Files
    public static final String HOME_PATH = "W:\\Users\\Andreas\\OneDrive";
    public static final String WORK_PATH = "C:\\Users\\p27389";
    public static final String ONEDRIVE_PATH = "\\OneDrive - FH OOe\\Dokumente\\FHOOE\\Forschung\\wind4you\\";
    public static final String IN_FILE_NAME = "cleaned.csv";
    public static final String OUT_FILE_NAME = "rule-result.txt";
    //    public static final String IN_FILE = HOME_PATH + ONEDRIVE_PATH + IN_FILE_NAME;
//    public static final String OUT_FILE = HOME_PATH + ONEDRIVE_PATH + OUT_FILE_NAME;
    public static final String IN_FILE = ".\\res\\felge1_run2.csv";
    public static final String OUT_FILE = ".\\res\\out.csv";

    // Logging
    protected static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        // Prepare rules
        log.debug("Creating rules...");
        Rule[] rules = {
                new TimedRule(new SimpleRule("vLuft_Windkanal", 16.5, new GreaterEquals()), 10),
                new RelationRule("qx_2", "qy_2", new LessThan()),
                new IntervalRule("vLuft_Windkanal", 16.5, 16.6, new InInterval())
        };
        analyze(rules);
    }


    public static void analyze(Rule[] rules) {
        analyze(rules, false);
    }

    public static void analyze(Rule[] rules, boolean and) {
        analyze(rules, and, null, null);
    }

    /**
     * Analyze the current IN_FILE and write the result to the current OUT_FILE
     *
     * @param rules    the set of rules that should be used for the analysis
     * @param and      if true, a line only matches, if all rules match, otherwise a single matching rule suffices
     * @param progress the progress bar used in the UI to indicate the analysis progress (can be null)
     * @param pd       the UI progress dialog - will be closed when the analysis is finished (can be null)
     */
    public static void analyze(@NotNull Rule[] rules, boolean and, @Nullable JProgressBar progress, @Nullable JDialog pd) {
        log.debug("Starting Analysis...");
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            // Open files...
            log.debug("Opening files...");
            // ...for reading
            br = new BufferedReader(new FileReader(IN_FILE));
            // ...for writing
            File outFile = new File(OUT_FILE);
            if (outFile.exists())
                outFile.delete();
            pw = new PrintWriter(OUT_FILE);

            // Read and split header line
            log.debug("Processing header...");
            String line = br.readLine();
            String[] cols = line.split(Pattern.quote(";"), -1);  // split by colon

            log.debug("Looking up column numbers...");
            extractColumnNumbers(cols, rules);

            log.debug("Reading from file...");
            int lineCount = 1;
            while (br.ready()) {
                lineCount++;
                if (progress != null)
                    progress.setValue(lineCount);
                log.trace("Splitting line...");
                line = br.readLine();
                String[] lineSplit = line.split(Pattern.quote(";"), -1);
                double[] lineData = new double[lineSplit.length];
                IntStream.range(0, lineSplit.length).parallel().forEach(i -> {
                    try {
                        lineSplit[i] = lineSplit[i].replace(',', '.');
                        if (i == 1 || lineSplit[i].isBlank()) return; // skip time column and empty columns
                        lineData[i] = Double.parseDouble(lineSplit[i]);
                    } catch (NumberFormatException nfe) {
                        log.error("Error: could not parse '{}' in column {} as double.", lineSplit[i], i);
                        lineData[i] = 0;
                    }
                });

                log.trace("Checking all Conditions...");
                final PrintWriter pwHelper = pw;
                final int lineCountHelper = lineCount;

                final AtomicBoolean match = new AtomicBoolean(true);
                Arrays.stream(rules).parallel().forEach(rule -> {
                    double newVal, newVal2 = 0;
                    if (rule.numColumns() > 1)
                        newVal2 = lineData[rule.getColumnNr2()];
                    newVal = lineData[rule.getColumnNr()];
                    if (((rule.numColumns() > 1 && rule.match(newVal, newVal2)) || rule.match(newVal)) && (!(rule instanceof TimedRule) || ((TimedRule) rule).isNew())) {
                        if (!and) {
                            // New CSVs have no id
//                            log.info("Match found for condition in line {} with id {}:\n{}", lineCountHelper, lineData[0], rule);
//                            pwHelper.println("Line " + lineCountHelper + " with id " + lineData[0] + ": " + rule);
                            log.info("Match found for condition in line {}", lineCountHelper);
                            if (rule.numColumns() > 1)
                                log.info(", values {}/{}", newVal, newVal2);
                            else
                                log.info(", value {}", newVal);
                            log.info(":\n{}", rule);
                            pwHelper.println("Line " + lineCountHelper + ", " + (rule.numColumns() > 1 ? "values " + newVal + "/" + newVal2 : "value " + newVal) + ": " + rule);
                        }
                    } else {
                        match.set(false);
                    }
                });
                if (and && match.get()) {
                    // New CSVs have no id
//                    log.info("Match found for all conditions in line {} with id {}", lineCountHelper, lineData[0]);
//                    pwHelper.println("Line " + lineCountHelper + " with id " + lineData[0] + ": ");
                    log.info("Match found for all conditions in line {}", lineCountHelper);
                    pwHelper.println("Line " + lineCountHelper + ": ");
                    for (Rule r : rules) {
                        pwHelper.println(r);
                    }
                }
            }
        } catch (IOException ioe) {
            log.error("IOException occurred - aborting", ioe);
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                log.error("Error closing BufferedReader", e);
            }
            try {
                pw.close();
            } catch (Exception e) {
                log.error("Error closing PrintWriter", e);
            }
        }

        if (pd != null)
            pd.setVisible(false);
        log.debug("...Analysis complete");
    }

    private static String[] cols = null;

    public static String[] getColumnNames() {
        if (cols == null)
            loadColumnNames();
        return cols;
    }

    private static void loadColumnNames() {
        BufferedReader br = null;
        try {
            // Open file for reading
            log.debug("Opening files...");
            br = new BufferedReader(new FileReader(RuleUtil.IN_FILE));

            // Read and split header line
            log.debug("Processing header...");
            String line = br.readLine();
            cols = line.split(Pattern.quote(";"), -1);  // split by colon
            //return cols;
        } catch (IOException ioe) {
            log.error("IOException occurred when loading column names", ioe);
            //return null;
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                log.error("Error closing BufferedReader", e);
            }
        }
    }

    public static int getLineCount() {
        char[] buff = new char[32768];
        int cnt = 0;
        int read = 0;
        try (FileReader in = new FileReader(IN_FILE)) {
            do {
                read = in.read(buff);
                for (int i = 0; i < read; i++) {
                    if (buff[i] == '\n')
                        cnt++;
                }
            } while (read > 0);
            return cnt;
        } catch (FileNotFoundException e) {
            log.error("Error counting lines - file not found", e);
            return -1;
        } catch (IOException e) {
            log.error("Error counting lines", e);
            return -1;
        }
    }

    public static void extractColumnNumbers(String[] cols, Rule... conds) {
        log.debug("Extracting Column Numbers...");
        for (int i = 0; i < cols.length; i++) {
            String col = cols[i];
            log.trace(col);
            for (Rule cond : conds) {
                if (col.equals(cond.getColumn())) {
                    log.info("Column with name {} was found at # {}", cond.getColumn(), i);
                    cond.setColumnNr(i);
                }
                if (cond.numColumns() > 1 && col.equals(cond.getColumn2())) {
                    log.info("Column2 with name {} was found at # {}", cond.getColumn2(), i);
                    cond.setColumnNr2(i);
                }
            }
        }
        for (Rule cond : conds) {
            log.info("{} has # {}", cond.getColumn(), cond.getColumnNr());
            if (cond.getColumnNr() == -1) {
                log.fatal("Column {} was not found - aborting", cond.getColumn());
                System.exit(-1);
            }
            if (cond.numColumns() > 1 && cond.getColumnNr2() == -1) {
                log.fatal("Column2 {} was not found - aborting", cond.getColumn());
                System.exit(-1);
            }
        }
    }

    public static Set<Class> findAllClassesUsingReflectionsLibrary(String packageName, Class<?> superClass) {
        if (superClass == null) {
            superClass = Object.class;
        }
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        return reflections.getSubTypesOf(Object.class)
                .stream()
                .collect(Collectors.toSet());
    }
}
