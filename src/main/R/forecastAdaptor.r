#
# Predictor using ETS functions. 
#
etsPredictor <- function() {
    yVals <- c()
    learnClosed <- FALSE
    r <- NULL
    f <- NULL

    learn <- function(y) {
        yVals <<- append(yVals, y)
    }


    forecast <- function(x) {
        if(!learnClosed) {
            learnClosed <<- TRUE
            r <<- ets(yVals)
            f <<- forecast(r)
        }
    }

    plot <- plot() {
        plot(f)
    }

    return(list(forecast=forecast, learn=learn, plot=plot))
}
