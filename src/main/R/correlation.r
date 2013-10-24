correlator <- function() {
    xVals <- c()
    yVals <- c()

    memo <- function(x, y) {
        xVals <<- union(xVals, x)
        yVals <<- union(yVals, y)
    }


    show <- function() {
        r <- lm(xVals ~ yVals)
        return(r)
    }

    return(list(memo=memo, show=show))
}
