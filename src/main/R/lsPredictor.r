#
# Linear regression predictor with infinite memory
#
lsPredictor <- function() {
    xVals <- c()
    yVals <- c()
    learnClosed <- FALSE
    r <- NULL

    learn <- function(x, y) {
        xVals <<- append(xVals, x)
        yVals <<- append(yVals, y)
    }


    predict <- function(x) {
        if(!learnClosed) {
            learnClosed <<- TRUE
            r <<- lm(yVals ~ xVals)
        }
        return(r$coefficients[[2]]*x + r$coefficients[[1]])
    }

    return(list(predict=predict, learn=learn))
}
