package at.fhooe.windpuls.analysis;

import java.io.*;
import java.util.regex.Pattern;

public class DataCleaner {
    public static final String[] mhNames = {"vLuft_Windkanal", "alpha", "beta"};
    public static final int mhnr = mhNames.length;
    public static final int[] mhCols = new int[mhnr];

    public static final String IN_FILE_NAME = "wt0028.csv";
    public static final String WEKA_FILE_NAME = "weka.csv";
    public static final String CLEANED_FILE_NAME = "cleaned.csv";
    public static final String IN_FILE = Util.HOME_PATH + Util.ONEDRIVE_PATH + IN_FILE_NAME;
    public static final String WEKA_FILE = Util.HOME_PATH + Util.ONEDRIVE_PATH + WEKA_FILE_NAME;
    public static final String CLEANED_FILE = Util.HOME_PATH + Util.ONEDRIVE_PATH + CLEANED_FILE_NAME;


    public static void main(String[] args) throws Exception {
        // clean file
        cleanFile(IN_FILE, CLEANED_FILE);
        // clean file for weka
        cleanFile(IN_FILE, WEKA_FILE, true);
    }

    public static void cleanFile(String in, String out) throws IOException {
        cleanFile(in, out, false);
    }

    public static void cleanFile(String in, String out, boolean weka) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        BufferedReader br = new BufferedReader(new FileReader(in));

        String delimiter=";";
        if(weka)
            delimiter=",";

        String line = br.readLine();
        final int colnr = Util.extractTargetColumns(line, mhNames, mhCols);

        System.out.println("Checking file...");
        int cntR = 0;
        int cntW = 0;

        while (br.ready()) {
            boolean drop = false;
            line = br.readLine();
            cntR++;
            String[] lineSplit = line.split(Pattern.quote(","), -1);
            if (lineSplit.length != colnr) {    // if number of columns does not match number of column names
                System.out.println("Row " + cntR + " has wrong number of columns: expected " + colnr + ", was " + lineSplit.length);
                drop = true;
            } else {
                for (int i : mhCols) {
                    if (lineSplit[i].isBlank()) {
                        System.out.println("Must-have column in row " + cntR + " is empty, but should have value - will not copy");
                        drop = true;
                        break;
                    }
                }
            }


            if (!drop) {
                for (int i = 0; i < lineSplit.length; i++) {
                    if (weka && lineSplit[i].isBlank())
                        lineSplit[i] = "?";
                }
                bw.write(String.join(delimiter, lineSplit));
                bw.newLine();
                cntW++;
            }

            if (cntR % 1000 == 0) {
                System.out.println(cntR + " lines read, " + cntW + " lines written");
            }
        }
        System.out.println(cntR + " lines read, " + cntW + " lines written, " + (cntR - cntW) + " dropped");
        bw.close();
        br.close();

    }
}
