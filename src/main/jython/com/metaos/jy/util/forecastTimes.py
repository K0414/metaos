from com.metaos import *
from com.metaos.datamgt import *
from com.metaos.engine import *
from com.metaos.util import *
from com.metaos.util.backtesting import *
from com.metaos.ext import *
from com.metaos.ext.filters import *
from com.metaos.signalgrt import *
from com.metaos.signalgrt.predictors import *
from java.util import *
from java.text import *

from com.metaos.jy.filters.MercadoContinuoIsOpen import MercadoContinuoIsOpen
from com.metaos.jy.filters.MercadoContinuo0915to1715 import \
        MercadoContinuo0915to1715

##
## Time signaling for prediction moment specific for static predictions.
##
## A prediction is "static" when it's made before the day bagins and
## doesn't change while the day runs.
##
class OneDayAvoidingWeekEnds(ForecastingTime):
    ##
    ## hourLimit: limit hour to consider the end of the day.
    ## minutesLimit: limit minutes in hout to consider the end of the day.
    ##
    ## DANGER: Pay attention: hourLimit-minutesLimit must be readable and
    ## included in event notifications. For example if LAST moment in day
    ## is really 17:35, you cannot say to this forecasting time object 
    ## 17:36 as the hour and minutes limit.
    ##
    def __init__(self):
        self.lastDay = 0
        self.lastMonth = 0
        self.lastYear = 0
        self.lastMinuteInDayReachedYesterday = False

    # Evals at the end of current day
    def shouldEvaluatePrediction(self, when):
        return self.isNotWeekend(when) and self.isLastMinuteInDay(when)

    # Predict at the begining of new day
    def shouldPredict(self, when):
        r = self.isNotWeekend(when)
        if self.lastDay == when.get(Calendar.DAY_OF_MONTH) \
                    and self.lastMonth == when.get(Calendar.MONTH) \
                    and self.lastYear == when.get(Calendar.YEAR):
            r = False

        # Integrity check: tests if new day has came and end of day has 
        # been reached  
        if r and self.lastDay!=0 and self.lastMonth!=0 and self.lastYear!=0:
            if not self.lastMinuteInDayReachedYesterday:
                print "Error: new day comes without being notified of " \
                        + "the end of previous day"
            else:
                self.lastMinuteInDayReachedYesterday = False

        # Updates last valid called moment
        self.lastDay = when.get(Calendar.DAY_OF_MONTH)
        self.lastMonth = when.get(Calendar.MONTH)
        self.lastYear = when.get(Calendar.YEAR)

        return r

    def setDailyHourLimit(self, hours, minutes):
        self.minutesLimit = minutes + (hours*60)


    # Private stuff ------

    def isNotWeekend(self, when):
        return when.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY \
            and when.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY

    def isLastMinuteInDay(self, when):
        minute = (when.get(Calendar.HOUR_OF_DAY)*60) + when.get(Calendar.MINUTE)
        if minute==self.minutesLimit:
            self.lastMinuteInDayReachedYesterday = True
            return True
        else:
            return False




##
## Time signalign for prediction moment specific for dynamic predictions.
## A prediction is "dynamic" if it changes in the measure that day runs.
##
class DayRunsAvoidingWeekEnds(ForecastingTime):
    def __init__(self, trainingMinutes):
        #self.openMoment = MercadoContinuoIsOpen()
        self.openMoment = MercadoContinuo0915to1715()
        self.trainingMinutes = trainingMinutes
        
    def shouldEvaluatePrediction(self, when):
        return self.isNotWeekend(when) and self.isLastMinuteInDay(when)

    def shouldPredict(self, when):
        return self.openMoment.filter(when, None, None) and \
                self.openMoment.minutesFromStart(when) > self.trainingMinutes
               

    # Private stuff ------

    def isNotWeekend(self, when):
        return when.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY \
            and when.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY

    def isLastMinuteInDay(self, when):
        minute = when.get(Calendar.HOUR_OF_DAY)*60 + when.get(Calendar.MINUTE)
        minute = int(minute)
        return minute>=1055

    def setDailyHourLimit(self, hours, minutes):
        None
