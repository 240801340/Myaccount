package com.example.accountkeeping;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountkeeping.Data.DayAccountRecord;
import com.example.accountkeeping.DoubleRecycleView.DoubleRecycleView;
import com.example.accountkeeping.frg.Homepage;
import com.example.accountkeeping.frg.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class InputActivity extends AppCompatActivity {
    private ActivityRecyclerViewAdapter activityRecyclerViewAdapter= null;
    private List<ACT> acts = new ArrayList<>();
    Button buttonOK;
    Button buttonDate;
    EditText editMoney;
    TextView editActivity;
    TextView editPayWay;
    Button editType;
    int iconID;

    private static final String[] m={"现金","支付宝","微信"};
    private Spinner spinner;
    private ArrayAdapter<String> adapter;

    //活动类
    private class ACT{
        public int iconId;
        public String actName;

        public ACT(int iconId, String actName) {
            this.iconId = iconId;
            this.actName = actName;
        }

        public int getIconId() {
            return iconId;
        }

        public void setIconId(int iconId) {
            this.iconId = iconId;
        }

        public String getActName() {
            return actName;
        }

        public void setActName(String actName) {
            this.actName = actName;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //对接资源
        buttonOK = this.findViewById(R.id.input_button);
        buttonDate = this.findViewById(R.id.inputDate_button);
        editMoney=findViewById(R.id.edit_input_money);
        editActivity=findViewById(R.id.edit_activity);
        editPayWay=findViewById(R.id.edit_input_payWay);
        editType=findViewById(R.id.edit_input_type);

        //支付方式下拉列表
        //下拉框
        spinner =(Spinner)  findViewById(R.id.Spinner_payWay);
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(InputActivity.this,android.R.layout.simple_list_item_1,m);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);
        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        //设置默认值
        spinner.setVisibility(View.VISIBLE);


        //初始化活动列表
        acts.add(acts.size(),new ACT(R.drawable.food,"吃喝饮食"));
        acts.add(acts.size(),new ACT(R.drawable.fund,"基金理财"));
        acts.add(acts.size(),new ACT(R.drawable.game,"游戏娱乐"));
        acts.add(acts.size(),new ACT(R.drawable.gift,"人情礼物"));
        acts.add(acts.size(),new ACT(R.drawable.medical,"医疗费用"));
        acts.add(acts.size(),new ACT(R.drawable.redpacket,"社交红包"));
        acts.add(acts.size(),new ACT(R.drawable.salary,"工资薪水"));
        acts.add(acts.size(),new ACT(R.drawable.shopping,"逛街购物"));
        acts.add(acts.size(),new ACT(R.drawable.sports,"运动健身"));
        acts.add(acts.size(),new ACT(R.drawable.vehicle,"交通出行"));
        acts.add(acts.size(),new ACT(R.drawable.other,"其他活动"));
        editActivity.setText(acts.get(0).getActName());
        iconID=acts.get(0).getIconId();

        //准备recycle view
        RecyclerView mainRecycleView=findViewById(R.id.recycle_view_activities);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mainRecycleView.setLayoutManager(layoutManager);
        activityRecyclerViewAdapter=new ActivityRecyclerViewAdapter(acts);
        mainRecycleView.setAdapter(activityRecyclerViewAdapter);


        //与main activity 通信
        Intent request_intent=getIntent();

        int op =request_intent.getIntExtra("op", -1);//操作 0表示增加 1表示修改
        String date = request_intent.getStringExtra("date");
        String type =  request_intent.getStringExtra("type");
        Double money = request_intent.getDoubleExtra("money",0 );
        String payWay = request_intent.getStringExtra("payWay");
        String activity = request_intent.getStringExtra("activity");

        if(1 == op){//不允许改日期
            buttonDate.setBackgroundColor( getResources().getColor(R.color.gray));
        }else if(0 == op){
            buttonDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //在点击事件部分调用，chooseBirthday为点击事件的控件
                    showDatePickDialog(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            buttonDate.setText(year + "-" + (month + 1) + "-" + day);
                        }
                    }, buttonDate.getText().toString());


                }
            });
        }

        if(date!=null){
            buttonDate.setText(date);
        }
        if(type!=null){
            editType.setText(type);
        }
        if(money!=0){
            editMoney.setText(money.toString());

        }
        if(payWay!=null){
            editPayWay.setText(payWay);
        }
        if(activity!=null){
            editActivity.setText(activity);
        }

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result_intent = new Intent();
                result_intent.putExtra("op", op);
                result_intent.putExtra("date",buttonDate.getText().toString());
                result_intent.putExtra("activity",editActivity.getText().toString());
                result_intent.putExtra("payWay",editPayWay.getText().toString());
                Double money= 0.0 ;
                if ((editMoney.getText().toString().isEmpty())){
                    callAlert("请填写金额");
                    return;
                }else{
                    try {
                        money = Double.parseDouble(editMoney.getText().toString());
                    }catch(Exception e){
                        callAlert("请输入数字");
                        return;
                    }
                    if(money <=0.0  ){
                        callAlert("金额必须为正数");
                        return;
                    }
                }
                result_intent.putExtra("money",money);
                result_intent.putExtra("type",editType.getText().toString());
                result_intent.putExtra("iconID",iconID);
                if(1 == op){
                    int dateIndex =request_intent.getIntExtra("dateIndex", -1);
                    int position =request_intent.getIntExtra("position", -1);
                    result_intent.putExtra("dateIndex",dateIndex);
                    result_intent.putExtra("position",position);
                }

                setResult(200,result_intent);
                InputActivity.this.finish();


            }
        });


        editType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在点击事件部分调用，chooseBirthday为点击事件的控件
                String type = editType.getText().toString();
                if( type.equals("收入")){
                    editType.setText("支出");
                }else if(type.equals("支出")){
                    editType.setText("收入");
                }

            }
        });

    }
    //使用数组形式操作下拉列表
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            editPayWay.setText(""+m[arg2]);

        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //调出警告
    public void callAlert(String str){
        AlertDialog.Builder alertDB = new AlertDialog.Builder(InputActivity.this);
        alertDB.setPositiveButton(InputActivity.this.getResources().getString(R.string.string_confirmation), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDB.setNegativeButton(InputActivity.this.getResources().getString(R.string.string_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDB.setMessage(str);
        alertDB.setTitle(InputActivity.this.getResources().getString(R.string.hint)).show();
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,DatePickerDialog.THEME_HOLO_LIGHT,listener, year,month , day);
        datePickerDialog.show();
    }



    private class ActivityRecyclerViewAdapter extends RecyclerView.Adapter {
        private List<ACT> acts;

        public ActivityRecyclerViewAdapter(List<ACT> acts ) {
            this.acts=acts;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.act_holder, parent, false);
            return new MyViewHolder(view);



        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder Holder, int position) {
            MyViewHolder holder= (MyViewHolder)Holder;

            holder.getImageView().setImageResource(acts.get(position).getIconId());
            holder.getTextViewName().setText(acts.get(position).getActName());
        }

        @Override
        public int getItemCount() {
            return acts.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final ImageView imageView;
            private final TextView textViewName;
            public MyViewHolder(View item_view) {
                super(item_view);

                this.imageView=item_view.findViewById(R.id.image_view_act_icon);
                this.textViewName=item_view.findViewById(R.id.text_view_actName);
                item_view.setOnClickListener(this);
            }

            public ImageView getImageView() {
                return imageView;
            }

            public TextView getTextViewName() {
                return textViewName;
            }


            @Override
            public void onClick(View view) {
                editActivity.setText( acts.get(getAdapterPosition()).getActName());
                iconID=acts.get(getAdapterPosition()).getIconId();
            }
        }



    }
}