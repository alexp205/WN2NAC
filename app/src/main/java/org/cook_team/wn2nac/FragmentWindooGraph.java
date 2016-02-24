package org.cook_team.wn2nac;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class FragmentWindooGraph extends android.support.v4.app.Fragment {

    private static EventBus bus = EventBus.getDefault();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
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

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    LineChart chart;

    public class WindooValueFormatter implements YAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            //return timeFormat.format(new Date(Math.round(value)));
            return String.format("%.1f", value);
        }
    }

    public class TimeValueFormatter implements XAxisValueFormatter {
        @Override
        public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
            //return String.valueOf(sec(Long.valueOf(original)));
            return timeFormat.format(new Date(Long.valueOf(original)));
        }
    } // TODO: time before now

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_graph, container, false);

        /** LINE CHART **/
        chart = (LineChart) rootView.findViewById(R.id.chart);
        chart.setGridBackgroundColor(Color.parseColor("#424242"));
        chart.setDescription("");

        /** X axis **/
        chart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
        chart.getXAxis().setTextSize(12);
        chart.getXAxis().setTextColor(ContextCompat.getColor(getActivity(), android.R.color.primary_text_dark));
        chart.getXAxis().setGridColor(ContextCompat.getColor(getActivity(), android.R.color.secondary_text_dark));
        chart.getXAxis().setValueFormatter(new TimeValueFormatter());
        //chart.getXAxis().setAvoidFirstLastClipping(true);

        /** Y axis **/
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setTextSize(12);
        chart.getAxisLeft().setTextColor(ContextCompat.getColor(getActivity(), android.R.color.primary_text_dark));
        chart.getAxisLeft().setGridColor(ContextCompat.getColor(getActivity(), android.R.color.secondary_text_dark));
        //chart.getAxisLeft().setAxisMinValue(-25);
        //chart.getAxisLeft().setAxisMaxValue(60);
        chart.getAxisLeft().setValueFormatter(new WindooValueFormatter());

        /*temperatureData.setAxisDependency(YAxis.AxisDependency.LEFT);
        humidityData.setAxisDependency(YAxis.AxisDependency.LEFT);
        pressureData.setAxisDependency(YAxis.AxisDependency.LEFT);
        windData.setAxisDependency(YAxis.AxisDependency.LEFT);*/

        return rootView;
    }

    /** Chart DATA **/
    private static ArrayList<String> timeData = new ArrayList<>();
    private static LineDataSet  temperatureData = new LineDataSet(new ArrayList<Entry>(), "Temperature"),
            humidityData = new LineDataSet(new ArrayList<Entry>(), "Humidity"),
            pressureData = new LineDataSet(new ArrayList<Entry>(), "Pressure"),
            windData = new LineDataSet(new ArrayList<Entry>(), "Wind speed");

    private static long timeFirst() { return WnObserver.getInstance().getTimeFirstWindooData(); }
    private static long timeNow() { return System.currentTimeMillis(); }
    private static int sec(long time) { return (int) Math.round((time - timeFirst())/1000.0); }
    private static void fillTimeDataFrom(long timeFrom) { for(int i=sec(timeFrom);i<=sec(timeNow());i++) timeData.add(String.valueOf(timeFirst() + i * 1000)); }

    public void onEventMainThread(WnObserver.WindooEvent event) {

        if (timeFirst() > 0) {

            // Time data
            if (timeData.size() == 0) fillTimeDataFrom(timeFirst());
            else fillTimeDataFrom(Long.valueOf(timeData.get(timeData.size() - 1))+1000);

            switch (event.getType()) {

                case JDCWindooEvent.JDCWindooNewTemperatureValue:

                    // Temperature data
                    for (int i = temperatureData.getEntryCount(); i < WnObserver.getInstance().getTemperature().size(); i++) {
                        float val = (float) (double) WnObserver.getInstance().getTemperature().get(i);
                        long time = WnObserver.getInstance().getTemperature().getKey(i);
                        temperatureData.addEntry(new Entry(
                                val,         // value
                                sec(time)     // seconds
                        ));
                    }
                /*LimitLine ll = new LimitLine(WnObserver, "");
                ll.setLineColor(Color.RED);
                ll.setLineWidth(4f);
                ll.setTextColor(Color.BLACK);
                ll.setTextSize(12f);
                leftAxis.addLimitLine(ll);
                break;*/

            }

            //chart.zoom(float scaleX, float scaleY, float x, float y)
            chart.setData(new LineData(timeData, temperatureData));
            chart.notifyDataSetChanged();
            chart.invalidate(); // refresh
        }
    }
}
