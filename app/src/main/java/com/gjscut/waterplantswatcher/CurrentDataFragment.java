package com.gjscut.waterplantswatcher;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
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
import com.gjscut.waterplantswatcher.model.Process;
import com.gjscut.waterplantswatcher.model.PumpRoomFirst;
import com.gjscut.waterplantswatcher.model.PumpRoomOut;
import com.gjscut.waterplantswatcher.model.PumpRoomSecond;
import com.gjscut.waterplantswatcher.model.SandLeachPool;
import com.gjscut.waterplantswatcher.model.SuctionWell;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by guojun on 4/19/17.
 */

public class CurrentDataFragment extends Fragment {
    private ScheduledExecutorService service;
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
    @BindView(R.id.zoomGridLayout) View zoomGridLayout;
    @BindView(R.id.alumGridLayout) View alumGridLayout;
    @BindView(R.id.chlorineGridLayout) View chlorineGridLayout;
    private String type;
    private Map<String, Pair<View, View>> classGridTextMap;

    public CurrentDataFragment() {
        service = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString("type");
        classGridTextMap.put(OzonePoolMain.class.toString(), new Pair<>(zoomGridLayout, zoomAmountTv));
        classGridTextMap.put(OzonePoolAdvance.class.toString(), new Pair<>(zoomGridLayout, zoomAmountTv));
        classGridTextMap.put(ChlorineAddPool.class.toString(), new Pair<>(chlorineGridLayout, chlorineAmountTv));
        classGridTextMap.put(CoagulatePool.class.toString(), new Pair<>(chlorineGridLayout, chlorineAmountTv));
        final DecimalFormat decimalFormat = new DecimalFormat(".000");
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Observable<? extends Process> observable = null;
                if (type.equals(ActivatedCarbonPool.class)) {
                    observable = NetHelper.api().getActivatedCarbonPool(1, "-createTime");
                } else if (type.equals(ChlorineAddPool.class)) {
                    observable = NetHelper.api().getChlorineAddPool(1, "-createTime");
                } else if (type.equals(CoagulatePool.class)) {
                    observable = NetHelper.api().getCoagulatePool(1, "-createTime");
                } else if (type.equals(CombinedWell.class)) {
                    observable = NetHelper.api().getCombinedWell(1, "-createTime");
                } else if (type.equals(DepositPool.class)) {
                    observable = NetHelper.api().getDepositPool(1, "-createTime");
                } else if (type.equals(DistributeWell.class)) {
                    observable = NetHelper.api().getDistributeWell(1, "-createTime");
                } else if (type.equals(OzonePoolAdvance.class)) {
                    observable = NetHelper.api().getOzonePoolAdvance(1, "-createTime");
                } else if (type.equals(OzonePoolMain.class)) {
                    observable = NetHelper.api().getOzonePoolMain(1, "-createTime");
                } else if (type.equals(PumpRoomFirst.class)) {
                    observable = NetHelper.api().getPumpRoomFirst(1, "-createTime");
                } else if (type.equals(PumpRoomSecond.class)) {
                    observable = NetHelper.api().getPumpRoomSecond(1, "-createTime");
                } else if (type.equals(PumpRoomOut.class)) {
                    observable = NetHelper.api().getPumpRoomOut(1, "-createTime");
                } else if (type.equals(SandLeachPool.class)) {
                    observable = NetHelper.api().getSandLeachPool(1, "-createTime");
                } else if (type.equals(SuctionWell.class)) {
                    observable = NetHelper.api().getSuctionWell(1, "-createTime");
                }
                if (observable == null) {
                    service.shutdown();
                    return;
                }
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Process>() {

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onNext(Process p) {
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
                                    while(iterator.hasNext()) {
                                        String className = iterator.next();
                                        if (type.equals(className)) {
                                            isShow = true;
                                            classGridTextMap.get(className).first.setVisibility(View.VISIBLE);
                                            if (type.equals(ChlorineAddPool.class)) {
                                                ChlorineAddPool chlorineAddPool = (ChlorineAddPool) p;
                                                ((TextView) classGridTextMap.get(className).second)
                                                        .setText(decimalFormat.format(chlorineAddPool.chlorineAmount));
                                            } else if (type.equals(CoagulatePool.class)) {
                                                CoagulatePool coagulatePool = (CoagulatePool) p;
                                                ((TextView) classGridTextMap.get(className).second)
                                                        .setText(decimalFormat.format(coagulatePool.alumAmount));
                                            } else if (type.equals(OzonePoolAdvance.class)) {
                                                OzonePoolAdvance ozonePoolAdvance = (OzonePoolAdvance) p;
                                                ((TextView) classGridTextMap.get(className).second)
                                                        .setText(decimalFormat.format(ozonePoolAdvance.zoneAmount));
                                            } else if (type.equals(OzonePoolMain.class)) {
                                                OzonePoolMain ozonePoolMain = (OzonePoolMain) p;
                                                ((TextView) classGridTextMap.get(className).second)
                                                        .setText(decimalFormat.format(ozonePoolMain.zoneAmount));
                                            } else if (type.equals(PumpRoomFirst.class)) {
                                                PumpRoomFirst pumpRoomFirst = (PumpRoomFirst) p;
                                            } else if (type.equals(PumpRoomSecond.class)) {
                                                PumpRoomSecond pumpRoomSecond = (PumpRoomSecond) p;
                                            } else if (type.equals(PumpRoomOut.class)) {
                                                PumpRoomOut pumpRoomOut = (PumpRoomOut) p;
                                            }
                                        } else {
                                            classGridTextMap.get(className).first.setVisibility(View.GONE);
                                        }
                                    }
                                    if (isShow) {
                                        paramTv.setVisibility(View.GONE);
                                    } else {
                                        paramTv.setVisibility(View.VISIBLE);
                                    }
                                } catch (ClassCastException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCompleted() {
                            }
                        });
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_data, container, false);
        ButterKnife.bind(this, view);
        // TODO Use fields...
        return view;
    }
}
