package com.gjscut.waterplantswatcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class ProcessesActivity extends AppCompatActivity {
    @BindView(R.id.process_list)
    RecyclerView mRecyclerView;
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
        initData();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final RecyclerView.Adapter mAdapter = new HomeAdapter();
        mRecyclerView.setAdapter(mAdapter);

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_down);
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                c.drawBitmap(bitmap, rect, rect, paint);
            }
        });
    }

    class ProcessItem {
        public String name;
        public Class className;

        public ProcessItem(String name, Class className) {
            this.name = name;
            this.className = className;
        }
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

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

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.item_process_name);
            }
        }
    }
}
