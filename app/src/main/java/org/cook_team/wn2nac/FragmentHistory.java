package org.cook_team.wn2nac;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import de.greenrobot.event.EventBus;
import me.grantland.widget.AutofitTextView;

public class FragmentHistory extends android.support.v4.app.Fragment implements ExpandableListView.OnChildClickListener, View.OnClickListener {

    private static EventBus bus = EventBus.getDefault();

    private HistoryListAdpater historyListAdpater = new HistoryListAdpater();
    ExpandableListView expListView;
    Button exportButton;

    public final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        expListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        expListView.setAdapter(historyListAdpater);
        expListView.setOnChildClickListener(this);

        exportButton = (Button) rootView.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return true;
    }

    public void onEventMainThread(WnHistory.DatasetChangedEvent event) { historyListAdpater.notifyDataSetChanged(); }

    /** HISTORY LIST ADAPTER **/

    public class HistoryListAdpater extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() { return WnHistory.size(); }

        @Override
        public long getGroupId(int groupPosition) { return groupPosition; }

        @Override
        public Object getGroup(int groupPosition) { return WnHistory.get(groupPosition); }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) WnService.context().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group, null);
            }
            AutofitTextView    time = (AutofitTextView) convertView.findViewById(R.id.time),
                                time2 = (AutofitTextView) convertView.findViewById(R.id.time2),
                                latlon = (AutofitTextView) convertView.findViewById(R.id.latlon),
                                wind = (AutofitTextView) convertView.findViewById(R.id.wind),
                                humidity = (AutofitTextView) convertView.findViewById(R.id.humidity),
                                temperature = (AutofitTextView) convertView.findViewById(R.id.temperature),
                                pressure = (AutofitTextView) convertView.findViewById(R.id.pressure),
                                sent = (AutofitTextView) convertView.findViewById(R.id.sent);
            Button  buttonGo = (Button) convertView.findViewById(R.id.buttonGo),
                    buttonSend = (Button) convertView.findViewById(R.id.buttonSend);

            final WindooMeasurement measurement = WnHistory.get(groupPosition);

            buttonGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WnMap.goTo = measurement;
                    bus.post(new WnMap.GotoEvent());
                }
            });
            buttonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bus.post(new WnNetwork.SendEvent(measurement));
                }
            });

            time.setText(new SimpleDateFormat("yyyy/MM/dd").format(measurement.getTimeStarted()));
            time2.setText(new SimpleDateFormat("HH:mm:ss").format(measurement.getTimeStarted()) + " ~ " + new SimpleDateFormat("HH:mm:ss").format(measurement.getTimeFinished()));
            latlon.setText(String.format("%.6f", (double) measurement.getLastLatitude()) + "\n" + String.format("%.6f", (double) measurement.getLastLongitude()));
            temperature.setText(String.format("%.2f", (double) measurement.getAvgTemperature()));
            humidity.setText(String.format("%.2f", (double) measurement.getAvgHumidity()));
            pressure.setText(String.format("%.2f", (double) measurement.getAvgPressure()));
            wind.setText(String.format("%.2f", (double) measurement.getAvgWind()));
            if (measurement.getTimeSent() > 0) {
                sent.setText("已傳送");
                buttonSend.setVisibility(View.GONE);
            }
            else {
                sent.setText("未傳送");
                buttonSend.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) { return 13; }

        @Override
        public long getChildId(int groupPosition, int childPosition) { return childPosition; }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            String[] title = {"編號: ", "測量開始時間: ", "測量結束時間: ", "緯度: ", "經度: ", "高度: ",
                    "溫度 (°c): ", "濕度 (%): ", "壓力 (hPa): ",  "風速 (m/s): ", "風向: ", "傳送時間: ", "Filename: "};
            String text = title[childPosititon];
            WindooMeasurement measurement = WnHistory.get(groupPosition);
            switch (childPosititon) {
                case 0:
                    text += measurement.getMeasurementID(); break;
                case 1:
                    text += dateFormat.format(measurement.getTimeStarted()); break;
                case 2:
                    text += dateFormat.format(measurement.getTimeFinished()); break;
                case 3:
                    text += measurement.getLastLatitude(); break;
                case 4:
                    text += measurement.getLastLongitude(); break;
                case 5:
                    text += measurement.getLastAltitude(); break;
                case 6:
                    text += String.format("%.2f", (double) measurement.getAvgTemperature()); break;
                case 7:
                    text += String.format("%.2f", (double) measurement.getAvgHumidity()); break;
                case 8:
                    text += String.format("%.2f", (double) measurement.getAvgPressure()); break;
                case 9:
                    text += String.format("%.2f", (double) measurement.getAvgWind()); break;
                case 10:
                    if (measurement.getOrientation() != -9999)
                        text += String.format("%.2f", (double) (-measurement.getOrientation()));
                    break;
                case 11:
                    if (measurement.getTimeSent() > 0)
                        text += dateFormat.format(measurement.getTimeSent());
                    break;
                case 12:
                        text += measurement.getFilename();
                    break;
            }
            return text;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.list_item, null);
            TextView childTextView = (TextView) linearLayout.findViewById(R.id.lblListItem);
            childTextView.setText(String.valueOf(getChild(groupPosition, childPosition)));
            return linearLayout;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void export()
    {
        String FILENAME = "measurement.csv";
        File file = new File(Environment.getExternalStorageDirectory(), FILENAME);

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println("User ID,Windoo ID,Time started,Time finished,Time sent,Latitude,Longitude,Temperature, Humidity,Pressure,Wind speed,Wind direction");
            for(int i=0; i<WnHistory.size(); i++) {
                WindooMeasurement measurement = WnHistory.get(i);
                pw.println(//measurement.getMeasurementID() + "," +
                        measurement.getUserID() + "," +
                         measurement.getWindooID() + "," +
                        ((measurement.getTimeStarted() > 0) ? dateFormat.format(measurement.getTimeStarted()) : "") + "," +
                        ((measurement.getTimeFinished() > 0) ? dateFormat.format(measurement.getTimeFinished()) : "") + "," +
                        ((measurement.getTimeSent() > 0) ? dateFormat.format(measurement.getTimeSent()) : "") + "," +
                        measurement.getLastLatitude() + "," +
                        measurement.getLastLongitude() + "," +
                        measurement.getAvgTemperature() + "," +
                        measurement.getAvgHumidity() + "," +
                        measurement.getAvgPressure() + "," +
                        measurement.getAvgWind() + "," +
                        ((measurement.getOrientation() != -9999.0) ? measurement.getOrientation() : "")
                );
            }
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        bus.post(new WnService.ToastEvent("量測資料已匯出至" + FILENAME));
    }

    @Override
    public void onClick(View v) {
        if (v == exportButton)
            export();
    }
}

