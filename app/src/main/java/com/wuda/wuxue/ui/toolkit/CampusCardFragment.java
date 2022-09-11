package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.CampusCardBill;
import com.wuda.wuxue.bean.CampusCardInfo;
import com.wuda.wuxue.network.HttpClient;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.ui.base.FullScreenDialog;
import com.wuda.wuxue.ui.base.WaitForLoginFragment;
import com.wuda.wuxue.ui.mine.AccountActivity;

import java.text.SimpleDateFormat;
import java.util.List;

public class CampusCardFragment extends ToolFragment {

    CampusCardViewModel mViewModel;

    TextView sno_tv;
    TextView balance_tv;
    TextView lost_tv;
    TextView expenditure_tv;
    TextView billListDate_tv;
    TableLayout billList_tb;

    ImageView balance_iv;
    ImageView lost_iv;
    ImageView bill_iv;

    FullScreenDialog fullScreenDialog;
    WaitForLoginFragment waitForLoginFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_campus_card, container, false);

        sno_tv = view.findViewById(R.id.campus_card_sno_textView);
        balance_tv = view.findViewById(R.id.campusCard_balance_textView);
        lost_tv = view.findViewById(R.id.campusCard_lost_textView);
        expenditure_tv = view.findViewById(R.id.campusCard_expenditure_textView);
        billListDate_tv = view.findViewById(R.id.campusCard_billList_dateRange_textView);
        billList_tb = view.findViewById(R.id.campusCard_billList_table);

        balance_iv = view.findViewById(R.id.campusCard_balance_imageView);
        lost_iv = view.findViewById(R.id.campusCard_lost_imageView);
        bill_iv = view.findViewById(R.id.campusCard_bill_imageView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fullScreenDialog = new FullScreenDialog();
        fullScreenDialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });
        waitForLoginFragment = new WaitForLoginFragment();
        waitForLoginFragment.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waitForLoginFragment.setState(WaitForLoginFragment.STATE_LOGIN);
                mViewModel.login();
            }
        });
        fullScreenDialog.setFragment(waitForLoginFragment);
        fullScreenDialog.show(getChildFragmentManager());

        mViewModel = new ViewModelProvider(this).get(CampusCardViewModel.class);
        mViewModel.login();

        eventBinding();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // 清除旧的登录Cookie（两个域名，登录只清一个）
        HttpClient.clearCookieStore();
    }

    private void eventBinding() {

        mViewModel.getFailResponse().observe(getViewLifecycleOwner(), new Observer<ResponseResult<?>>() {
            @Override
            public void onChanged(ResponseResult<?> result) {
                if (result == null) return;
                DialogFactory.errorInfoDialog(requireContext(), result).show();
                if (result.getFlag().equals("LOGIN")) {
                    waitForLoginFragment.setState(WaitForLoginFragment.STATE_FAIL);
                }
                mViewModel.clearFailResponse();
            }
        });

        mViewModel.getLoginResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("fail")) {
                    Intent intent = new Intent(requireContext(), AccountActivity.class);
                    startActivity(intent);
                    waitForLoginFragment.setState(WaitForLoginFragment.STATE_FAIL);
                } else {
                    mViewModel.requestCardInfo();
                    fullScreenDialog.dismiss();
                    fullScreenDialog = null;
                    waitForLoginFragment = null;
                }
            }
        });

        mViewModel.getCardInfoResponse().observe(getViewLifecycleOwner(), new Observer<CampusCardInfo>() {
            @Override
            public void onChanged(CampusCardInfo cardInfo) {
                sno_tv.setText(cardInfo.getSno());
                balance_tv.setText("¥ " + cardInfo.getBalance().toString());
                lost_tv.setText(cardInfo.isLost()? "已挂失": "未挂失");

                mViewModel.requestBillList();
            }
        });

        mViewModel.getBillListResponse().observe(getViewLifecycleOwner(), new Observer<List<CampusCardBill>>() {
            @Override
            public void onChanged(List<CampusCardBill> campusCardBills) {
                inflateBillList(campusCardBills);
            }
        });

        mViewModel.getDepositResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mViewModel.requestCardInfo();
            }
        });

        mViewModel.getLostResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mViewModel.requestCardInfo();
            }
        });

        bill_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<?> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setSelection(
                                new Pair<>(
                                        MaterialDatePicker.todayInUtcMilliseconds(),
                                        MaterialDatePicker.todayInUtcMilliseconds()
                                )
                        )
                        .build();
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Pair<Long, Long> dateSelection = (Pair<Long, Long>) selection;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        mViewModel.billStartDate = df.format(dateSelection.first);
                        mViewModel.billEndDate = df.format(dateSelection.second);
                        mViewModel.requestBillList();
                    }
                });
                datePicker.show(requireActivity().getSupportFragmentManager(), datePicker.toString());

            }
        });

        lost_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextInputEditText pwd_et = new TextInputEditText(requireContext());
                pwd_et.setHint("请输入密码！");

                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle(mViewModel.cardInfo.isLost()? "解挂": "挂失")
                        .setView(pwd_et)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mViewModel.lost(pwd_et.getText().toString());
                            }
                        })
                        .create()
                        .show();
            }
        });

        balance_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() == null)
                    return;

                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                TextInputEditText money_et = new TextInputEditText(getContext());
                TextInputEditText pwd_et = new TextInputEditText(getContext());
                linearLayout.addView(money_et);
                linearLayout.addView(pwd_et);
                money_et.setInputType(InputType.TYPE_CLASS_NUMBER);
                money_et.setHint("请输入金额(<=100)");
                pwd_et.setHint("请输入密码");

                new AlertDialog.Builder(getContext())
                        .setTitle("充值")
                        .setView(linearLayout)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                double tranAmt = Double.parseDouble(String.valueOf(money_et.getText()));
                                // 有吞钱的风险，加个上限
                                if (tranAmt > 100) {
                                    Toast.makeText(requireContext(), "金额不得超过100", Toast.LENGTH_SHORT).show();
                                } else {
                                    String pwd = pwd_et.getText().toString();
                                    mViewModel.deposit(tranAmt, pwd);
                                }
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void inflateBillList(List<CampusCardBill> billList) {
        // 显示当前选择的时间
        billListDate_tv.setText(mViewModel.billStartDate + " / " + mViewModel.billEndDate);
        // 清除旧结果
        billList_tb.removeAllViews();

        TableRow.LayoutParams params = new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        TableRow headerRow = new TableRow(getContext());
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        headerRow.setPadding(0, 4, 24, 4);
        TextView headerTimePlace_tv = new TextView(getContext());
        TextView headerTranAtm_tv = new TextView(getContext());
        TextView headerCardBal_tv = new TextView(getContext());
        // 增加间隔
        headerCardBal_tv.setPadding(64, 0, 0, 0);

        headerTranAtm_tv.setGravity(Gravity.END);
        headerCardBal_tv.setGravity(Gravity.END);

        headerTimePlace_tv.setLayoutParams(params);

        headerTimePlace_tv.setTypeface(null, Typeface.BOLD);
        headerTranAtm_tv.setTypeface(null, Typeface.BOLD);
        headerCardBal_tv.setTypeface(null, Typeface.BOLD);

        headerTimePlace_tv.setText("时间 / 地点");
        headerTranAtm_tv.setText("消费金额");
        headerCardBal_tv.setText("余额");


        headerRow.addView(headerTimePlace_tv);
        headerRow.addView(headerTranAtm_tv);
        headerRow.addView(headerCardBal_tv);

        billList_tb.addView(headerRow);

        float expenditure = 0;

        for (CampusCardBill bill: billList) {
            TableRow billRow = new TableRow(getContext());
            billRow.setPadding(0, 4, 24, 4);
            billRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView timePlace_tv = new TextView(getContext());
            TextView tranAtm_tv = new TextView(getContext());
            TextView cardBal_tv = new TextView(getContext());
            tranAtm_tv.setGravity(Gravity.CENTER);
            cardBal_tv.setGravity(Gravity.END);

            timePlace_tv.setLayoutParams(params);

            timePlace_tv.setText(bill.getTime() + "\n" + bill.getPlace());
            tranAtm_tv.setText(bill.getTranAmt().toString());
            cardBal_tv.setText(bill.getCardBal().toString());

            float val = bill.getTranAmt();
            if (val < 0) {
                expenditure += val;
            }

            billRow.addView(timePlace_tv);
            billRow.addView(tranAtm_tv);
            billRow.addView(cardBal_tv);

            billList_tb.addView(billRow);
        }

        expenditure_tv.setText(String.format("%.2f", expenditure));
    }
}