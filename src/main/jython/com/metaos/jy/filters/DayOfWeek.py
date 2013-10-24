from com.metaos.datamgt import *
from java.util import *


##
## Filters only for the given day of week
##
class DayOfWeek(Filter):
    ##
    ## @param dayOfWeek according to Calendar.SUNDAY,... Calendar.SATURDAY
    ## constants, the day of week to filter.
    ##
    def __init__(self, dayOfWeek):
        self.dayOfWeek = dayOfWeek

    def filter(self, when, symbol, values):
        return when.get(Calendar.DAY_OF_WEEK) == self.dayOfWeek

    def toString(self):
        return "DayOfWeek is " + str(self.dayOfWeek)
