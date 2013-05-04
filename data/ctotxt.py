''' This scripts modifies all C-Arrays in a given directory and writes the new files to another given directory '''
''' Author: Marcela '''
''' Note: Change path in line 10, and path in line 31 to match your own directories'''


import sys
import os.path

data = []

for filename in os.listdir('./bigdata/'):
	with open('./bigdata/'+filename, 'r') as f:
		data = f.readlines()
	ctr = 0

	filename1 = filename[0 : len(filename)-4]

	bytes = []

	for i in range (0, len(data)):
		split = data[i].split(',')

		for s in split:
			st = s.strip()

			if len(st) > 4:
				st = st[0:4]

			if st.startswith('0x') == True:
				bytes.append(st+'\n')

	with open('./packet_bytes/'+filename1+'.txt','w') as f:
		f.writelines(bytes)			

	f.close()
