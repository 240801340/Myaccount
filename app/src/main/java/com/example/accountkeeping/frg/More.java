package com.example.accountkeeping.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.accountkeeping.Data.AllAccountManager;
import com.example.accountkeeping.Data.DataBank;
import com.example.accountkeeping.Data.DayAccountRecord;
import com.example.accountkeeping.R;

import java.util.List;

public class More extends Fragment {
    //private MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private List<DayAccountRecord> dayAccountRecords ;
    private AllAccountManager allAccountManager;
    private DataBank dataBank;

    TextView textView_pure;
    TextView text_view_all_income;
    TextView text_view_all_expend;

    public More(AllAccountManager allAccountManager, DataBank dataBank) {
        this.allAccountManager = allAccountManager;
        this.dayAccountRecords = allAccountManager.getDayAccountRecords();
        this.dataBank = dataBank;
        //mainRecyclerViewAdapter =new Homepage.MainRecyclerViewAdapter(dayAccountRecords);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }
}
