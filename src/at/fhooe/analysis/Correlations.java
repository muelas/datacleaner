package at.fhooe.analysis;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.regex.Pattern;


public class Correlations {

    public static final String[] targetNames = {"vLuft_Windkanal", "alpha", "beta"};    // names of target columns
    public static final int tnr = targetNames.length;   // number of target columns
    public static final int[] targetCols = new int[tnr];    // ids of target columns (to be exctracted from file)
    public static final int PC = 0;
    public static final int SR = 1;
    public static int colnr;    // number of overall columns in file

    // Files
    public static final String HOME_PATH = "W:\\Users\\Andreas\\OneDrive";
    public static final String WORK_PATH = "C:\\Users\\p27389";
    public static final String ONEDRIVE_PATH = "\\OneDrive - FH OOe\\Dokumente\\FHOOE\\Forschung\\Datenanalyse\\";
    public static final String IN_FILE_NAME = "cleaned.csv";
    public static final String OUT_FILE_NAME = "pc.txt";
    public static final String IN_FILE = WORK_PATH + ONEDRIVE_PATH + IN_FILE_NAME;
    public static final String OUT_FILE = WORK_PATH + ONEDRIVE_PATH + OUT_FILE_NAME;


    public static void main(String[] args) throws Exception {

        //@work
        BufferedReader br = new BufferedReader(new FileReader(IN_FILE));

        String line = br.readLine();
        String[] cols = line.split(Pattern.quote(";"), -1);  // split by colon

        colnr = Util.extractTargetColumns(line, targetNames, targetCols, null, false);
        br.close();


        // calculate pairwise correlation for single column
//        double[] c = calculateCorrelation("Accel1 X");

        // calculate pairwise correlation for all columns
        PrintWriter pw = new PrintWriter(new FileWriter(OUT_FILE));

        for (int i = 0; i < colnr; i++) {
            double[][] corr = calculateCorrelation(cols[i]);
            pw.println(cols[i] + " pearson correlation to ");
            for (int j = 0; j < tnr; j++) {
                pw.println("  " + targetNames[j] + " = " + corr[PC][j]);
            }
            /* GARBAGE!
            pw.println(cols[i] + " spearman correlation to ");
            for (int j = 0; j < tnr; j++) {
                pw.println("  " + targetNames[j] + " = " + corr[SR][j]);
            }
            */
        }
        pw.close();
    }

    public static final double[][] calculateCorrelation(String s) throws Exception {
        final int x;
        final Util.CorrelationData cd = new Util.CorrelationData();
        cd.name = s;

        System.out.println("Calculating correlation for '" + cd.name + "'...");

        BufferedReader br = new BufferedReader(new FileReader(IN_FILE));

        String line = br.readLine();
        colnr = Util.extractTargetColumns(line, targetNames, targetCols, cd, false);

        // for Pearson Correlation
        final double[] sum = new double[tnr];
        final double[] sumSq = new double[tnr];
        double sumCorr = 0;
        double sumSqCorr = 0;
        final double[] sumProd = new double[tnr];

        // for Spearmans Rho
        final double[] d = new double[tnr];

        int linenr = 0;
        while (br.ready()) {
            line = br.readLine();
            double[] lineData = Util.parseLine(line, colnr, targetCols, cd.col);
            if (lineData == null) {
                continue;
            }
            linenr++;

            // sum up target columns, their squares and their product
            int i = 0;
            for (int col : targetCols) {
                // calculations for Pearsons correlation
                sum[i] += lineData[col];
                sumSq[i] += lineData[col] * lineData[col];
                sumProd[i] += lineData[col] * lineData[cd.col];
                // calculate d for Spearmans Rho
                d[i] += (lineData[cd.col] - lineData[col]) * (lineData[cd.col] - lineData[col]);


                i++;
            }
            // sum up correlation column
            sumCorr += lineData[cd.col];
            sumSqCorr += lineData[cd.col] * lineData[cd.col];

        }

        br.close();

        // NOPE! - finally calculate mean by division
//        for (int i = 0; i < sum.length; i++) {
//            sum[i] /= linenr;
//            sumSq[i] /= linenr;
//            sumProd[i] /= linenr;
//        }
//        sumCorr /= linenr;


        /* =================== */
        /* Pearson Correlation */
        /* =================== */
        // Calculation according to: https://www.scribbr.com/statistics/pearson-correlation-coefficient/
        double[][] corr = new double[2][tnr];
        int n = linenr;
        for (int i = 0; i < tnr; i++) {
            corr[PC][i] = (n * sumProd[i] - sum[i] * sumCorr) / Math.sqrt((n * sumSq[i] - (sum[i] * sum[i])) * (n * sumSqCorr - (sumCorr * sumCorr)));
            if (corr[PC][i] > 0.9 || corr[PC][i] < -0.9)
                System.out.print("\u001B[35m");
            System.out.println("Pearson Correlation between '" + cd.name + "' and '" + targetNames[i] + "' is " + corr[PC][i] + "\u001B[30m");
        }

        // Calculation according to: https://www.scribbr.com/statistics/correlation-coefficient/#spearmans-rho
        // GARBAGE!!! - 2022-12-07
        BigDecimal bigN = new BigDecimal(n, MathContext.DECIMAL128);
        BigDecimal denominator = new BigDecimal(n, MathContext.DECIMAL128);
        denominator = denominator.multiply(bigN, MathContext.DECIMAL128);
        denominator = denominator.multiply(bigN, MathContext.DECIMAL128);
        denominator = denominator.subtract(bigN, MathContext.DECIMAL128);
        for (int i = 0; i < tnr; i++) {
            BigDecimal bigCorr = new BigDecimal(1, MathContext.DECIMAL128);
            BigDecimal numerator = new BigDecimal(6, MathContext.DECIMAL128);
            numerator = numerator.multiply(new BigDecimal(d[i], MathContext.DECIMAL128));
            bigCorr = bigCorr.subtract(numerator.divide(denominator, MathContext.DECIMAL128), MathContext.DECIMAL128);
            corr[SR][i] = bigCorr.doubleValue(); //1-((6*d[i])/(n*n*n-n));
            if (corr[SR][i] > 0.9)
                System.out.print("\u001B[35m");
            // System.out.println("Spearmans Rho between '" + cd.name + "' and '" + targetNames[i] + "' is " + corr[SR][i] + "\u001B[30m");
        }
        // END OF GARBAGE

        // return pearson correlation between the chosen column and the target columns
        return corr;
    }

}
