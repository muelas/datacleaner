package at.fhooe.windpuls.analysis;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Pattern;


public class Correlations {

    public static final String[] targetNames = {"vLuft_Windkanal", "alpha", "beta"};    // names of target columns
    public static final int tnr = targetNames.length;   // number of target columns
    public static final int[] targetCols = new int[tnr];    // ids of target columns (to be exctracted from file)
    public static final int PC = 0;
    public static final int SR = 1;
    public static final int WINDOW = 2500;
    public static final int OFFSET = 2000;
    public static int colnr;    // number of overall columns in file

    public static final String IN_FILE_NAME = "cleaned.csv";
    public static final String OUT_FILE_NAME = "pc-with-window.txt";
    public static final boolean home = false;
    public static final String IN_FILE = (home ? Util.HOME_PATH : Util.WORK_PATH) + Util.ONEDRIVE_PATH + IN_FILE_NAME;
    public static final String OUT_FILE = (home ? Util.HOME_PATH : Util.WORK_PATH) + Util.ONEDRIVE_PATH + OUT_FILE_NAME;


    public static void main(String[] args) throws Exception {

        //@work
        BufferedReader br = new BufferedReader(new FileReader(IN_FILE));

        String line = br.readLine();
        String[] cols = line.split(Pattern.quote(";"), -1);  // split by colon

        colnr = Util.extractTargetColumns(line, targetNames, targetCols, null, false);
        br.close();


        /*
        // calculate pairwise correlation for single column
        String colName = "Pressure3";
        Util.CorrelationResult corr = calculateCorrelation(colName, WINDOW, OFFSET);
        System.out.println("'" + colName + "' pearson correlation to ");
        for (int j = 0; j < tnr; j++) {
            System.out.println("  '" + targetNames[j] + "' = " + corr.pc[j]);
        }
        if (WINDOW > 0 && corr.correlatios.size() > 0) {
            System.out.println("Windowed, size=" + WINDOW + ", offset=" + OFFSET);
            for (Util.Correlation c : corr.correlatios) {
                System.out.print("\u001B[31m");
                //System.out.println("Pearson Correlation between '" + cd.name + "' and '" + targetNames[i] + "' is " + wPC);
                System.out.println(c);
                System.out.print("\u001B[30m");
            }
        }
        System.out.println("");
        */

        // calculate pairwise correlation for all columns
        PrintWriter pw = new PrintWriter(new FileWriter(OUT_FILE));

        for (int i = 0; i < colnr; i++) {
            Util.CorrelationResult corr = calculateCorrelation(cols[i], WINDOW, OFFSET);
            pw.println("'" + cols[i] + "' pearson correlation to ");
            for (int j = 0; j < tnr; j++) {
                pw.println("  '" + targetNames[j] + "' = " + corr.pc[j]);
            }
            if (WINDOW > 0 && corr.correlatios.size() > 0) {
                pw.println("  Windowed, size=" + WINDOW + ", offset=" + OFFSET);
                for (Util.Correlation c : corr.correlatios) {
                    pw.print("   ");
                    pw.println(c);
                }
            }
            pw.println("");
            // GARBAGE!
//            pw.println(cols[i] + " spearman correlation to ");
//            for (int j = 0; j < tnr; j++) {
//                pw.println("  " + targetNames[j] + " = " + corr[SR][j]);
//            }
        }
        pw.close();
    }

    public static final Util.CorrelationResult calculateCorrelation(String s) throws Exception {
        return calculateCorrelation(s, 0, 0);
    }


    public static final Util.CorrelationResult calculateCorrelation(String s, int window, int minOffset) throws Exception {
        // Stores name and column number of correlation column
        final Util.CorrelationColumn cc = new Util.CorrelationColumn();
        // Name of correlation column is known (parameter s)
        cc.name = s;

        System.out.println("Calculating correlation for '" + cc.name + "'...");

        // Open input file for reading
        BufferedReader br = new BufferedReader(new FileReader(IN_FILE));

        String line = br.readLine();
        colnr = Util.extractTargetColumns(line, targetNames, targetCols, cc, false);

        // for Pearson Correlation
        final double[] sum = new double[tnr];
        final double[] sumSq = new double[tnr];
        double sumCorr = 0;
        double sumSqCorr = 0;
        final double[] sumProd = new double[tnr];

        // for Spearmans Rho - Useless!
        final double[] d = new double[tnr];

        // Sliding window
        final double[] wSum = new double[tnr];
        final double[] wSumSq = new double[tnr];
        double wSumCorr = 0;
        double wSumSqCorr = 0;
        final double[] wSumProd = new double[tnr];
        double wPC = 0;

        ArrayBlockingQueue<Double>[] history = null;
        if (window > 0) {
            history = new ArrayBlockingQueue[tnr + 1];
            for (int i = 0; i < tnr + 1; i++)
                history[i] = new ArrayBlockingQueue<Double>(window);
        }

        // Datastructure to store results
        Util.CorrelationResult corr = new Util.CorrelationResult();

        int linenr = 0;
        int offsetCnt = 0;
        while (br.ready()) {
            line = br.readLine();
            double[] lineData = Util.parseLine(line, colnr, targetCols, cc.col);
            if (lineData == null) {
                continue;
            }

            // sum up correlation column
            sumCorr += lineData[cc.col];
            sumSqCorr += lineData[cc.col] * lineData[cc.col];

            double subCol = 0;
            if (window > 0 && linenr >= window)
                subCol = history[history.length - 1].take();


            // sum up target columns, their squares and their product
            int i = 0;
            for (int col : targetCols) {
                // calculations for Pearsons correlation
                sum[i] += lineData[col];
                sumSq[i] += lineData[col] * lineData[col];
                sumProd[i] += lineData[col] * lineData[cc.col];
                // calculate d for Spearmans Rho
                d[i] += (lineData[cc.col] - lineData[col]) * (lineData[cc.col] - lineData[col]);

                // windowed stuff
                if (window > 0) {
                    // are there enough lines to fill the window
                    if (linenr >= window) {
                        final int n = window;
                        wPC = (n * wSumProd[i] - wSum[i] * wSumCorr) / Math.sqrt((n * wSumSq[i] - (wSum[i] * wSum[i])) * (n * wSumSqCorr - (wSumCorr * wSumCorr)));

                        final double LIMIT = 0.85;
                        if ((wPC > LIMIT || wPC < -LIMIT) && (wPC != Double.POSITIVE_INFINITY && wPC != Double.NEGATIVE_INFINITY) && (offsetCnt > minOffset || corr.correlatios.isEmpty())) {
                            Util.Correlation c = new Util.Correlation();
                            c.col = new Util.CorrelationColumn(cc.name, cc.col);
                            c.target = new Util.CorrelationColumn(targetNames[i], targetCols[i]);
                            c.pc = wPC;
                            c.wTo = linenr;
                            c.wFrom = linenr - window;
                            corr.correlatios.add(c);
//                            System.out.print("\u001B[31m");
//                            //System.out.println("Pearson Correlation between '" + cd.name + "' and '" + targetNames[i] + "' is " + wPC);
//                            System.out.println(c);
//                            System.out.print("\u001B[30m");
                            offsetCnt = 0;
                        }

                        final double subTarg = history[i].take();
                        wSum[i] -= subTarg;
                        wSumSq[i] -= subTarg * subTarg;
                        wSumProd[i] -= subTarg * subCol;
                    } else {
                        offsetCnt = 0;
                    }
                    // calculations for pearsons correlation with sliding window
                    wSum[i] += lineData[col];
                    wSumSq[i] += (lineData[col] * lineData[col]);
                    wSumProd[i] += (lineData[col] * lineData[cc.col]);

                    history[i].add(lineData[col]);
                }
                i++;
            }

            wSumCorr += lineData[cc.col];
            wSumSqCorr += lineData[cc.col] * lineData[cc.col];

            if (window > 0) {
                history[i].add(lineData[cc.col]);
                if (linenr >= window) {
                    wSumCorr -= subCol;
                    wSumSqCorr -= subCol * subCol;
                }
            }

            linenr++;
            offsetCnt++;
        }

        br.close();

        // Useless! - finally calculate mean by division
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
        corr.pc = new double[tnr];

        final int n = linenr;   // just to make the formula somewhat more readable
        for (int i = 0; i < tnr; i++) {
            corr.pc[i] = (n * sumProd[i] - sum[i] * sumCorr) / Math.sqrt((n * sumSq[i] - (sum[i] * sum[i])) * (n * sumSqCorr - (sumCorr * sumCorr)));
            if (corr.pc[i] > 0.9 || corr.pc[i] < -0.9)
                System.out.print("\u001B[35m");
            System.out.println("Pearson Correlation between '" + cc.name + "' and '" + targetNames[i] + "' is " + corr.pc[i] + "\u001B[30m");
            System.out.print("\u001B[30m");
        }

        // Calculation according to: https://www.scribbr.com/statistics/correlation-coefficient/#spearmans-rho
        // GARBAGE!!! (because wrong) - 2022-12-07
        corr.sr = new double[tnr];
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
            corr.sr[i] = bigCorr.doubleValue(); //1-((6*d[i])/(n*n*n-n));
//            if (corr.sr[i] > 0.9)
//                System.out.print("\u001B[35m");
            // System.out.println("Spearmans Rho between '" + cd.name + "' and '" + targetNames[i] + "' is " + corr[SR][i] + "\u001B[30m");
        }
        // END OF GARBAGE

        // return pearson correlation between the chosen column and the target columns
        return corr;
    }

}
