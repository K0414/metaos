#
# Predictor using moving average
#
Predictor <- function(windowSize) {
    library(TTR)
    yVals <- c()
    learnClosed <- FALSE
    f <- NULL
    windowSize <- windowSize

    
    clean <- function() {
        learnClosed <<- FALSE
        yVals <<- c()
        f <<- NULL
    }


    learn <- function(y) {
        yVals <<- append(yVals, y)
    }


    forecast <- function() {
        if(!learnClosed) {
            learnClosed <<- TRUE
            f <<- SMA(yVals, windowSize)
        }

        return(f[length(f)])
    }

    return(list(forecast=forecast, learn=learn, clean=clean))
}
