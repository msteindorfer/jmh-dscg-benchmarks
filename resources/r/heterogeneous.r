#!/usr/bin/env Rscript
setwd("~/Development/jmh-dscg-benchmarks/")

library(reshape2)

benchmark_fileName <- "map_sizes_heterogeneous_exponential"
benchmark_data <- read.csv(paste0(benchmark_fileName, ".csv"), sep=",", header=TRUE)

benchmark_data.m <- melt(benchmark_data, id.vars=c('elementCount', 'className'), measure.vars=c('footprintInBytes'))
benchmark_data.c <- dcast(benchmark_data.m,  className ~ elementCount, median, fill=0)

normalize <- function(x) { 
  x <- sweep(x, 2, apply(x, 2, min)) 
  sweep(x, 2, apply(x, 2, max), "/") 
} 

benchmark_data.c.norm <- benchmark_data.c
benchmark_data.c.norm[-1] <- normalize(benchmark_data.c[-1]) 

result_fileName <- paste0(benchmark_fileName, "_result.csv")
write.csv2(benchmark_data.c, file = result_fileName, quote = FALSE)

resultNorm_fileName <- paste0(benchmark_fileName, "_result.norm.csv")
write.csv2(benchmark_data.c.norm, file = resultNorm_fileName, quote = FALSE)

tmp <- data.frame(t(benchmark_data.c.norm[-1]))
colnames(tmp) <- benchmark_data.c.norm[,1]

resultNormTransposed_fileName <- paste0(benchmark_fileName, "_result.norm.transposed.csv")
write.csv2(tmp, file = resultNormTransposed_fileName, quote = FALSE)