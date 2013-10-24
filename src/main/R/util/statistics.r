#
# See com.metaos.util.Statistics for more information.
#
# Represents a collector of data to show statistics.
#
Statistics <- function() {
    eVals <- c()

    reset <- function() {
        eVals <<- c()
    }

    addValue <- function(x) {
        eVals <<- append(eVals, x)
    }

    listAll <- function() {
        return(eVals)
    }

    return(list(reset=reset, addValue=addValue, listAll=listAll))
}
