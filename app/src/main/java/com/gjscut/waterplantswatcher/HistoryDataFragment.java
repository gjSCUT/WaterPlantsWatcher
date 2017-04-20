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
import android.widget.SeekBar;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
    final DecimalFormat decimalFormat = new DecimalFormat(".000");
    private Unbinder unbinder;
    private Context mContext;
    private String type;

    @BindView(R.id.processes_progress)
    ProgressBar mProgressView;
    @BindView(R.id.processes_form)
    View mFormView;

    @BindView(R.id.historyChart) LineChart mChart;

    public HistoryDataFragment() {
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.mContext = context;
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

        getHistoryDates();

        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawGridBackground(true);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawBorders(false);

        mChart.getAxisLeft().setEnabled(true);
        mChart.getAxisLeft().setDrawAxisLine(true);
        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getXAxis().setDrawGridLines(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        return view;
    }

    private void getHistoryDates() {
        Observable observable = null;
        if (type.equals(ActivatedCarbonPool.class.toString())) {
            observable = NetHelper.api(mContext).getActivatedCarbonPool(8640, "-createTime");
        } else if (type.equals(ChlorineAddPool.class.toString())) {
            observable = NetHelper.api(mContext).getChlorineAddPool(8640, "-createTime");
        } else if (type.equals(CoagulatePool.class.toString())) {
            observable = NetHelper.api(mContext).getCoagulatePool(8640, "-createTime");
        } else if (type.equals(CombinedWell.class.toString())) {
            observable = NetHelper.api(mContext).getCombinedWell(8640, "-createTime");
        } else if (type.equals(DepositPool.class.toString())) {
            observable = NetHelper.api(mContext).getDepositPool(8640, "-createTime");
        } else if (type.equals(DistributeWell.class.toString())) {
            observable = NetHelper.api(mContext).getDistributeWell(8640, "-createTime");
        } else if (type.equals(OzonePoolAdvance.class.toString())) {
            observable = NetHelper.api(mContext).getOzonePoolAdvance(8640, "-createTime");
        } else if (type.equals(OzonePoolMain.class.toString())) {
            observable = NetHelper.api(mContext).getOzonePoolMain(8640, "-createTime");
        } else if (type.equals(PumpRoomFirst.class.toString())) {
            observable = NetHelper.api(mContext).getPumpRoomFirst(8640, "-createTime");
        } else if (type.equals(PumpRoomSecond.class.toString())) {
            observable = NetHelper.api(mContext).getPumpRoomSecond(8640, "-createTime");
        } else if (type.equals(PumpRoomOut.class.toString())) {
            observable = NetHelper.api(mContext).getPumpRoomOut(8640, "-createTime");
        } else if (type.equals(SandLeachPool.class.toString())) {
            observable = NetHelper.api(mContext).getSandLeachPool(8640, "-createTime");
        } else if (type.equals(SuctionWell.class.toString())) {
            observable = NetHelper.api(mContext).getSuctionWell(8640, "-createTime");
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
                            showProgress(false);
                        }

                        @Override
                        public void onNext(List<Process> processList) {
                            if (processList.isEmpty()) {
                                return;
                            }
                            logger.info(type + " get history result success. Result size = " + processList.size());
                            handleNext(processList);
                        }

                        @Override
                        public void onCompleted() {
                            showProgress(false);
                            mChart.animateY(3000);
                        }
                    });
        }
    }

    public void handleNext(List<Process> processes) {
        mChart.resetTracking();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        ArrayList<Entry> valuesIn = new ArrayList<>();
        ArrayList<Entry> valuesOut = new ArrayList<>();

        for (int i = 0; i < processes.size(); i++) {
            valuesIn.add(new Entry(i, processes.get(i).phIn));
            valuesOut.add(new Entry(i, processes.get(i).phOut));
        }

        LineDataSet lineDataSetIn = new LineDataSet(valuesIn, "DataSet 1");
        lineDataSetIn.setLineWidth(2.5f);
        lineDataSetIn.setCircleRadius(4f);
        lineDataSetIn.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        lineDataSetIn.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        lineDataSetIn.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSets.add(lineDataSetIn);

        LineDataSet lineDataSetOut = new LineDataSet(valuesIn, "DataSet 2");
        lineDataSetOut.setLineWidth(2.5f);
        lineDataSetOut.setCircleRadius(4f);
        lineDataSetOut.setColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        lineDataSetOut.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        lineDataSetOut.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSets.add(lineDataSetOut);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
