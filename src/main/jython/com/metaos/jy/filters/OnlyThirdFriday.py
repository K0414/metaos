from com.metaos.datamgt import Filter
from java.util import *

##
## Filters only (or only not) third monthly friday.
##
class OnlyThirdFriday(Filter):
    ##
    ## @param positive is >0 to filter only for third friday in the month
    ## or <0 to filter for not third friday in the month.
    ##
    def __init__(self, positive):
        self.positive = positive

    def filter(self, when, symbol, values):
        isThirdWeek = when.get(Calendar.WEEK_OF_MONTH) == 3
        isThirdFriday = when.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY \
                and isThirdWeek                
        
        if self.positive>0: return isThirdFriday
        else: return not isThirdFriday


    def toString(self):
        return "OnlyThirdFriday"
