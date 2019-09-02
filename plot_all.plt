fnames=system("/bin/ls ./clustering/cluster1/*.dat")
set nokey
set xlabel "Time [s]"
set ylabel "Normalized-Voltage"
plot for[fn in fnames] sprintf("%s",fn) using ($0*0.1):1 with line
replot "./clustering/cluster1/mean1.dat" using ($0*0.1):1 w l linewidth 5 lc rgb "red"
set term png
set output "./clustering/cluster1/cluster1.png"
replot

fnames=system("/bin/ls ./clustering/cluster2/*.dat")
set nokey
set xlabel "Time [s]"
set ylabel "Normalized-Voltage"
plot for[fn in fnames] sprintf("%s",fn) using ($0*0.1):1 with line
replot "./clustering/cluster2/mean2.dat" using ($0*0.1):1 w l linewidth 5 lc rgb "red"
set term png
set output "./clustering/cluster2/cluster2.png"
replot

fnames=system("/bin/ls ./clustering/cluster3/*.dat")
set nokey
set xlabel "Time [s]"
set ylabel "Normalized-Voltage"
plot for[fn in fnames] sprintf("%s",fn) using ($0*0.1):1 with line
replot "./clustering/cluster3/mean3.dat" using ($0*0.1):1 w l linewidth 5 lc rgb "red"
set term png
set output "./clustering/cluster3/cluster3.png"
replot

fnames=system("/bin/ls ./clustering/cluster4/*.dat")
set nokey
set xlabel "Time [s]"
set ylabel "Normalized-Voltage"
plot for[fn in fnames] sprintf("%s",fn) using ($0*0.1):1 with line
replot "./clustering/cluster4/mean4.dat" using ($0*0.1):1 w l linewidth 5 lc rgb "red"
set term png
set output "./clustering/cluster4/cluster4.png"
replot

fnames=system("/bin/ls ./clustering/cluster5/*.dat")
set nokey
set xlabel "Time [s]"
set ylabel "Normalized-Voltage"
plot for[fn in fnames] sprintf("%s",fn) using ($0*0.1):1 with line
replot "./clustering/cluster5/mean5.dat" using ($0*0.1):1 w l linewidth 5 lc rgb "red"
set term png
set output "./clustering/cluster5/cluster5.png"
replot

fnames=system("/bin/ls ./clustering/cluster6/*.dat")
set nokey
set xlabel "Time [s]"
set ylabel "Normalized-Voltage"
plot for[fn in fnames] sprintf("%s",fn) using ($0*0.1):1 with line
replot "./clustering/cluster6/mean6.dat" using ($0*0.1):1 w l linewidth 5 lc rgb "red"
set term png
set output "./clustering/cluster6/cluster6.png"
replot