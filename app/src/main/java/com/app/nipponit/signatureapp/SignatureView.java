package com.app.nipponit.signatureapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by manojm on 08/03/2018.
 */

public class SignatureView extends View {

    private static float STROKE_WIDTH = 5f;
    private static float HALF_STROKE_WIDTH = STROKE_WIDTH/2;

    private Paint paint = new Paint();
    private Path path = new Path();

    private float lastTouchX;
    private float lastTouchY;
    private RectF dirtyRect = new RectF();

    private Bitmap Signature;

    public SignatureView(Context context) {
        super(context);

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);

        this.setBackgroundColor(Color.WHITE);

        this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


    }

    private void getSignature(){
        Bitmap signatureBitmap = null;

        if(signatureBitmap==null){
            signatureBitmap = Bitmap.createBitmap(this.getWidth(),this.getHeight(),Bitmap.Config.RGB_565);
        }

        Canvas canvas = new Canvas(signatureBitmap);
        this.draw(canvas);

        this.Signature = signatureBitmap;
    }

    public Bitmap _getSignature(){
        this.getSignature();
        return Signature;
    }

    public void clearSignature(){

            path.reset();
            this.invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(this.path,this.paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX,eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;
            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_UP:
                resetDirtyRect(eventX,eventY);
                int historySize = event.getHistorySize();
                for(int i=0; i<historySize;i++){
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);

                    expandDirtyRect(historicalX,historicalY);
                    path.lineTo(historicalX,historicalY);
                }
                path.lineTo(eventX,eventY);
                break;

            default:

                return false;
        }

        invalidate(
                (int) (dirtyRect.left-HALF_STROKE_WIDTH),
                (int)(dirtyRect.top-HALF_STROKE_WIDTH),
                (int)(dirtyRect.right+HALF_STROKE_WIDTH),
                (int)(dirtyRect.bottom+HALF_STROKE_WIDTH));

        lastTouchY = eventX;
        lastTouchY = eventY;

        return  true;
    }

    private void expandDirtyRect(Float historicalX, float historicalY){
        if(historicalX < dirtyRect.left){
            dirtyRect.left = historicalX;
        }else if (historicalX > dirtyRect.right){
            dirtyRect.right = historicalX;
        }

        if(historicalY < dirtyRect.top){
            dirtyRect.top = historicalY;
        }else if (historicalY > dirtyRect.bottom){
            dirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float eventX, float eventY){
        dirtyRect.left = Math.min(lastTouchX,eventX);
        dirtyRect.right = Math.max(lastTouchX,eventX);
        dirtyRect.top = Math.min(lastTouchY,eventY);
        dirtyRect.bottom = Math.max(lastTouchY,eventY);
    }
}
