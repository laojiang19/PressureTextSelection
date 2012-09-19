/*******************************************************************************
*
* sockwrapper.cc
* socket wrapper implementation
*
********************************************************************************
*/

#include "sockwrapper.h"
#include <iostream>


/*
 * Send a customized structure
 * returns 0 if successful
 */
int SendMessage(SOCKET s, SockMSG msg) {
   int size = sizeof(SockMSG);
   //std::cout << "Sending type=" << msg.msgType << " value=" << msg.value << std::endl;
   if (INVALID_SOCKET == send(s, (char*)&msg, size, 0)) {
      std::cout << "send error" << std::endl;
      return -1;
   }
   return 0;
}

/*
 * Receive a customized structure
 * returns 0 if successful
 */
/*
int ReceiveMEssage(SOCKET s, SockMSG &msg) {
   int size = sizeof(SockMSG);
   char buffer[size];
   if (INVALID_SOCKET == recv(s, buffer, size, 0)) {
      std::cout << "Send error" << std::endl;
      return -1;
   }
   msg = *reinterpret_cast<SockMSG*>(buffer);
   
   std::cout << "message type: " << msg.msgType << std::endl;
   std::cout << "message content: " << msg.value << std::endl;
   
   return 0;
}
*/

/*
 * Send generic message 
 * returns 0 if successful
 */
int SendMessage(SOCKET s, string msg) {
   std::cout << "Sending : " << msg << std::endl;
   char *buffer = (char*)msg.c_str();
   if (INVALID_SOCKET == send(s, buffer, strlen(buffer), 0)) {
      std::cout << "send error" << std::endl;
      return -1;
   }
   return 0;
}

/*
 * Receives generic message
 * returns 0 if successful
 */
int ReceiveMessage(SOCKET s, string *msg) {
    char buffer[MAX_BUFFER];
        
    int result;
    result = recv(s, buffer, MAX_BUFFER,0);
    
    if (result == INVALID_SOCKET) {
       std::cout << "recv error" << std::endl;
       return -1;
    }
    
    msg->clear();
    msg->append(buffer);    
    std::cout << "Received " << *msg << std::endl;
    return 0;
}


/*
 * Copied from Windows Sockets: A Quick And Dirty Primer
 */
SOCKET establishTCP(int portnum) {

	SOCKET	listenSocket;
	listenSocket = socket(AF_INET, SOCK_STREAM,IPPROTO_TCP);
	if (listenSocket == INVALID_SOCKET) {
       return INVALID_SOCKET;
	}
	SOCKADDR_IN saServer;		
	saServer.sin_family = AF_INET;
	saServer.sin_addr.s_addr = INADDR_ANY;	// Let WinSock supply address
	saServer.sin_port = htons((short)portnum);		// Use port from command line
	

    if (SOCKET_ERROR == bind(listenSocket, (LPSOCKADDR)&saServer, sizeof(struct sockaddr))) {
       return INVALID_SOCKET;
    }    
   return listenSocket;
}

/*
 * Copied from Windows Sockets: A Quick And Dirty Primer
 */
SOCKET connectTCP(char *hostname, int portnum) {
    LPHOSTENT lpHostEntry;
	lpHostEntry = gethostbyname(hostname);
    if (lpHostEntry == NULL) {
        return INVALID_SOCKET;
    }

	SOCKET	theSocket;
	theSocket = socket(AF_INET, SOCK_STREAM,IPPROTO_TCP);
	if (theSocket == INVALID_SOCKET) {
		return INVALID_SOCKET;
	}


	SOCKADDR_IN saServer;
	saServer.sin_family = AF_INET;
	saServer.sin_addr = *((LPIN_ADDR)*lpHostEntry->h_addr_list);										
	saServer.sin_port = htons((short)portnum);

	int nRet;
	nRet = connect(theSocket,(LPSOCKADDR)&saServer, sizeof(struct sockaddr));
	if (nRet == SOCKET_ERROR) {
		closesocket(theSocket);
		return INVALID_SOCKET;
	}

   return theSocket;
}


SOCKET createFromExistingSocket(SOCKET old, int newPort) {
	SOCKET	theSocket;
	theSocket = socket(AF_INET, SOCK_STREAM,IPPROTO_TCP);
	if (theSocket == INVALID_SOCKET) {
		return INVALID_SOCKET;
	}
	SOCKADDR_IN saServer;
	int size = sizeof(SOCKADDR_IN);
	if (getpeername(old, (LPSOCKADDR)&saServer, &size) == SOCKET_ERROR) {
       std::cout << "blah" << std::endl;
	   return INVALID_SOCKET;
	}
	
	char *ip = inet_ntoa(saServer.sin_addr);
	return connectTCP(ip, newPort);
	
	/*
	std::cout << "from : " << inet_ntoa(saServer.sin_addr) << std::endl;
	SOCKADDR_IN saServer2;
	saServer2.sin_family = AF_INET;
	saServer2.sin_addr = saServer.sin_addr;
	saServer2.sin_port = htons(newPort);	
	
	
	
	int nRet;
	nRet = connect(theSocket,(LPSOCKADDR)&saServer2, sizeof(struct sockaddr));
	if (nRet == SOCKET_ERROR) {
	    std::cout << "blah2" << std::endl;
		closesocket(theSocket);
		return INVALID_SOCKET;
	}

   return theSocket;	
   */
}


// Close the socket connection
// Params
//    s - socket
//
// Returns
//    -1 if unsuccessful
//     0 if successful
int CloseConnection(SOCKET s) {

	// Disable datasend
    if (shutdown(s, SD_SEND) == SOCKET_ERROR) {
        return -1;
    }

/* Grab leftover stuff....comment this out for now
  
    char acReadBuffer[MAX_BUFFER];
    while (1) {
        int nNewBytes = recv(s, acReadBuffer, MAX_BUFFER, 0);
        if (nNewBytes == SOCKET_ERROR) {
            return false;
        }
        else if (nNewBytes != 0) {
            cerr << endl << "FYI, received " << nNewBytes <<
                    " unexpected bytes during shutdown." << endl;
        }
        else {
            // Okay, we're done!
            break;
        }
    }
*/

    // Close the socket.
    if (closesocket(s) == SOCKET_ERROR) {
        return -1;
    }
	return 0;
}
