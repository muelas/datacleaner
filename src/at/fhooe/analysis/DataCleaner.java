package at.fhooe.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Pattern;

public class DataCleaner {
    public static final String[] mhNames = {"vLuft_Windkanal", "alpha", "beta"};
    public static final int mhnr = mhNames.length;
    public static final int[] mhCols = new int[mhnr];

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("W:\\Users\\Andreas\\OneDrive\\OneDrive - FH OOe\\Dokumente\\FHOOE\\Forschung\\Datenanalyse\\wt0028.csv"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("W:\\Users\\Andreas\\OneDrive\\OneDrive - FH OOe\\Dokumente\\FHOOE\\Forschung\\Datenanalyse\\weka.csv"));

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
//                System.out.println("Row "+cntR+" has wrong number of columns: expected "+colnr+", was "+lineSplit.length);
                drop = true;
            } else {
                for (int i : mhCols) {
                    if (lineSplit[i].isBlank()) {
                        System.out.println("Row " + cntR + " is empty, but should have value - will not copy");
                        drop = true;
                        break;
                    }
                }
            }

            if (!drop) {
                for (int i = 0; i < lineSplit.length; i++) {
                    if (lineSplit[i].isBlank())
                        lineSplit[i] = "?";
                }
                bw.write(String.join(",", lineSplit));
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
