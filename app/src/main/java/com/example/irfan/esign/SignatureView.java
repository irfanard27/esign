package com.example.irfan.esign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Irfan on 18/01/18.
 */
public class SignatureView extends ImageView {

    Bitmap signatureBitmap;
    float x = 200;
    float y = 200;

    public SignatureView(Context context, Bitmap pdfBitmap) {
        super(context);

        String root = Environment.getExternalStorageDirectory().toString();
        Bitmap b = BitmapFactory.decodeFile(root + "/saved_signature/mysignature.png");
        signatureBitmap = Bitmap.createScaledBitmap(b, pdfBitmap.getWidth()/5, pdfBitmap.getHeight()/5, true);

        Log.e("DIM Bitmap:",""+signatureBitmap.getWidth()+ "," + signatureBitmap.getHeight());

        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        y = event.getY();
                        Log.d("TUOCHED Down",""+x+","+y);
                        invalidate();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        x = event.getX();
                        y = event.getY();
                        Log.d("TUOCHED",""+x+","+y);
                        invalidate();
                        break;
                }

                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawBitmap(signatureBitmap,(signatureBitmap.getWidth() / 2),(signatureBitmap.getWidth() / 2),null);
        canvas.restore();
    }
}
