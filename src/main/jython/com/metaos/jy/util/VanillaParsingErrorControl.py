from com.metaos.datamgt import *

##
## The most simple parsing error control.
##
class VanillaParsingErrorControl(LineParser.ErrorControl):
    def unknownType(self, line, position, formatter, part):
        if len(part)>0 and not line.startswith('#'):
            print 'Parsing unknown type ' + str(part) + ' in line ' + str(line)

    def exception(self, line, position, formatter, part, exception):
        if len(part)>0 and not line.startswith('#'):
            print 'Exception parsing ' + str(part) + ' in line ' + str(line) \
                    + ' using parser ' + str(formatter) + '. Exception text "' \
                    + str(exception) + '".'
