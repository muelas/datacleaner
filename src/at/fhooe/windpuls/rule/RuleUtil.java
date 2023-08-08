package at.fhooe.windpuls.rule;

import at.fhooe.windpuls.analysis.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;
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

            // Prepare conditions
            log.debug("Creating conditions...");
            Condition[] conditions = {
                    new TimedCondition(new SimpleCondition("vLuft_Windkanal", 16.5, new GreaterEquals()), 10)
            };
            log.debug("Looking up column numbers...");
            extractColumnNumbers(cols, conditions);

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
                Arrays.stream(conditions).parallel().forEach(cond -> {
                    double newVal = lineData[cond.getColumnNr()];
                    if (cond.match(newVal)) {
                        if (!(cond instanceof TimedCondition) || ((TimedCondition) cond).isNew()) {
                            log.info("Match found for condition in line {}:\n{}", lineData[0], cond);
                            pwHelper.println("Line " + lineData[0] + ": " + cond);
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

    public static void extractColumnNumbers(String[] cols, Condition... conds) {
        log.debug("Extracting Column Numbers...");
        for (int i = 0; i < cols.length; i++) {
            String col = cols[i];
            log.trace(col);
            for (Condition cond : conds) {
                if (col.equals(cond.getColumn())) {
                    log.info("Column with name {} was found at # {}", cond.getColumn(), i);
                    cond.setColumnNr(i);
                }
            }
        }
        for (Condition cond : conds) {
            log.info("{} has # {}", cond.getColumn(), cond.getColumnNr());
            if (cond.getColumnNr() == -1) {
                log.fatal("Column {} was not found - aborting", cond.getColumn());
                System.exit(-1);
            }
        }
    }
}
