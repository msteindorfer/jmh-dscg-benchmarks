java -jar target/benchmarks.jar "nl.cwi.swat.jmh_dscg_benchmarks.JmhSetBenchmarks.timeEqualsDeltaDuplicate$" -p valueFactoryFactory=VF_PDB_PERSISTENT_MEMOIZED_LAZY -jvmArgs "-Xms4g -Xmx4g" -wi 15 -i 15 -f 1 -t 1 -r 1 -p run=0 -p sampleDataSelection=MATCH -p producer=PDB_INTEGER -gc true -rf csv -v EXTRA -foe true -bm avgt -rff ./latest-results.csv -p size=1048576 
# 1>./latest-std-console.log 2>./latest-err-console.log