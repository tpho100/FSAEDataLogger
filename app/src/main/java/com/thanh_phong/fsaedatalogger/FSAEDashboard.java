package com.thanh_phong.fsaedatalogger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Thanh-Phong on 3/25/2016.
 */
public class FSAEDashboard extends View {

    //Mobile device specifications, used for general layout
    private int screenResolutionX = 1280;
    private int screenResolutionY = 720;
    PE3ECUCANBus pe3;

    /*
        Arcs and ovals are drawn using inscribed circles. The polygon will always be a rectangle.
        These rectangle objects create the boundaries for the circle.
     */

    //These are arcs with very wide strokes. Used to fill the "meter".
    private RectF arcMeter1; //Yellow
    private RectF arcMeter2; //Green
    private RectF arcMeter3; //Red

    private RectF arcOval; //Box drawn around main tachometer arc
    private RectF ticksOval; //Oval for ticks on the tachometer

    //Used to display general information
    private RectF genericArcOval1; //Oil pressure
    private RectF genericArcOval2; //Coolant temp
    private RectF genericArcOval3; //TPS

    //Used to fill the genericArc "meters"
    private RectF genericArcMeter1;
    private RectF genericArcMeter2;
    private RectF genericArcMeter3;

    //Main tachometer arc parameters
    private float arcStartAngle = 240;
    private float arcSweepAngle = 60;
    private float arcStrokeWidth = 2;
    private float arcDiameter = 1500;
    private float arcPositionX = 640;
    private float arcPositionY = 800; //800
    private float offset = 49; //Offset spacing for arcMeter

    private float genericStrokeWidth = 25;
    private float genericOffset = 25;
    private int barWidth = 40; //Also used for sweep thickness

    /*
        Number of ticks for RPM. Currently set to 1 tick per 1000 RPM
     */
    private float numberOfTicks = 13;
    private int tickLength = 15;

    /*
        Paint objects to color shapes
     */
    private Paint gearPaint;
    private Paint arcPaint;
    private Paint barFillPaint1;
    private Paint barFillPaint2;
    private Paint barFillPaint3;

    private Paint rpmTextPaint;
    private Paint genericPaint;
    private Paint sensorPaint;
    private Paint sensorLabelPaint;
    private Paint oilPressurePaint;
    private Paint coolantTempPaint;
    private Paint batteryPaint;
    private Paint TPSPaint;
    private Paint rpmTextPaint2;
    private Paint listViewPaint;

    /*
        Threshold for when to change color to indicate
        shift time
     */
    private float shiftThreshold1 = 6000;
    private float shiftThreshold2 = 7000;

    //RPM
    private String[] rpmLabel = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13"};
    private float maxRPM = 13000;
    private int rpmTextLocationX = screenResolutionX/2;
    private int rpmTextLocationY = 165; //165
    private float rpmTextSize = 95;
    private float rpmTextSize2 = 0.25F*rpmTextSize;
    private String rpmTextValue = "";
    private String rpmTextLabel = "RPM";
    private float arcStart,arcStart1,arcStart2,arcStart3;
    private float arcSweepLength,arcSweepLength1,arcSweepLength2,arcSweepLength3;
    private float x1,x2,y1,y2;
    private float radius,currentAngle,steps,maxAngle;
    private int count;

    //OIL PRESSURE
    private float maxOilPressure = 100;
    private float oilPressureThreshold1 = 35;
    private float oilPressureThreshold2 = 75;
    private String oilTextValue = "";
    private String oilTextLabel = "OIL(psi)";

    //COOLANT TEMPERATURE
    private float maxCoolantTemperature = 200;
    private float coolantTemperatureThreshold1 = 150;
    private float coolantTemperatureThreshold2 = 180;
    private String coolantTextValue = "";
    private String coolantTextLabel = "CLT(°F)";

    //TPS
    private float maxTPS = 100;
    private String tpsTextValue = "";
    private String tpsTextLabel = "TPS(%";

    //BATTERY VOLTAGE
    private float batteryTextSize = 35;
    private String batteryTextValue = "";
    private String batteryTextLabel = "V";

    //SPEEDOMETER
    private float TopSpeed = 0;
    private String speedText = "";
    private String topSpeedText = "";

    private float genericTextSize = 50;
    private float genericTextLabelSize = 0.50F*genericTextSize;
    private String currentGear = "N";
    private float gearTextSize = 125;

    private float batteryTextPosX = screenResolutionX-160;
    private float batteryTextPosY = 700;

    private float gearTextPositionX = screenResolutionX - 145;
    private float gearTextPositionY = 135;
    private float gearTextXOffset = -25;

    private float miscellaneousTextX = 50;
    private float miscellaneousTextY1 = gearTextPositionY;

    private float genericStartAngle = 130;
    private float genericSweepAngle = 280;
    private float genericArcDiameter = 150;

    private float genericArcPositionX1 = screenResolutionX/2 - genericArcDiameter - 50; //move from midscreen - left - 50px spacing
    private float genericArcPositionY1 = screenResolutionY - genericArcDiameter/2 - 65;

    private float genericArcPositionX2 = screenResolutionX/2; //put coolant at the center
    private float genericArcPositionY2 = screenResolutionY - genericArcDiameter/2 - 65;

    private float genericArcPositionX3 = screenResolutionX/2 + genericArcDiameter + 50; //move from midscreen + right + 50px spacing
    private float genericArcPositionY3 = screenResolutionY - genericArcDiameter/2 - 65;

    public int RPMnum = 0;
    public float GPSSpeed = 0;
    public float TPSNum = 0;
    public float FuelOpenTimeNum = 0;
    public float IgnitionAngleNum = 0;

    public float MAPNum = 0;
    public float LambdaNum = 0;

    public float AnalogInput5Num = 0; //Oil pressure
    public float Frequency1Num = 0; //Speed sensor

    public float BatteryVoltageNum = 0;
    public float AirTempNum = 0;
    public float CoolantTempNum = 0;

    public FSAEDashboard(Context context) {
        super(context);
        init();
    }

    public FSAEDashboard(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        init();
    }

    private void init(){
        pe3 = PE3ECUCANBus.getInstance(); //Bind instance to this variable. Only 1 instance exists.
        /*
            Instantiate objects separately to optimize performance
         */
        arcOval = generateBox(arcPositionX,arcPositionY,arcDiameter);
        ticksOval = arcOval;

        arcMeter1 = generateBox(arcPositionX,arcPositionY,arcDiameter+offset);
        arcMeter2 = generateBox(arcPositionX,arcPositionY,arcDiameter+offset);
        arcMeter3 = generateBox(arcPositionX,arcPositionY,arcDiameter+offset);

        genericArcOval1 = generateBox(genericArcPositionX1,genericArcPositionY1,genericArcDiameter);
        genericArcOval2 = generateBox(genericArcPositionX2,genericArcPositionY2,genericArcDiameter);
        genericArcOval3 = generateBox(genericArcPositionX3,genericArcPositionY3,genericArcDiameter);

        genericArcMeter1 = generateBox(genericArcPositionX1,genericArcPositionY1,genericArcDiameter-genericOffset);
        genericArcMeter2 = generateBox(genericArcPositionX2,genericArcPositionY2,genericArcDiameter-genericOffset);
        genericArcMeter3 = generateBox(genericArcPositionX3,genericArcPositionY3,genericArcDiameter-genericOffset);

        arcPaint = new Paint();
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(arcStrokeWidth);
        arcPaint.setColor(Color.WHITE);
        arcPaint.setAntiAlias(true);

        barFillPaint1 = new Paint();
        barFillPaint2 = new Paint();
        barFillPaint3 = new Paint();

        barFillPaint1.setColor(Color.YELLOW);
        barFillPaint1.setStyle(Paint.Style.STROKE);
        barFillPaint1.setStrokeWidth(barWidth);
        barFillPaint1.setAntiAlias(true);

        barFillPaint2.setColor(Color.GREEN);
        barFillPaint2.setStyle(Paint.Style.STROKE);
        barFillPaint2.setStrokeWidth(barWidth);
        barFillPaint2.setAntiAlias(true);

        barFillPaint3.setColor(Color.RED);
        barFillPaint3.setStyle(Paint.Style.STROKE);
        barFillPaint3.setStrokeWidth(barWidth);
        barFillPaint3.setAntiAlias(true);

        rpmTextPaint = new Paint();
        rpmTextPaint.setTextSize(rpmTextSize);
        rpmTextPaint.setColor(Color.WHITE);
        rpmTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        rpmTextPaint.setAntiAlias(true);

        rpmTextPaint2 = new Paint();
        rpmTextPaint2.setTextSize(rpmTextSize2);
        rpmTextPaint2.setColor(Color.WHITE);
        rpmTextPaint2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        rpmTextPaint2.setAntiAlias(true);

        gearPaint = new Paint();
        gearPaint.setTextSize(gearTextSize);
        gearPaint.setColor(Color.WHITE);
        gearPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        gearPaint.setAntiAlias(true);

        batteryPaint = new Paint();
        batteryPaint.setTextSize(batteryTextSize);
        batteryPaint.setColor(Color.WHITE);
        batteryPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        batteryPaint.setAntiAlias(true);

        genericPaint = new Paint();
        genericPaint.setStyle(Paint.Style.STROKE);
        genericPaint.setStrokeWidth(arcStrokeWidth);
        genericPaint.setColor(Color.WHITE);
        genericPaint.setAntiAlias(true);

        oilPressurePaint = new Paint();
        oilPressurePaint.setStyle(Paint.Style.STROKE);
        oilPressurePaint.setStrokeWidth(genericStrokeWidth);
        oilPressurePaint.setColor(Color.MAGENTA);
        oilPressurePaint.setAntiAlias(true);

        coolantTempPaint = new Paint();
        coolantTempPaint.setStyle(Paint.Style.STROKE);
        coolantTempPaint.setStrokeWidth(genericStrokeWidth);
        coolantTempPaint.setColor(Color.BLUE);
        coolantTempPaint.setAntiAlias(true);

        TPSPaint = new Paint();
        TPSPaint.setStyle(Paint.Style.STROKE);
        TPSPaint.setStrokeWidth(genericStrokeWidth);
        TPSPaint.setColor(Color.CYAN);
        TPSPaint.setAntiAlias(true);

        sensorPaint = new Paint();
        sensorPaint.setTextSize(genericTextSize);
        sensorPaint.setColor(Color.WHITE);
        sensorPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        sensorPaint.setAntiAlias(true);

        sensorLabelPaint = new Paint();
        sensorLabelPaint.setTextSize(genericTextLabelSize);
        sensorLabelPaint.setColor(Color.WHITE);
        sensorLabelPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        sensorLabelPaint.setAntiAlias(true);

        listViewPaint = new Paint();
        listViewPaint.setTextSize(genericTextLabelSize);
        listViewPaint.setColor(Color.WHITE);
        sensorLabelPaint.setAntiAlias(true);
    }

    private void drawBackgroundAndArc(Canvas canvas){
        canvas.drawArc(arcOval,arcStartAngle,arcSweepAngle,false,arcPaint);
    }

    private void drawTicks(Canvas canvas) {
        radius = arcDiameter/2;
        currentAngle = arcStartAngle;
        maxAngle = arcStartAngle+arcSweepAngle;
        steps = arcSweepAngle / numberOfTicks;

        //float x1,y1,x2,y2;
        count = 0;

        while(currentAngle <= maxAngle) {
            x1 = ticksOval.centerX() + (radius-tickLength)* (float) Math.cos(Math.toRadians(currentAngle));
            y1 = ticksOval.centerY() + (radius-tickLength)* (float)Math.sin(Math.toRadians(currentAngle));
            x2 = ticksOval.centerX() + radius*(float)Math.cos(Math.toRadians(currentAngle));
            y2 = ticksOval.centerY() + radius*(float)Math.sin(Math.toRadians(currentAngle));
            canvas.drawLine(x1, y1, x2, y2, arcPaint);
            canvas.drawText(rpmLabel[count],(x1-5),(y1+20),arcPaint);
            currentAngle+=steps;
            count++;
        }

        x1 = ticksOval.centerX() + (radius-tickLength)* (float)Math.cos(Math.toRadians(maxAngle));
        y1 = ticksOval.centerY() + (radius-tickLength)* (float)Math.sin(Math.toRadians(maxAngle));
        x2 = ticksOval.centerX() + radius*(float)Math.cos(Math.toRadians(maxAngle));
        y2 = ticksOval.centerY() + radius*(float)Math.sin(Math.toRadians(maxAngle));
        canvas.drawLine(x1,y1,x2,y2,arcPaint);
        canvas.drawText(rpmLabel[13],(x1-5),(y1+20),arcPaint);
    }

//    private void drawBars(Canvas canvas){
//        double radius = arcDiameter/2;
//        double offset = arcStrokeWidth;
//        float currentAngle = arcStartAngle;
//        float maxAngle = arcStartAngle+arcSweepAngle;
//        double steps = arcSweepAngle / numberOfBars;
//
//        while(currentAngle <= maxAngle) {
//            double x2 = barsOval.centerX() + (radius+barLength)* Math.cos( toRadians(currentAngle) );
//            double y2 = barsOval.centerY() + (radius+barLength)* Math.sin(toRadians(currentAngle));
//            double x1 = barsOval.centerX() + (radius+offset)*Math.cos(toRadians(currentAngle));
//            double y1 = barsOval.centerY() + (radius+offset)*Math.sin(toRadians(currentAngle));
//            canvas.drawLine((float)x1,(float)y1,(float)x2,(float)y2,barPaint);
//            currentAngle+=steps;
//        }
//    }

    private void drawArcMeter(Canvas canvas){

        if(RPMnum < shiftThreshold1){
            arcStart = arcStartAngle;
            arcSweepLength = arcSweepAngle*RPMnum/maxRPM;
            canvas.drawArc(arcMeter1,arcStart,arcSweepLength,false,barFillPaint1); //Just draw straight red up to RPM value

        } else if( RPMnum >= shiftThreshold1 && RPMnum < shiftThreshold2){ //Draw red up to max percentage, then switch to green

            arcStart1 = arcStartAngle;
            arcSweepLength1 = arcSweepAngle*shiftThreshold1/maxRPM;

            arcStart2 = arcStart1+arcSweepLength1;
            arcSweepLength2 = (RPMnum-shiftThreshold1)*arcSweepAngle/maxRPM;

            canvas.drawArc(arcMeter1,arcStart1,arcSweepLength1,false,barFillPaint1); //Draw red up to limit
            canvas.drawArc(arcMeter2,arcStart2,arcSweepLength2,false,barFillPaint2); //Draw the rest in green

        } else { //RPM > Threshold2

            arcStart1 = arcStartAngle;
            arcSweepLength1 = arcSweepAngle*shiftThreshold1/maxRPM;

            arcStart2 = arcStart1+arcSweepLength1;
            arcSweepLength2 = arcSweepAngle*(shiftThreshold2-shiftThreshold1)/maxRPM;

            arcStart3 = arcStart1+arcSweepLength1+arcSweepLength2;
            arcSweepLength3 = arcSweepAngle*(RPMnum-shiftThreshold2)/maxRPM;

            canvas.drawArc(arcMeter1,arcStart1,arcSweepLength1,false,barFillPaint1); //Draw red up to limit
            canvas.drawArc(arcMeter2,arcStart2,arcSweepLength2,false,barFillPaint2); //Draw green up to limit
            canvas.drawArc(arcMeter3,arcStart3,arcSweepLength3,false,barFillPaint3); //Draw green up to limit

        }
    }

//    private void fillBars(Canvas canvas, int percentage){
//        drawBars(canvas); //Used to reset filled bars
//
//        RectF oval = arcOval;
//        double radius = arcDiameter/2;
//        double offset = arcStrokeWidth;
//        float currentAngle = arcStartAngle;
//        float maxAngle = arcStartAngle+arcSweepAngle;
//        double steps = arcSweepAngle / numberOfBars;
//
//        int barsFilled = 0;
//        int barsToFill = numberOfBars*percentage/100;
//
//        while(currentAngle <= maxAngle ) {
//            double x2 = oval.centerX() + (radius+barLength)* Math.cos( toRadians(currentAngle) );
//            double y2 = oval.centerY() + (radius+barLength)* Math.sin(toRadians(currentAngle));
//            double x1 = oval.centerX() + (radius+offset)*Math.cos(toRadians(currentAngle));
//            double y1 = oval.centerY() + (radius+offset)*Math.sin(toRadians(currentAngle));
//
//            canvas.drawText(currentGear,50,150,gearPaintClear);
//            if(barsFilled <= shiftThreshold1 && barsFilled > 0){
//                canvas.drawLine((float) x1, (float) y1, (float)x2,(float)y2,barFillPaint1);
//                currentGear = "1";
//                drawGearText(canvas);
//            } else if(barsFilled <= shiftThreshold2 && barsFilled > shiftThreshold1){
//                canvas.drawLine((float) x1, (float) y1, (float)x2,(float)y2,barFillPaint2);
//                currentGear = "2";
//                drawGearText(canvas);
//            } else if(barsFilled >= shiftThreshold2) {
//                canvas.drawLine((float) x1, (float) y1, (float)x2,(float)y2,barFillPaint3);
//                currentGear = "3";
//                drawGearText(canvas);
//            } else {
//                currentGear = "N";
//                drawGearText(canvas);
//            }
//
//            currentAngle+=steps;
//            barsFilled++;
//            if( barsFilled >= barsToFill ){
//                break;
//            }
//        }
//    }

    private void drawRPMText(Canvas canvas){
        rpmTextValue = String.valueOf(RPMnum);
        canvas.drawText(rpmTextValue, screenResolutionX/2 - rpmTextPaint.measureText(rpmTextValue)/2, rpmTextLocationY, rpmTextPaint);
        canvas.drawText(rpmTextLabel,screenResolutionX/2 + rpmTextPaint.measureText(rpmTextValue)/2,rpmTextLocationY,rpmTextPaint2);
    }

    private void drawGearText(Canvas canvas){
        //speedText = String.format("%.0f",Frequency1Num);
        //topSpeedText = String.format("%.0f",TopSpeed);

        canvas.drawText(currentGear, gearTextPositionX, gearTextPositionY,gearPaint);
        //canvas.drawText(speedText,15,gearTextPositionY,gearPaint);
        //canvas.drawText("MPH",15+gearPaint.measureText(speedText),gearTextPositionY,rpmTextPaint2);

        //canvas.drawText("Max Speed: "+ topSpeedText + " mph",15,gearTextPositionY+35,rpmTextPaint2);
    }

    private void drawBatteryText(Canvas canvas) {
        canvas.drawText(String.format("%.2f",BatteryVoltageNum) + " V",batteryTextPosX,batteryTextPosY,batteryPaint);
    }

    private void drawThreeSmallArcs(Canvas canvas){

        //1 OIL
        canvas.drawArc(genericArcOval1, genericStartAngle, genericSweepAngle, false, genericPaint);
        canvas.drawText(oilTextLabel, genericArcPositionX1 - sensorLabelPaint.measureText(oilTextLabel) / 2, genericArcMeter1.centerY() + 40, sensorLabelPaint);

        //2 COOLANT
        canvas.drawArc(genericArcOval2, genericStartAngle,genericSweepAngle,false,genericPaint);
        canvas.drawText(coolantTextLabel, genericArcPositionX2 - sensorLabelPaint.measureText(coolantTextLabel) / 2, genericArcMeter2.centerY() + 40, sensorLabelPaint);

        //3 TPS
        canvas.drawArc(genericArcOval3, genericStartAngle, genericSweepAngle, false, genericPaint);
        canvas.drawText(tpsTextLabel, genericArcPositionX3 - sensorLabelPaint.measureText(tpsTextLabel) / 2, genericArcMeter3.centerY() + 40, sensorLabelPaint);
    }

    private void drawGenericArcMeter1(Canvas canvas) {
        oilTextValue = String.format("%.2f", AnalogInput5Num);

        canvas.drawArc(genericArcMeter1,genericStartAngle,genericSweepAngle*AnalogInput5Num/maxOilPressure,false,oilPressurePaint);
        canvas.drawText(oilTextValue,genericArcPositionX1- sensorPaint.measureText(oilTextValue)/2,genericArcMeter1.centerY()+15,sensorPaint);

    }

    private void drawGenericArcMeter2(Canvas canvas) {
        coolantTextValue = String.format("%.2f", CoolantTempNum);

        canvas.drawArc(genericArcMeter2,genericStartAngle,genericSweepAngle*CoolantTempNum/maxCoolantTemperature,false,coolantTempPaint);
        canvas.drawText(coolantTextValue, genericArcPositionX2 - sensorPaint.measureText(coolantTextValue)/2, genericArcMeter2.centerY()+ 15, sensorPaint);
    }

    private void drawGenericArcMeter3(Canvas canvas) {
        tpsTextValue = String.format("%.2f", TPSNum);

        canvas.drawArc(genericArcMeter3, genericStartAngle, genericSweepAngle * TPSNum / maxTPS, false, TPSPaint);
        canvas.drawText(tpsTextValue, genericArcPositionX3 - sensorPaint.measureText(tpsTextValue) / 2, genericArcMeter3.centerY() + 15, sensorPaint);
    }

    private void drawListViewData(Canvas canvas){
        canvas.drawText("RPM: " + String.format("%.2f",pe3.getRPM()),200,300,listViewPaint);
        canvas.drawText("TPS: " + String.format("%.2f",pe3.getTPS()),200,330,listViewPaint);
        canvas.drawText("FuelOpenTime: " + String.format("%.2f",pe3.getFuelOpenTime()),200,360,listViewPaint);
        canvas.drawText("IgnitionAngle: " + String.format("%.2f",pe3.getIgnitionAngle()),200,390,listViewPaint);
        canvas.drawText("MAP: " + String.format("%.2f",pe3.getMAP()),500,300,listViewPaint);
        canvas.drawText("Lambda: " + String.format("%.2f",pe3.getLambda()),500,330,listViewPaint);
        canvas.drawText("AnalogInput5: " + String.format("%.2f",pe3.getAnalogInput5()),500,360,listViewPaint);
        canvas.drawText("Frequency1: " + String.format("%.2f",pe3.getFrequency1()),500,390,listViewPaint);
        canvas.drawText("BatteryVolt: " + String.format("%.2f",pe3.getBatteryVolt()),500,420,listViewPaint);
        canvas.drawText("AirTemp: " + String.format("%.2f",pe3.getAirTemp()),500,450,listViewPaint);
        canvas.drawText("CoolantTemp: " + String.format("%.2f",pe3.getCoolantTemp()),500,480,listViewPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawColor(Color.BLACK); //Clears canvas
        drawBackgroundAndArc(canvas); //Draws main RPM arc
        drawTicks(canvas); //Draws tiny ticks on main RPM arc
        drawThreeSmallArcs(canvas);


        //Refresh values
        RPMnum = (int)pe3.getRPM();
        TPSNum = pe3.getTPS();
        FuelOpenTimeNum = pe3.getFuelOpenTime();
        IgnitionAngleNum = pe3.getIgnitionAngle();

        MAPNum = pe3.getMAP();
        LambdaNum = pe3.getLambda();

        AnalogInput5Num = pe3.getAnalogInput5();
        Frequency1Num = pe3.getFrequency1();

        if(Frequency1Num > TopSpeed){
            TopSpeed = Frequency1Num; //still in hertz
        }

        BatteryVoltageNum = pe3.getBatteryVolt();
        AirTempNum = pe3.getAirTemp();
        CoolantTempNum = pe3.getCoolantTemp();
        //System.out.println("IS SETUP? " + setupDone);

        drawListViewData(canvas); //List of all values
        drawArcMeter(canvas); //Draws the fill meter for RPM (colors as YELLOW, GREEN, RED)
        drawRPMText(canvas); //Draws the value of RPM as an integer
        //drawGearText(canvas); //Draws the gear letter
        drawGenericArcMeter1(canvas);
        drawGenericArcMeter2(canvas);
        drawGenericArcMeter3(canvas);
        drawBatteryText(canvas); //Draws battery voltage text
    }

    private RectF generateBox(float centerX, float centerY, float boxWidth){
        //Creates a RectF object by specifying the center of the object and the width
        //Width equals height
        float radius = boxWidth / 2.0F;
        RectF oval = new RectF();
        oval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        return oval;
    }

    //@Override
//    public boolean onTouchEvent(MotionEvent e){
//        percentage+=2.5;
//
//        if(percentage > 100){
//            percentage = 0;
//        }
//        RPM = percentage*maxRPM/100;
//
//        super.invalidate();
//        return super.onTouchEvent(e);
//
//    }


}
