#
# Predictor using ETS functions. 
#
Predictor <- function(p,d,q) {
    yVals <- c()
    learnClosed <- FALSE
    f <- NULL
    pars <- c(p,d,q)

    
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
            ar <- arima(x=yVals, order=pars)
            f <<- predict(ar)
        }

        return(f$pred)
    }

    return(list(forecast=forecast, learn=learn, clean=clean))
}
