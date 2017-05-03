package com.gjscut.waterplantswatcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.gjscut.waterplantswatcher.model.ActivatedCarbonPool;
import com.gjscut.waterplantswatcher.model.ChlorineAddPool;
import com.gjscut.waterplantswatcher.model.CoagulatePool;
import com.gjscut.waterplantswatcher.model.CombinedWell;
import com.gjscut.waterplantswatcher.model.DepositPool;
import com.gjscut.waterplantswatcher.model.DistributeWell;
import com.gjscut.waterplantswatcher.model.OzonePoolAdvance;
import com.gjscut.waterplantswatcher.model.OzonePoolMain;
import com.gjscut.waterplantswatcher.model.Process;
import com.gjscut.waterplantswatcher.model.PumpRoomFirst;
import com.gjscut.waterplantswatcher.model.PumpRoomOut;
import com.gjscut.waterplantswatcher.model.PumpRoomSecond;
import com.gjscut.waterplantswatcher.model.SandLeachPool;
import com.gjscut.waterplantswatcher.model.SuctionWell;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HistoryDataFragment extends Fragment implements OnChartValueSelectedListener {
    private final Logger logger = Logger.getLogger("HistoryDataFragment");
    private final int HISTORY_LIMIT = 360;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat formatter = new SimpleDateFormat("HH");
    private Unbinder unbinder;
    private Context mContext;
    private String type;
    private String field;
    private Map<String, List<Field>> fieldMap = new HashMap<>();
    private boolean isFirst = true;
    private List<Process> processList;

    @BindView(R.id.processes_progress)
    ProgressBar mProgressView;
    @BindView(R.id.processes_form)
    View mFormView;

    @BindView(R.id.historyChart) LineChart mChart;
    @BindView(R.id.historySpinner)
    MaterialSpinner mSpinner;

    public HistoryDataFragment() {
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        this.mContext = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString("type");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_data, container, false);
        unbinder = ButterKnife.bind(this, view);

        try {
            fieldMap.put("水流量", Arrays.asList(Process.class.getField("flowIn"), Process.class.getField("flowOut")));
            fieldMap.put("Ph值", Arrays.asList(Process.class.getField("phIn"), Process.class.getField("phOut")));
            fieldMap.put("水温", Arrays.asList(Process.class.getField("waterTemperIn"), Process.class.getField("waterTemperOut")));
            fieldMap.put("浊度", Arrays.asList(Process.class.getField("turbidityIn"), Process.class.getField("turbidityOut")));
            fieldMap.put("氨氮", Arrays.asList(Process.class.getField("amlN2In"), Process.class.getField("amlN2Out")));
            fieldMap.put("COD", Arrays.asList(Process.class.getField("codIn"), Process.class.getField("codOut")));
            fieldMap.put("TOC", Arrays.asList(Process.class.getField("tocIn"), Process.class.getField("tocOut")));

            fieldMap.put("预臭氧投加量", Arrays.asList(OzonePoolAdvance.class.getField("zoneAmount")));
            fieldMap.put("主臭氧投加量", Arrays.asList(OzonePoolMain.class.getField("zoneAmount")));
            fieldMap.put("矾投加量", Arrays.asList(CoagulatePool.class.getField("alumAmount")));
            fieldMap.put("氯投加量", Arrays.asList(ChlorineAddPool.class.getField("chlorineAmount")));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(true);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawBorders(false);
        mChart.getAxisLeft().setEnabled(true);
        mChart.getAxisLeft().setDrawAxisLine(false);
        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setDrawLabels(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        mChart.setAutoScaleMinMaxEnabled(true);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setYEntrySpace(10);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        List<String> spinnerList = new ArrayList<>();
        spinnerList.add("水流量");
        spinnerList.add("Ph值");
        spinnerList.add("水温");
        spinnerList.add("浊度");
        spinnerList.add("氨氮");
        spinnerList.add("COD");
        spinnerList.add("TOC");

        mSpinner.setItems(spinnerList);
        mSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                field = item.toString();
                getHistoryDates();
            }

        });

        if (!spinnerList.isEmpty())
            field = spinnerList.get(0);
        getHistoryDates();

        return view;
    }

    private void getHistoryDates() {
        if (!isFirst) {
            handleNext(processList);
            return;
        }
        Observable observable = null;
        if (type.equals(ActivatedCarbonPool.class.toString())) {
            observable = NetHelper.api(mContext).getActivatedCarbonPool(HISTORY_LIMIT, "-createTime");
        } else if (type.equals(ChlorineAddPool.class.toString())) {
            observable = NetHelper.api(mContext).getChlorineAddPool(HISTORY_LIMIT, "-createTime");
            // spinnerList.add("氯投加量");
        } else if (type.equals(CoagulatePool.class.toString())) {
            observable = NetHelper.api(mContext).getCoagulatePool(HISTORY_LIMIT, "-createTime");
            // spinnerList.add("矾投加量");
        } else if (type.equals(CombinedWell.class.toString())) {
            observable = NetHelper.api(mContext).getCombinedWell(HISTORY_LIMIT, "-createTime");
        } else if (type.equals(DepositPool.class.toString())) {
            observable = NetHelper.api(mContext).getDepositPool(HISTORY_LIMIT, "-createTime");
        } else if (type.equals(DistributeWell.class.toString())) {
            observable = NetHelper.api(mContext).getDistributeWell(HISTORY_LIMIT, "-createTime");
        } else if (type.equals(OzonePoolAdvance.class.toString())) {
            observable = NetHelper.api(mContext).getOzonePoolAdvance(HISTORY_LIMIT, "-createTime");
            // spinnerList.add("预臭氧投加量");
        } else if (type.equals(OzonePoolMain.class.toString())) {
            observable = NetHelper.api(mContext).getOzonePoolMain(HISTORY_LIMIT, "-createTime");
            // spinnerList.add("主臭氧投加量");
        } else if (type.equals(PumpRoomFirst.class.toString())) {
            observable = NetHelper.api(mContext).getPumpRoomFirst(HISTORY_LIMIT, "-createTime");
        } else if (type.equals(PumpRoomSecond.class.toString())) {
            observable = NetHelper.api(mContext).getPumpRoomSecond(HISTORY_LIMIT, "-createTime");
        } else if (type.equals(PumpRoomOut.class.toString())) {
            observable = NetHelper.api(mContext).getPumpRoomOut(HISTORY_LIMIT, "-createTime");
        } else if (type.equals(SandLeachPool.class.toString())) {
            observable = NetHelper.api(mContext).getSandLeachPool(HISTORY_LIMIT, "-createTime");
        } else if (type.equals(SuctionWell.class.toString())) {
            observable = NetHelper.api(mContext).getSuctionWell(HISTORY_LIMIT, "-createTime");
        } else {
            logger.warning("type error");
        }

        if (observable != null) {
            showProgress(true);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Process>>() {
                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            logger.info(type + " get history result error ");
                            showProgress(false);
                        }

                        @Override
                        public void onNext(List<Process> processList) {
                            if (processList.isEmpty()) {
                                return;
                            }
                            logger.info(type + " get history result success. Result size = " + processList.size());
                            handleNext(processList);
                            HistoryDataFragment.this.processList = processList;
                        }

                        @Override
                        public void onCompleted() {
                            showProgress(false);
                            isFirst = false;
                        }
                    });
        }
    }

    public void handleNext(List<Process> processes) {
        mChart.resetTracking();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        ArrayList<Entry> valuesIn = new ArrayList<>();
        ArrayList<Entry> valuesOut = new ArrayList<>();

        float xValue = Float.valueOf(formatter.format(new Date()));
        int size = 36;
        for (int i = processes.size() > size ? 0 : size - processes.size(); i < size; i++) {
            float pos;
            if (processes.size() > size) {
                pos = (processes.size() - 1) * i / (size - 1);
            } else {
                pos = i + processes.size() - size;
            }
            Process process = processes.get((int)pos);
            try {
                /*Date createTime = df.parse(process.createTime);
                float xValue = Float.valueOf(formatter.format(createTime));*/
                valuesIn.add(new Entry(i, fieldMap.get(field).get(0).getFloat(process)));
                valuesOut.add(new Entry(i, fieldMap.get(field).get(1).getFloat(process)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } /*catch (ParseException e) {
                e.printStackTrace();
            }*/
        }

        LineDataSet lineDataSetIn = new LineDataSet(valuesIn, "进水指标");
        lineDataSetIn.setLineWidth(2.5f);
        lineDataSetIn.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        lineDataSetIn.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSetIn.setDrawValues(false);
        dataSets.add(lineDataSetIn);

        LineDataSet lineDataSetOut = new LineDataSet(valuesOut, "出水指标");
        lineDataSetOut.setLineWidth(2.5f);
        lineDataSetOut.setColor(ColorTemplate.MATERIAL_COLORS[2]);
        lineDataSetOut.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSetOut.setDrawValues(false);
        dataSets.add(lineDataSetOut);

        if (field == null) {
            return;
        } else if (field.equals("水流量")) {
            for (ILineDataSet iSet : dataSets) {
                LineDataSet set = (LineDataSet) iSet;
                set.setDrawFilled(true);
                set.setDrawCircles(false);
            }
        } else {
            for (ILineDataSet iSet : dataSets) {
                LineDataSet set = (LineDataSet) iSet;
                set.setDrawFilled(false);
                set.setDrawCircles(false);
            }
        }
        LineData data = new LineData(dataSets);
        mChart.setData(data);
        mChart.invalidate();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (mContext == null) {
            return;
        }
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
