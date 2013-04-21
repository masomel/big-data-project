#-----------------------------------------------------------------------
# makefile
# Author: Madhuvanthi Jayakumar
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
# Dependency rules for non-file targets
#-----------------------------------------------------------------------

all: server client

clobber: clean
	rm -f *~ \#*\# core
clean:
	rm -f *.o ish
#-----------------------------------------------------------------------
# Dependency rules for file targets
#-----------------------------------------------------------------------

server: server.o
	gcc server.o -o server

client: client.o
	gcc client.o -o client

client.o: client.c
	gcc -c client.c

server.o: server.c
	gcc -c server.c
