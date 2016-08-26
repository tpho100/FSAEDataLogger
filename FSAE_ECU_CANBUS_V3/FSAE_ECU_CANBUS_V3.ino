#include <SPI.h>
#include "mcp_can.h"

const int SPI_CS_PIN = 10; //DEFAULT SHIELD PARAMETER
MCP_CAN CAN(SPI_CS_PIN); //DEFAULT SHIELD PARAMETER

void setup()
{
    Serial.begin(9600); //Baud rate for Bluetooth Serial (default: 9600)
    while (CAN_OK != CAN.begin(CAN_250KBPS))// init can bus : baudrate = 250k for PE3 ECU
    {
        //Serial.println("CAN BUS Shield FAILED TO INITIALIZE.");
        //Serial.println("    ATTEMPTING TO INITIALIZE AGAIN...");
        delay(100);
    }
    //Serial.println("CAN BUS Shield INITIALIZED. SUCCESS!!");
}

void loop()
{
    unsigned char len = 0;
    unsigned char buf[8];
    long unsigned int ID;
    char PACKET_DELIM = 'M';
    char DATA_DELIM = ',';
    if(CAN_MSGAVAIL == CAN.checkReceive())
    {
        CAN.readMsgBufID(&ID, &len, buf);//len: data length, buf: data buf
        /*
          Read the buffer, check where the message came from and 
          use the appropriate CAN group to update the ECU parameters
        */
        
        //SEND DATA AS: 218099784,1,2,3,4,5,6,7,8,M - M tells BT where to end the packet
        Serial.print(ID);
        Serial.print(DATA_DELIM);
        
        for(int i = 0; i < len; i++)
        {
          Serial.print(buf[i]);
          Serial.print(DATA_DELIM);
        }
        Serial.print(PACKET_DELIM);
    }
}
