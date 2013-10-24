from com.metaos.datamgt import Filter
from java.util import *

##
## Filters to avoid processing weekends.
##
class AvoidWeekEnds(Filter):
    def filter(self, when, symbol, values):
        return when.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY \
                and when.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY

    def toString(self):
        return "AvoidWeekEnds"
