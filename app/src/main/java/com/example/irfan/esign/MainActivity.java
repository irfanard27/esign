package com.example.irfan.esign;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final int FILE_PICKER_REQUEST_CODE = 1;

    // function yang di panggil ketika aplikasi di launch pertama kali
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // function yang di gunakan ketika button new signature di klik
    public void newSignature(View v){

        // kode untuk berpindah ke activity newSignatureActivity
        Intent intent = new Intent(this, NewSignatureActivity.class);
        startActivity(intent);
    }

    // function yang di gunakan ketika button open pdf di klik
    public void openFilePicker(View v) {

        // memanggil File picker
        new MaterialFilePicker()
                .withActivity(this)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .withTitle("Load PDF file")
                .start();
    }

    // function yang di gunakan ketika seleasi melakukan pemilihan file pdf
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            // mendapatkan path lokasi file pdf
            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            // menyiapkan class pdf editor
            Intent intent = new Intent(this,PdfEditorActivity.class);

            // file path dari pdf, di kirim ketika berpindah ke pdf editor
            intent.putExtra("filename",path);

            // berpindah ke pdf edito activity
            startActivity(intent);
        }
    }
}
