package com.example.jc.eatery_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.example.jc.eatery_android.NetworkUtils.NetworkUtilities;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListAdapter.ListAdapterOnClickHandler{

    public RecyclerView mRecyclerView;
    public ArrayList<CafeteriaModel> cafeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ProcessJson().execute("");





    }

    @Override
    public void onClick(int position) {
        Toast.makeText(this,""+cafeList.size(),Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this,MenuActivity.class);

        intent.putExtra("testData", cafeList);

        startActivity(intent);
    }

    public class ProcessJson extends AsyncTask<String, Void, ArrayList<CafeteriaModel>>{


        @Override
        protected ArrayList<CafeteriaModel> doInBackground(String... params) {


            cafeList = NetworkUtilities.getJson();
            return cafeList;


        }

        @Override
        protected void onPostExecute(ArrayList<CafeteriaModel> result) {
            super.onPostExecute(result);

            mRecyclerView = findViewById(R.id.cafe_list);

            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false);
            mRecyclerView.setLayoutManager(layoutManager);

            ListAdapter listAdapter = new ListAdapter(getApplicationContext(), MainActivity.this,result.size());
            mRecyclerView.setAdapter(listAdapter);


        }
    }
}
