package com.thanh_phong.fsaedatalogger;

import java.util.StringTokenizer;

/**
 * Created by Thanh-Phong on 4/7/2016.
 */
public class PE3ECUCANBus {

    //CAN Bus protocol for PE3 ECU: http://pe-ltd.com/assets/an400_can_protocol_b.pdf

    private static final int PE1 = 218099784; //CAN ID (hex) 0CFFF048
    private static final int PE2 = 218100040; //CAN ID (hex) 0CFFF148
    private static final int PE3 = 218100296; //CAN ID (hex) 0CFFF248
    private static final int PE4 = 218100552; //CAN ID (hex) 0CFFF348
    private static final int PE5 = 218100808; //CAN ID (hex) 0CFFF448
    private static final int PE6 = 218101064; //CAN ID (hex) 0CFFF548
    //private static final int PE7 = 218101320; //CAN ID (hex) 0CFFF648, DON'T NEED TO USE THIS ONE

    private double[] PE3CANData;
    private String pressureType = "psi";
    private String tempType = "F";

    public PE3ECUCANBus(){
        PE3CANData = new double[24];
        //Initialize data structure to all 0 values
        for(double d : PE3CANData){
            d = 0;
        }
    }

    public void insertData(String message) {
        int[] tempData = getValues(message);

        switch(tempData[0]){
            case PE1: //RPM, TPS, FuelOpenTime, IgnitionAngle - Rate 50 ms
                PE3CANData[0] = getNumber(tempData[1],tempData[2]); //1 rpm/bit, 0 offset
                PE3CANData[1] = getNumber(tempData[3],tempData[4])*0.1; //0.1 %/bit, 0 offset
                PE3CANData[2] = getNumber(tempData[5],tempData[6])*0.01; //0.1 msec/bit, 0 offset
                PE3CANData[3] = getNumber(tempData[7],tempData[8])*0.01; //0.1 deg/bit, 0 offset
                break;
            case PE2: //Barometer, MAP, Lambda, PressureType - Rate 50 ms
                PE3CANData[4] = getNumber(tempData[1],tempData[2])*0.01; //0.01/bit, 0 offset
                PE3CANData[5] = getNumber(tempData[3],tempData[4])*0.01; //0.01/bit, 0 offset
                PE3CANData[6] = getNumber(tempData[5],tempData[6])*0.001; //0.001/bit, 0 offset
                PE3CANData[7] = getNumber(tempData[7],tempData[8]); //0 - psi, 1 - kPa
                break;
            case PE3: //Analog Input #1 through #4 [volts] - Rate 100 ms
                PE3CANData[8] = getNumber(tempData[1],tempData[2])*0.001; //0.001 volt/bit, 0 offset
                PE3CANData[9] = getNumber(tempData[3],tempData[4])*0.001; //0.001 volt/bit, 0 offset
                PE3CANData[10] = getNumber(tempData[5],tempData[6])*0.001; //0.001 volt/bit, 0 offset
                PE3CANData[11] = getNumber(tempData[7],tempData[8])*0.001; //0.001 volt/bit, 0 offset
                break;
            case PE4: //Analog Input #5 through #8 [volts] - Rate 100 ms
                PE3CANData[12] = getNumber(tempData[1],tempData[2])*0.001; //0.001 volt/bit, 0 offset
                PE3CANData[13] = getNumber(tempData[3],tempData[4])*0.001; //0.001 volt/bit, 0 offset
                PE3CANData[14] = getNumber(tempData[5],tempData[6])*0.001; //0.001 volt/bit, 0 offset
                PE3CANData[15] = getNumber(tempData[7],tempData[8])*0.001; //0.001 volt/bit, 0 offset
                break;
            case PE5: //Frequency 1 through 4 [hertz] - Rate 100 ms
                PE3CANData[16] = getNumber(tempData[1],tempData[2])*0.2; //0.2 hz/bit, 0 offset
                PE3CANData[17] = getNumber(tempData[3],tempData[4])*0.2; //0.2 hz/bit, 0 offset
                PE3CANData[18] = getNumber(tempData[5],tempData[6])*0.2; //0.2 hz/bit, 0 offset
                PE3CANData[19] = getNumber(tempData[7],tempData[8])*0.2; //0.2 hz/bit, 0 offset
                break;
            case PE6: //BatteryVolt, AirTemp, CoolantTemp, TempType - Rate 1000 ms
                PE3CANData[20] = getNumber(tempData[1],tempData[2])*0.01; //0.01 volts/bit, 0 offset
                PE3CANData[21] = getNumber(tempData[3],tempData[4])*0.1; //0.1 deg/bit, 0 offset
                PE3CANData[22] = getNumber(tempData[5],tempData[6])*0.1; //0.1 deg/bit, 0 offset
                PE3CANData[23] = getNumber(tempData[7],tempData[8]); //0-Fahrenheit,1-Celsius
                break;
        }

    }

    private int getNumber(int LOW_BYTE, int HIGH_BYTE){
        int Num = HIGH_BYTE*256 + LOW_BYTE;
        if(Num > 32767){
            Num = Num - 65536;
        }
        return Num;
    }

    private int[] getValues(String message){
        int dataLength = 9;
        //INDEX MAPPING
        //[PE3ID, BUF1, BUF2, BUF3, BUF4, BUF5, BUF6, BUF7, BUF8
        //message will look like 218099784,0,1,2,3,4,5,6,7,

        StringTokenizer st = new StringTokenizer(message); //Tokenize the message
        int ID = Integer.parseInt(st.nextToken(",")); //1st position is PE3 ID
        int[] data = new int[dataLength]; //Create data structure for message
        data[0] = ID; //Place ID at beginning of data structure

        for(int i = 1; i < dataLength; i++){ //Populate the data structure
            data[i] = Integer.parseInt(st.nextToken(","));
        }
        return data;
    }

    private double limit(double value, double min, double max){
        if(value > max) {
            value = max;
        } else if (value < min) {
            value = min;
        } else {

        }
        return value;
    }

    // PE1
    public double getRPM(){
        return limit(PE3CANData[0],0,30000);
    }

    public double getTPS(){
        return limit(PE3CANData[1],0,100);
    }

    public double getFuelOpenTime(){
        return limit(PE3CANData[2],0,30);
    }

    public double getIgnitionAngle(){
        return limit(PE3CANData[3],-20,1000);
    }

    // PE2
    public double getBarometer(){
        return PE3CANData[4];
    }

    public double getMAP(){
        return PE3CANData[5];
    }

    public double getLambda(){
        return PE3CANData[6];
    }

    public String getPressureType(){
        if(PE3CANData[7] == 0) {
            pressureType = "psi";
            return pressureType;
        }
        pressureType = "kPa";
        return pressureType;
    }

    // PE3
    public double getAnalogInput1(){
        return limit(PE3CANData[8],0,5);
    }

    public double getAnalogInput2(){
        return limit(PE3CANData[9],0,5);
    }

    public double getAnalogInput3(){
        return limit(PE3CANData[10],0,5);
    }

    public double getAnalogInput4(){
        return limit(PE3CANData[11],0,5);
    }

    // PE4
    public double getAnalogInput5(){
        return limit(PE3CANData[12],0,5);
    }

    public double getAnalogInput6(){
        return limit(PE3CANData[13],0,5);
    }

    public double getAnalogInput7(){
        return limit(PE3CANData[14],0,5);
    }

    public double getAnalogInput8(){
        return limit(PE3CANData[15],0,22);
    }

    // PE5
    public double getFrequency1(){
        return limit(PE3CANData[16],0,6000);
    }

    public double getFrequency2(){
        return limit(PE3CANData[17],0,6000);
    }

    public double getFrequency3(){
        return limit(PE3CANData[18],0,6000);
    }

    public double getFrequency4(){
        return limit(PE3CANData[19],0,6000);
    }

    // PE6
    public double getBatteryVolt(){
        return limit(PE3CANData[20],0,22);
    }

    public double getAirTemp(){
        return limit(PE3CANData[21],-1000,1000);
    }

    public double getCoolantTemp(){
        return limit(PE3CANData[22],-1000,1000);
    }

    public String getTempType(){
        if(PE3CANData[23] == 0) {
            tempType = "F";
            return tempType;
        }
        tempType = "C";
        return tempType;
    }
}
