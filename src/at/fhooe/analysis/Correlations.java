package at.fhooe.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.regex.Pattern;


public class Correlations {

    public static final String[] targetNames = {"vLuft_Windkanal", "alpha", "beta"};    //names of target columns
    public static final int tnr = targetNames.length;
    public static final int[] targetCols = new int[tnr];
    public static int colnr;

    public static final String HOME = "W:\\Users\\Andreas\\OneDrive\\OneDrive - FH OOe\\Dokumente\\FHOOE\\Forschung\\Datenanalyse\\cleaned.csv";
    public static final String WORK = "C:\\Users\\p27389\\OneDrive - FH OOe\\Dokumente\\FHOOE\\Forschung\\Datenanalyse\\cleaned.csv";
    public static final String FILE = WORK;

    public static void main(String[] args) throws Exception {

        //@work
        BufferedReader br = new BufferedReader(new FileReader(FILE));

        String line = br.readLine();
        String[] cols = line.split(Pattern.quote(";"), -1);  // split by colon

        colnr = Util.extractTargetColumns(line, targetNames, targetCols, null, true);
        br.close();


        double[] c = calculateCorrelation("Accel1 Y");
        System.out.println("CORR: " + Arrays.asList(c));

        /*
        double[] corr;
        for (int i = 0; i < colnr; i++) {
            corr = calculateCorrelation(cols[i]);
        }
        */
    }

    public static final double[] calculateCorrelation(String s) throws Exception {
        final int x;
        final Util.CorrelationData cd = new Util.CorrelationData();
        cd.name = s;

        System.out.println("Calculating correlation for '" + cd.name + "'...");

        BufferedReader br = new BufferedReader(new FileReader(FILE));

        String line = br.readLine();
        colnr = Util.extractTargetColumns(line, targetNames, targetCols, cd, false);

        final double[] mean = new double[tnr];
        double meanCorr = 0;
        final double[] meanProd = new double[tnr];

        int linenr = 0;
        while (br.ready()) {
            line = br.readLine();
            double[] lineData = Util.parseLine(line, colnr, targetCols, cd.col);
            if (lineData == null) {
                continue;
            }
            linenr++;

            // sum up target columns for mean and product for mean
            int i = 0;
            for (int col : targetCols) {
                mean[i] += lineData[col];
                meanProd[i++] += lineData[col] * lineData[cd.col];
            }
            // sum up correlation column for mean
            meanCorr += lineData[cd.col];
        }

        br.close();

        // finally calculate mean by division
        for (int i = 0; i < mean.length; i++) {
            mean[i] /= linenr;
            meanProd[i] /= linenr;
        }
        meanCorr /= linenr;
        System.out.println("meanCorr="+meanCorr);

        double[] corr = new double[tnr];
        for (int i = 0; i < tnr; i++) {
            corr[i] = (meanProd[i] - (mean[i] * meanCorr));
            System.out.println("Correlation between '" + cd.name + "' and '" + targetNames[i] + "' is " + corr[i]);
        }

        return corr;
    }

}
