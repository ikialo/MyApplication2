package com.metrolinq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.metrolinq.isaac.myapplication.MyAdapter;
import com.metrolinq.isaac.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<String> myDataset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);




        myDataset = new ArrayList<>();

        myDataset.add("Waikele Bus Stop");
        myDataset.add("Gerehu Stage 2 Main Bus Stop");
        myDataset.add("Rainbow Main Bus Stop");
        myDataset.add("Ensisi LTI Bus Stop");
        myDataset.add("Waigani Main Bus Stop");
        myDataset.add("Sir Hubert Murray Bus Stop");
        myDataset.add("Defence Haus Town");
        myDataset.add("Cuthberthson Haus Town");



        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this, myDataset);
        recyclerView.setAdapter(mAdapter);


    }
}
