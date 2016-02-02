package org.cook_team.wn2nac;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import de.greenrobot.event.EventBus;

public class FragmentHistory extends android.support.v4.app.Fragment implements ExpandableListView.OnChildClickListener {

    private static EventBus bus = EventBus.getDefault();

    public static class HistoryListAdpater extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return WnHistory.history.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return WnHistory.history.get(groupPosition).getSeq();
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) WnService.context().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group, null);
            }
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView time2 = (TextView) convertView.findViewById(R.id.time2);
            TextView latlon = (TextView) convertView.findViewById(R.id.latlon);
            TextView wind = (TextView) convertView.findViewById(R.id.wind);
            TextView humidity = (TextView) convertView.findViewById(R.id.humidity);
            TextView temperature = (TextView) convertView.findViewById(R.id.temperature);
            TextView pressure = (TextView) convertView.findViewById(R.id.pressure);
            TextView sent = (TextView) convertView.findViewById(R.id.sent);
            Button buttonGo = (Button) convertView.findViewById(R.id.buttonGo);
            Button buttonSend = (Button) convertView.findViewById(R.id.buttonSend);

            final WindooMeasurement measurement = WnHistory.history.get(groupPosition);

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
                    bus.post(new WnNetwork.SendMeasurementEvent(measurement));
                }
            });

            time.setText(new SimpleDateFormat("yyyy/MM/dd").format(measurement.getCreatedAt()));
            time2.setText(new SimpleDateFormat("HH:mm:ss").format(measurement.getCreatedAt()) + " ~ " + new SimpleDateFormat("HH:mm:ss").format(measurement.getUpdatedAt()));
            latlon.setText(String.format("%.6f", (double) measurement.getLatitude()) + "\n" + String.format("%.6f", (double) measurement.getLongitude()));
            wind.setText(String.format("%.2f", (double) measurement.getWind()));
            humidity.setText(String.format("%.2f", (double) measurement.getHumidity()));
            temperature.setText(String.format("%.2f", (double) measurement.getTemperature()));
            pressure.setText(String.format("%.2f", (double) measurement.getPressure()));
            if (measurement.getSentAt() != null) {
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
        public int getChildrenCount(int groupPosition) {
            return 12;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            String[] title = {"編號: ", "測量開始時間: ", "測量結束時間: ", "緯度: ", "經度: ", "高度: ",
                    "溫度 (°c): ", "濕度 (%): ", "壓力 (hPa): ",  "風速 (m/s): ", "風向: ", "傳送時間: "};
            String text = title[childPosititon];
            WindooMeasurement measurement = WnHistory.history.get(groupPosition);
                switch (childPosititon) {
                    case 0:
                        text += String.valueOf(measurement.getSeq()); break;
                    case 1:
                        text += dateFormat.format(measurement.getCreatedAt()); break;
                    case 2:
                        text += dateFormat.format(measurement.getUpdatedAt()); break;
                    case 3:
                        text += measurement.getLatitude(); break;
                    case 4:
                        text += measurement.getLongitude(); break;
                    case 5:
                        text += measurement.getAltitude(); break;
                    case 6:
                        text += String.format("%.2f", (double) measurement.getTemperature()); break;
                    case 7:
                        text += String.format("%.2f", (double) measurement.getHumidity()); break;
                    case 8:
                        text += String.format("%.2f", (double) measurement.getPressure()); break;
                    case 9:
                        text += String.format("%.2f", (double) measurement.getWind()); break;
                    case 10:
                        if (measurement.getOrientation() != -9999)
                            text += String.format("%.2f", (double) (-measurement.getOrientation()));
                        break;
                    case 11:
                        if (measurement.getSentAt() != null)
                            text += dateFormat.format(measurement.getSentAt());
                        break;
                }
            return text;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) WnService.context().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }
            TextView childTextView = (TextView) convertView.findViewById(R.id.lblListItem);
            childTextView.setText(String.valueOf(getChild(groupPosition, childPosition)));

            return convertView;
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

    ExpandableListView expListView;
    HistoryListAdpater historyListAdpater = new HistoryListAdpater();

    public FragmentHistory() {
        WnSettings.read();
        WnHistory.read();
    }

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

    public void onEventMainThread(WnHistory.RefreshEvent event) {
        historyListAdpater.notifyDataSetChanged();
    }
}

