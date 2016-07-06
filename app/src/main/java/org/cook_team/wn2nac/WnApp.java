package org.cook_team.wn2nac;

import android.app.Application;
import android.content.Context;

public class WnApp extends Application {

    private boolean show_screen = true;
    private boolean use_en = false;
    private boolean use_zh = false;
    private boolean use_es = false;

    //Strings for code that cannot directly access strings.xml files.
    private String fragmentwindoograph2;
    private String fragmentwindoograph3;
    private String wnhistory1;
    private String wnhistory2;
    private String wnlocation1;
    private String wnlocation2;
    private String wnlocation3;
    private String wnlocation4;
    private String wnlocation5;
    private String wnlocation6;
    private String wnlocation7;
    private String wnlocation8;
    private String wnlocation9;
    private String wnmeasure1;
    private String wnmeasure2;
    private String wnnetwork1;
    private String wnnetwork2;
    private String wnnetwork3;
    private String wnnotification;
    private String wnsettings;

    private static Context context;
    public static Context getContext(){ return context; }
    private static WnApp instance = null;
    public static WnApp getInstance() {return instance;}

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        instance = this;
    }

    public void initializeStrings() {
        if (use_en) {
            fragmentwindoograph2 = "min";
            fragmentwindoograph3 = "sec ago";
            wnhistory1 = "Measurement Record Read Failure\n";
            wnhistory2 = "Measurement Record Save Failure\n";
            wnlocation1 = "Location Enabled";
            wnlocation2 = "Failed to Enable Location";
            wnlocation3 = "Location Disabled";
            wnlocation4 = "Failed to Disable Location";
            wnlocation5 = "Last Location Retrieved";
            wnlocation6 = "Failed to Retrieve Last Location";
            wnlocation7 = "New Location: ";
            wnlocation8 = "Location Provider Enabled:\n";
            wnlocation9 = "Location Provider Disabled:\n";
            wnmeasure1 = "No location information, make sure that positioning is on.";
            wnmeasure2 = "Lack of data, the measuring failed.";
            wnnetwork1 = "Sending...";
            wnnetwork2 = "Measurement Data Transferred Successfully";
            wnnetwork3 = "Measurement Data Transfer Failure\nData Saved\nPlease Attempt Transfer Later";
            wnnotification = "Windoo Observation Sensor, Press X to Close";
            wnsettings = "Settings Saved";
        } else if (use_zh) {
            fragmentwindoograph2 = "分";
            fragmentwindoograph3 = "秒前";
            wnhistory1 = "測量記錄讀取失敗\n";
            wnhistory2 = "測量記錄儲存失敗\n";
            wnlocation1 = "位置啟用";
            wnlocation2 = "無法啟用定位";
            wnlocation3 = "位置禁用";
            wnlocation4 = "無法停用位置";
            wnlocation5 = "最後位置檢索";
            wnlocation6 = "無法檢索最後位置";
            wnlocation7 = "新位置: ";
            wnlocation8 = "位置提供商啟用:\n";
            wnlocation9 = "位置提供者禁用:\\n";
            wnmeasure1 = "沒有位置資訊，請確認定位已開啟";
            wnmeasure2 = "數據不足，測量失敗";
            wnnetwork1 = "傳送中...";
            wnnetwork2 = "量測資料傳送成功";
            wnnetwork3 = "量測資料傳送失敗\\n資料已儲存\\n請稍後再傳送";
            wnnotification = "Windoo儀器觀測中，按X以關閉";
            wnsettings = "設定已儲存";
        }
    }

    public boolean getShow_screen() {return show_screen;}
    public void turnOffShow_screen() {
        show_screen = false;
    }

    public boolean getUse_en() {return use_en;}
    public void setUse_en() {
        use_zh = false;
        use_en = true;
        use_es = false;
    }

    public boolean getUse_zh() {return use_zh;}
    public void setUse_zh() {
        use_zh = true;
        use_en = false;
        use_es = false;
    }

    public boolean getUse_es() {return use_es;}
    public void setUse_es() {
        use_en = false;
        use_zh = false;
        use_es = true;
    }

    public String getFragmentwindoograph2() {return fragmentwindoograph2;}
    public String getFragmentwindoograph3() {return fragmentwindoograph3;}
    public String getWnhistory1() {return wnhistory1;}
    public String getWnhistory2() {return wnhistory2;}
    public String getWnlocation1() {return wnlocation1;}
    public String getWnlocation2() {return wnlocation2;}
    public String getWnlocation3() {return wnlocation3;}
    public String getWnlocation4() {return wnlocation4;}
    public String getWnlocation5() {return wnlocation5;}
    public String getWnlocation6() {return wnlocation6;}
    public String getWnlocation7() {return wnlocation7;}
    public String getWnlocation8() {return wnlocation8;}
    public String getWnlocation9() {return wnlocation9;}
    public String getWnmeasure1() {return wnmeasure1;}
    public String getWnmeasure2() {return wnmeasure2;}
    public String getWnnetwork1() {return wnnetwork1;}
    public String getWnnetwork2() {return wnnetwork2;}
    public String getWnnetwork3() {return wnnetwork3;}
    public String getWnnotification() {return wnnotification;}
    public String getWnsettings() {return wnsettings;}
}

