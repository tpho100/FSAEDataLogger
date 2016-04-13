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

    //Flags to prevent the canvas from redrawing objects that do not need to be re-drawn each frame
    private boolean ticksDrawn = false;
    private boolean arcSweepDrawn = false;
    private boolean genericArc1Drawn = false;
    private boolean genericArc2Drawn = false;
    private boolean genericArc3Drawn = false;
    private boolean genericArc4Drawn = false;
    private boolean canvasCleared = false;

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

    private RectF barsOval; //For bars to fill the "meter". Either use this or arcMeter. not both.

    //Used to display general information
    private RectF genericArcOval1; //Oil pressure
    private RectF genericArcOval2; //Coolant temp
    private RectF genericArcOval3; //TPS
    private RectF genericArcOval4; //Anything you want

    //Used to fill the genericArc "meters"
    private RectF genericArcMeter1;
    private RectF genericArcMeter2;
    private RectF genericArcMeter3;
    private RectF genericArcMeter4;

    //Main tachometer arc parameters
    private float arcStartAngle = 240;
    private float arcSweepAngle = 60;
    private float arcStrokeWidth = 2;
    private float arcDiameter = 1500;
    private float arcPositionX = 640;
    private float arcPositionY = 800;
    private float offset = 49; //Offset spacing for arcMeter

    private float genericStrokeWidth = 25;
    private float genericOffset = 25;

    /*
        Number of ticks for RPM. Currently set to 1 tick per 1000 RPM
     */
    private float numberOfTicks = 13;
    private int tickLength = 20;

    /*
        Paint objects to color shapes
     */
    private Paint gearPaint;
    private Paint gearPaintClear;
    private Paint arcPaint;
    private Paint barPaint;
    private Paint barFillPaint1;
    private Paint barFillPaint2;
    private Paint barFillPaint3;
    private Paint rpmTextPaint;
    private Paint genericPaint;
    private Paint sensorPaint;
    private Paint oilPressurePaint;
    private Paint coolantTempPaint;
    private Paint TPSPaint;
    private Paint rpmTextPaint2;

    /*
        Number of bars for the meter
        Currently set to 100 bars for 100%
     */
    private int numberOfBars = 100;
    private int barLength = 95;
    private float percentage = 0;
    private int barWidth = 40; //Also used for sweep thickness

    /*
        Threshold for when to change color to indicate
        shift time
     */
    private float shiftThreshold1 = 3000;
    private float shiftThreshold2 = 10000;

    /*
        Parameters to adjust RPM scaling on dial
     */
    private String[] rpmLabel = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13"};
    private float RPM = 13000;
    private float maxRPM = 13000;
    private int rpmTextLocationX = screenResolutionX/2;
    private int rpmTextLocationY = 250;
    private float rpmTextSize = 175;
    private float rpmTextSize2 = 0.25F*rpmTextSize;
    private String rpmText;

    private float oilpressure = 77;
    private float maxOilPressure = 100;
    private float coolantTemperature = 34;
    private float maxCoolantTemperature = 100;
    private float maxTPS = 100;
    private float oilPressureThreshold1 = 35;
    private float oilPressureThreshold2 = 75;
    private float coolantTemperatureThreshold1 = 45;
    private float coolantTemperatureThreshold2 = 95;
    private float genericTextSize = 50;
    private String currentGear = "N";
    private float gearTextSize = 125;
    private float gearTextPositionX = screenResolutionX - 145;
    private float gearTextPositionY = 135;
    private float gearTextXOffset = -25;

    private float miscellaneousTextX = 50;
    private float miscellaneousTextY1 = gearTextPositionY;

    private String batteryText;

    private float genericStartAngle = 130;
    private float genericSweepAngle = 280;
    private float genericArcDiameter = 150;

    private float genericArcPositionX1 = screenResolutionX - genericArcDiameter + 50;
    private float genericArcPositionY1 = 100 + genericArcDiameter;

    private float genericArcPositionX2 = screenResolutionX - genericArcDiameter + 50;
    private float genericArcPositionY2 = 125 + genericArcDiameter*2;

    private float genericArcPositionX3 = screenResolutionX - genericArcDiameter + 50;
    private float genericArcPositionY3 = 150 + genericArcDiameter*3;

    private float genericArcPositionX4 = screenResolutionX - genericArcDiameter + 50;
    private float genericArcPositionY4 = 100 + genericArcDiameter*4;

    /*
    Other engine parameters
     */
    private float TPS = 27.5F;
    private double FuelOpenTime = 0;
    private double IgnitionAngle = 0;
    private double Barometer = 0;
    private double MAP = 0;
    private double Lambda = 0;
    private String PressureType = "";

    private double AnalogInput1 = 0;
    private double AnalogInput2 = 0;
    private double AnalogInput3 = 0;
    private double AnalogInput4 = 0;

    private double AnalogInput5 = 0;
    private double AnalogInput6 = 0;
    private double AnalogInput7 = 0;
    private double AnalogInput8 = 0;

    private double Frequency1 = 0;
    private double Frequency2 = 0;
    private double Frequency3 = 0;
    private double Frequency4 = 0;

    private double BatteryVolt = 11.7;
    private double AirTemp = 0;
    private double CoolantTemp = 0;
    private String TempType = "";

    private PE3ECUCANBus pe3 = new PE3ECUCANBus();

    public FSAEDashboard(Context context) {
        super(context);
        init();
    }

    public FSAEDashboard(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        init();
    }

    private void init(){
        /*
            Instantiate objects separately to optimize performance
         */
        arcOval = generateBox(arcPositionX,arcPositionY,arcDiameter);
        ticksOval = arcOval;
        barsOval = arcOval;
        arcMeter1 = generateBox(arcPositionX,arcPositionY,arcDiameter+offset);
        arcMeter2 = generateBox(arcPositionX,arcPositionY,arcDiameter+offset);
        arcMeter3 = generateBox(arcPositionX,arcPositionY,arcDiameter+offset);

        genericArcOval1 = generateBox(genericArcPositionX1,genericArcPositionY1,genericArcDiameter);
        genericArcOval2 = generateBox(genericArcPositionX2,genericArcPositionY2,genericArcDiameter);
        genericArcOval3 = generateBox(genericArcPositionX3,genericArcPositionY3,genericArcDiameter);
        genericArcOval4 = generateBox(genericArcPositionX4,genericArcPositionY4,genericArcDiameter);

        genericArcMeter1 = generateBox(genericArcPositionX1,genericArcPositionY1,genericArcDiameter-genericOffset);
        genericArcMeter2 = generateBox(genericArcPositionX2,genericArcPositionY2,genericArcDiameter-genericOffset);
        genericArcMeter3 = generateBox(genericArcPositionX3,genericArcPositionY3,genericArcDiameter-genericOffset);
        genericArcMeter4 = generateBox(genericArcPositionX4,genericArcPositionY4,genericArcDiameter-genericOffset);

        arcPaint = new Paint();
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(arcStrokeWidth);
        arcPaint.setColor(Color.WHITE);
        arcPaint.setAntiAlias(true);

        barPaint = new Paint();
        barPaint.setColor(Color.GRAY);
        barPaint.setStyle(Paint.Style.STROKE);
        barPaint.setStrokeWidth(barWidth);
        barPaint.setAntiAlias(true);

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
        //gearTextPositionX = screenResolutionX - gearPaint.measureText(currentGear) + gearTextXOffset;

        gearPaintClear = new Paint();
        gearPaintClear.setTextSize(gearTextSize);
        gearPaintClear.setColor(Color.BLACK);
        gearPaintClear.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        gearPaintClear.setAntiAlias(true);

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

        Thread t = new Thread( new Runnable(){

            @Override
            public void run() {
                //Used for testing dynamic displays
                while(true){
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    RPM += 11.1;
                    TPS += 0.1;
                    coolantTemperature += 0.02;
                    oilpressure += 0.08;

                    if(TPS > 100){
                        TPS = 0;
                    }

                    if(coolantTemperature > 95){
                        coolantTemperature = 24;
                    }

                    if(oilpressure > 100){
                        oilpressure = 34;
                    }

                    if(RPM > 13000){
                        RPM = 0;
                    }
                }
            }
        });
        t.start();
    }

    private void drawBackgroundAndArc(Canvas canvas){
        canvas.drawArc(arcOval,arcStartAngle,arcSweepAngle,false,arcPaint);
    }

    private void drawTicks(Canvas canvas) {
        float radius = arcDiameter/2;
        float currentAngle = arcStartAngle;
        float maxAngle = arcStartAngle+arcSweepAngle;
        float steps = arcSweepAngle / numberOfTicks;

        int count = 0;
        while(currentAngle <= maxAngle) {
            double x1 = ticksOval.centerX() + (radius-tickLength)* Math.cos( toRadians(currentAngle) );
            double y1 = ticksOval.centerY() + (radius-tickLength)* Math.sin(toRadians(currentAngle));
            double x2 = ticksOval.centerX() + radius*Math.cos(toRadians(currentAngle));
            double y2 = ticksOval.centerY() + radius*Math.sin(toRadians(currentAngle));
            canvas.drawLine((float) x1, (float) y1, (float) x2, (float) y2, arcPaint);
            canvas.drawText(rpmLabel[count],(float) (x1-5), (float) (y1+20),arcPaint);
            currentAngle+=steps;
            count++;
        }

        double x1 = ticksOval.centerX() + (radius-tickLength)* Math.cos( toRadians(maxAngle) );
        double y1 = ticksOval.centerY() + (radius-tickLength)* Math.sin(toRadians(maxAngle));
        double x2 = ticksOval.centerX() + radius*Math.cos(toRadians(maxAngle));
        double y2 = ticksOval.centerY() + radius*Math.sin(toRadians(maxAngle));
        canvas.drawLine((float)x1,(float)y1,(float)x2,(float)y2,arcPaint);
        canvas.drawText(rpmLabel[13],(float) (x1-5), (float) (y1+20),arcPaint);
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
        if(RPM < shiftThreshold1){
            float arcStart = arcStartAngle;
            float arcSweepLength = arcSweepAngle*RPM/maxRPM;
            canvas.drawArc(arcMeter1,arcStart,arcSweepLength,false,barFillPaint1); //Just draw straight red up to RPM value

        } else if( RPM >= shiftThreshold1 && RPM < shiftThreshold2){ //Draw red up to max percentage, then switch to green

            float arcStart1 = arcStartAngle;
            float arcSweepLength1 = arcSweepAngle*shiftThreshold1/maxRPM;

            float arcStart2 = arcStart1+arcSweepLength1;
            float arcSweepLength2 = (RPM-shiftThreshold1)*arcSweepAngle/maxRPM;

            canvas.drawArc(arcMeter1,arcStart1,arcSweepLength1,false,barFillPaint1); //Draw red up to limit
            canvas.drawArc(arcMeter2,arcStart2,arcSweepLength2,false,barFillPaint2); //Draw the rest in green

        } else { //RPM > Threshold2

            float arcStart1 = arcStartAngle;
            float arcSweepLength1 = arcSweepAngle*shiftThreshold1/maxRPM;

            float arcStart2 = arcStart1+arcSweepLength1;
            float arcSweepLength2 = arcSweepAngle*(shiftThreshold2-shiftThreshold1)/maxRPM;

            float arcStart3 = arcStart1+arcSweepLength1+arcSweepLength2;
            float arcSweepLength3 = arcSweepAngle*(RPM-shiftThreshold2)/maxRPM;

            canvas.drawArc(arcMeter1,arcStart1,arcSweepLength1,false,barFillPaint1); //Draw red up to limit
            canvas.drawArc(arcMeter2,arcStart2,arcSweepLength2,false,barFillPaint2); //Draw green up to limit
            canvas.drawArc(arcMeter3,arcStart3,arcSweepLength3,false,barFillPaint3); //Draw green up to limit

        }

        invalidate();
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
        rpmText = String.valueOf((int) RPM);
        canvas.drawText(rpmText, screenResolutionX/2 - rpmTextPaint.measureText(rpmText)/2, rpmTextLocationY, rpmTextPaint);
        canvas.drawText("RPM",screenResolutionX/2 + rpmTextPaint.measureText(rpmText)/2,rpmTextLocationY,rpmTextPaint2);
    }

    private void drawGearText(Canvas canvas){
        canvas.drawText(currentGear, gearTextPositionX, gearTextPositionY,gearPaint);
    }

    private void drawGenericArcMeter1(Canvas canvas){
        String oilPressureString = String.valueOf((int) oilpressure);

        canvas.drawArc(genericArcMeter1,genericStartAngle,genericSweepAngle*oilpressure/maxOilPressure,false,oilPressurePaint);

        if(!genericArc1Drawn){
            canvas.drawArc(genericArcOval1, genericStartAngle, genericSweepAngle, false, genericPaint);
            genericArc1Drawn = true;
        }
        canvas.drawText(oilPressureString,genericArcPositionX1- sensorPaint.measureText(oilPressureString)/2,genericArcMeter1.centerY()+15,sensorPaint);
    }

    private void drawGenericArcMeter2(Canvas canvas){
        String coolantTemperatureString = String.valueOf((int) coolantTemperature);

        canvas.drawArc(genericArcMeter2,genericStartAngle,genericSweepAngle*coolantTemperature/maxCoolantTemperature,false,coolantTempPaint);

        if(!genericArc2Drawn){
            canvas.drawArc(genericArcOval2, genericStartAngle,genericSweepAngle,false,genericPaint);
        }

        canvas.drawText(coolantTemperatureString, genericArcPositionX2 - sensorPaint.measureText(coolantTemperatureString)/2, genericArcMeter2.centerY()+15, sensorPaint);
    }

    private void drawGenericArcMeter3(Canvas canvas){
        String TPSString = String.valueOf((int) TPS);

        canvas.drawArc(genericArcMeter3,genericStartAngle,genericSweepAngle*TPS/maxTPS,false,TPSPaint);

        if(!genericArc3Drawn){
            canvas.drawArc(genericArcOval3, genericStartAngle,genericSweepAngle,false,genericPaint);
        }

        canvas.drawText(TPSString, genericArcPositionX3 - sensorPaint.measureText(TPSString) / 2, genericArcMeter3.centerY() + 15, sensorPaint);
    }

    private void drawGenericArcMeter4(Canvas canvas){
        canvas.drawArc(genericArcOval4, genericStartAngle, genericSweepAngle, false, genericPaint);
    }

    private void drawMiscellaneousData(Canvas canvas){
        canvas.drawText(String.valueOf( BatteryVolt ) + "V", miscellaneousTextX, miscellaneousTextY1,gearPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!canvasCleared){
            canvas.drawColor(Color.BLACK); //Clears canvas
            canvasCleared = true;
        }

        if(!arcSweepDrawn){
            drawBackgroundAndArc(canvas);
            arcSweepDrawn = true;
        }

        if(!ticksDrawn){
            drawTicks(canvas);
            ticksDrawn = true;
        }

        //drawBars(canvas);
        //fillBars(canvas, rpmPercentage);
        drawArcMeter(canvas);
        drawRPMText(canvas);
        drawGearText(canvas);
        drawGenericArcMeter1(canvas);
        drawGenericArcMeter2(canvas);
        drawGenericArcMeter3(canvas);
        //drawGenericArcMeter4(canvas);
        //drawMiscellaneousData(canvas);

    }

    public void updateData(String data){
        pe3.insertData(data);
        invalidate();
    }

    private RectF generateBox(float centerX, float centerY, float boxWidth){
        //Creates a RectF object by specifying the center of the object and the width
        //Width equals height
        float radius = boxWidth / 2;
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

    private double toRadians(double a){
        return a*Math.PI/180;
    }
}
