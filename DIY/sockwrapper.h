/*******************************************************************************
*
* sockwrapper.h
* socket wrapper functions, currently stuck to windows
* implementation
********************************************************************************
*/


#ifndef _WSOCKETLIB_H_
#define _WSOCKETLIB_H_

#pragma once

#include <winsock2.h>
#include <string>
#include <vector>


#define MAX_BUFFER 1024

using namespace std;

// message pasing structure
struct SockMSG {
   int id;
   //char msgType;
   //char fromId; // need to change this to long or integer
   //char value[MAX_BUFFER];
};


int SendMessage(SOCKET s, SockMSG msg);
int ReceiveMEssage(SOCKET s, SockMSG &msg);

int SendMessage(SOCKET s, string msg);
int ReceiveMessage(SOCKET s, string *msg);
int CloseConnection(SOCKET s);

SOCKET establishTCP(int portnum);
SOCKET connectTCP(char *hostname, int portnum);
SOCKET createFromExistingSocket(SOCKET old, int newPort);



#endif
