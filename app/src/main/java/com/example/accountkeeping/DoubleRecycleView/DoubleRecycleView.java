package com.example.accountkeeping.DoubleRecycleView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountkeeping.Data.AccountRecord;
import com.example.accountkeeping.Data.DataBank;
import com.example.accountkeeping.Data.DayAccountRecord;
import com.example.accountkeeping.InputActivity;
import com.example.accountkeeping.MainActivity;
import com.example.accountkeeping.R;
import com.example.accountkeeping.Util.TimeTools;



import java.util.List;

public class DoubleRecycleView {
    private MainActivity mainActivity;
    private Context context;
    private DataBank dataBank;
    private List<DayAccountRecord> dayAccountRecords;
    private MainRecyclerViewAdapter mainRecyclerViewAdapter;

    public DoubleRecycleView(MainActivity mainActivity, Context context, List<DayAccountRecord> dayAccountRecords, DataBank dataBank) {
        this.mainActivity = mainActivity;
        this.context = context;
        this.dataBank = dataBank;
        this.dayAccountRecords = dayAccountRecords;
        this.dataBank = dataBank;
        mainRecyclerViewAdapter =new MainRecyclerViewAdapter(dayAccountRecords);
    }

    public MainRecyclerViewAdapter getMainRecyclerViewAdapter() {
        return mainRecyclerViewAdapter;
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
            return new MainRecyclerViewAdapter.MainViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder Holder, int position) {
            MainRecyclerViewAdapter.MainViewHolder holder = (MainRecyclerViewAdapter.MainViewHolder) Holder;
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



        private class MainViewHolder extends RecyclerView.ViewHolder {

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
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                subRecycleView.setLayoutManager(layoutManager);

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
            return new SubRecyclerViewAdapter.SubViewHolder(view);


        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder Holder, int position) {
            SubRecyclerViewAdapter.SubViewHolder holder = (SubRecyclerViewAdapter.SubViewHolder) Holder;
            holder.getImageView().setImageResource(accountRecords.get(position).getIconId());
            holder.getTextViewActivity().setText(accountRecords.get(position).getActivity());
            holder.getTextViewPayWay().setText(accountRecords.get(position).getPayWay());
            holder.getTextViewMoney().setText(accountRecords.get(position).getMoney().toString());
        }

        @Override
        public int getItemCount() {
            return accountRecords.size();
        }



        private class SubViewHolder extends RecyclerView.ViewHolder {

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





        }
    }
}
