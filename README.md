# Hybrid CEP Engine

### Description

A Hybrid Solution proposed for near-real-time Complex Event Processing in a resource-constraint environment. 
This is achieved through the combination of two existing state-of-the-art engines, namely the SASE system, 
which was originally introduced in [SASE](http://sase.cs.umass.edu/uploads/pubs/sase-sigmod2006.pdf), 
and a [CET](https://dl.acm.org/doi/10.1145/3472456.3472526) (Complete Event Trend) detection solution, proposed  by Mei H. 
The latter departs from the NFA rationale, and it is capable of trading latency for significantly lower resource usage in such a beneficial manner that, in practice, it can run much more queries than previously.

***
### Architecture

![architecture image](https://github.com/KyraStyl/hybrid_cep_system/blob/master/arch-3.png)
***

### Compilation 

For Linux users, first install maven, if necessary, by running ```sudo apt install maven```.

Run ```mvn package``` under ```/implementation``` folder.
Then, a JAR named ```hybridEngineCEP-v01.jar``` is created under ```/target``` folder

***

### Input Parameters

#### 1. Required parameters:

    -q, --query          Path of query file

    -i, --inputStream    Path of Input Stream file

    -t, --eventtype      Type of events: stock or check

#### 2. Optional parameters:

    -e, --engine         The engine to run: sase or cet (default sase)

    -p, --parallelism    Degree of parallelism for cet

    -w, --write          Whether to write or not the output

    -o, --output         The output file for results

    -c, --conf           The stream configuration file

#### 3. Guide:
    -h, --help           Prints the helper, with required and optional parameters

***

### Run
```
java -jar target/hybridEngineCEP-v01.jar [options]
```
***

### Output
****Profiling Numbers****
* **Engine used**: *SASE OR CET*
* **Total Running Time**: *SECONDS* seconds
* **Number Of Events Processed**: *EVENTS*
* **Number Of Runs Created**: *RUNS*
* **Number Of Matches Found**: *MATCHES*
* **Used memory is bytes**: *BYTES*
* **Used memory is megabytes**: *MB*
* **Number of cets per slide**: *(only for CET engine)*
* **Maximum Latency per slide in nano**: *(only for CET engine)*
* **Minimum Latency per slide in nano**: *(only for CET engine)*
* **Average Latency per slide in nano**: *(only for CET engine)*
* **Maximum Latency in nano**: *MAX_L*
* **Minimum Latency in nano**: *MIN_L*
* **Average Latency in nano**: *AVG_L*
* **Throughput**: *X* events/second

### Examples
#### 1. Circular Kiting Fraud Detection (using CET engine)

```
java -jar target/hybridEngineCEP-v01.jar -q queries/qkite.query -i datasets/kite-big-transf.stream -t check -e cet -w -o output/results-kite.res -p 8
```

#### 2. Sub-sequences with same symbol (using SASE engine)
```
java -jar target/hybridEngineCEP-v01.jar -q queries/qstock1.query -i datasets/stock-random.stream -t stock -e sase -w -o output/results-symbol.res 
```

#### 3. Sub-sequences with same price (using SASE engine)
```
java -jar target/hybridEngineCEP-v01.jar -q queries/qstock2.query -i datasets/dataset-test.stream -t stock -e sase -w -o output/results-price.res 
```

### Stream Generators

There are two stream generators under the folder ```datasets/generators/```.

Run ```python checkGen.py -h``` or ```python stockGen.py -h``` to find out the required and optional
parameters for each generator.
