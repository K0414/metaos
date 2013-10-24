from com.metaos.util import CalUtils
from java.util import Calendar

##
## Generator of "instants" for VolumeViews
##
class LocalTimeMinutes(CalUtils.InstantGenerator):
    def __init__(self, resolutionInMinutes=1):
        self.resolutionInMinutes = resolutionInMinutes

    def generate(self, when):
        minute = when.get(Calendar.HOUR_OF_DAY)*60 + when.get(Calendar.MINUTE)
        return int(minute/self.resolutionInMinutes)

    def maxInstantValue(self):
        return int(60*24/self.resolutionInMinutes)
