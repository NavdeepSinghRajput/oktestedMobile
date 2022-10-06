package com.oktested.utils;

import com.oktested.dashboard.model.GetUserDataresponse;
import com.oktested.entity.AdSettingDataResponse;
import com.oktested.entity.AdSettingResponse;
import com.oktested.entity.AppSetting;
import com.oktested.entity.AppUpdate;
import com.oktested.home.model.AnchorsListModel;

import java.util.ArrayList;
import java.util.HashMap;

public class DataHolder {

    private static volatile DataHolder instance;
    public ArrayList<AnchorsListModel> anchorList = new ArrayList<>();
    public GetUserDataresponse getUserDataresponse;
    public AdSettingResponse adSettingResponse;
    public AppUpdate appUpdate;
    public AppSetting appSetting;
    public HashMap<String, AdSettingDataResponse.AdSettingValue> adSettingValueHashMap = new HashMap<>();

    private DataHolder() {
    }

    public synchronized static DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }
}