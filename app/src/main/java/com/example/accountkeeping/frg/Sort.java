package com.example.accountkeeping.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountkeeping.Data.AllAccountManager;
import com.example.accountkeeping.Data.DataBank;
import com.example.accountkeeping.Data.DayAccountRecord;
import com.example.accountkeeping.DoubleRecycleView.DoubleRecycleView;
import com.example.accountkeeping.MainActivity;
import com.example.accountkeeping.R;

import java.util.HashMap;
import java.util.List;

public class Sort extends Fragment {
    private  DoubleRecycleView.MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private List<DayAccountRecord> dayAccountRecords ;
    private AllAccountManager allAccountManager;
    private DataBank dataBank;

    private static final String[] m={"吃喝饮食","基金理财","游戏娱乐","人情礼物","医疗费用","社交红包","工资薪水","逛街购物","运动健身","交通出行","其他活动"};
    private TextView my_view ;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;

    private RecyclerView mainRecycleView;
    private TextView income;
    private TextView expend;

    public Sort(AllAccountManager allAccountManager, DataBank dataBank) {
        this.allAccountManager = allAccountManager;
        this.dayAccountRecords = allAccountManager.getDayAccountRecords();
        this.dataBank = dataBank;

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort, container, false);

        //下拉框
        my_view = view.findViewById(R.id.spinnerText_sort);
        spinner =(Spinner)  view.findViewById(R.id.Spinner_sort);
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,m);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);

        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());

        //设置默认值
        spinner.setVisibility(View.VISIBLE);

        mainRecycleView=view.findViewById(R.id.recycle_view_sort);

        //绑定文本框
        income = view.findViewById(R.id.view_text_sort_income);
        expend = view.findViewById(R.id.view_text_sort_expend);
        return view;
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            my_view.setText("请选择分类："+m[arg2]);

            List<DayAccountRecord> dayAccountRecords=allAccountManager.getActivityDayRecords(m[arg2]);

            //recycle view
            DoubleRecycleView doubleRecycleView =new DoubleRecycleView(((MainActivity) requireActivity()), getContext(),dayAccountRecords , dataBank);
            mainRecyclerViewAdapter = doubleRecycleView.getMainRecyclerViewAdapter();
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mainRecycleView.setLayoutManager(layoutManager);
            mainRecycleView.setAdapter(mainRecyclerViewAdapter);
            mainRecyclerViewAdapter.notifyDataSetChanged();

            HashMap analysis = allAccountManager.getAnalysis(dayAccountRecords);
            income.setText(analysis.get("income").toString());
            expend.setText(analysis.get("expend").toString());

        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}
