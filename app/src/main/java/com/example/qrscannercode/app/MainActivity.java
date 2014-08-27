package com.example.qrscannercode.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.app.FragmentTransaction;

import com.example.qrscannercode.app.ScannerAPI.IntentIntegrator;
import com.example.qrscannercode.app.ScannerAPI.IntentResult;

public class MainActivity extends ActionBarActivity {

    private String[] mOptions;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private int state = 0;
    private String urlString = "";

    public static final int REQUEST_CODE = 0x0000c0de;

    public void onSaveInstanceState(Bundle savedInstanceState) {

        if (urlString != null) {
            savedInstanceState.putString("CONTENT", urlString);
        }
        savedInstanceState.putInt("STATE", state);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOptions = getResources().getStringArray(R.array.options_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState != null) {
            state = savedInstanceState.getInt("STATE");
            urlString = savedInstanceState.getString("CONTENT");

            savedInstanceState.clear();
        }

        selectItem(state);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        if (position == 0) {

            state = 0;

            QRScannerFragment rFragment = new QRScannerFragment();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, rFragment);
            ft.commit();
            mDrawerLayout.closeDrawer(mDrawerList);
        }

        else if (position == 1) {

            state = 1;

            Fragment dataFragment = new QRDataFragment();

            Bundle args = new Bundle();
            args.putString("CONTENT", urlString);
            dataFragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, dataFragment).commit();

            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    public void buttonClick(View view) {

        IntentIntegrator mIntent = new IntentIntegrator(this);
        mIntent.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            if (requestCode == REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    String contents = intent.getStringExtra("SCAN_RESULT"); // https://portal.pushstrength.com/api/v1/exercises
                    String formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
                    byte[] rawBytes = intent.getByteArrayExtra("SCAN_RESULT_BYTES");
                    int intentOrientation = intent.getIntExtra("SCAN_RESULT_ORIENTATION", Integer.MIN_VALUE);

                    urlString = intent.getStringExtra("SCAN_RESULT");
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
