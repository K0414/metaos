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
 * Testing MedianVolumeProfileDifferentEachDayOfWeek.
 */
public class TestMedianVolumeProfileDifferentEachDayOfWeek {
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
    public void testSimulatedLearning() throws Exception {
        final CalUtils.InstantGenerator instantGenerator =
                new LocalTimeMinutes(5);

        final String symbol = "TEF.MC";
                
        // Synthesize data
        final double coreDailyVols[][] = new double[15][]; // Three weeks
        int pos = 0;
        for(int dayOfWeek=1; dayOfWeek<=5; dayOfWeek++) {
            for(int week=1; week<=3; week++) {
                coreDailyVols[pos] = new double[104];
                coreDailyVols[pos][103] = 1000;
                for(int i=0; i<103; i++) {
                    coreDailyVols[pos][i] = ((1000*.1) / week);
                }
                pos++;
            }
        }


        // Synthesized times
        final Calendar moments[] = new Calendar[15]; // Three weeks
        for(int i=0; i<moments.length; i++) moments[i] = Calendar.getInstance();
        moments[0].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-03 09:00:00"));
        moments[1].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-04 09:00:00"));
        moments[2].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-05 09:00:00"));
        moments[3].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-06 09:00:00"));
        moments[4].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-07 09:00:00"));

        moments[5].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-10 09:00:00"));
        moments[6].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-11 09:00:00"));
        moments[7].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-12 09:00:00"));
        moments[8].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-13 09:00:00"));
        moments[9].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-14 09:00:00"));
 
        moments[10].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-17 09:00:00"));
        moments[11].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-18 09:00:00"));
        moments[12].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-19 09:00:00"));
        moments[13].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-20 09:00:00"));
        moments[14].setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                .parse("2011-10-21 09:00:00"));
 

        // Creates predictor
        final PredictorListener predictor = 
                new MedianVolumeProfileDifferentEachDayOfWeek(3, // Three weeks
                        instantGenerator, symbol);


       for(int day=0; day<moments.length; day++) {
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
                .parse("2011-10-24 00:00:00"));
 
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

/*
        for(int i=0; i<prediction.length; i++) {
            assertEquals(prediction[i], synthesizedExpectedPrediction[i], 
                    0.0001d);
        }
*/
        System.out.println("Ok, but numerical calculi have not been tested");
    }



    public static void main(final String args[]) throws Exception {
        try {
            new Engine();
            final TestMedianVolumeProfileDifferentEachDayOfWeek test =
                    new TestMedianVolumeProfileDifferentEachDayOfWeek();
            test.testSimulatedLearning();
        } finally {
            Engine.getR().end();
        }
    }
}
