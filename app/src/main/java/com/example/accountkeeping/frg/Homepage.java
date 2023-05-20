package com.example.accountkeeping.frg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountkeeping.Data.AccountRecord;
import com.example.accountkeeping.Data.AllAccountManager;
import com.example.accountkeeping.Data.DataBank;
import com.example.accountkeeping.Data.DayAccountRecord;
import com.example.accountkeeping.InputActivity;
import com.example.accountkeeping.MainActivity;
import com.example.accountkeeping.R;
import com.example.accountkeeping.Util.TimeTools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.List;

public class Homepage extends Fragment {

    private MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private List<DayAccountRecord> dayAccountRecords ;
    private AllAccountManager allAccountManager;
    private DataBank dataBank;

    TextView textView_pure;
    TextView text_view_all_income;
    TextView text_view_all_expend;

    public Homepage(AllAccountManager allAccountManager, DataBank dataBank) {
        this.allAccountManager = allAccountManager;
        this.dayAccountRecords = allAccountManager.getDayAccountRecords();
        this.dataBank = dataBank;
        mainRecyclerViewAdapter =new MainRecyclerViewAdapter(dayAccountRecords);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_homepage, container, false);


        RecyclerView mainRecycleView=view.findViewById(R.id.recycle_view_day_account_record_items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mainRecycleView.setLayoutManager(layoutManager);
        mainRecycleView.setAdapter(mainRecyclerViewAdapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=null;
                intent = new Intent(((MainActivity) requireActivity()),InputActivity.class);
                String dateStr = TimeTools.getDate( allAccountManager.getCalendar());
                intent.putExtra("date",dateStr);
                intent.putExtra("op", 0);
                ((MainActivity) requireActivity()).launcherAdd.launch(intent);


            }
        });

        // 在fragment中注册一个广播用于接收,Activity中发送过来的消息
        IntentFilter filter = new IntentFilter();
        filter.addAction("home");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // 去做一些业务处理,比如联网操作, 解析操作...

                        int dateIndex = intent.getIntExtra("dateIndex",0);
                        int position = intent.getIntExtra("position",0);
                        mainRecyclerViewAdapter.notifyDataSetChanged();
//                        int op =intent.getIntExtra("op", -1);
//                        if(0 == op){//是新增
//                            if(-1 == dateIndex){ //新加一天
//                                if(-1==position){
//                                    return;
//                                }
//                                mainRecyclerViewAdapter.notifyItemInserted(position);
//                            }
//                            else{
//                                mainRecyclerViewAdapter.notifyItemChanged(dateIndex);
//                            }
//                        }else{//是修改
//                            mainRecyclerViewAdapter.notifyDataSetChanged();
//                        }




                        //更新总资产
                        setHeaderViewText();
                    }
                }, filter);


        textView_pure = view.findViewById(R.id.text_view_pure);
        text_view_all_income = view.findViewById(R.id.text_view_all_income);
        text_view_all_expend = view.findViewById(R.id.text_view_all_expend);
        setHeaderViewText();
        return view;

    }

    private void setHeaderViewText() {
        textView_pure.setText("￥" + allAccountManager.getPure().toString());
        text_view_all_income.setText("￥" + allAccountManager.getAll_income().toString());
        text_view_all_expend.setText("￥" + allAccountManager.getAll_expend().toString());
    }


    public class MainRecyclerViewAdapter extends RecyclerView.Adapter {
        private List<DayAccountRecord> dayAccountRecords;

        public MainRecyclerViewAdapter(List<DayAccountRecord> dayAccountRecords) {
            this.dayAccountRecords = dayAccountRecords;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.day_account_record_holder, parent, false);
                return new MainViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder Holder, int position) {
            MainViewHolder holder = (MainViewHolder) Holder;
            holder.getTextViewDate().setText(dayAccountRecords.get(position).getDate());
            holder.getTextViewIncome().setText(dayAccountRecords.get(position).getIncome()+"");
            holder.getTextViewExpend().setText(dayAccountRecords.get(position).getExpend() + "");
            holder.setSubRecyclerViewAdapter(new SubRecyclerViewAdapter(dayAccountRecords.get(position).getAccountRecords(),position));
            holder.getSubRecycleView().setAdapter(holder.getSubRecyclerViewAdapter());


        }

        @Override
        public int getItemCount() {
            return dayAccountRecords.size();
        }



        private class MainViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

            private final TextView textViewDate;
            private final TextView textViewIncome;
            private final TextView textViewExpend;
            public static final int MENU_ID_ADD = 1;
            private final int MENU_ID_DELETE=2;
            private SubRecyclerViewAdapter subRecyclerViewAdapter;

            public RecyclerView getSubRecycleView() {
                return subRecycleView;
            }

            private final RecyclerView subRecycleView;
            public MainViewHolder(View item_view) {
                super(item_view);
                this.textViewDate = item_view.findViewById(R.id.text_view_date1);
                this.textViewIncome = item_view.findViewById(R.id.text_view_income);
                this.textViewExpend = item_view.findViewById(R.id.text_view_expend);
                subRecycleView = item_view.findViewById(R.id.recycle_view_account_record_items);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                subRecycleView.setLayoutManager(layoutManager);

                item_view.findViewById(R.id.date_view).setOnCreateContextMenuListener(this);
            }

            public TextView getTextViewDate() {
                return textViewDate;
            }

            public TextView getTextViewIncome() {
                return textViewIncome;
            }

            public TextView getTextViewExpend() {
                return textViewExpend;
            }

            public SubRecyclerViewAdapter getSubRecyclerViewAdapter() {
                return subRecyclerViewAdapter;
            }
            public void setSubRecyclerViewAdapter(SubRecyclerViewAdapter subRecyclerViewAdapter) {
                this.subRecyclerViewAdapter=subRecyclerViewAdapter;
            }


            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                int position=getAdapterPosition();
                MenuItem add = contextMenu.add(Menu.NONE,MENU_ID_ADD,MENU_ID_ADD,getContext().getResources().getString(R.string.string_menu_add));
                MenuItem delete = contextMenu.add(Menu.NONE,MENU_ID_DELETE,MENU_ID_DELETE,getContext().getResources().getString(R.string.string_menu_delete));
                delete.setOnMenuItemClickListener(this);
                add.setOnMenuItemClickListener(this);
            }


            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int position = getAdapterPosition();
                Intent intent=null;
                switch(menuItem.getItemId()){
                    case MENU_ID_ADD:
                        intent = new Intent(((MainActivity) requireActivity()),InputActivity.class);
                        String dateStr =TimeTools.getDate(dayAccountRecords.get(position).getCalendar());
                        intent.putExtra("op", 0);
                        intent.putExtra("date",dateStr);
                        ((MainActivity) requireActivity()).launcherAdd.launch(intent);
                        break;
                    case MENU_ID_DELETE:
                        AlertDialog.Builder alertDB = new AlertDialog.Builder(getContext());
                        alertDB.setPositiveButton(getContext().getResources().getString(R.string.string_confirmation), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dayAccountRecords.remove(position);
                                dataBank.saveData();
                                MainRecyclerViewAdapter.this.notifyItemRemoved(position);
                                allAccountManager.updateRestAbbr();
                                setHeaderViewText();
                            }
                        });
                        alertDB.setNegativeButton(getContext().getResources().getString(R.string.string_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alertDB.setMessage(getContext().getResources().getString(R.string.string_confirm_delete) +dayAccountRecords.get(position).getDate()+"的记录"+"？");
                        alertDB.setTitle(getContext().getResources().getString(R.string.hint)).show();
                        break;
                }
                return false;
            }

        }
    }

    /*---------------------------内层recycle view-----------------------------*/
    public class SubRecyclerViewAdapter extends RecyclerView.Adapter {
        private List<AccountRecord> accountRecords;
        private int parent_position;
        public SubRecyclerViewAdapter(List<AccountRecord> accountRecords,int parent_position) {
            this.accountRecords = accountRecords;
            this.parent_position=parent_position;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.account_record_holder, parent, false);
            return new SubViewHolder(view);


        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder Holder, int position) {
            SubViewHolder holder = (SubViewHolder) Holder;
            holder.getImageView().setImageResource(accountRecords.get(position).getIconId());
            holder.getTextViewActivity().setText(accountRecords.get(position).getActivity());
            holder.getTextViewPayWay().setText(accountRecords.get(position).getPayWay());
            holder.getTextViewMoney().setText(accountRecords.get(position).getMoney().toString());
        }

        @Override
        public int getItemCount() {
            return accountRecords.size();
        }



        private class SubViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

            private final ImageView imageView;
            private final TextView textViewActivity;
            private final TextView textViewPayWay;
            private final TextView textViewMoney;


            public static final int MENU_ID_ADD = 1;
            public static final int MENU_ID_EDIT = 2;
            public static final int MENU_ID_DELETE = 3;

            public SubViewHolder(View item_view) {
                super(item_view);
                this.imageView = item_view.findViewById(R.id.image_view_account_record);
                this.textViewActivity= item_view.findViewById(R.id.text_view_activity);
                this.textViewPayWay = item_view.findViewById(R.id.text_view_pay_way);
                this.textViewMoney = item_view.findViewById(R.id.text_view_money);
                item_view.setOnCreateContextMenuListener(this);
            }


            public ImageView getImageView() {
                return imageView;
            }

            public TextView getTextViewActivity() {
                return textViewActivity;
            }

            public TextView getTextViewPayWay() {
                return textViewPayWay;
            }

            public TextView getTextViewMoney() {
                return textViewMoney;
            }

            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                int position=getAdapterPosition();

                MenuItem add = contextMenu.add(Menu.NONE,MENU_ID_ADD,MENU_ID_ADD,getContext().getResources().getString(R.string.string_menu_add));
                MenuItem edit = contextMenu.add(Menu.NONE,MENU_ID_EDIT,MENU_ID_EDIT,getContext().getResources().getString(R.string.string_menu_edit));
                MenuItem delete = contextMenu.add(Menu.NONE,MENU_ID_DELETE,MENU_ID_DELETE,getContext().getResources().getString(R.string.string_menu_delete));
                add.setOnMenuItemClickListener(this);
                edit.setOnMenuItemClickListener(this);
                delete.setOnMenuItemClickListener(this);
            }


            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int position = getAdapterPosition();
                Intent intent=null;
                String dateStr;
                String type;
                switch(menuItem.getItemId()){

                    case MENU_ID_ADD:
                        intent = new Intent(((MainActivity) requireActivity()),InputActivity.class);
                        dateStr =TimeTools.getDate(dayAccountRecords.get(parent_position).getCalendar());
                        intent.putExtra("date",dateStr);
                        intent.putExtra("op", 0);
                        ((MainActivity) requireActivity()).launcherAdd.launch(intent);
                        break;

                    case MENU_ID_EDIT:
                        intent = new Intent(((MainActivity) requireActivity()),InputActivity.class);
                        dateStr =TimeTools.getDate(dayAccountRecords.get(parent_position).getCalendar());
                        intent.putExtra("date",dateStr);
                        intent.putExtra("dateIndex",parent_position);
                        intent.putExtra("position",position);
                        if(Double.parseDouble(getTextViewMoney().getText().toString())>0){
                            type="收入";
                        }else{
                            type = "支出";
                        }
                        intent.putExtra("op", 1);
                        intent.putExtra("type", type);
                        intent.putExtra("money",Double.parseDouble(getTextViewMoney().getText().toString()));
                        intent.putExtra("payWay",getTextViewPayWay().getText().toString());
                        intent.putExtra("activity",getTextViewActivity().getText().toString());
                        ((MainActivity) requireActivity()).launcherAdd.launch(intent);
                        break;

                    case MENU_ID_DELETE:
                        AlertDialog.Builder alertDB = new AlertDialog.Builder(getContext());
                        alertDB.setPositiveButton(getContext().getResources().getString(R.string.string_confirmation), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                boolean delete_parent = allAccountManager.deleteAccountRecord( parent_position, position);
                                dataBank.saveData();
                                mainRecyclerViewAdapter.notifyDataSetChanged();
                                setHeaderViewText();
                            }
                        });
                        alertDB.setNegativeButton(getContext().getResources().getString(R.string.string_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alertDB.setMessage(getContext().getResources().getString(R.string.string_confirm_delete) +accountRecords.get(position).getActivity()+":"+accountRecords.get(position).getMoney()+"？");
                        alertDB.setTitle(getContext().getResources().getString(R.string.hint)).show();
                        break;
                }
                return false;
            }
        }
    }



}
