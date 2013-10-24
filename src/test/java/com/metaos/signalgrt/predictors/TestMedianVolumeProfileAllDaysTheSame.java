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
 * Testing MedianVolumeProfileAllDaysTheSame.
 */
public class TestMedianVolumeProfileAllDaysTheSame {
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
        final CalUtils.InstantGenerator instantGenerator =
                new LocalTimeMinutes(5);

        final String symbol = "TEF.MC";
                
        final double coreDailyVols[][] = new double[][] {
                new double[] {55219 , 262256 , 202661 , 218359 , 178244 , 99610 , 92348 , 124795 , 214370 , 153854 , 204116 , 173501 , 85390 , 156835 , 108070 , 23755 , 118573 , 70117 , 55768 , 52643 , 71485 , 407645 , 442909 , 129109 , 188896 , 79590 , 422121 , 290067 , 227955 , 69257 , 41634 , 446002 , 579188 , 86237 , 1606794 , 83676 , 166393 , 84987 , 875905 , 117013 , 399084 , 116190 , 149507 , 207221 , 60857 , 155612 , 448006 , 198637 , 67695 , 65423 , 180038 , 88774 , 80273 , 86065 , 85231 , 38867 , 29330 , 116353 , 26887 , 34170 , 102518 , 72246 , 21274 , 70752 , 37912 , 49367 , 100472 , 49461 , 41735 , 45795 , 36578 , 311945 , 249039 , 70487 , 121906 , 136424 , 195136 , 166308 , 331734 , 343180 , 419616 , 104613 , 1354058 , 162678 , 141067 , 147039 , 149115 , 271162 , 543989 , 184421 , 340679 , 201939 , 293860 , 171035 , 263964 , 260198 , 428087 , 565126 , 385874 , 547890 , 384416 , 256696 , 0 , 4738359},
                new double[] {1298630 , 678084 , 488607 , 224766 , 434263 , 356933 , 576571 , 219236 , 252805 , 414776 , 166828 , 174665 , 146281 , 110944 , 145234 , 179739 , 253111 , 175685 , 64925 , 216682 , 494507 , 100205 , 67371 , 101019 , 158026 , 316281 , 334067 , 954850 , 115547 , 163051 , 130303 , 107600 , 1407996 , 90357 , 110452 , 451866 , 238004 , 3096215 , 2672803 , 190170 , 111282 , 107135 , 453389 , 60821 , 98292 , 1310864 , 1132267 , 241907 , 89915 , 175676 , 61621 , 521553 , 212388 , 288651 , 193578 , 272161 , 256777 , 236382 , 802159 , 230248 , 387068 , 160647 , 106999 , 391933 , 465080 , 374577 , 340378 , 330708 , 416320 , 200347 , 251986 , 336664 , 311970 , 600559 , 508011 , 922379 , 311581 , 352459 , 508727 , 159316 , 1355635 , 246541 , 389672 , 805957 , 370754 , 382556 , 316971 , 564228 , 437166 , 277733 , 1284505 , 1763095 , 169661 , 280682 , 969102 , 540315 , 451895 , 308036 , 715130 , 642966 , 981563 , 900778 , 0 , 7155528},
                new double[] {679280 , 229518 , 346536 , 347215 , 316025 , 313890 , 235844 , 199995 , 1920617 , 129356 , 172084 , 207860 , 317578 , 10369008 , 480990 , 1403537 , 1021730 , 156125 , 94833 , 366987 , 145687 , 322957 , 328120 , 66657 , 176001 , 271003 , 133121 , 558624 , 264638 , 638663 , 165080 , 129439 , 5126344 , 5438632 , 248806 , 250616 , 112716 , 54523 , 198097 , 67772 , 1414565 , 244509 , 246205 , 151540 , 98584 , 51217 , 94193 , 111763 , 104726 , 45880 , 64242 , 78893 , 60706 , 48117 , 133085 , 101941 , 5103803 , 5084823 , 168230 , 75537 , 815036 , 73409 , 422412 , 437127 , 115802 , 326536 , 54707 , 81759 , 94420 , 208637 , 50361 , 1458556 , 84257 , 129114 , 54632 , 105873 , 57165 , 77578 , 233302 , 195560 , 134194 , 180928 , 140433 , 123154 , 221422 , 339866 , 1343886 , 114699 , 170052 , 150679 , 181731 , 160943 , 192590 , 125556 , 132656 , 154740 , 320932 , 140929 , 117889 , 381656 , 393635 , 306177 , 0 , 21629250},
                new double[] {526909 , 167180 , 199570 , 149154 , 142141 , 320881 , 223750 , 102275 , 258400 , 202197 , 120202 , 93404 , 178631 , 106401 , 346186 , 231729 , 163656 , 1622531 , 125689 , 2656587 , 5336032 , 2385985 , 335692 , 86118 , 130551 , 99047 , 81695 , 98846 , 238413 , 4831684 , 293262 , 124652 , 106642 , 112048 , 14284646 , 111209 , 2204635 , 128940 , 83395 , 134816 , 116320 , 65412 , 165020 , 126511 , 92217 , 111751 , 47320 , 82219 , 19044177 , 70827 , 21676 , 211214 , 103108 , 22771 , 61629 , 4816563 , 63806 , 33989 , 130104 , 146897 , 15046441 , 44977 , 40889 , 54584 , 54591 , 76634 , 238536 , 68583 , 110591 , 75012 , 503760 , 209479 , 217929 , 86397 , 102284 , 81878 , 252785 , 135884 , 129149 , 112760 , 266851 , 110863 , 67866 , 55205 , 150165 , 699438 , 184450 , 270270 , 4270036 , 345303 , 895116 , 217142 , 145398 , 301231 , 10260595 , 136317 , 442910 , 371357 , 189023 , 538928 , 438973 , 926728 , 9137 , 8879481},
                new double[] {1318228 , 1391326 , 574558 , 441739 , 719144 , 522626 , 404351 , 383602 , 490710 , 284952 , 2984474 , 216339 , 10220195 , 247067 , 166223 , 224310 , 10181837 , 126161 , 9764418 , 692337 , 25907353 , 1518741 , 1179929 , 120730 , 10173292 , 290045 , 19824327 , 402527 , 277859 , 3116841 , 7164061 , 332021 , 10560006 , 2334129 , 121753 , 200177 , 246402 , 10106648 , 1137272 , 2084673 , 461849 , 125108 , 465907 , 156972 , 139083 , 127389 , 237263 , 311691 , 156536 , 155322 , 133368 , 329715 , 256088 , 116835 , 5192615 , 823762 , 183836 , 1110239 , 2414836 , 385072 , 599637 , 387285 , 291580 , 2796924 , 12977051 , 338582 , 884415 , 525622 , 322587 , 223348 , 668858 , 143039 , 627590 , 239797 , 232788 , 256503 , 209425 , 375474 , 558106 , 290991 , 1176648 , 286550 , 149539 , 297435 , 602136 , 152733 , 212363 , 178992 , 179644 , 295428 , 933636 , 349405 , 660749 , 226061 , 868852 , 318539 , 469303 , 538061 , 273643 , 444084 , 347730 , 825808 , 12011 , 7792372}};

        final Calendar moments[] = new Calendar[] {
                Calendar.getInstance(), Calendar.getInstance(),
                Calendar.getInstance(), Calendar.getInstance(),
                Calendar.getInstance()};
        moments[0].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-26 09:00:00"));
        moments[1].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-27 09:00:00"));
        moments[2].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-28 09:00:00"));
        moments[3].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-30 08:00:00"));
        moments[4].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-11-01 08:00:00"));

        final PredictorListener predictor = 
                new MedianVolumeProfileAllDaysTheSame(5, instantGenerator,
                        symbol);


       for(int day=0; day<5; day++) {
            for(int i=0; i<coreDailyVols[0].length; i++) {
                final Calendar now = (Calendar) moments[day].clone();
                final ParseResult parseResult = new ParseResult();
                parseResult.newTimestamp(0);
                parseResult.getLocalTimestamp(0).setTime(now.getTime());
                parseResult.addSymbol(symbol);
                parseResult.putValue(symbol, new Field.VOLUME(),
                        coreDailyVols[day][i]);
                predictor.notify(parseResult);
                moments[day].add(Calendar.MINUTE, 5);
            }
        }

        final Calendar predictionMoment = Calendar.getInstance();
        predictionMoment.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-11-02 00:00:00"));
 
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


        final double coreExpectedPrediction[] = new double[] {
2.2171978, 1.3472999, 1.0411398, 0.8597762, 0.9157012, 0.9147369,
0.9415250, 0.5618512, 1.1012929, 0.7904013, 0.5058020, 0.5147044,
0.5202958, 0.5941967, 0.5551930, 0.5394660, 0.6486650, 0.3865978,
0.2864995, 0.5553059, 0.5288909, 0.7997084, 1.4125695, 0.2903559,
0.4358149, 0.6710595, 0.6975588, 1.3832687, 0.6682515, 0.6682515,
0.4087723, 0.5245273, 0.7985112, 0.4430293, 0.4430293, 0.4814261,
0.5925973, 0.5425709, 0.6099493, 0.5672967, 1.1107478, 0.3008850,
0.7680692, 0.3775180, 0.3126435, 0.4702407, 0.5706180, 0.6199518,
0.3459721, 0.3361006, 0.1590765, 0.7929653, 0.4338716, 0.2809884,
0.3295460, 0.2593307, 0.2684914, 0.5977457, 0.5474689, 0.5900725,
0.9919659, 0.3711527, 0.2742137, 0.7012505, 0.2867497, 0.8085707,
0.8723102, 0.2885927, 0.4653595, 0.5134431, 0.6457819, 0.8814736,
0.9170307, 0.3635528, 0.5598556, 0.6168903, 0.7985102, 0.8543818,
1.3037498, 0.4842470, 2.1557127, 0.5374332, 0.3596416, 0.7153318,
0.7247100, 0.8415786, 0.7761533, 1.1372781, 1.1203555, 0.7117655,
2.2453966, 0.9137190, 0.6118251, 0.7193232, 1.3560745, 0.7660870,
1.1581025, 1.2940378, 0.7953961, 1.6477734, 1.8471690, 1.9860700,
0.0000000, 24.3425915};
        final double synthesizedExpectedPrediction[] = new double[
                instantGenerator.maxInstantValue()];
        int n = (int) (7 * 60 / 5);
        int m = (int) (15.5 * 60 / 5) + 2;
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
            final TestMedianVolumeProfileAllDaysTheSame test =
                    new TestMedianVolumeProfileAllDaysTheSame();
            test.testDirectLearning();
        } finally {
            Engine.getR().end();
        }
    }
}
