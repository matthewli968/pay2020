package com.mfypay.pay3.f;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.mfypay.pay3.R;
import com.mfypay.pay3.a.AAdapter;
import com.mfypay.pay3.b.OrderBean;
import com.mfypay.pay3.util.LGU;
import com.mfypay.pay3.util.SPU;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AFragment extends BF implements View.OnClickListener {

    private Button btnBind, btnUnbind, btnQuery, btnTestAlipay;
    private RecyclerView rvAli;
    private AAdapter aAdapter;
    private EditText etQuery;
    private List<OrderBean> mList = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String no = intent.getStringExtra("no");
            String money = intent.getStringExtra("money");
            String mark = intent.getStringExtra("mark");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            String format = simpleDateFormat.format(new Date());

            if ("登录超时".equalsIgnoreCase(mark)) {
                resetAdapter("请打开支付宝,点击进入商家服务浏览" + format, System.currentTimeMillis() + "");
                return;
            }

            resetAdapter("<font color='black' size='24'>订单号:</font>" + "<span color='red'>" + no + "<span>-----金额:" + "<span color='red'>" + money + "</span>" + "<span>-----备注:" + "<span color='red'>"
                    + mark + "</span>" + "-----获取时间:" + format, no);
            LGU.D(intent.getStringExtra("money") + "=---===");
        }
    };
    private LinearLayoutManager manager;

    public AFragment() {

    }


    public static AFragment newInstance() {

        AFragment fragment = new AFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_a, container, false);
        init(inflate);
        return inflate;
    }

    private void init(View view) {
        initView(view);
        initListen();
        r();


    }

    private void r() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.mfypay.aliorder");
        getActivity().registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void initListen() {
        btnUnbind.setOnClickListener(this);
        btnBind.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btnTestAlipay.setOnClickListener(this);
        etQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SPU.setParam(getActivity(), "deploy", etQuery.getText().toString());
            }
        });


    }

    private void initView(View view) {
        btnBind = view.findViewById(R.id.btn_bind);
        btnUnbind = view.findViewById(R.id.btn_unbind);
        btnQuery = view.findViewById(R.id.btn_query);
        rvAli = view.findViewById(R.id.rv_ali);
        etQuery = view.findViewById(R.id.et_query);
        btnTestAlipay = view.findViewById(R.id.btn_test_alipay);
        view.findViewById(R.id.fee).setOnClickListener(this);
        view.findViewById(R.id.withdraw).setOnClickListener(this);

        manager = new LinearLayoutManager(getContext());
        manager.setSmoothScrollbarEnabled(true);
        manager.setOrientation(OrientationHelper.VERTICAL);
        rvAli.setLayoutManager(manager);
        rvAli.setNestedScrollingEnabled(false);
    }

    @Override
    public String toString() {
        return "支付宝";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_bind:
                b();
                break;
            case R.id.btn_unbind:
                ub();
                break;
            case R.id.btn_query:
                query();
                break;
            case R.id.btn_test_alipay:
                testAlipay();
                break;
            case R.id.fee:
                fee();
                break;
            case R.id.withdraw:
                withDraw();
                break;
            default:
                break;

        }
    }


    private void withDraw() {

        String uri = "alipays://platformapi/startapp?appId=09999988&actionType=toCard&sourceId=bill&cardNo=6217231202002224944&bankAccount=%e6%9d%8e%e4%b8%8a%e5%87%a1&money=0.01&amount=0.01&bankMark=ICBC&bankName=%e4%b8%ad%e5%9b%bd%e5%b7%a5%e5%95%86%e9%93%b6%e8%a1%8c&pwd=126999";
        Intent intent = new Intent();
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    private void testAlipay() {
        Intent intent = new Intent();
        intent.setAction("store.imea1.alipay.start");

        intent.putExtra("mark", UUID.randomUUID().toString().substring(0, 8));
        intent.putExtra("money", "0.01");
        getActivity().sendBroadcast(intent);
    }


    private void fee() {
        Intent intent = new Intent();
        intent.setAction("store.imea1.alipay.start");
        intent.putExtra("rType", "fee");

        intent.putExtra("mark", UUID.randomUUID().toString().substring(0, 8));
        intent.putExtra("money", "0.01");
        getActivity().sendBroadcast(intent);
    }

    private void query() {
        long deploy = 5;
        if (TextUtils.isEmpty(etQuery.getText().toString())) {
            deploy = 5;
        }

        try {
            deploy = Long.parseLong(etQuery.getText().toString());
        } catch (Exception e) {

            deploy = 5;
        }

        Intent intent = new Intent("store.imea1.alipay.start");
        intent.putExtra("rType", "query");
        intent.putExtra("deploy", deploy);
        getActivity().sendBroadcast(intent);
    }

    private void b() {
        Intent intent = new Intent("store.imea1.alipay.start");
        intent.putExtra("rType", "bind");
        getActivity().sendBroadcast(intent);
    }

    private void ub() {
        Intent intent = new Intent("store.imea1.alipay.start");
        intent.putExtra("rType", "unbind");

        getActivity().sendBroadcast(intent);
    }


    private void resetAdapter(String msg, String no) {
        LGU.D(msg);

        if (mList == null) {
            mList = new ArrayList<>();
        }
        if (mList != null && mList.size() > 2000) {
            mList = null;
            mList = new ArrayList<>();
        }
        OrderBean ob = new OrderBean();
        ob.setNo(no);
        ob.setShow(msg);

        if (!mList.contains(ob)) {

            mList.add(0, ob);

            LGU.D(mList.toString());
            if (aAdapter != null) {
                aAdapter.notifyDataSetChanged();
            } else {
                aAdapter = new AAdapter(mList, getActivity());
                rvAli.setAdapter(aAdapter);
            }


        }


    }
}
