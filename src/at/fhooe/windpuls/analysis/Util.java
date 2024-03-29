package at.fhooe.windpuls.analysis;

import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Util {

    // Files
    public static final String HOME_PATH = "W:\\Users\\Andreas\\OneDrive";
    public static final String WORK_PATH = "C:\\Users\\p27389";
    public static final String ONEDRIVE_PATH = "\\OneDrive - FH OOe\\Dokumente\\FHOOE\\Forschung\\Datenanalyse\\";
    public static final String IN_FILE_NAME = "cleaned.csv";
    public static final String OUT_FILE_NAME = "pc.txt";

    /**
     * Extract the indices of the target columns provided in the names array.
     * Returns the overall number of columns in the header.
     *
     * @param header      The column headers to be analyzed, e.g., the first line of a csv file.
     * @param targetNames Names of the columns that should be found
     * @param targetCols  Output parameter! Contains the ids (column-numbers) of the target columns in the header.
     * @param cd          Datastructure that should contain the name of the column that should be analyzed for
     *                    correlation with the target columns. If the column is found, the respective id (column-number)
     *                    will be set in the datastructure. Ignored if null.
     * @param debug       If true, the method will print debug output.
     * @return Returns the number of overall columns found in the header.
     * @throws IOException Caused by file access.
     */
    public static int extractTargetColumns(String header, String[] targetNames, int[] targetCols, CorrelationColumn cd, boolean debug) throws IOException {
        if (debug) System.out.println("Reading column titles to identify target columns...");
//        System.out.println("...splitting...");
        String[] cols = header.split(Pattern.quote(";"), -1);  // split by colon
        if (cd != null) cd.col = -1;
        final int colnr = cols.length;
        int mhc = 0;    // counter for the target-columns

        if (debug) {
            System.out.print("...searching for columns ");
            for (String name : targetNames) {
                System.out.print(name);
                System.out.print(", ");
            }
            System.out.println("...");
        }

        for (int i = 0; i < cols.length; i++) {
//            System.out.println("...comparing column '"+cols[i]+"'...");
            for (String name : targetNames) {   // check all target-names
                if (name.equals(cols[i].trim())) { // if the current column is one of the target-columns
                    targetCols[mhc++] = i;  // store column id where target-column was found
                    if (debug) System.out.println("...'" + name + "' is in column " + i + "...");
                    break;  // break checking the names, since we already had a match
                }
            }
            if (cd != null && cd.name != null && cd.name.equals(cols[i])) { // if the current column is the correlation column
                cd.col = i;  // store column id where target-column was found
                if (debug) System.out.println("...correlation column '" + cd.name + "' is in column " + i + "...");
            }
        }
        if (mhc < targetNames.length)
            throw new RuntimeException("Could not find all target columns. mhc=" + mhc + ", length=" + targetNames.length);
        if (cd != null && cd.col == -1) throw new RuntimeException("Correlation column '" + cd.name + "' not found");
        if (debug) System.out.println("...done.");
        return colnr;
    }

    public static int extractTargetColumns(String header, String[] targetNames, int[] targetCols, CorrelationColumn cd) throws IOException {
        return extractTargetColumns(header, targetNames, targetCols, cd, false);
    }


    public static int extractTargetColumns(String header, String[] targetNames, int[] targetCols) throws IOException {
        return extractTargetColumns(header, targetNames, targetCols, null, false);
    }


    public static double[] parseLine(String line, final int colnr, int[] targetCols, int corrCol) {
        String[] lineSplit = line.split(Pattern.quote(";"), -1);

        if (lineSplit.length < corrCol || lineSplit[corrCol].isBlank()) {
//                    System.out.println("Row " + cntR + " is empty, but should have value - will not copy");
            return null;
        }
        for (int i : targetCols) {
            if (lineSplit.length < i || lineSplit[i].isBlank()) {
//                    System.out.println("Row " + cntR + " is empty, but should have value - will not copy");
                return null;
            }
        }

        double[] lineData = new double[colnr];
        for (int i = 0; i < lineSplit.length; i++) {
            try {
                lineSplit[i] = lineSplit[i].replace(',', '.');
                if (i == 1 || lineSplit[i].isBlank()) continue; // skip time column and empty columns
                lineData[i] = Double.parseDouble(lineSplit[i]);
            } catch (NumberFormatException nfe) {
                System.out.println("Error: could not parse '" + lineSplit[i] + "' in column " + i + " as double.");
                return null;
            }
        }
        return lineData;
    }

    public static class CorrelationColumn {
        public String name;
        public int col;

        public CorrelationColumn() {
            this(null, -1);
        }

        public CorrelationColumn(String name, int col) {
            this.name = name;
            this.col = col;
        }
    }

    public static class CorrelationResult {
        public double[] pc;
        public double[] sr;
        public LinkedList<Correlation> correlatios = new LinkedList<>();
    }

    public static class Correlation {
        public CorrelationColumn col;
        public CorrelationColumn target;
        public double pc;
        public int wFrom, wTo;

        @Override
        public String toString() {
            return "Correlation{" +
                    "col=" + col.name +
                    ", target=" + target.name +
                    ", pc=" + pc +
                    ", wFrom=" + wFrom +
                    ", wTo=" + wTo +
                    '}';
        }
    }
}
