package com.example.accountkeeping.Data;


import android.content.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataBank {
    public static final String DATA_FILE_NAME = "data";
    private final Context context;
    //List<DayAccountRecord> dayAccountRecords;
    AllAccountManager allAccountManager;
    public DataBank(Context context) {
        this.context=context;
    }

    public /*List<DayAccountRecord>*/AllAccountManager loadData() {
        //dayAccountRecords =new ArrayList<>();
        allAccountManager = new AllAccountManager();
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(context.openFileInput(DATA_FILE_NAME));
            //dayAccountRecords = (ArrayList<DayAccountRecord>) objectInputStream.readObject();
            allAccountManager = (AllAccountManager) objectInputStream.readObject();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        //return dayAccountRecords;
        allAccountManager.updateRestAbbr();
        return allAccountManager;
    }

    public void saveData() {
        ObjectOutputStream objectOutputStream=null;
        try{
            objectOutputStream = new ObjectOutputStream(context.openFileOutput(DATA_FILE_NAME, Context.MODE_PRIVATE));
            //objectOutputStream.writeObject(dayAccountRecords);
            objectOutputStream.writeObject(allAccountManager);
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
