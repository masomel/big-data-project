#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <sys/types.h>
#include <netdb.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdlib.h>
#include "proxy_parse.h"

/* Server: capable of receiving text messages
 * from clients over TCP sockets. 
 * Prints messages to stdout
 * Data from client received from port known to clients
 * Handle conections sequentially
 * Accept connections from multiple clients*/

#define MAXBUF 4096
#define LISTEN_BACKLOG 50
#define handle_error(msg) \
	do { perror(msg); exit(EXIT_FAILURE); } while(0)

int opensock(char *host);

void get(char *h, char *p, int);

int main( int argc, char *argv[] ){
	/* Variable Declarations */
	int sockfd, cfd, rec, s, wr=0, sent, lenn;
	int start = 0;
	char pcBuf[MAXBUF];
	char fulBuf[MAXBUF];
	int received;
	struct sockaddr client_addr;
	struct addrinfo hints;
	struct addrinfo *res;
	socklen_t client_addr_size;

	/* struct setup*/
	hints.ai_family = AF_INET;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_protocol = IPPROTO_TCP;	
	s = getaddrinfo(NULL, argv[1], &hints, &res);	
	if( s!=0){
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(s));
		exit(EXIT_FAILURE);
	}
	/* create socket */
	sockfd = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
	if( sockfd == -1)
		handle_error("socket");

	/* bind the socket */
	if( bind(sockfd, res->ai_addr, 
		res->ai_addrlen) == -1)
		handle_error("bind");

	/* listen for client */
	if( listen(sockfd, LISTEN_BACKLOG) == -1)
		handle_error("listen");

	freeaddrinfo(res);
	/* accept connection */
	/* int accept(int sockfd, struct sockaddr *addr, socketlen_t *addrlen) */
	client_addr_size = sizeof(struct sockaddr);
	while(1){
	  cfd = accept( sockfd, (struct sockaddr *) &client_addr, 
		&client_addr_size);
	  if( cfd <= 0)
	    handle_error("accept"); 
	  /*receive GET request from client */
          received= recv(cfd, pcBuf, MAXBUF, 0);
          int recc= recv(cfd, pcBuf+received, MAXBUF, 0);
      	  if(received == -1)
	    fprintf(stderr, "recv: failed");		
	  /*print received GET request on server terminal*/
	  if(write(1, pcBuf, received+recc) == -1){
	    handle_error("write");
	  }
	  /* echo what was received from the client */
	  if((sent = send(cfd,pcBuf, received, 0)) <0)
	    handle_error("send");
	  
	  int len = strlen(pcBuf);
	  struct ParsedRequest *req = ParsedRequest_create();
	  if(ParsedRequest_parse(req, pcBuf, len) <0) {
	    printf("parse failed\n");
	    return -1;
	  }

	  char *h = req->host;
	  char *p = req->path;

	  get(h, p, cfd);	
	  
	  close(cfd);
  	}/* end infinite loop */
        /* Clean up */
        close(sockfd);
        return 0;
} /* end main */

int opensock(char *host){
	struct addrinfo *r;
	const char* PORT = "80";
	int address = getaddrinfo(host, PORT, 0, &r);

	int fileDesc = socket(AF_INET,SOCK_STREAM,0);

	int retVal = connect(fileDesc, r->ai_addr, r->ai_addrlen);
	return fileDesc;
}

void get(char *h, char *p, int cfd){

        int sock;
        sock=opensock(h);        //get connected to socket

	char *buffer = malloc(1024);
	
	printf("\n");	
        sprintf(buffer, "GET %s HTTP/1.0\r\n\r\n", p);

	/*buffer = orig;*/
        int ret=0;
        int sent=0;

	/* Send request to web server */
	do{
	       ret=send(sock, buffer+sent, strlen(buffer+sent), 0);
               sent=sent+ret;
        } while(sent<strlen(buffer));

        int ret1=0;
        int bufsize=100000;
        int received=0;
        char *buffer1=malloc(bufsize);

	/* Receive content from web server*/
	do{
		ret1=recv(sock, buffer1+received, bufsize-received, 0);
		received = received + ret1;
	}while(ret1!=0);	

	/* Send content to client */
	send(cfd, buffer1, received, 0);
	
        free(buffer);
        free(buffer1);
}

