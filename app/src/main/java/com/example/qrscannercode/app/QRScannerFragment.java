package com.example.qrscannercode.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qrscannercode.app.ScannerAPI.IntentIntegrator;
import com.example.qrscannercode.app.ScannerAPI.IntentResult;


public class QRScannerFragment extends Fragment
{
    public static final int REQUEST_CODE = 0x0000c0de;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.drawer_list_item_scanner, container, false);
    }

} 