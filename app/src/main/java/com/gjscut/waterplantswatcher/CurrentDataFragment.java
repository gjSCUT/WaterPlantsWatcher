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
    public TextView phInTv;
    public TextView waterTemperInTv;
    public TextView turbidityInTv;
    public TextView amlN2InTv;
    public TextView codInTv;
    public TextView tocInTv;
    public TextView flowInTv;
    public TextView phOutTv;
    public TextView waterTemperOutTv;
    public TextView turbidityOutTv;
    public TextView amlN2OutTv;
    public TextView codOutTv;
    public TextView tocOutTv;
    public TextView flowOutTv;
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
        return inflater.inflate(R.layout.fragment_current_data, container, false);
    }
}
