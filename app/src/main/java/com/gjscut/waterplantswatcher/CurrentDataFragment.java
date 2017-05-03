package com.gjscut.waterplantswatcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Context mContext;
    private Unbinder unbinder;
    private List<Pump> mDatas = new ArrayList<>();
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
    @BindView(R.id.pumpList) View mRecyclerView;

    @BindView(R.id.zoomGridLayout) View zoomGridLayout;
    @BindView(R.id.alumGridLayout) View alumGridLayout;
    @BindView(R.id.chlorineGridLayout) View chlorineGridLayout;
    @BindView(R.id.pumpGridLayout) View pumpGridLayout;

    @BindView(R.id.item_pump_frequency1) TextView pumpFrequency1;
    @BindView(R.id.item_pump_flow1) TextView pumpFlow1;
    @BindView(R.id.item_pump_head1) TextView pumpHead1;
    @BindView(R.id.item_pump_frequency2) TextView pumpFrequency2;
    @BindView(R.id.item_pump_flow2) TextView pumpFlow2;
    @BindView(R.id.item_pump_head2) TextView pumpHead2;
    @BindView(R.id.item_pump_frequency3) TextView pumpFrequency3;
    @BindView(R.id.item_pump_flow3) TextView pumpFlow3;
    @BindView(R.id.item_pump_head3) TextView pumpHead3;
    @BindView(R.id.item_pump_frequency4) TextView pumpFrequency4;
    @BindView(R.id.item_pump_flow4) TextView pumpFlow4;
    @BindView(R.id.item_pump_head4) TextView pumpHead4;
    private String type;
    private Map<String, Pair<View, View>> classGridTextMap;
    private boolean isFirst;

    public CurrentDataFragment() {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_data, container, false);
        unbinder = ButterKnife.bind(this, view);

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
                                logger.info(type + " get current result error ");
                                if (e instanceof SocketTimeoutException) {
                                    service.shutdownNow();
                                    mFormView.setVisibility(View.GONE);
                                    mProgressView.setVisibility(View.GONE);
                                    mErrorView.setVisibility(View.VISIBLE);
                                    return;
                                }
                                if (isFirst) {
                                    isFirst = false;
                                    showProgress(false);
                                }
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
        service = Executors.newSingleThreadScheduledExecutor();
        getRealTimeDates();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        service.shutdownNow();
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

        Field[] fields = Process.class.getFields();
        String message = "";
        for (int i = 0; i < fields.length; i++) {
            String name = fields[i].getName();
            if (name.equals("createTime") || name.equals("updateTIme"))
                continue;
            message = addMessage(name, message, p.valid(name));
            Process.STATUS status = p.valid(name);
        }
        message += "\n";
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
                        message = addMessage("chlorineAmount", message, chlorineAddPool.valid("chlorineAmount"));
                    } else if (p instanceof CoagulatePool) {
                        CoagulatePool coagulatePool = (CoagulatePool) p;
                        ((TextView) classGridTextMap.get(className).second)
                                .setText(decimalFormat.format(coagulatePool.alumAmount));
                        message = addMessage("alumAmount", message, coagulatePool.valid("alumAmount"));
                    } else if (p instanceof OzonePoolAdvance) {
                        OzonePoolAdvance ozonePoolAdvance = (OzonePoolAdvance) p;
                        ((TextView) classGridTextMap.get(className).second)
                                .setText(decimalFormat.format(ozonePoolAdvance.zoneAmount));
                        message = addMessage("ozoneAmount", message, ozonePoolAdvance.valid("ozoneAmount"));
                    } else if (p instanceof OzonePoolMain) {
                        OzonePoolMain ozonePoolMain = (OzonePoolMain) p;
                        ((TextView) classGridTextMap.get(className).second)
                                .setText(decimalFormat.format(ozonePoolMain.zoneAmount));
                        message = addMessage("ozoneAmount", message, ozonePoolMain.valid("ozoneAmount"));
                    } else if (p instanceof PumpRoomFirst) {
                        PumpRoomFirst pumpRoomFirst = (PumpRoomFirst) p;
                        pumpFrequency1.setText(decimalFormat.format(pumpRoomFirst.pumps[0].frequency));
                        pumpHead1.setText(decimalFormat.format(pumpRoomFirst.pumps[0].head));
                        pumpFlow1.setText(decimalFormat.format(pumpRoomFirst.pumps[0].flow));
                        pumpFrequency2.setText(decimalFormat.format(pumpRoomFirst.pumps[1].frequency));
                        pumpHead2.setText(decimalFormat.format(pumpRoomFirst.pumps[1].head));
                        pumpFlow2.setText(decimalFormat.format(pumpRoomFirst.pumps[1].flow));
                        pumpFrequency3.setText(decimalFormat.format(pumpRoomFirst.pumps[2].frequency));
                        pumpHead3.setText(decimalFormat.format(pumpRoomFirst.pumps[2].head));
                        pumpFlow3.setText(decimalFormat.format(pumpRoomFirst.pumps[2].flow));
                        pumpFrequency4.setText(decimalFormat.format(pumpRoomFirst.pumps[3].frequency));
                        pumpHead4.setText(decimalFormat.format(pumpRoomFirst.pumps[3].head));
                        pumpFlow4.setText(decimalFormat.format(pumpRoomFirst.pumps[3].flow));

                        message = addMessage("order " + 0 + " frequency", message, pumpRoomFirst.valid("pumpfrequency", 0));
                        message = addMessage("order " + 0 + " head", message, pumpRoomFirst.valid("pumphead", 0));
                        message = addMessage("order " + 0 + " flow", message, pumpRoomFirst.valid("pumpflow", 0));
                        message = addMessage("order " + 1 + " frequency", message, pumpRoomFirst.valid("pumpfrequency", 1));
                        message = addMessage("order " + 1 + " head", message, pumpRoomFirst.valid("pumphead", 1));
                        message = addMessage("order " + 1 + " flow", message, pumpRoomFirst.valid("pumpflow", 1));
                        message = addMessage("order " + 2 + " frequency", message, pumpRoomFirst.valid("pumpfrequency", 2));
                        message = addMessage("order " + 2 + " head", message, pumpRoomFirst.valid("pumphead", 2));
                        message = addMessage("order " + 2 + " flow", message, pumpRoomFirst.valid("pumpflow", 2));
                        message = addMessage("order " + 3 + " frequency", message, pumpRoomFirst.valid("pumpfrequency", 3));
                        message = addMessage("order " + 3 + " head", message, pumpRoomFirst.valid("pumphead", 3));
                        message = addMessage("order " + 3 + " flow", message, pumpRoomFirst.valid("pumpflow", 3));
                    } else if (p instanceof PumpRoomSecond) {
                        PumpRoomSecond pumpRoomSecond = (PumpRoomSecond) p;
                        pumpFrequency1.setText(decimalFormat.format(pumpRoomSecond.pumps[0].frequency));
                        pumpHead1.setText(decimalFormat.format(pumpRoomSecond.pumps[0].head));
                        pumpFlow1.setText(decimalFormat.format(pumpRoomSecond.pumps[0].flow));
                        pumpFrequency2.setText(decimalFormat.format(pumpRoomSecond.pumps[1].frequency));
                        pumpHead2.setText(decimalFormat.format(pumpRoomSecond.pumps[1].head));
                        pumpFlow2.setText(decimalFormat.format(pumpRoomSecond.pumps[1].flow));
                        pumpFrequency3.setText(decimalFormat.format(pumpRoomSecond.pumps[2].frequency));
                        pumpHead3.setText(decimalFormat.format(pumpRoomSecond.pumps[2].head));
                        pumpFlow3.setText(decimalFormat.format(pumpRoomSecond.pumps[2].flow));
                        pumpFrequency4.setText(decimalFormat.format(pumpRoomSecond.pumps[3].frequency));
                        pumpHead4.setText(decimalFormat.format(pumpRoomSecond.pumps[3].head));
                        pumpFlow4.setText(decimalFormat.format(pumpRoomSecond.pumps[3].flow));

                        message = addMessage("order " + 0 + " frequency", message, pumpRoomSecond.valid("pumpfrequency", 0));
                        message = addMessage("order " + 0 + " head", message, pumpRoomSecond.valid("pumphead", 0));
                        message = addMessage("order " + 0 + " flow", message, pumpRoomSecond.valid("pumpflow", 0));
                        message = addMessage("order " + 1 + " frequency", message, pumpRoomSecond.valid("pumpfrequency", 1));
                        message = addMessage("order " + 1 + " head", message, pumpRoomSecond.valid("pumphead", 1));
                        message = addMessage("order " + 1 + " flow", message, pumpRoomSecond.valid("pumpflow", 1));
                        message = addMessage("order " + 2 + " frequency", message, pumpRoomSecond.valid("pumpfrequency", 2));
                        message = addMessage("order " + 2 + " head", message, pumpRoomSecond.valid("pumphead", 2));
                        message = addMessage("order " + 2 + " flow", message, pumpRoomSecond.valid("pumpflow", 2));
                        message = addMessage("order " + 3 + " frequency", message, pumpRoomSecond.valid("pumpfrequency", 3));
                        message = addMessage("order " + 3 + " head", message, pumpRoomSecond.valid("pumphead", 3));
                        message = addMessage("order " + 3 + " flow", message, pumpRoomSecond.valid("pumpflow", 3));
                    } else if (p instanceof PumpRoomOut) {
                        PumpRoomOut pumpRoomOut = (PumpRoomOut) p;
                        pumpFrequency1.setText(decimalFormat.format(pumpRoomOut.pumps[0].frequency));
                        pumpHead1.setText(decimalFormat.format(pumpRoomOut.pumps[0].head));
                        pumpFlow1.setText(decimalFormat.format(pumpRoomOut.pumps[0].flow));
                        pumpFrequency2.setText(decimalFormat.format(pumpRoomOut.pumps[1].frequency));
                        pumpHead2.setText(decimalFormat.format(pumpRoomOut.pumps[1].head));
                        pumpFlow2.setText(decimalFormat.format(pumpRoomOut.pumps[1].flow));
                        pumpFrequency3.setText(decimalFormat.format(pumpRoomOut.pumps[2].frequency));
                        pumpHead3.setText(decimalFormat.format(pumpRoomOut.pumps[2].head));
                        pumpFlow3.setText(decimalFormat.format(pumpRoomOut.pumps[2].flow));
                        pumpFrequency4.setText(decimalFormat.format(pumpRoomOut.pumps[3].frequency));
                        pumpHead4.setText(decimalFormat.format(pumpRoomOut.pumps[3].head));
                        pumpFlow4.setText(decimalFormat.format(pumpRoomOut.pumps[3].flow));

                        message = addMessage("order " + 0 + " frequency", message, pumpRoomOut.valid("pumpfrequency", 0));
                        message = addMessage("order " + 0 + " head", message, pumpRoomOut.valid("pumphead", 0));
                        message = addMessage("order " + 0 + " flow", message, pumpRoomOut.valid("pumpflow", 0));
                        message = addMessage("order " + 1 + " frequency", message, pumpRoomOut.valid("pumpfrequency", 1));
                        message = addMessage("order " + 1 + " head", message, pumpRoomOut.valid("pumphead", 1));
                        message = addMessage("order " + 1 + " flow", message, pumpRoomOut.valid("pumpflow", 1));
                        message = addMessage("order " + 2 + " frequency", message, pumpRoomOut.valid("pumpfrequency", 2));
                        message = addMessage("order " + 2 + " head", message, pumpRoomOut.valid("pumphead", 2));
                        message = addMessage("order " + 2 + " flow", message, pumpRoomOut.valid("pumpflow", 2));
                        message = addMessage("order " + 3 + " frequency", message, pumpRoomOut.valid("pumpfrequency", 3));
                        message = addMessage("order " + 3 + " head", message, pumpRoomOut.valid("pumphead", 3));
                        message = addMessage("order " + 3 + " flow", message, pumpRoomOut.valid("pumpflow", 3));
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

    private String addMessage(String name, String message, Process.STATUS status) {
        switch (status) {
            case HIGHER:
                message += name + "指标过高";
                break;
            case LOWER:
                message += name + "指标过低";
                break;
            case INVALID:
                message += name + "数值不合法";
                break;
        }
        message += "\n";
        return message;
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showNormalDialog(String message){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setTitle("警告！水质数据异常");
        normalDialog.setMessage(message);
        normalDialog.setPositiveButton("确定", null);
        normalDialog.show();
    }

    class PumpAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_pump, null);
                holder= new MyViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder)convertView.getTag();
            }

            holder.orderTv.setText(String.valueOf(mDatas.get(position).order));
            holder.frequencyTv.setText(String.valueOf(mDatas.get(position).frequency));
            holder.headTv.setText(String.valueOf(mDatas.get(position).head));
            holder.flowTv.setText(String.valueOf(mDatas.get(position).flow));
            return convertView;
        }


    }

    class MyViewHolder {
        TextView orderTv;
        TextView frequencyTv;
        TextView headTv;
        TextView flowTv;

        MyViewHolder(View view) {
            orderTv = (TextView) view.findViewById(R.id.item_pump_order);
            frequencyTv = (TextView) view.findViewById(R.id.item_pump_frequency);
            headTv = (TextView) view.findViewById(R.id.item_pump_head);
            flowTv = (TextView) view.findViewById(R.id.item_pump_flow);
        }
    }
}
