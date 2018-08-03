package com.app.nipponit.signatureapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by manojm on 08/03/2018.
 */

public class SignatureMainLayout extends LinearLayout {

    LinearLayout buttonLayout;
    Menu menu;
    MenuInflater menuInflater;
    SignatureView signatureView;

    public SignatureMainLayout(Context context) {
        super(context);
        View inflater = inflate(context,R.layout.activity_main,null);

        this.setOrientation(LinearLayout.HORIZONTAL);
        signatureView = new SignatureView(context);

        this.addView(inflater);

    }




}
