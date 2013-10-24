/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors;

import com.metaos.engine.*;
import com.metaos.util.*;
import com.metaos.signalgrt.predictors.simple.*;
import com.metaos.signalgrt.predictors.specific.*;
import java.text.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * Testing PCAPredictorTransposed.
 */
public class TestPCAPredictorTransposed {
    private static class LocalTimeMinutes implements CalUtils.InstantGenerator {
        private final int minutes;
        public LocalTimeMinutes(final int minutes) { this.minutes = minutes; }
        public int generate(final Calendar when) {
            return when.get(Calendar.HOUR_OF_DAY)*60+when.get(Calendar.MINUTE);
        }
        public int maxInstantValue() { return 60*24/minutes; }
    }

    @Test
    public void testPrediction1() throws Exception {
        // Tests blocks of five minutes using only the first eigenvector

        final CalUtils.InstantGenerator instantGenerator =
                new LocalTimeMinutes(5);
                
        final double coreDailyVols[][] = new double[][] {
                new double[] {55219 , 262256 , 202661 , 218359 , 178244 , 99610 , 92348 , 124795 , 214370 , 153854 , 204116 , 173501 , 85390 , 156835 , 108070 , 23755 , 118573 , 70117 , 55768 , 52643 , 71485 , 407645 , 442909 , 129109 , 188896 , 79590 , 422121 , 290067 , 227955 , 69257 , 41634 , 446002 , 579188 , 86237 , 1606794 , 83676 , 166393 , 84987 , 875905 , 117013 , 399084 , 116190 , 149507 , 207221 , 60857 , 155612 , 448006 , 198637 , 67695 , 65423 , 180038 , 88774 , 80273 , 86065 , 85231 , 38867 , 29330 , 116353 , 26887 , 34170 , 102518 , 72246 , 21274 , 70752 , 37912 , 49367 , 100472 , 49461 , 41735 , 45795 , 36578 , 311945 , 249039 , 70487 , 121906 , 136424 , 195136 , 166308 , 331734 , 343180 , 419616 , 104613 , 1354058 , 162678 , 141067 , 147039 , 149115 , 271162 , 543989 , 184421 , 340679 , 201939 , 293860 , 171035 , 263964 , 260198 , 428087 , 565126 , 385874 , 547890 , 384416 , 256696 , 0 , 4738359},
                new double[] {1298630 , 678084 , 488607 , 224766 , 434263 , 356933 , 576571 , 219236 , 252805 , 414776 , 166828 , 174665 , 146281 , 110944 , 145234 , 179739 , 253111 , 175685 , 64925 , 216682 , 494507 , 100205 , 67371 , 101019 , 158026 , 316281 , 334067 , 954850 , 115547 , 163051 , 130303 , 107600 , 1407996 , 90357 , 110452 , 451866 , 238004 , 3096215 , 2672803 , 190170 , 111282 , 107135 , 453389 , 60821 , 98292 , 1310864 , 1132267 , 241907 , 89915 , 175676 , 61621 , 521553 , 212388 , 288651 , 193578 , 272161 , 256777 , 236382 , 802159 , 230248 , 387068 , 160647 , 106999 , 391933 , 465080 , 374577 , 340378 , 330708 , 416320 , 200347 , 251986 , 336664 , 311970 , 600559 , 508011 , 922379 , 311581 , 352459 , 508727 , 159316 , 1355635 , 246541 , 389672 , 805957 , 370754 , 382556 , 316971 , 564228 , 437166 , 277733 , 1284505 , 1763095 , 169661 , 280682 , 969102 , 540315 , 451895 , 308036 , 715130 , 642966 , 981563 , 900778 , 0 , 7155528},
                new double[] {679280 , 229518 , 346536 , 347215 , 316025 , 313890 , 235844 , 199995 , 1920617 , 129356 , 172084 , 207860 , 317578 , 10369008 , 480990 , 1403537 , 1021730 , 156125 , 94833 , 366987 , 145687 , 322957 , 328120 , 66657 , 176001 , 271003 , 133121 , 558624 , 264638 , 638663 , 165080 , 129439 , 5126344 , 5438632 , 248806 , 250616 , 112716 , 54523 , 198097 , 67772 , 1414565 , 244509 , 246205 , 151540 , 98584 , 51217 , 94193 , 111763 , 104726 , 45880 , 64242 , 78893 , 60706 , 48117 , 133085 , 101941 , 5103803 , 5084823 , 168230 , 75537 , 815036 , 73409 , 422412 , 437127 , 115802 , 326536 , 54707 , 81759 , 94420 , 208637 , 50361 , 1458556 , 84257 , 129114 , 54632 , 105873 , 57165 , 77578 , 233302 , 195560 , 134194 , 180928 , 140433 , 123154 , 221422 , 339866 , 1343886 , 114699 , 170052 , 150679 , 181731 , 160943 , 192590 , 125556 , 132656 , 154740 , 320932 , 140929 , 117889 , 381656 , 393635 , 306177 , 0 , 21629250},
                new double[] {526909 , 167180 , 199570 , 149154 , 142141 , 320881 , 223750 , 102275 , 258400 , 202197 , 120202 , 93404 , 178631 , 106401 , 346186 , 231729 , 163656 , 1622531 , 125689 , 2656587 , 5336032 , 2385985 , 335692 , 86118 , 130551 , 99047 , 81695 , 98846 , 238413 , 4831684 , 293262 , 124652 , 106642 , 112048 , 14284646 , 111209 , 2204635 , 128940 , 83395 , 134816 , 116320 , 65412 , 165020 , 126511 , 92217 , 111751 , 47320 , 82219 , 19044177 , 70827 , 21676 , 211214 , 103108 , 22771 , 61629 , 4816563 , 63806 , 33989 , 130104 , 146897 , 15046441 , 44977 , 40889 , 54584 , 54591 , 76634 , 238536 , 68583 , 110591 , 75012 , 503760 , 209479 , 217929 , 86397 , 102284 , 81878 , 252785 , 135884 , 129149 , 112760 , 266851 , 110863 , 67866 , 55205 , 150165 , 699438 , 184450 , 270270 , 4270036 , 345303 , 895116 , 217142 , 145398 , 301231 , 10260595 , 136317 , 442910 , 371357 , 189023 , 538928 , 438973 , 926728 , 9137 , 8879481},
                new double[] {1318228 , 1391326 , 574558 , 441739 , 719144 , 522626 , 404351 , 383602 , 490710 , 284952 , 2984474 , 216339 , 10220195 , 247067 , 166223 , 224310 , 10181837 , 126161 , 9764418 , 692337 , 25907353 , 1518741 , 1179929 , 120730 , 10173292 , 290045 , 19824327 , 402527 , 277859 , 3116841 , 7164061 , 332021 , 10560006 , 2334129 , 121753 , 200177 , 246402 , 10106648 , 1137272 , 2084673 , 461849 , 125108 , 465907 , 156972 , 139083 , 127389 , 237263 , 311691 , 156536 , 155322 , 133368 , 329715 , 256088 , 116835 , 5192615 , 823762 , 183836 , 1110239 , 2414836 , 385072 , 599637 , 387285 , 291580 , 2796924 , 12977051 , 338582 , 884415 , 525622 , 322587 , 223348 , 668858 , 143039 , 627590 , 239797 , 232788 , 256503 , 209425 , 375474 , 558106 , 290991 , 1176648 , 286550 , 149539 , 297435 , 602136 , 152733 , 212363 , 178992 , 179644 , 295428 , 933636 , 349405 , 660749 , 226061 , 868852 , 318539 , 469303 , 538061 , 273643 , 444084 , 347730 , 825808 , 12011 , 7792372}};

        final SimpleDateFormat dateBuilder = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar moments[] = new Calendar[] { Calendar.getInstance(), 
                Calendar.getInstance(), Calendar.getInstance(), 
                Calendar.getInstance(), Calendar.getInstance(),
                Calendar.getInstance() };

        moments[0].setTime(dateBuilder.parse("2011-10-26"));
        moments[1].setTime(dateBuilder.parse("2011-10-27"));
        moments[2].setTime(dateBuilder.parse("2011-10-28"));
        moments[3].setTime(dateBuilder.parse("2011-10-30"));
        moments[4].setTime(dateBuilder.parse("2011-11-01"));
        moments[5].setTime(dateBuilder.parse("2011-11-02"));

        // Nomralize data to sum 100 (i.e., have an expectance of 100/104)
        for(int i=0; i<5; i++) {
            double sum = 0;
            for(int j=0; j<104; j++) sum += coreDailyVols[i][j];
            for(int j=0; j<104; j++) {
                coreDailyVols[i][j] = coreDailyVols[i][j]*100 / sum;
            }
        }

        final double[][] synthesizedDailyVols = new double[
                coreDailyVols.length][instantGenerator.maxInstantValue()];
        final Calendar openTime = Calendar.getInstance();
        final int n = 9*60 / 5;
        final int m = n + coreDailyVols[0].length;
        for(int i=0; i<5; i++) {
            for(int j=0; j<n; j++) {
                synthesizedDailyVols[i][j] = Double.NaN;
            }
            for(int j=0; j<coreDailyVols[i].length; j++) {
                synthesizedDailyVols[i][j+n] = coreDailyVols[i][j];
            }
            for(int j=m; j<instantGenerator.maxInstantValue(); j++) {
                synthesizedDailyVols[i][j] = Double.NaN;
            }
        }


        final Predictor predictor = new PCAPredictorTransposed(
                instantGenerator, 5);
        for(int i=0; i<5; i++) {
            predictor.learnVector(moments[i], synthesizedDailyVols[i]);
        }
        final double prediction[] = predictor.predictVector(moments[5]);
        final double coreExpectedPrediction[] = new double[] {
0.21383323, -0.08665493, -0.28527934, -0.45293966, -0.35525111, -0.49117788,
-0.38114565, -0.60141152, -0.09029406, -0.44815719, -0.45152723, -0.58079146,
-0.29110153, 1.54262599, -0.59900211, -0.48107804, -0.03280398, -0.59369964,
-0.44886852, -0.43868587, 0.88745208, -0.10900099, -0.26251035, -0.71557572,
-0.20051598, -0.57674404, 0.53793693, 0.15857465, -0.53212067, -0.13529962,
-0.49171709, -0.32334222, 2.16101856, 0.46824882, 2.13337330, -0.48802957,
-0.40084079, 1.62077396, 1.93080019, -0.59114756, -0.09429057, -0.68952951,
-0.39819841, -0.63019599, -0.78762027, 0.12458423, 0.34847712, -0.52481068,
0.69730449, -0.74290105, -0.68866588, -0.45964670, -0.69174165, -0.64825389,
-0.49908622, -0.30049621, 0.35726449, 0.47210113, -0.25058065, -0.72112704,
0.79345044, -0.73245678, -0.75581875, -0.40885896, -0.08033429, -0.56030291,
-0.54967743, -0.63571829, -0.58889882, -0.71099478, -0.67055922, -0.03850658,
-0.40339282, -0.43003588, -0.44813762, -0.14347007, -0.48441216, -0.48851502,
-0.15427010, -0.39484463, 0.52701151, -0.61335693, 0.89776479, -0.18821502,
-0.46457371, -0.39850394, -0.26234640, -0.21535441, 0.33669589, -0.48879971,
0.43892192, 0.52101182, -0.42851659, -0.51364225, 0.85831207, -0.24052028,
-0.04192086, -0.02287821, 0.01522206, 0.24307671, 0.27369478, 0.11058009,
-0.95575786, 14.90733910 };
        final double synthesizedExpectedPrediction[] = new double[
                instantGenerator.maxInstantValue()];
        for(int j=0; j<n; j++) {
            synthesizedExpectedPrediction[j] = Double.NaN;
        }
        for(int j=0; j<coreExpectedPrediction.length; j++) {
            synthesizedExpectedPrediction[j+n] = coreExpectedPrediction[j];
        }
        for(int j=m; j<instantGenerator.maxInstantValue(); j++) {
            synthesizedExpectedPrediction[j] = Double.NaN;
        }

        assertEquals(prediction.length, synthesizedExpectedPrediction.length);

        for(int i=0; i<prediction.length; i++) {
            assertEquals(prediction[i], synthesizedExpectedPrediction[i], 
                    0.0001d);
        }
        System.out.println("Ok");
    }



    public static void main(final String args[]) throws Exception {
        try {
            new Engine();
            final TestPCAPredictorTransposed test =
                    new TestPCAPredictorTransposed();
            test.testPrediction1();
        } finally {
            Engine.getR().end();
        }
    }
}
