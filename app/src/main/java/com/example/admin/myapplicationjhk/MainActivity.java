package com.example.admin.myapplicationjhk;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.admin.myapplicationjhk.databinding.ActivityMainBinding;
import com.example.admin.myapplicationjhk.helpers.MyResultReceiver;
import com.example.admin.myapplicationjhk.helpers.OnItemClickListener;
import com.example.admin.myapplicationjhk.helpers.RecyclerItemClickListener;
import com.example.admin.myapplicationjhk.model.DBHelper;
import com.example.admin.myapplicationjhk.model.DBManager;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MyResultReceiver.Receiver {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mManager;
    private MyResultReceiver mReceiver;
    ActivityMainBinding activityMainBinding;
    Intent intent;

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        ArrayList<ShortWeather> listForRecView = resultData.getParcelableArrayList("list");
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mManager);
        mAdapter = new CustomAdapter(listForRecView, this);
        activityMainBinding.recycleview.setAdapter(mAdapter);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView viewById = (TextView) view.findViewById(R.id.textNameDay);
                        Log.d("Problem", viewById.getText().toString());
                        Intent intent = new Intent(MainActivity.this, ActivityExtraWeather.class);
                        intent.putExtra("dayOfWeek", viewById.getText());
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    public void onClickCreate(View view) {
        intent = MyIntentService.getIntent(this);
        intent.putExtra("receiverTag", mReceiver);
        startService(intent);
    }

    public void refreshData(View view) {
        new DBHelper(this).getWritableDatabase().execSQL("DROP TABLE IF EXISTS Weathers");
        new DBHelper(this).getWritableDatabase().execSQL("create table Weathers(id integer primary key, day text, apparentTemperatureHigh text, " +
                "apparentTemperatureLow text, cloudCover text, pressure text, temperature text)");
        startService(intent);
    }
}
