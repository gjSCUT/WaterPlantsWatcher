package com.gjscut.waterplantswatcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gjscut.waterplantswatcher.model.*;
import com.gjscut.waterplantswatcher.model.Process;

import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by guojun on 4/19/17.
 */

public class CurrentDataFragment extends Fragment {
    private ScheduledExecutorService service;
    private String type;
    private TextView phInTv;
    private TextView waterTemperInTv;
    private TextView turbidityInTv;
    private TextView amlN2InTv;
    private TextView codInTv;
    private TextView tocInTv;
    private TextView flowInTv;
    private TextView phOutTv;
    private TextView waterTemperOutTv;
    private TextView turbidityOutTv;
    private TextView amlN2OutTv;
    private TextView codOutTv;
    private TextView tocOutTv;
    private TextView flowOutTv;
    private TextView zoneAmount;
    private TextView chlorineAmount;
    private TextView alumAmount;
    public CurrentDataFragment() {
        service = Executors.newSingleThreadScheduledExecutor();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                                    if (type.equals(ActivatedCarbonPool.class)) {
                                        p = (ActivatedCarbonPool) p;

                                    } else if (type.equals(ChlorineAddPool.class)) {
                                        p = (ChlorineAddPool) p;
                                    } else if (type.equals(CoagulatePool.class)) {
                                        p = (CoagulatePool) p;
                                    } else if (type.equals(CombinedWell.class)) {
                                        p = (CombinedWell) p;
                                    } else if (type.equals(DepositPool.class)) {
                                        p = (DepositPool) p;
                                    } else if (type.equals(DistributeWell.class)) {
                                        p = (DistributeWell) p;
                                    } else if (type.equals(OzonePoolAdvance.class)) {
                                        p = (OzonePoolAdvance) p;
                                    } else if (type.equals(OzonePoolMain.class)) {
                                        p = (OzonePoolMain) p;
                                    } else if (type.equals(PumpRoomFirst.class)) {
                                        p = (PumpRoomFirst) p;
                                    } else if (type.equals(PumpRoomSecond.class)) {
                                        p = (PumpRoomSecond) p;
                                    } else if (type.equals(PumpRoomOut.class)) {
                                        p = (PumpRoomOut) p;
                                    } else if (type.equals(SandLeachPool.class)) {
                                        p = (SandLeachPool) p;
                                    } else if (type.equals(SuctionWell.class)) {
                                        p = (SuctionWell) p;
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

        phInTv = (TextView) container.findViewById(R.id.phInTv);
        waterTemperInTv = (TextView) container.findViewById(R.id.waterTemperInTv);
        turbidityInTv = (TextView) container.findViewById(R.id.turbidityInTv);
        amlN2InTv = (TextView) container.findViewById(R.id.amlN2InTv);
        codInTv = (TextView) container.findViewById(R.id.codInTv);
        tocInTv = (TextView) container.findViewById(R.id.tocInTv);
        flowInTv = (TextView) container.findViewById(R.id.flowInTv);
        phOutTv = (TextView) container.findViewById(R.id.phOutTv);
        waterTemperOutTv = (TextView) container.findViewById(R.id.phInEd);
        turbidityOutTv = (TextView) container.findViewById(R.id.phInEd);
        amlN2OutTv = (TextView) container.findViewById(R.id.phInEd);
        codOutTv = (TextView) container.findViewById(R.id.phInEd);
        tocOutTv = (TextView) container.findViewById(R.id.phInEd);
        flowOutTv = (TextView) container.findViewById(R.id.phInEd);
        zoneAmount = (TextView) container.findViewById(R.id.phInEd);
        chlorineAmount = (TextView) container.findViewById(R.id.phInEd);
        alumAmount = (TextView) container.findViewById(R.id.phInEd);
        return inflater.inflate(R.layout.fragment_current_data, container, false);
    }
}
