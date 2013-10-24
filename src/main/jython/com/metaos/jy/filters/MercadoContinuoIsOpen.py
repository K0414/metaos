from com.metaos.datamgt import *
from java.util import *

##
## Filters for open hours for Spanish M.C.
##
class MercadoContinuoIsOpen(Filter):
    ##
    ## minutes: size of bin, in minutes.
    ##
    def __init__(self, minutes = 1):
        self.minutes = minutes

    def filter(self, when, symbol, values):
        minute = when.get(Calendar.HOUR_OF_DAY)*60 + when.get(Calendar.MINUTE)
        minute = int(minute)
        return minute<=1055+self.minutes and minute>=540

    def minutesFromStart(self, when):
        minute = when.get(Calendar.HOUR_OF_DAY)*60 + when.get(Calendar.MINUTE)
        minute = int(minute)
        return minute - 540

    def maxDailyHour(self):
        return 17

    def maxDailyMinutes(self):
        return 35 + self.minutes - 1

    def toString(self):
        return "MercadoContinuoIsOpen"
