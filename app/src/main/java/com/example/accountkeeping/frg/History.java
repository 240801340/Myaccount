package com.example.accountkeeping.frg;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountkeeping.Data.AllAccountManager;
import com.example.accountkeeping.Data.DataBank;
import com.example.accountkeeping.Data.DayAccountRecord;
import com.example.accountkeeping.DoubleRecycleView.DoubleRecycleView;
import com.example.accountkeeping.InputActivity;
import com.example.accountkeeping.MainActivity;
import com.example.accountkeeping.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class History extends Fragment {
    private  DoubleRecycleView.MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private List<DayAccountRecord> dayAccountRecords ;
    private AllAccountManager allAccountManager;
    private DataBank dataBank;



    private RecyclerView mainRecycleView;
    private TextView income;
    private TextView expend;
    private Button startDate;
    private Button endDate;
    private Button history_OK;

    boolean start_ok =false;
    boolean end_ok =false;

    public History(AllAccountManager allAccountManager, DataBank dataBank) {
        this.allAccountManager = allAccountManager;
        this.dayAccountRecords = allAccountManager.getDayAccountRecords();
        this.dataBank = dataBank;

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);



        mainRecycleView=view.findViewById(R.id.recycle_view_history);

        //绑定文本框
        income = view.findViewById(R.id.view_text_history_income);
        expend = view.findViewById(R.id.view_text_history_expend);

        //绑定日期选择器和确定按钮
        startDate=view.findViewById(R.id.input_start_date_button);
        endDate=view.findViewById(R.id.input_end_date_button);
        start_ok =false;
        end_ok =false;
        history_OK = view.findViewById(R.id.history_OK);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在点击事件部分调用，chooseBirthday为点击事件的控件
                showDatePickDialog(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        startDate.setText(year + "-" + (month + 1) + "-" + day);
                    }
                }, startDate.getText().toString());
                start_ok=true;

            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在点击事件部分调用，chooseBirthday为点击事件的控件
                showDatePickDialog(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        endDate.setText(year + "-" + (month + 1) + "-" + day);
                    }
                }, endDate.getText().toString());

                end_ok=true;
            }
        });
        history_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start_ok && end_ok){
                    String startDateStr =startDate.getText().toString();
                    String endDateStr =endDate.getText().toString();
                    List<DayAccountRecord> dayAccountRecords=allAccountManager.getHistoryDayRecords(startDateStr,endDateStr);
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
                }else{
                    callAlert(getContext());
                }



            }
        });
        return view;
    }

    //调出警告
    public void callAlert(Context context){
        AlertDialog.Builder alertDB = new AlertDialog.Builder(context);
        alertDB.setPositiveButton(context.getResources().getString(R.string.string_confirmation), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDB.setNegativeButton(context.getResources().getString(R.string.string_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDB.setMessage("请选择好日期");
        alertDB.setTitle(context.getResources().getString(R.string.hint)).show();
    }


    //日期选择的方法
    /**
     * 日期选择
     * @param listener
     * @param curDate
     */
    public void showDatePickDialog(DatePickerDialog.OnDateSetListener listener, String curDate) {
        Calendar calendar = Calendar.getInstance();
        int year = 0,month = 0,day = 0;
        try {
            year =Integer.parseInt(curDate.substring(0,curDate.indexOf("-"))) ;
            month =Integer.parseInt(curDate.substring(curDate.indexOf("-")+1,curDate.lastIndexOf("-")))-1 ;
            day =Integer.parseInt(curDate.substring(curDate.lastIndexOf("-")+1,curDate.length())) ;
        } catch (Exception e) {
            e.printStackTrace();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day=calendar.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),DatePickerDialog.THEME_HOLO_LIGHT,listener, year,month , day);
        datePickerDialog.show();
    }
}
