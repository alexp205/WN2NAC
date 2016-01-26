package org.cook_team.wn2nac;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import ch.skywatch.windoo.api.JDCWindooMeasurement;
import de.greenrobot.event.EventBus;

public class Wn2nacHistory extends BaseExpandableListAdapter {

    private static EventBus bus = EventBus.getDefault();

    public Wn2nacHistory() {
       // if (!bus.isRegistered(this)) bus.register(this);
    }

    public static List<WindooMeasurement> measurement = new ArrayList<>();
    public static int nextSeq = 0;

    /*public void onEventMainThread(Wn2nacMeasure.MeasureSaveEvent event) {
        Wn2nacMeasure.currentMeasurement.setSeq(nextSeq++);
        measurement.add(Wn2nacMeasure.currentMeasurement);
        Wn2nacPreferences.write();
        write(Wn2nacMeasure.currentMeasurement);
    }*/

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        String[] items = {"量測開始時間: ", "量測結束時間: ", "緯度: ", "經度: ", "高度: ", "風速 (m/s): ", "溫度 (°c): ", "濕度 (%): ", "壓力 (hPa): ", "傳送"};
        String text = items[childPosititon];
        try {
            switch (childPosititon) {
                case 0:
                    text += WindooEvent.dateFormat.format(measurement.get(groupPosition).getCreatedAt()); break;
                case 1:
                    text += WindooEvent.dateFormat.format(measurement.get(groupPosition).getUpdatedAt()); break;
                case 2:
                    text += measurement.get(groupPosition).getLatitude(); break;
                case 3:
                    text += measurement.get(groupPosition).getLongitude(); break;
                case 4:
                    text += measurement.get(groupPosition).getAltitude(); break;
                case 5:
                    text += String.format("%.2f", (double) Wn2nacService.liveMeasurement.getWind()); break;
                case 6:
                    text += String.format("%.2f", (double) Wn2nacService.liveMeasurement.getTemperature()); break;
                case 7:
                    text += String.format("%.2f", (double) Wn2nacService.liveMeasurement.getHumidity()); break;
                case 8:
                    text += String.format("%.2f", (double) Wn2nacService.liveMeasurement.getPressure()); break;
            }
        } catch(Exception exception){}
        return text;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) Wn2nacService.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView childTextView = (TextView) convertView.findViewById(R.id.lblListItem);
        childTextView.setText(String.valueOf(getChild(groupPosition, childPosition)));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 10;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return measurement.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return measurement.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) Wn2nacService.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        try {
            lblListHeader.setText(WindooEvent.dateFormat.format(measurement.get(groupPosition).getUpdatedAt()));
        } catch (Exception e) {}

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return (childPosition == 9);
    }

    public static void write(WindooMeasurement currentMeasurement) {
        try {
            String filename = "org.cook_team.wn2nac.HISTORY." + Integer.toString(currentMeasurement.getSeq());
            SharedPreferences sharedPref = Wn2nacService.context.getSharedPreferences(filename, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("Seq", currentMeasurement.getSeq());
            editor.putString("ID", currentMeasurement.getNickname());
            editor.putString("StartedAt", WindooEvent.dateFormat.format(currentMeasurement.getCreatedAt()));
            editor.putString("FinishedAt", WindooEvent.dateFormat.format(currentMeasurement.getUpdatedAt()));
            if (!(currentMeasurement.getSentAt() == null))
                editor.putString("SentAt", WindooEvent.dateFormat.format(currentMeasurement.getSentAt()));
            editor.putString("Latitude", currentMeasurement.getLatitude().toString());
            editor.putString("Longitude", currentMeasurement.getLongitude().toString());
            editor.putString("Altitude", currentMeasurement.getAltitude().toString());
            editor.putString("Wind", currentMeasurement.getWind().toString());
            editor.putString("Temperature", currentMeasurement.getTemperature().toString());
            editor.putString("Humidity", currentMeasurement.getHumidity().toString());
            editor.putString("Pressure", currentMeasurement.getPressure().toString());
            //editor.putString("Orientation", currentMeasurement.getOrientation());
            editor.commit(); }
        catch (Exception e) {
            bus.post(new Wn2nacService.ToastEvent("量測記錄儲存失敗" + "\n" + e.toString() + "\n" + e.getStackTrace()));
        }
    }

    public static void read() {
        try {
            File prefsdir = new File(Wn2nacService.context.getApplicationInfo().dataDir,"shared_prefs");
            File files[] = prefsdir.listFiles();
            if(!(files == null)) for (File file : files) {
                if((!file.getName().equals("org.cook_team.wn2nac.PREFERENCE_FILE_KEY.xml")) && (!file.getName().equals("org.cook_team.wn2nac_preferences.xml"))) {
                    WindooMeasurement currentMeasurement = new WindooMeasurement();
                    SharedPreferences sharedPref = Wn2nacService.context.getSharedPreferences(file.getName().substring(0, file.getName().length() - 4), Context.MODE_PRIVATE);
                    currentMeasurement.setSeq(sharedPref.getInt("Seq", 0));
                    currentMeasurement.setNickname(sharedPref.getString("ID", "ID"));
                    currentMeasurement.setLatitude(Double.valueOf(sharedPref.getString("Latitude", "0")));
                    currentMeasurement.setLongitude(Double.valueOf(sharedPref.getString("Longitude", "0")));
                    currentMeasurement.setAltitude(Double.valueOf(sharedPref.getString("Altitude", "0")));
                    currentMeasurement.setWind(Double.valueOf(sharedPref.getString("Wind", "0")));
                    currentMeasurement.setTemperature(Double.valueOf(sharedPref.getString("Temperature", "0")));
                    currentMeasurement.setHumidity(Double.valueOf(sharedPref.getString("Humidity", "0")));
                    currentMeasurement.setPressure(Double.valueOf(sharedPref.getString("Pressure", "0")));
                    String createdAt = sharedPref.getString("StartedAt", null);
                    if(!(createdAt == null)) currentMeasurement.setCreatedAt(WindooEvent.dateFormat.parse(createdAt));
                    String updatedAt = sharedPref.getString("FinishedAt", null);
                    if(!(updatedAt == null)) currentMeasurement.setUpdatedAt(WindooEvent.dateFormat.parse(updatedAt));
                    String sentAt = sharedPref.getString("SentAt", null);
                    if(!(sentAt == null)) currentMeasurement.setSentAt(WindooEvent.dateFormat.parse(sentAt));
                    measurement.add(currentMeasurement);
                }
            }
        } catch (Exception e) {
            bus.post(new Wn2nacService.ToastEvent("量測記錄讀取失敗" + "\n" + e.toString() + "\n" + e.getStackTrace()));
        }
    }
}