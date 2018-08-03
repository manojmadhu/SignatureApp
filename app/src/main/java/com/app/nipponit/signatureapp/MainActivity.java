package com.app.nipponit.signatureapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    Dialog dialog;
    LinearLayout mContent;
    SignatureView mSignature;
    Button btnClear,btnCancel,btnSave;
    Bitmap signatureBitmap;
    File myDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setContentView(new SignatureMainLayout(this));


        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCancelable(true);

        Button btnsign = (Button)findViewById(R.id.btnsign);
        btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_action();
            }
        });

    }


    public void dialog_action(){
        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new SignatureView(getApplicationContext());
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        btnClear = dialog.findViewById(R.id.clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignature.clearSignature();
            }
        });

        btnCancel = dialog.findViewById(R.id.cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
                dialog.dismiss();
            }
        });

        btnSave = dialog.findViewById(R.id.getsign);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureBitmap = mSignature._getSignature();
                SaveSignature();
            }
        });

        dialog.show();
    }


    private void SaveSignature(){
        String root = Environment.getExternalStorageDirectory().toString();
        myDir = new File(root+"/Signature");

        if (Build.VERSION.SDK_INT >= 23) {
            isStoragePermissionGranted();
        }else
        {
            if(!myDir.exists()){
                myDir.mkdirs();
            }
            Save();
        }
    }

    private void Save()
    {
        String fileName = "Signature.png";
        File file = new File(myDir,fileName);

        if(file.exists()){
            file.delete();
        }

        try{
            FileOutputStream fos = new FileOutputStream(file);
            signatureBitmap.compress(Bitmap.CompressFormat.PNG,90,fos);
            Toast.makeText(this,"Signature Saved Successfully.",Toast.LENGTH_SHORT).show();
            mSignature.clearSignature();
            fos.flush();
            fos.close();
        }catch (Exception ex){
            Log.w(ex.getMessage(),"error");
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Save();
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            mContent.setDrawingCacheEnabled(true);
            if(!myDir.exists()){
                myDir.mkdirs();
            }
            Save();
        }
        else
        {
            Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
        }
    }

}
