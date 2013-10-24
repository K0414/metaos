/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors;

import com.metaos.engine.*;
import com.metaos.datamgt.*;
import com.metaos.signalgrt.predictors.simple.*;
import com.metaos.signalgrt.predictors.specific.volume.*;
import com.metaos.util.*;
import java.text.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * Testing PCAVolumeProfileConsideringMarketPredictor.
 */
public class TestPCAVolumeProfileConsideringMarketPredictor {
    private static class LocalTimeMinutes implements CalUtils.InstantGenerator {
        private final int minutes;
        public LocalTimeMinutes(final int minutes) { this.minutes = minutes; }
        public int generate(final Calendar when) {
            return (when.get(Calendar.HOUR_OF_DAY)*60+when.get(Calendar.MINUTE))
                / minutes;
        }
        public int maxInstantValue() { return 60*24/minutes; }
    }

    @Test
    public void testDirectLearning() throws Exception {
        final String symbols[] = new String[] {
             "BBVA.MC", "BKIA.MC", "BKT.MC", "BME.MC", "SAN.MC" };

        final CalUtils.InstantGenerator instantGenerator =
                new LocalTimeMinutes(5);
                
        final double coreDailyVols[][] = new double[][] {
                new double[] {55219 , 262256 , 202661 , 218359 , 178244 , 99610 , 92348 , 124795 , 214370 , 153854 , 204116 , 173501 , 85390 , 156835 , 108070 , 23755 , 118573 , 70117 , 55768 , 52643 , 71485 , 407645 , 442909 , 129109 , 188896 , 79590 , 422121 , 290067 , 227955 , 69257 , 41634 , 446002 , 579188 , 86237 , 1606794 , 83676 , 166393 , 84987 , 875905 , 117013 , 399084 , 116190 , 149507 , 207221 , 60857 , 155612 , 448006 , 198637 , 67695 , 65423 , 180038 , 88774 , 80273 , 86065 , 85231 , 38867 , 29330 , 116353 , 26887 , 34170 , 102518 , 72246 , 21274 , 70752 , 37912 , 49367 , 100472 , 49461 , 41735 , 45795 , 36578 , 311945 , 249039 , 70487 , 121906 , 136424 , 195136 , 166308 , 331734 , 343180 , 419616 , 104613 , 1354058 , 162678 , 141067 , 147039 , 149115 , 271162 , 543989 , 184421 , 340679 , 201939 , 293860 , 171035 , 263964 , 260198 , 428087 , 565126 , 385874 , 547890 , 384416 , 256696 , 0 , 4738359},
                new double[] {1298630 , 678084 , 488607 , 224766 , 434263 , 356933 , 576571 , 219236 , 252805 , 414776 , 166828 , 174665 , 146281 , 110944 , 145234 , 179739 , 253111 , 175685 , 64925 , 216682 , 494507 , 100205 , 67371 , 101019 , 158026 , 316281 , 334067 , 954850 , 115547 , 163051 , 130303 , 107600 , 1407996 , 90357 , 110452 , 451866 , 238004 , 3096215 , 2672803 , 190170 , 111282 , 107135 , 453389 , 60821 , 98292 , 1310864 , 1132267 , 241907 , 89915 , 175676 , 61621 , 521553 , 212388 , 288651 , 193578 , 272161 , 256777 , 236382 , 802159 , 230248 , 387068 , 160647 , 106999 , 391933 , 465080 , 374577 , 340378 , 330708 , 416320 , 200347 , 251986 , 336664 , 311970 , 600559 , 508011 , 922379 , 311581 , 352459 , 508727 , 159316 , 1355635 , 246541 , 389672 , 805957 , 370754 , 382556 , 316971 , 564228 , 437166 , 277733 , 1284505 , 1763095 , 169661 , 280682 , 969102 , 540315 , 451895 , 308036 , 715130 , 642966 , 981563 , 900778 , 0 , 7155528},
                new double[] {679280 , 229518 , 346536 , 347215 , 316025 , 313890 , 235844 , 199995 , 1920617 , 129356 , 172084 , 207860 , 317578 , 10369008 , 480990 , 1403537 , 1021730 , 156125 , 94833 , 366987 , 145687 , 322957 , 328120 , 66657 , 176001 , 271003 , 133121 , 558624 , 264638 , 638663 , 165080 , 129439 , 5126344 , 5438632 , 248806 , 250616 , 112716 , 54523 , 198097 , 67772 , 1414565 , 244509 , 246205 , 151540 , 98584 , 51217 , 94193 , 111763 , 104726 , 45880 , 64242 , 78893 , 60706 , 48117 , 133085 , 101941 , 5103803 , 5084823 , 168230 , 75537 , 815036 , 73409 , 422412 , 437127 , 115802 , 326536 , 54707 , 81759 , 94420 , 208637 , 50361 , 1458556 , 84257 , 129114 , 54632 , 105873 , 57165 , 77578 , 233302 , 195560 , 134194 , 180928 , 140433 , 123154 , 221422 , 339866 , 1343886 , 114699 , 170052 , 150679 , 181731 , 160943 , 192590 , 125556 , 132656 , 154740 , 320932 , 140929 , 117889 , 381656 , 393635 , 306177 , 0 , 21629250},
                new double[] {526909 , 167180 , 199570 , 149154 , 142141 , 320881 , 223750 , 102275 , 258400 , 202197 , 120202 , 93404 , 178631 , 106401 , 346186 , 231729 , 163656 , 1622531 , 125689 , 2656587 , 5336032 , 2385985 , 335692 , 86118 , 130551 , 99047 , 81695 , 98846 , 238413 , 4831684 , 293262 , 124652 , 106642 , 112048 , 14284646 , 111209 , 2204635 , 128940 , 83395 , 134816 , 116320 , 65412 , 165020 , 126511 , 92217 , 111751 , 47320 , 82219 , 19044177 , 70827 , 21676 , 211214 , 103108 , 22771 , 61629 , 4816563 , 63806 , 33989 , 130104 , 146897 , 15046441 , 44977 , 40889 , 54584 , 54591 , 76634 , 238536 , 68583 , 110591 , 75012 , 503760 , 209479 , 217929 , 86397 , 102284 , 81878 , 252785 , 135884 , 129149 , 112760 , 266851 , 110863 , 67866 , 55205 , 150165 , 699438 , 184450 , 270270 , 4270036 , 345303 , 895116 , 217142 , 145398 , 301231 , 10260595 , 136317 , 442910 , 371357 , 189023 , 538928 , 438973 , 926728 , 9137 , 8879481},
                new double[] {1318228 , 1391326 , 574558 , 441739 , 719144 , 522626 , 404351 , 383602 , 490710 , 284952 , 2984474 , 216339 , 10220195 , 247067 , 166223 , 224310 , 10181837 , 126161 , 9764418 , 692337 , 25907353 , 1518741 , 1179929 , 120730 , 10173292 , 290045 , 19824327 , 402527 , 277859 , 3116841 , 7164061 , 332021 , 10560006 , 2334129 , 121753 , 200177 , 246402 , 10106648 , 1137272 , 2084673 , 461849 , 125108 , 465907 , 156972 , 139083 , 127389 , 237263 , 311691 , 156536 , 155322 , 133368 , 329715 , 256088 , 116835 , 5192615 , 823762 , 183836 , 1110239 , 2414836 , 385072 , 599637 , 387285 , 291580 , 2796924 , 12977051 , 338582 , 884415 , 525622 , 322587 , 223348 , 668858 , 143039 , 627590 , 239797 , 232788 , 256503 , 209425 , 375474 , 558106 , 290991 , 1176648 , 286550 , 149539 , 297435 , 602136 , 152733 , 212363 , 178992 , 179644 , 295428 , 933636 , 349405 , 660749 , 226061 , 868852 , 318539 , 469303 , 538061 , 273643 , 444084 , 347730 , 825808 , 12011 , 7792372}};


        // Complete data with not-open-market moments represented as NaN.
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


        final PredictorListener predictor = 
                new PCAVolumeProfileConsideringMarketPredictor(
                    instantGenerator, 0.0d, symbols);

        final Calendar moment = Calendar.getInstance();
        moment.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-26 00:00:00"));
        for(int i=0; i<synthesizedDailyVols[0].length; i++) {
            final double binData[] = new double[5];
            final Calendar now = (Calendar) moment.clone();
            for(int j=0; j<5; j++) {
                binData[j] = synthesizedDailyVols[j][i];
            }
            predictor.learnVector(now, binData);
            moment.add(Calendar.MINUTE, 5);
        }

        final Calendar predictionMoment = Calendar.getInstance();
        predictionMoment.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-27 00:00:00"));

        final double prediction[] = predictor.predictVector(predictionMoment);

        double sum = 0;
        for(int i=0; i<prediction.length; i++) {
            if( ! Double.isNaN(prediction[i]) ) {
                sum += prediction[i];
            }
        }

        // Check if prediction sums 100
        assertEquals(sum, 100.0d, 0.000001d);

        // Check if there is no negative values
        for(int i=0; i<prediction.length; i++) {
            if( ! Double.isNaN(prediction[i]) ) {
                assertEquals(true, prediction[i]>=0);
            }
        }


        // Check the whole prediction

        final double coreExpectedPrediction[] = new double[] {
         1.9288035,1.5177448,0.9332691,0.6917373,0.9226087,0.7836382,
         0.7738424,0.5111597,1.1244135,0.6359005,0.5037461,0.4346380,
         0.4012883,0.4283590,0.5155573,0.6262221,0.6433344,1.0639806,
         0.1995134,0.6614736,0.7967144,1.3838254,1.3177505,0.2612888,
         0.3982873,0.4867157,0.7851480,1.1897632,0.5728564,0.5297141,
         0.4111288,0.7186205,1.6064740,0.3591866,0.2788936,0.5225355,
         0.4710717,0.3902378,1.6495111,0.8969267,1.0190033,0.2823669,
         0.7617028,0.3711431,0.2148944,1.0168186,1.2270160,0.5254874,
         0.2223704,0.2518789,0.2597858,0.6560785,0.3639917,0.2926088,
         0.2736937,0.6651141,0.2898492,0.8638302,1.1774701,0.4286046,
         0.8646737,0.3718615,0.3104019,0.5365190,0.4799254,0.5199251,
         0.8793190,0.5422813,0.4963591,0.3227974,0.7813186,0.9484949,
         0.8559482,0.5814706,0.5639950,0.8369635,0.5824984,0.6162544,
         0.9984222,0.6363279,1.9798710,0.4541408,0.4314029,0.8058571,
         0.7680111,0.8377700,0.7831125,0.8088483,1.0056747,0.6680795,
         2.06074038,1.2474098,0.8186405,0.5901474,1.4477580,0.8018070,
         1.1874030,1.1958628,1.0192618,1.4630908,1.4049921,1.7359802,
         0.0000000,22.2667882};

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




    /**
     * Tests cleaning method.
     */
    public void testTraversalCleaning() throws Exception {
        final String symbols[] = new String[] {
             "BBVA.MC", "BKIA.MC", "BKT.MC", "BME.MC", "SAN.MC" };

        final CalUtils.InstantGenerator instantGenerator =
                new LocalTimeMinutes(5);
                
        final double coreDailyVols[][] = new double[][] {
                new double[] {55219 , 262256 , 202661 , 218359 , 178244 , 99610 , 92348 , 124795 , 214370 , 153854 , 204116 , 173501 , 85390 , 156835 , 108070 , 23755 , 118573 , 70117 , 55768 , 52643 , 71485 , 407645 , 442909 , 129109 , 188896 , 79590 , 422121 , 290067 , 227955 , 69257 , 41634 , 446002 , 579188 , 86237 , 1606794 , 83676 , 166393 , 84987 , 875905 , 117013 , 399084 , 116190 , 149507 , 207221 , 60857 , 155612 , 448006 , 198637 , 67695 , 65423 , 180038 , 88774 , 80273 , 86065 , 85231 , 38867 , 29330 , 116353 , 26887 , 34170 , 102518 , 72246 , 21274 , 70752 , 37912 , 49367 , 100472 , 49461 , 41735 , 45795 , 36578 , 311945 , 249039 , 70487 , 121906 , 136424 , 195136 , 166308 , 331734 , 343180 , 419616 , 104613 , 1354058 , 162678 , 141067 , 147039 , 149115 , 271162 , 543989 , 184421 , 340679 , 201939 , 293860 , 171035 , 263964 , 260198 , 428087 , 565126 , 385874 , 547890 , 384416 , 256696 , 0 , 4738359},
                new double[] {1298630 , 678084 , 488607 , 224766 , 434263 , 356933 , 576571 , 219236 , 252805 , 414776 , 166828 , 174665 , 146281 , 110944 , 145234 , 179739 , 253111 , 175685 , 64925 , 216682 , 494507 , 100205 , 67371 , 101019 , 158026 , 316281 , 334067 , 954850 , 115547 , 163051 , 130303 , 107600 , 1407996 , 90357 , 110452 , 451866 , 238004 , 3096215 , 2672803 , 190170 , 111282 , 107135 , 453389 , 60821 , 98292 , 1310864 , 1132267 , 241907 , 89915 , 175676 , 61621 , 521553 , 212388 , 288651 , 193578 , 272161 , 256777 , 236382 , 802159 , 230248 , 387068 , 160647 , 106999 , 391933 , 465080 , 374577 , 340378 , 330708 , 416320 , 200347 , 251986 , 336664 , 311970 , 600559 , 508011 , 922379 , 311581 , 352459 , 508727 , 159316 , 1355635 , 246541 , 389672 , 805957 , 370754 , 382556 , 316971 , 564228 , 437166 , 277733 , 1284505 , 1763095 , 169661 , 280682 , 969102 , 540315 , 451895 , 308036 , 715130 , 642966 , 981563 , 900778 , 0 , 7155528},
                new double[] {679280 , 229518 , 346536 , 347215 , 316025 , 313890 , 235844 , 199995 , 1920617 , 129356 , 172084 , 207860 , 317578 , 10369008 , 480990 , 1403537 , 1021730 , 156125 , 94833 , 366987 , 145687 , 322957 , 328120 , 66657 , 176001 , 271003 , 133121 , 558624 , 264638 , 638663 , 165080 , 129439 , 5126344 , 5438632 , 248806 , 250616 , 112716 , 54523 , 198097 , 67772 , 1414565 , 244509 , 246205 , 151540 , 98584 , 51217 , 94193 , 111763 , 104726 , 45880 , 64242 , 78893 , 60706 , 48117 , 133085 , 101941 , 5103803 , 5084823 , 168230 , 75537 , 815036 , 73409 , 422412 , 437127 , 115802 , 326536 , 54707 , 81759 , 94420 , 208637 , 50361 , 1458556 , 84257 , 129114 , 54632 , 105873 , 57165 , 77578 , 233302 , 195560 , 134194 , 180928 , 140433 , 123154 , 221422 , 339866 , 1343886 , 114699 , 170052 , 150679 , 181731 , 160943 , 192590 , 125556 , 132656 , 154740 , 320932 , 140929 , 117889 , 381656 , 393635 , 306177 , 0 , 21629250},
                new double[] {526909 , 167180 , 199570 , 149154 , 142141 , 320881 , 223750 , 102275 , 258400 , 202197 , 120202 , 93404 , 178631 , 106401 , 346186 , 231729 , 163656 , 1622531 , 125689 , 2656587 , 5336032 , 2385985 , 335692 , 86118 , 130551 , 99047 , 81695 , 98846 , 238413 , 4831684 , 293262 , 124652 , 106642 , 112048 , 14284646 , 111209 , 2204635 , 128940 , 83395 , 134816 , 116320 , 65412 , 165020 , 126511 , 92217 , 111751 , 47320 , 82219 , 19044177 , 70827 , 21676 , 211214 , 103108 , 22771 , 61629 , 4816563 , 63806 , 33989 , 130104 , 146897 , 15046441 , 44977 , 40889 , 54584 , 54591 , 76634 , 238536 , 68583 , 110591 , 75012 , 503760 , 209479 , 217929 , 86397 , 102284 , 81878 , 252785 , 135884 , 129149 , 112760 , 266851 , 110863 , 67866 , 55205 , 150165 , 699438 , 184450 , 270270 , 4270036 , 345303 , 895116 , 217142 , 145398 , 301231 , 10260595 , 136317 , 442910 , 371357 , 189023 , 538928 , 438973 , 926728 , 9137 , 8879481},
                new double[] {1318228 , 1391326 , 574558 , 441739 , 719144 , 522626 , 404351 , 383602 , 490710 , 284952 , 2984474 , 216339 , 10220195 , 247067 , 166223 , 224310 , 10181837 , 126161 , 9764418 , 692337 , 25907353 , 1518741 , 1179929 , 120730 , 10173292 , 290045 , 19824327 , 402527 , 277859 , 3116841 , 7164061 , 332021 , 10560006 , 2334129 , 121753 , 200177 , 246402 , 10106648 , 1137272 , 2084673 , 461849 , 125108 , 465907 , 156972 , 139083 , 127389 , 237263 , 311691 , 156536 , 155322 , 133368 , 329715 , 256088 , 116835 , 5192615 , 823762 , 183836 , 1110239 , 2414836 , 385072 , 599637 , 387285 , 291580 , 2796924 , 12977051 , 338582 , 884415 , 525622 , 322587 , 223348 , 668858 , 143039 , 627590 , 239797 , 232788 , 256503 , 209425 , 375474 , 558106 , 290991 , 1176648 , 286550 , 149539 , 297435 , 602136 , 152733 , 212363 , 178992 , 179644 , 295428 , 933636 , 349405 , 660749 , 226061 , 868852 , 318539 , 469303 , 538061 , 273643 , 444084 , 347730 , 825808 , 12011 , 7792372}};


        // Complete data with not-open-market moments represented as NaN.
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


        final PredictorListener predictor = 
                new PCAVolumeProfileConsideringMarketPredictor(
                    instantGenerator, 0.0d, symbols);

        final Calendar moment = Calendar.getInstance();
        moment.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-26 00:00:00"));
        for(int i=0; i<synthesizedDailyVols[0].length; i++) {
            final double binData[] = new double[5];
            final Calendar now = (Calendar) moment.clone();
            for(int j=0; j<5; j++) {
                binData[j] = synthesizedDailyVols[j][i];
            }
            predictor.learnVector(now, binData);
            moment.add(Calendar.MINUTE, 5);
        }

        final Calendar predictionMoment = Calendar.getInstance();
        predictionMoment.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-27 00:00:00"));

        final double prediction[] = predictor.predictVector(predictionMoment);

        double sum = 0;
        for(int i=0; i<prediction.length; i++) {
            if( ! Double.isNaN(prediction[i]) ) {
                sum += prediction[i];
            }
        }

        // Check if prediction sums 100
        assertEquals(sum, 100.0d, 0.000001d);

        // Check if there is no negative values
        for(int i=0; i<prediction.length; i++) {
            if( ! Double.isNaN(prediction[i]) ) {
                assertEquals(prediction[i]>=0, true);
            }
        }


        // Check the whole prediction

        final double coreExpectedPrediction[] = new double[] {
            1.1753717, 0.8748835, 0.6762591, 0.5085988, 0.6062873, 0.4703606,
            0.5803928, 0.3601269, 0.8712444, 0.5133813, 0.5100112, 0.3807470,
            0.6704369, 2.5041645, 0.3625363, 0.4804604, 0.9287345, 0.3678388,
            0.5126699, 0.5228526, 1.8489905, 0.8525375, 0.6990281, 0.2459627,
            0.7610225, 0.3847944, 1.4994754, 1.1201131, 0.4294178, 0.8262388,
            0.4698214, 0.6381962, 3.1225570, 1.4297873, 3.0949118, 0.4735089,
            0.5606977, 2.5823124, 2.8923386, 0.3703909, 0.8672479, 0.2720090,
            0.5633400, 0.3313425, 0.1739182, 1.0861227, 1.3100156, 0.4367278,
            1.6588430, 0.2186374, 0.2728726, 0.5018918, 0.2697968, 0.3132846,
            0.4624522, 0.6610423, 1.3188030, 1.4336396, 0.7109578, 0.2404114,
            1.7549889, 0.2290817, 0.2057197, 0.5526795, 0.8812042, 0.4012355,
            0.4118610, 0.3258202, 0.3726396, 0.2505437, 0.2909792, 0.9230319,
            0.5581456, 0.5315026, 0.5134008, 0.8180684, 0.4771263, 0.4730234,
            0.8072684, 0.5666938, 1.4885500, 0.3481815, 1.8593032, 0.7733234,
            0.4969647, 0.5630345, 0.6991921, 0.7461840, 1.2982344, 0.4727388,
            1.4004604, 1.4825503, 0.5330219, 0.4478962, 1.8198505, 0.7210182,
            0.9196176, 0.9386603, 0.9767605, 1.2046152, 1.2352332, 1.0721186,
            0.0057806, 15.8688776 };
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
            final TestPCAVolumeProfileConsideringMarketPredictor test =
                    new TestPCAVolumeProfileConsideringMarketPredictor();
            test.testDirectLearning();
            test.testTraversalCleaning();
        } finally {
            Engine.getR().end();
        }
    }
}
