
#define _WINSOCKAPI_ 
#include <Windows.h>
#include <SynKit.h>
#include <sstream>

#pragma comment(lib, "SynCOM.lib") // For access point SynCreateAPI
#pragma comment(lib, "wsock32.lib")


#include "sockwrapper.h"

#define PORTNUM 3333

int main(int argc, char** argv) {
   WORD wVersionRequested = MAKEWORD(1,1);
   WSADATA wsaData;
   WSAStartup(wVersionRequested, &wsaData);	

   SOCKET s; 
   if ((s = establishTCP(PORTNUM)) == INVALID_SOCKET) { 
      /* plug in the phone */ 
      perror("establish"); 
      system("PAUSE");
      exit(1); 
   } 
   listen(s, 1);

     


    // Wait object will indicate when new data is available
    HANDLE hEvent = CreateEvent(NULL, FALSE, FALSE, NULL);
    
    // Entry point to Synaptics API
    ISynAPI* pAPI = NULL;
    SynCreateAPI(&pAPI);
    
    // Find the first USB TouchPad device connected to the system
    LONG lHandle = -1;
    if (pAPI->FindDevice(SE_ConnectionUSB, SE_DeviceTouchPad, &lHandle) == SYNE_NOTFOUND) {
        printf("ForcePad not found\n");
        return EXIT_FAILURE;
    }
    
    // Create an interface to the ForcePad
    ISynDevice* pDevice = NULL;
    pAPI->CreateDevice(lHandle, &pDevice);

    // Tell the device to signal hEvent when data is ready
    pDevice->SetEventNotification(hEvent);

    // Enable multi-finger touch and grouped reporting
    pDevice->SetProperty(SP_IsMultiFingerReportEnabled, 1);
    pDevice->SetProperty(SP_IsGroupReportEnabled, 1);

    // Get the maximum number of fingers the device will report
    LONG lNumMaxReportedFingers;
    pDevice->GetProperty(SP_NumMaxReportedFingers, &lNumMaxReportedFingers);

	// Hack
	LONG rectLeft, rectBottom, rectRight, rectTop;
	pDevice->GetProperty(SP_XLoSensor, &rectLeft);
	pDevice->GetProperty(SP_YLoSensor, &rectBottom);
	pDevice->GetProperty(SP_XHiSensor, &rectRight);
	pDevice->GetProperty(SP_YHiSensor, &rectTop);



	printf("Border %d %d %d %d \n", rectLeft, rectBottom, rectRight, rectTop);
	

    // Create an ISynGroup instance to receive per-frame data
    ISynGroup* pGroup = NULL;
    pDevice->CreateGroup(&pGroup);
    
    // Create an ISynPacket instance to receive per-touch data
    ISynPacket* pPacket;
    pDevice->CreatePacket(&pPacket);

    // Stop the ForcePad reporting to the operating system
    pDevice->Acquire(SF_AcquireAll);


    printf("Touch the surface to see properties\n");
    printf("Touch with %d fingers to quit\n", lNumMaxReportedFingers);

    LONG lFingerCount = 0;
  SOCKET newSocket;
    printf("Waiting for connection...");
  newSocket = accept(s, NULL, NULL);



    do {

        // Wait until the event signals that data is ready
        WaitForSingleObject(hEvent, INFINITE);

		// Load data into the ISynGroup instance, repeating until there is no more data
        while (pDevice->LoadGroup(pGroup) != SYNE_FAIL) {
            
            // For each touch (packet)
            lFingerCount = 0;
            for (LONG i = 0; i != lNumMaxReportedFingers; ++i) {
                // Load data into the SynPacket object
                pGroup->GetPacketByIndex(i, pPacket);
                // Is there a finger present?
                LONG lFingerState;
                pPacket->GetProperty(SP_FingerState, &lFingerState);
                if (lFingerState & SF_FingerPresent) {
                    ++lFingerCount;
                    // Extract the position and force of the touch
                    LONG lX, lY, lZForce;
                    pPacket->GetProperty(SP_X, &lX);
                    pPacket->GetProperty(SP_Y, &lY);
                    pPacket->GetProperty(SP_ZForce, &lZForce);

                     // Hack
                     float xcoord = (float)lX - rectLeft;
                     float ycoord = (float)lY - rectBottom;
                     xcoord /= abs(rectRight - rectLeft);
                     ycoord /= abs(rectTop - rectBottom);

                     LONG c0, c1, c2, c3;
                     pGroup->GetPropertyByIndex(SP_ForceRaw, 0, &c0);
                     pGroup->GetPropertyByIndex(SP_ForceRaw, 1, &c1);
                     pGroup->GetPropertyByIndex(SP_ForceRaw, 2, &c2);
                     pGroup->GetPropertyByIndex(SP_ForceRaw, 3, &c3);
                     /*
                     pPacket->GetProperty(SP_ForceRaw, &c1);
                     pPacket->GetProperty(SP_ForceRaw, &c2);
                     pPacket->GetProperty(SP_ForceRaw, &c3);
                     pPacket->GetProperty(SP_ForceRaw, &c4);
                     */
                     // Hurray for ultra inefficient code !!!
                     stringstream ss (stringstream::in | stringstream::out);
                     ss << i;
                     ss << "|";
                     ss << xcoord;
                     ss << "|";
                     ss << ycoord;
                     ss << "|";
                     ss << lZForce;
                     ss << "|";
                     ss << c0;
                     ss << "|";
                     ss << c1;
                     ss << "|";
                     ss << c2;
                     ss << "|";
                     ss << c3;
                     ss << "\n";


                     // if success then success else wait conn
                    if (SendMessage(newSocket, ss.str()) != 0) {
                         printf("Waiting for connection...");
                         newSocket = accept(s, NULL, NULL);
                     }
                     printf("   Touch %d: Coordinates (%4f, %4f), force +%3d grams. Corners [%d, %d, %d, %d]\n", i, xcoord, ycoord, lZForce, c0, c1, c2, c3);
                }
            }
        }

    } while (lFingerCount < lNumMaxReportedFingers);

    printf("%d finger gesture detected; exiting\n", lNumMaxReportedFingers);
    //SendMessage(newSocket, "Q");

    // Don't signal any more data
    pDevice->SetEventNotification(NULL);
    
    // Release the COM objects we have created
    pPacket->Release();
    pGroup->Release();
    pDevice->Release();
    pAPI->Release();

    // Release the wait object
    CloseHandle(hEvent);

    return EXIT_SUCCESS;
    
}
