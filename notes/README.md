big-data-project

================
Minimizing the required bandwidth for mobile web browsing
================

ARGUMENTS:
1. Path to data folder
2. Name of website
3. Number of files
4. # of bytes per chunk
5. size of mobile cache
6. size of proxy cache

Output:
Creates Folder in given-path/ReconstructedBytes/ and stores reconstructed content inside that folder

My path:
/Users/madhuvanthijayakumar/Dropbox/EclipseWorkspace/BigData/big-data-project/data amazon 10 50000 2048 8



Execution:
1. Execution starts at Main: 
	a. Accepts two arguments: PATH-to-data and #bytes-per-chunk
	b. simulate()
2. SimulatorV1
	a. Call Chunking with filename: open file
	b. Create proxyCache instance		
	c. Create mobile instance
	d. Loop over files of given website: 
	e. For each file: create an arraylist of chunks
	f. Create fingerprints of all chunks
	g. send fingerprints to mobile
	h. send chunks to proxyCache
	i. mobile sends an arraylist of needed fingerprints
	j. proxycache prepares arraylist of chunks
	k. mobile receives needed chunks
	l. mobile reconstructs data
	m. print stats

Code Structure:


Questions: 
Proxy cache and mobile cache capacities?
prepareData() doesn't need chunks as parameter
we don't want to make mobile calculate fps? computation heavy?


TODO:
	Mobile:
	webcontent = new byte[1]; //dummy array for now

TODO:

DONE - 1. Create Proxy class and move marked functions.

DONE - 2. Make cache sizes arguments to the Simulator when running.

DONE - 3. Automate how sites are inspected (right now hardcoded for-loop): Want to be able to give specific sites as arguments and how many of them to compare.

3a. Make version that compares multiple different websites with one mobile device.
3b. Make version of simulator that compares one website with multiple mobile devices.
3c. Make version of simulator that compares multiple websites with multiple mobile devices.

4. Experiments with different cache sizes and chunk sizes (holding one fixed and changing the other param).

5. Experiments with 1 and multiple mobile devices and repeat task 4.

STARTED - 6. Make networked simulator \[SimulatorV3\].

7. Make Demo.

8. Write paper.
