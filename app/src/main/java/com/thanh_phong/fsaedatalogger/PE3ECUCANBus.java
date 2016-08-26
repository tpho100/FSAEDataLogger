package com.thanh_phong.fsaedatalogger;

import android.util.Log;
import java.util.StringTokenizer;

/**
 * Created by Thanh-Phong on 4/7/2016.
 */
public final class PE3ECUCANBus {

    private static volatile PE3ECUCANBus instance;

    //CAN Bus protocol for PE3 ECU: http://pe-ltd.com/assets/an400_can_protocol_b.pdf

    private static final int PE1 = 218099784; //CAN ID (hex) 0CFFF048
    private static final int PE2 = 218100040; //CAN ID (hex) 0CFFF148
    private static final int PE3 = 218100296; //CAN ID (hex) 0CFFF248
    private static final int PE4 = 218100552; //CAN ID (hex) 0CFFF348
    private static final int PE5 = 218100808; //CAN ID (hex) 0CFFF448
    private static final int PE6 = 218101064; //CAN ID (hex) 0CFFF548
    //private static final int PE7 = 218101320; //CAN ID (hex) 0CFFF648, DON'T NEED TO USE THIS ONE

    private float[] PE3CANData;
    private int[] tempData;
    private String pressureType = "psi";
    private String tempType = "C"; //Celsius

    private PE3ECUCANBus () {
        PE3CANData = new float[24];
        //Initialize data structure to all 0 values
        for(float d : PE3CANData){
            d = 0;
        }
    }

    public static PE3ECUCANBus getInstance(){
        if(instance == null){
            synchronized (PE3ECUCANBus.class) {
                if(instance == null){
                    instance = new PE3ECUCANBus();
                }
            }
        }

        return instance;
    }

    public void insertData(String message) {
        tempData = getValues(message);
        switch(tempData[0]){
            case PE1: //RPM, TPS, FuelOpenTime, IgnitionAngle - Rate 50 ms
                PE3CANData[0] = getNumber(tempData[1],tempData[2]); //1 rpm/bit, 0 offset
                PE3CANData[1] = getNumber(tempData[3],tempData[4])*0.1F; //0.1 %/bit, 0 offset
                PE3CANData[2] = getNumber(tempData[5],tempData[6])*0.01F; //0.01 msec/bit, 0 offset
                PE3CANData[3] = getNumber(tempData[7],tempData[8])*0.1F; //0.1 deg/bit, 0 offset
                break;
            case PE2: //Barometer, MAP, Lambda, PressureType - Rate 50 ms
                PE3CANData[4] = getNumber(tempData[1],tempData[2])*0.01F; //0.01/bit, 0 offset
                PE3CANData[5] = getNumber(tempData[3],tempData[4])*0.01F; //0.01/bit, 0 offset
                PE3CANData[6] = getNumber(tempData[5],tempData[6])*0.001F; //0.001/bit, 0 offset
                PE3CANData[7] = tempData[7];
                break;
            case PE3: //Analog Input #1 through #4 [volts] - Rate 100 ms
                PE3CANData[8] = getNumber(tempData[1],tempData[2])*0.001F; //0.001 volt/bit, 0 offset
                PE3CANData[9] = getNumber(tempData[3],tempData[4])*0.001F; //0.001 volt/bit, 0 offset
                PE3CANData[10] = getNumber(tempData[5],tempData[6])*0.001F; //0.001 volt/bit, 0 offset
                PE3CANData[11] = getNumber(tempData[7],tempData[8])*0.001F; //0.001 volt/bit, 0 offset
                break;
            case PE4: //Analog Input #5 through #8 [volts] - Rate 100 ms
                PE3CANData[12] = ((getNumber(tempData[1],tempData[2])*0.001F)-0.5F)*25; //0.001 volt/bit, 0 offset
                PE3CANData[13] = getNumber(tempData[3],tempData[4])*0.001F; //0.001 volt/bit, 0 offset
                PE3CANData[14] = getNumber(tempData[5],tempData[6])*0.001F; //0.001 volt/bit, 0 offset
                PE3CANData[15] = getNumber(tempData[7],tempData[8])*0.001F; //0.001 volt/bit, 0 offset
                break;
            case PE5: //Frequency 1 through 4 [hertz] - Rate 100 ms
                PE3CANData[16] = getNumber(tempData[1],tempData[2])*0.2F; //0.2 hz/bit, 0 offset
                PE3CANData[17] = getNumber(tempData[3],tempData[4])*0.2F; //0.2 hz/bit, 0 offset
                PE3CANData[18] = getNumber(tempData[5],tempData[6])*0.2F; //0.2 hz/bit, 0 offset
                PE3CANData[19] = getNumber(tempData[7],tempData[8])*0.2F; //0.2 hz/bit, 0 offset
                break;
            case PE6: //BatteryVolt, AirTemp, CoolantTemp, TempType - Rate 1000 ms
                PE3CANData[20] = getNumber(tempData[1],tempData[2])*0.01F; //0.01 volts/bit, 0 offset
                PE3CANData[21] = getNumber(tempData[3],tempData[4])*0.1F; //0.1 deg/bit, 0 offset
                PE3CANData[22] = getNumber(tempData[5],tempData[6])*0.1F; //0.1 deg/bit, 0 offset
                PE3CANData[23] = tempData[7]; //0 or 1
                break;
            default:
                System.out.println("UNRECOGNIZED PE ID ?????===="+tempData[0]);
                break;
        }
    }
    private float getNumber(int LOW_BYTE, int HIGH_BYTE){
        float Num =  HIGH_BYTE*256 + LOW_BYTE;
        if(Num > 32767){
            Num = Num - 65536;
        }
        return Num;
    }

    private int[] getValues(String message){
        //int len = message.indexOf(0);
        int dataLength = 9;
        int[] data = new int[dataLength]; //Create data structure for message
        //INDEX MAPPING
        //[PE3ID, BUF1, BUF2, BUF3, BUF4, BUF5, BUF6, BUF7, BUF8
        //message will look like 218099784,0,1,2,3,4,5,6,7,

        try{
            StringTokenizer st = new StringTokenizer(message); //Tokenize the message
            int ID = Integer.parseInt(st.nextToken(",")); //1st position is PE3 ID
            data[0] = ID; //Place ID at beginning of data structure

            for(int i = 1; i < dataLength; i++){ //Populate the data structure
                data[i] = Integer.parseInt(st.nextToken(","));
            }
        }catch(NumberFormatException nfe){
            Log.e("","COULD NOT PARSE DATA. BAD STRING====."+message);
        }
        return data;
    }

    // PE1
    public float getRPM(){
        return PE3CANData[0];
    }

    public float getTPS(){
        return PE3CANData[1];
    }

    public float getFuelOpenTime(){
        return PE3CANData[2];
    }

    public float getIgnitionAngle(){
        return PE3CANData[3];
    }

    // PE2
    public float getBarometer(){
        return PE3CANData[4];
    }

    public float getMAP(){
        return PE3CANData[5];
    }

    public float getLambda(){
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
    public float getAnalogInput1(){
        return PE3CANData[8];
    }

    public float getAnalogInput2(){
        return PE3CANData[9];
    }

    public float getAnalogInput3(){
        return PE3CANData[10];
    }

    public float getAnalogInput4(){
        return PE3CANData[11];
    }

    // PE4
    public float getAnalogInput5(){
        return PE3CANData[12];
    }

    public float getAnalogInput6(){
        return PE3CANData[13];
    }

    public float getAnalogInput7(){
        return PE3CANData[14];
    }

    public float getAnalogInput8(){
        return PE3CANData[15];
    }

    // PE5
    public float getFrequency1(){
        return PE3CANData[16];
    }

    public float getFrequency2(){
        return PE3CANData[17];
    }

    public float getFrequency3(){
        return PE3CANData[18];
    }

    public float getFrequency4(){
        return PE3CANData[19];
    }

    // PE6
    public float getBatteryVolt(){
        return PE3CANData[20];
    }

    public float getAirTemp(){
        return PE3CANData[21];
    }

    public float getCoolantTemp(){
        return PE3CANData[22];
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
