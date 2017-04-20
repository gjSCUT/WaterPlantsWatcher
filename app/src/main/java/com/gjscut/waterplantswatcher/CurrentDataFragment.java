package com.gjscut.waterplantswatcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gjscut.waterplantswatcher.model.ActivatedCarbonPool;
import com.gjscut.waterplantswatcher.model.ChlorineAddPool;
import com.gjscut.waterplantswatcher.model.CoagulatePool;
import com.gjscut.waterplantswatcher.model.CombinedWell;
import com.gjscut.waterplantswatcher.model.DepositPool;
import com.gjscut.waterplantswatcher.model.DistributeWell;
import com.gjscut.waterplantswatcher.model.OzonePoolAdvance;
import com.gjscut.waterplantswatcher.model.OzonePoolMain;
import com.gjscut.waterplantswatcher.model.Process;
import com.gjscut.waterplantswatcher.model.Pump;
import com.gjscut.waterplantswatcher.model.PumpRoomFirst;
import com.gjscut.waterplantswatcher.model.PumpRoomOut;
import com.gjscut.waterplantswatcher.model.PumpRoomSecond;
import com.gjscut.waterplantswatcher.model.SandLeachPool;
import com.gjscut.waterplantswatcher.model.SuctionWell;

import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CurrentDataFragment extends Fragment {
    private final Logger logger = Logger.getLogger("CurrentDataFragment");
    final DecimalFormat decimalFormat = new DecimalFormat(".000");
    final RecyclerView.Adapter mAdapter = new PumpAdapter();
    private Context mContext;
    private Unbinder unbinder;
    private Pump[] mDatas = {};
    private ScheduledExecutorService service;

    @BindView(R.id.processes_progress)
    ProgressBar mProgressView;
    @BindView(R.id.processes_form)
    View mFormView;
    @BindView(R.id.processes_error)
    View mErrorView;

    @BindView(R.id.phInTv) TextView phInTv;
    @BindView(R.id.waterTemperInTv) TextView waterTemperInTv;
    @BindView(R.id.turbidityInTv) TextView turbidityInTv;
    @BindView(R.id.amlN2InTv) TextView amlN2InTv;
    @BindView(R.id.codInTv) TextView codInTv;
    @BindView(R.id.tocInTv) TextView tocInTv;
    @BindView(R.id.flowInTv) TextView flowInTv;
    @BindView(R.id.phOutTv) TextView phOutTv;
    @BindView(R.id.waterTemperOutTv) TextView waterTemperOutTv;
    @BindView(R.id.turbidityOutTv) TextView turbidityOutTv;
    @BindView(R.id.amlN2OutTv) TextView amlN2OutTv;
    @BindView(R.id.codOutTv) TextView codOutTv;
    @BindView(R.id.tocOutTv) TextView tocOutTv;
    @BindView(R.id.flowOutTv) TextView flowOutTv;

    @BindView(R.id.paramTv) View paramTv;
    @BindView(R.id.zoomAmountTv) View zoomAmountTv;
    @BindView(R.id.alumAmountTv) View alumAmountTv;
    @BindView(R.id.chlorineAmountTv) View chlorineAmountTv;
    @BindView(R.id.pump_list) View mRecyclerView;
    @BindView(R.id.zoomGridLayout) View zoomGridLayout;
    @BindView(R.id.alumGridLayout) View alumGridLayout;
    @BindView(R.id.chlorineGridLayout) View chlorineGridLayout;
    @BindView(R.id.pumpGridLayout) View pumpGridLayout;

    private String type;
    private Map<String, Pair<View, View>> classGridTextMap;
    private boolean isFirst;

    public CurrentDataFragment() {
        service = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_data, container, false);
        unbinder = ButterKnife.bind(this, view);

        ((RecyclerView) mRecyclerView).setLayoutManager(new LinearLayoutManager(mContext));
        ((RecyclerView) mRecyclerView).setAdapter(mAdapter);

        type = getArguments().getString("type");
        isFirst = true;

        classGridTextMap = new HashMap<>();
        classGridTextMap.put(OzonePoolMain.class.toString(), new Pair<>(zoomGridLayout, zoomAmountTv));
        classGridTextMap.put(OzonePoolAdvance.class.toString(), new Pair<>(zoomGridLayout, zoomAmountTv));
        classGridTextMap.put(ChlorineAddPool.class.toString(), new Pair<>(chlorineGridLayout, chlorineAmountTv));
        classGridTextMap.put(CoagulatePool.class.toString(), new Pair<>(alumGridLayout, alumAmountTv));
        classGridTextMap.put(PumpRoomFirst.class.toString(), new Pair<>(pumpGridLayout, mRecyclerView));
        classGridTextMap.put(PumpRoomSecond.class.toString(), new Pair<>(pumpGridLayout, mRecyclerView));
        classGridTextMap.put(PumpRoomOut.class.toString(), new Pair<>(pumpGridLayout, mRecyclerView));

        return view;
    }


    private void getRealTimeDates() {
        if (isFirst) {
            showProgress(true);
        }
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Observable observable;
                if (type.equals(ActivatedCarbonPool.class.toString())) {
                    observable = NetHelper.api(mContext).getActivatedCarbonPool(1, "-createTime");
                } else if (type.equals(ChlorineAddPool.class.toString())) {
                    observable = NetHelper.api(mContext).getChlorineAddPool(1, "-createTime");
                } else if (type.equals(CoagulatePool.class.toString())) {
                    observable = NetHelper.api(mContext).getCoagulatePool(1, "-createTime");
                } else if (type.equals(CombinedWell.class.toString())) {
                    observable = NetHelper.api(mContext).getCombinedWell(1, "-createTime");
                } else if (type.equals(DepositPool.class.toString())) {
                    observable = NetHelper.api(mContext).getDepositPool(1, "-createTime");
                } else if (type.equals(DistributeWell.class.toString())) {
                    observable = NetHelper.api(mContext).getDistributeWell(1, "-createTime");
                } else if (type.equals(OzonePoolAdvance.class.toString())) {
                    observable = NetHelper.api(mContext).getOzonePoolAdvance(1, "-createTime");
                } else if (type.equals(OzonePoolMain.class.toString())) {
                    observable = NetHelper.api(mContext).getOzonePoolMain(1, "-createTime");
                } else if (type.equals(PumpRoomFirst.class.toString())) {
                    observable = NetHelper.api(mContext).getPumpRoomFirst(1, "-createTime");
                } else if (type.equals(PumpRoomSecond.class.toString())) {
                    observable = NetHelper.api(mContext).getPumpRoomSecond(1, "-createTime");
                } else if (type.equals(PumpRoomOut.class.toString())) {
                    observable = NetHelper.api(mContext).getPumpRoomOut(1, "-createTime");
                } else if (type.equals(SandLeachPool.class.toString())) {
                    observable = NetHelper.api(mContext).getSandLeachPool(1, "-createTime");
                } else if (type.equals(SuctionWell.class.toString())) {
                    observable = NetHelper.api(mContext).getSuctionWell(1, "-createTime");
                } else {
                    service.shutdown();
                    return;
                }
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<Process>>() {
                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                if (isFirst) {
                                    isFirst = false;
                                    showProgress(false);
                                }
                                if (e instanceof SocketTimeoutException) {
                                    service.shutdown();
                                }
                                mErrorView.setVisibility(View.VISIBLE);
                                mFormView.setVisibility(View.GONE);
                                mProgressView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onNext(List<Process> processList) {
                                if (service.isShutdown() || processList.isEmpty()) {
                                    return;
                                }
                                logger.info(type + " get result success. Result size = " + processList.size());
                                Process p = processList.get(0);
                                handleNext(p);
                            }

                            @Override
                            public void onCompleted() {
                                if (isFirst) {
                                    isFirst = false;
                                    showProgress(false);
                                }
                            }
                        });
            }
        }, 0, 10, TimeUnit.SECONDS);
    }


    @Override
    public void onStart() {
        super.onStart();
        getRealTimeDates();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        service.shutdown();
    }
    
    public void handleNext(Process p) {
        phInTv.setText(decimalFormat.format(p.phIn));
        waterTemperInTv.setText(decimalFormat.format(p.waterTemperIn));
        turbidityInTv.setText(decimalFormat.format(p.turbidityIn));
        amlN2InTv.setText(decimalFormat.format(p.amlN2In));
        codInTv.setText(decimalFormat.format(p.codIn));
        tocInTv.setText(decimalFormat.format(p.tocIn));
        flowInTv.setText(decimalFormat.format(p.flowIn));
        phOutTv.setText(decimalFormat.format(p.phOut));
        waterTemperOutTv.setText(decimalFormat.format(p.waterTemperOut));
        turbidityOutTv.setText(decimalFormat.format(p.turbidityOut));
        amlN2OutTv.setText(decimalFormat.format(p.amlN2Out));
        codOutTv.setText(decimalFormat.format(p.codOut));
        tocOutTv.setText(decimalFormat.format(p.tocOut));
        flowOutTv.setText(decimalFormat.format(p.flowOut));
        try {
            Iterator<String> iterator = classGridTextMap.keySet().iterator();
            boolean isShow = false;
            View showView = null;
            while(iterator.hasNext()) {
                String className = iterator.next();
                if (type.equals(className)) {
                    isShow = true;
                    showView = classGridTextMap.get(className).first;
                    showView.setVisibility(View.VISIBLE);
                    if (p instanceof ChlorineAddPool) {
                        ChlorineAddPool chlorineAddPool = (ChlorineAddPool) p;
                        ((TextView) classGridTextMap.get(className).second)
                                .setText(decimalFormat.format(chlorineAddPool.chlorineAmount));
                    } else if (p instanceof CoagulatePool) {
                        CoagulatePool coagulatePool = (CoagulatePool) p;
                        ((TextView) classGridTextMap.get(className).second)
                                .setText(decimalFormat.format(coagulatePool.alumAmount));
                    } else if (p instanceof OzonePoolAdvance) {
                        OzonePoolAdvance ozonePoolAdvance = (OzonePoolAdvance) p;
                        ((TextView) classGridTextMap.get(className).second)
                                .setText(decimalFormat.format(ozonePoolAdvance.zoneAmount));
                    } else if (p instanceof OzonePoolMain) {
                        OzonePoolMain ozonePoolMain = (OzonePoolMain) p;
                        ((TextView) classGridTextMap.get(className).second)
                                .setText(decimalFormat.format(ozonePoolMain.zoneAmount));
                    } else if (p instanceof PumpRoomFirst) {
                        PumpRoomFirst pumpRoomFirst = (PumpRoomFirst) p;
                        mDatas = pumpRoomFirst.pumps;
                        mAdapter.notifyDataSetChanged();
                    } else if (p instanceof PumpRoomSecond) {
                        PumpRoomSecond pumpRoomSecond = (PumpRoomSecond) p;
                        mDatas = pumpRoomSecond.pumps;
                        mAdapter.notifyDataSetChanged();
                    } else if (p instanceof PumpRoomOut) {
                        PumpRoomOut pumpRoomOut = (PumpRoomOut) p;
                        mDatas = pumpRoomOut.pumps;
                        mAdapter.notifyDataSetChanged();
                    }
                } else if (classGridTextMap.get(className).first != showView){
                    classGridTextMap.get(className).first.setVisibility(View.GONE);
                }
            }
            if (isShow) {
                paramTv.setVisibility(View.VISIBLE);
            } else {
                paramTv.setVisibility(View.GONE);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    class PumpAdapter extends RecyclerView.Adapter<PumpAdapter.MyViewHolder> {

        @Override
        public PumpAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PumpAdapter.MyViewHolder(LayoutInflater.from(
                    mContext).inflate(R.layout.item_pump, parent,
                    false));
        }

        @Override
        public void onBindViewHolder(PumpAdapter.MyViewHolder holder, int position) {
            holder.orderTv.setText(decimalFormat.format(mDatas[position].order));
            holder.frequencyTv.setText(decimalFormat.format(mDatas[position].frequency));
            holder.headTv.setText(decimalFormat.format(mDatas[position].head));
            holder.flowTv.setText(decimalFormat.format(mDatas[position].flow));
        }

        @Override
        public int getItemCount() {
            return mDatas.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.item_pump_order) TextView orderTv;
            @BindView(R.id.item_pump_frequency) TextView frequencyTv;
            @BindView(R.id.item_pump_head) TextView headTv;
            @BindView(R.id.item_pump_flow) TextView flowTv;

            MyViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

}
