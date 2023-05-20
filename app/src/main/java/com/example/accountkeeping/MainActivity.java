package com.example.accountkeeping;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.example.accountkeeping.Data.AllAccountManager;
import com.example.accountkeeping.Data.DayAccountRecord;
import com.example.accountkeeping.Data.DataBank;
import com.example.accountkeeping.frg.Account;
import com.example.accountkeeping.frg.History;
import com.example.accountkeeping.frg.Homepage;
import com.example.accountkeeping.frg.More;
import com.example.accountkeeping.frg.Sort;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    NavigationView navigationView=null;
    private List<DayAccountRecord> dayAccountRecords;
    private DataBank dataBank;
    private AllAccountManager allAccountManager;

    Fragment fragment = null;
    Homepage homepageFragment = null;
    History historyFragment = null;
    Sort sortFragment =null;
    Account accountFragment =null;
    More moreFragment = null;



    public static final int INPUT_RESULT_CODE = 200;
    public ActivityResultLauncher<Intent> launcherAdd = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent input_intent = result.getData();
                    int resultCode = result.getResultCode();

                    if(resultCode== INPUT_RESULT_CODE){
                        int op =input_intent.getIntExtra("op", 0);//操作 0表示增加 1表示修改
                        String dateStr = input_intent.getStringExtra("date");
                        String activity = input_intent.getStringExtra("activity");
                        String payWay = input_intent.getStringExtra("payWay");
                        String type = input_intent.getStringExtra("type");
                        Double money = input_intent.getDoubleExtra("money",0 );
                        int iconID = input_intent.getIntExtra("iconID",0 );
                        if(type.equals("支出")){
                            money=-money;
                        }

                        if(0==op){ //增加
                            boolean date_same=false;
                            int dateIndex = -1;
                            int position = -1;

                            //判断是否有那一天的记录，如果有返回index
                            dateIndex = allAccountManager.checkDateExist(dateStr);
                            if(-1 == dateIndex){//没有那一天的记录
                                position = allAccountManager.addDayRecord(dateStr,activity,payWay,iconID,money);
                            }
                            else{//有那一天的记录
                                allAccountManager.addRecord(dateStr,dateIndex,activity,payWay,iconID,money);

                            }
                            dataBank.saveData();

                            Intent intent = new Intent();
                            intent.putExtra("op", op);
                            intent.putExtra("position",position);
                            intent.putExtra("dateIndex",dateIndex);
                            intent.setAction("home");
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                        }
                        else{//修改
                            int position = input_intent.getIntExtra("position",-1);
                            int dateIndex = input_intent.getIntExtra("dateIndex",-1);
                            if(-1 == position || -1==dateIndex){
                                return;
                            }else{
                                //返回新的日期
                                allAccountManager.modifyRecord(dateStr,dateIndex,position,activity,payWay,iconID,money);
                                dataBank.saveData();


                                Intent intent = new Intent();
                                intent.putExtra("op", op);
                                intent.setAction("home");
                                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                            }



                        }




                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //准备数据
        initData();

        homepageFragment = new Homepage(allAccountManager,dataBank);
        historyFragment = new History(allAccountManager,dataBank);
        sortFragment =new Sort(allAccountManager,dataBank);
        accountFragment =new Account(allAccountManager,dataBank);
        moreFragment = new More(allAccountManager,dataBank);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        //渲染主页的内容
        navigationView.setCheckedItem(R.id.nav_homepage);
        displaySelectedFragment(R.id.nav_homepage);

    }

    public void initData(){
        dataBank = new DataBank(this);
        //dayAccountRecords = dataBank.loadData();
        allAccountManager = dataBank.loadData();
        dayAccountRecords = allAccountManager.getDayAccountRecords();

    }


    @Override
    public void onBackPressed() {
        //当点后退键时侧滑菜单仍打开，则关闭侧滑菜单
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    //渲染菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //菜单项选择响应函数
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //更换片段
        displaySelectedFragment(item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //根据不同菜单项更换页面片段
    public void displaySelectedFragment(int item_id){

        switch (item_id){
            case R.id.nav_homepage:
                fragment = homepageFragment;

                break;
            case R.id.nav_history:
                fragment = historyFragment;
                break;
            case R.id.nav_sort:
                fragment = sortFragment;
                break;
            case R.id.nav_account:
                fragment = accountFragment;
                break;
            case R.id.nav_more:
                fragment = moreFragment;
                break;
        }
        if( fragment!=null ){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //this is where the id(R.id.container) of the FrameLayout in content_main.xml is being mentioned. Hence the fragment would be loaded into the framelayout
            ft.replace(R.id.container, fragment);
            ft.commit();
            setScreenTitle(item_id);
        }

    }


    //根据不同的菜单项改变屏幕标题
    public void setScreenTitle(int item_id){
        String title = "";
        switch (item_id){
            case R.id.nav_homepage:
                title=getString(R.string.menu_homepage);
                break;
            case R.id.nav_history:
                title=getString(R.string.menu_history);
                break;
            case R.id.nav_sort:
                title=getString(R.string.menu_sort);
                break;
            case R.id.nav_account:
                title=getString(R.string.menu_account);
                break;
            case R.id.nav_more:
                title=getString(R.string.menu_more);
                break;
        }
        getSupportActionBar().setTitle(title);
    }

}