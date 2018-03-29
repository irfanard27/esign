package com.example.irfan.esign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;

public class PdfEditorActivity extends AppCompatActivity {

    /* variable yang di gunakan untuk keperluan di activity pdf editor */
    ImageView imageView;
    Bitmap pdfBitmap;
    Bitmap signatureBitmap;
    boolean loadedSignature = false;
    boolean isChangePos = false;
    boolean isChangeSize = false;
    Canvas pdfCanvas;
    String sharedFilename;
    String path;

    // function yang di panggil ketika aplikasi di launch pertama kali
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_editor);

        // mendapatkan string yang di kirim pada activy sebelumnya
        Intent intent = getIntent();
        String file = intent.getStringExtra("filename");

        imageView = (ImageView) findViewById(R.id.imageView);

        try {
            // membuka pdf file
            openPDF(file);


        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // function yang di gunakn untuk membuat menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdf_editor_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // function yang digunakan ketika menu di klik
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // ketika menu new signature di klik
        if(id == R.id.pdf_menu_new_sign){

            // function signature bitmap
            loadSignatureBitmap();
            Toast.makeText(this,"Sentuh Untuk Pilih Posisi",Toast.LENGTH_SHORT).show();

        // ketika menu save pdf di klik
        } else if(id == R.id.pdf_menu_save){

            // create pdf
            createPdf();
            //share();
        }
        return super.onOptionsItemSelected(item);
    }

    // function untuk open pdf
    private void openPDF(String targetPdf) throws IOException {
        File file = new File(targetPdf);


        ParcelFileDescriptor fileDescriptor = null;
        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

        //min. API Level 21
        PdfRenderer pdfRenderer = null;
        pdfRenderer = new PdfRenderer(fileDescriptor);

        final int pageCount = pdfRenderer.getPageCount();
        Toast.makeText(this, "pageCount = " + pageCount, Toast.LENGTH_LONG).show();

        //Display page 0
        PdfRenderer.Page rendererPage = pdfRenderer.openPage(0);

        // mendapatkan height & width dari pdf
        int rendererPageWidth = rendererPage.getWidth();
        int rendererPageHeight = rendererPage.getHeight();

        // membuat bitmap dari pdf
        pdfBitmap = Bitmap.createBitmap(rendererPageWidth, rendererPageHeight, Bitmap.Config.ARGB_8888);
        rendererPage.render(pdfBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        rendererPage.close();

        // menampilkan pdf ke imageview
        imageView.setImageBitmap(pdfBitmap);
        pdfRenderer.close();
        fileDescriptor.close();
    }

    // mengambil file ttd yang sudah di buat sebelumnya
    private void loadSignatureBitmap(){

        // untuk mengambil direktori lokasi
        String root = Environment.getExternalStorageDirectory().toString();
        Bitmap b = BitmapFactory.decodeFile(root + "/saved_signature/mysignature.png");
        signatureBitmap = Bitmap.createScaledBitmap(b, pdfBitmap.getWidth()/6, pdfBitmap.getHeight()/6, true);

        Log.e("DIM Bitmap:",""+signatureBitmap.getWidth()+ "," + signatureBitmap.getHeight());
        //Bitmap pallet = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        loadedSignature = true;
        isChangePos = true;

    }

   public void share(){
        File outputFile = new File("sdcard/Download/" + sharedFilename);
        Uri uri = Uri.fromFile(outputFile);

        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("application/pdf");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,"no whatsapp",Toast.LENGTH_SHORT).show();
        }
    }

    // ketika user melakukan sentuhan pada layar smartphone
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(loadedSignature){
            switch (event.getAction()){
                // ketika sentuhan di mulai
                case MotionEvent.ACTION_DOWN:
                    if(isChangePos){
                        // mendapatkan x & y dari sentuhan user
                        float newX = event.getX();
                        float newY = event.getY();

                        pdfCanvas = new Canvas(pdfBitmap);
                        Matrix mtx = new Matrix();
                        mtx.postTranslate(newX - signatureBitmap.getWidth(),newY - (signatureBitmap.getHeight() * 3));

                        // drawing bitmao ttd ke pdf sesuai dengan sentuhan user
                        pdfCanvas.drawBitmap(signatureBitmap,mtx,null);
                        imageView.invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("TOUCHED UP:","DONE");
                    isChangePos = false;
            }
        } else {
           Toast.makeText(this,"Load signature before",Toast.LENGTH_SHORT).show();
        }

        return super.onTouchEvent(event);
    }

    // function untuk membuat pdf
    private void createPdf(){
        // membuat dokumen baru
        PdfDocument document = new PdfDocument();

        // membuat page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pdfBitmap.getWidth(), pdfBitmap.getHeight(), 1).create();


        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        // membuat halaman dengan konten berdasarkan pdf yang sudah di ttd
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        canvas.drawBitmap(drawable.getBitmap(), 0, 0, null);

        document.finishPage(page);

        // menyimpan file dengan format tanggal
        Date date=new Date();
        String filename = "signed_"+date.toString();
        sharedFilename = filename;

        String root = Environment.getExternalStorageDirectory().toString();
        String targetPdf = "sdcard/Download/"+filename+".pdf";
        File filePath = new File(targetPdf);

        // jika proses tidak mengalami ganguan
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done Create Pdf", Toast.LENGTH_LONG).show();

            // jika proses mengalami kesalahan, makan akan kluar toast "something wrong"
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();

    }

}
