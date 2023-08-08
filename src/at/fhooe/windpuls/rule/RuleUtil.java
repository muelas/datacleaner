package at.fhooe.windpuls.rule;

import at.fhooe.windpuls.rule.operation.binary.GreaterEquals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
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
    public static final String IN_FILE = HOME_PATH + ONEDRIVE_PATH + IN_FILE_NAME;
    public static final String OUT_FILE = HOME_PATH + ONEDRIVE_PATH + OUT_FILE_NAME;
    // Logging
    protected static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            // Open files...
            log.debug("Opening files...");
            // ...for reading
            br = new BufferedReader(new FileReader(IN_FILE));
            // ...for writing
            pw = new PrintWriter(OUT_FILE);

            // Read and split header line
            log.debug("Processing header...");
            String line = br.readLine();
            String[] cols = line.split(Pattern.quote(";"), -1);  // split by colon

            // Prepare rules
            log.debug("Creating rules...");
            Rule[] rules = {
                    new TimedRule(new SimpleRule("vLuft_Windkanal", 16.5, new GreaterEquals()), 10)
            };
            log.debug("Looking up column numbers...");
            extractColumnNumbers(cols, rules);

            log.debug("Reading from file...");
            while (br.ready()) {
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
                Arrays.stream(rules).parallel().forEach(rule -> {
                    double newVal = lineData[rule.getColumnNr()];
                    if (rule.match(newVal)) {
                        if (!(rule instanceof TimedRule) || ((TimedRule) rule).isNew()) {
                            log.info("Match found for condition in line {}:\n{}", lineData[0], rule);
                            pwHelper.println("Line " + lineData[0] + ": " + rule);
                        }
                    }
                });
            }
        } catch (IOException ioe) {
            log.error("IOException occured - aborting", ioe);
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
    }

    public static String[] loadColumnNames() {
        BufferedReader br = null;
        try {
            // Open file for reading
            log.debug("Opening files...");
            br = new BufferedReader(new FileReader(RuleUtil.IN_FILE));

            // Read and split header line
            log.debug("Processing header...");
            String line = br.readLine();
            String[] cols = line.split(Pattern.quote(";"), -1);  // split by colon
            return cols;
        } catch (IOException ioe) {
            log.error("IOException occurred when loading column names", ioe);
            return null;
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                log.error("Error closing BufferedReader", e);
            }
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
            }
        }
        for (Rule cond : conds) {
            log.info("{} has # {}", cond.getColumn(), cond.getColumnNr());
            if (cond.getColumnNr() == -1) {
                log.fatal("Column {} was not found - aborting", cond.getColumn());
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
