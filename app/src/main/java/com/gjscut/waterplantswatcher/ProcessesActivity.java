package com.gjscut.waterplantswatcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gjscut.waterplantswatcher.model.ActivatedCarbonPool;
import com.gjscut.waterplantswatcher.model.ChlorineAddPool;
import com.gjscut.waterplantswatcher.model.CoagulatePool;
import com.gjscut.waterplantswatcher.model.CombinedWell;
import com.gjscut.waterplantswatcher.model.DepositPool;
import com.gjscut.waterplantswatcher.model.DistributeWell;
import com.gjscut.waterplantswatcher.model.OzonePoolAdvance;
import com.gjscut.waterplantswatcher.model.OzonePoolMain;
import com.gjscut.waterplantswatcher.model.PumpRoomFirst;
import com.gjscut.waterplantswatcher.model.PumpRoomOut;
import com.gjscut.waterplantswatcher.model.PumpRoomSecond;
import com.gjscut.waterplantswatcher.model.SandLeachPool;
import com.gjscut.waterplantswatcher.model.SuctionWell;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProcessesActivity extends AppCompatActivity {
    @BindView(R.id.process_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.processImage)
    Button mProcessImage;

    private List<ProcessItem> mDatas;

    protected void initData() {
        mDatas = new ArrayList<>();
        mDatas.add(new ProcessItem("提升泵房1", PumpRoomFirst.class));
        mDatas.add(new ProcessItem("配水井", DistributeWell.class));
        mDatas.add(new ProcessItem("预臭氧接触池", OzonePoolAdvance.class));
        mDatas.add(new ProcessItem("混凝池", CoagulatePool.class));
        mDatas.add(new ProcessItem("沉淀池", DepositPool.class));
        mDatas.add(new ProcessItem("砂滤池", SandLeachPool.class));
        mDatas.add(new ProcessItem("结合井", CombinedWell.class));
        mDatas.add(new ProcessItem("提升泵房2", PumpRoomSecond.class));
        mDatas.add(new ProcessItem("主臭氧接触池", OzonePoolMain.class));
        mDatas.add(new ProcessItem("生物活性炭滤池", ActivatedCarbonPool.class));
        mDatas.add(new ProcessItem("清水池", ChlorineAddPool.class));
        mDatas.add(new ProcessItem("吸水井", SuctionWell.class));
        mDatas.add(new ProcessItem("二泵房", PumpRoomOut.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processes);
        ButterKnife.bind(this);
        initData();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("水处理流程");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final RecyclerView.Adapter mAdapter = new ProcessAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mProcessImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProcessesActivity.this, SummaryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        getMenuInflater().inflate(R.menu.process_menu, menu);
        return true;
    }

    //and this to handle actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SharedPreferences.Editor editor = getSharedPreferences("water_plants", Context.MODE_PRIVATE).edit();
            editor.putString("accessToken", "");
            editor.putString("tokenType", "");
            editor.putString("refreshToken", "");
            editor.putInt("expiresIn", 0);
            editor.apply();
            Intent intent = new Intent(ProcessesActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_contact) {
            Intent intent = new Intent(this, ContactActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class ProcessItem {
        String name;
        Class className;

        ProcessItem(String name, Class className) {
            this.name = name;
            this.className = className;
        }
    }

    class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    ProcessesActivity.this).inflate(R.layout.item_process, parent,
                    false));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    Intent intent = new Intent(ProcessesActivity.this, DataActivity.class);
                    intent.putExtra("type", mDatas.get(position).className.toString());
                    intent.putExtra("title", mDatas.get(position).name);
                    startActivity(intent);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(mDatas.get(position).name);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv;

            MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.item_process_name);
            }
        }
    }
}
