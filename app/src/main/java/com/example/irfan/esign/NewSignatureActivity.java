package com.example.irfan.esign;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.kyanogen.signatureview.SignatureView;

import java.io.File;
import java.io.FileOutputStream;

public class NewSignatureActivity extends AppCompatActivity {

    SignaturePad signaturePad;

    // function yang di panggil ketika aplikasi di launch pertama kali
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_signature);
        signaturePad = (SignaturePad) findViewById(R.id.signature_pad);
    }


    // clear ttd yang sudah digambar
    public void clearSignature(View v){
        signaturePad.clear();
    }

    // menyimpan ttd yang sudah di gambar
    public void saveSignature(View v){
        // mendapatkan ttd tanpa background putih
        Bitmap img = signaturePad.getTransparentSignatureBitmap();
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/saved_signature");

        // membuat direktori
        if(!dir.exists()){
            dir.mkdirs();
        }

        // menyimpan dengan dama mysignature.png
        String filename = "mysignature.png";
        File file = new File (dir, filename);

        if (file.exists ()) file.delete ();

        // menyimpan dengan extensi PNG
        try {
            FileOutputStream out = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // jika berhasil maka akan keluat toast
        Toast.makeText(this, "Your image is saved to this folder", Toast.LENGTH_LONG).show();
        Log.e("TAG",root + "/saved_signature");
        finish();
    }
}
