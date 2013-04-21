#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <netdb.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <fcntl.h>

/* input: server-IP-address port-number 
 * Things to handle: buffer management, return values 
 * Blocking: if socket call can't be done immediately,
 * then process waits until it can
 * 	Ex: internal buffer is full, no data can be written
 * 	    buffer is empty, no data to read
 * If data available: call returns number of bytes read or written*/
#define handle_error(msg) \
	do { perror(msg);  exit(EXIT_FAILURE);} while(0)

#define MAXBUF 4096
int main( int argc, char *argv[] ) {
	/* Variable Declarations */
	int sockfd, s, fd, re;
	int sent, alreadyRead;;
	int wr = 0;
	char pcBuf[MAXBUF];
	char buf[MAXBUF];
	struct addrinfo hints;
	struct addrinfo *res;

	int counter = 0;
	/* struct set-up */
	hints.ai_family = AF_INET;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_protocol = IPPROTO_TCP;
	s = getaddrinfo(argv[1], argv[2], &hints, &res);
	if( s!=0){
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(s));
		exit(EXIT_FAILURE);
	}
	/* create socket */
	sockfd = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
	if ( sockfd == -1 )
		handle_error("socket");

	/* connect to server, blocking */
	if( connect(sockfd, res->ai_addr, res->ai_addrlen) == -1 )
	{
		handle_error("connect");
	}
	freeaddrinfo(res);
	fd = open("myfile.txt", O_RDONLY);
  	while ((alreadyRead = read(fd, pcBuf, MAXBUF)) != 0) {
	  if(alreadyRead == -1)
	    handle_error("read");	
      	    if ((sent = send(sockfd, pcBuf+wr, alreadyRead-wr, 0)) < 0) {
		handle_error("send");
            }
	/*
	    while((re = recv(sockfd, buf, MAXBUF, 0)) !=0){
		if(re == -1)
		  fprintf(stderr, "recv: failed");
		if(write(1, buf, re) == -1){
		  handle_error("write");	
		}
            }
	*/
	}
	    if((re = recv(sockfd, buf, MAXBUF, 0)) !=0){
		if(re == -1)
		  fprintf(stderr, "recv: failed");
		if(write(1, buf, re) == -1){
		  handle_error("write");	
		}
            }
	
	
	    if((re = recv(sockfd, buf, MAXBUF, 0)) !=0){
		if(re == -1)
		  fprintf(stderr, "recv: failed");
		if(write(1, buf, re) == -1){
		  handle_error("write");	
		}
            }
  	close(sockfd);
  	return 0;
}
