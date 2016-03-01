package org.cook_team.wn2nac;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class FragmentWindooGraph extends android.support.v4.app.Fragment {

    private static EventBus bus = EventBus.getDefault();

    /** FRAGMENT **/

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_graph, container, false);

        chartTemperature = (LineChart) rootView.findViewById(R.id.chartTemperature);
        chartHumidity = (LineChart) rootView.findViewById(R.id.chartHumidity);
        chartPressure = (LineChart) rootView.findViewById(R.id.chartPressure);
        chartWind = (LineChart) rootView.findViewById(R.id.chartWind);

        init();

        return rootView;
    }

    /** DATE FORMAT **/
    private static SimpleDateFormat timeFormat1 = new SimpleDateFormat("HH:mm:ss"),
                                    timeFormat2 = new SimpleDateFormat("HH:mm");

    /** CHARTs **/
    private static ArrayList<LineChart> charts = new ArrayList<>();
    private LineChart chartTemperature, chartHumidity, chartPressure, chartWind;

    private static boolean autoscroll = true;

    /** Chart DATA SETs **/
    private static ArrayList<String> timeData = new ArrayList<>();
    private static LineDataSet temperatureData = new LineDataSet(new ArrayList<Entry>(), "Temperature"),
                                humidityData = new LineDataSet(new ArrayList<Entry>(), "Humidity"),
                                pressureData = new LineDataSet(new ArrayList<Entry>(), "Pressure"),
                                windData = new LineDataSet(new ArrayList<Entry>(), "Wind speed");

    /** Chart VALUE FORMATTERs **/
    public static class TimeValueFormatter implements XAxisValueFormatter {
        @Override
        public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
            float ago = (System.currentTimeMillis()-Long.valueOf(original))/1000.0f;
            return ago < 60 ?
                    String.valueOf(Math.round(ago)) + "秒前" :
                    String.valueOf(Math.round(ago/60.0f)) + "分前"; // + (timeFormat2).format(new Date(Long.valueOf(original))) + ")";
        }
    }

    public static class WindooValueFormatter implements YAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return value<100 || yAxis.getAxisMaxValue()-yAxis.getAxisMinValue()<10 ? String.format("%.1f", value) : String.format("%.0f", value);
        }
    }

    /** Chart LISTENERs **/
    private abstract static class OnChartTranslateListener implements OnChartGestureListener {
        @Override
        public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
        @Override
        public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
        @Override
        public void onChartLongPressed(MotionEvent me) {}
        @Override
        public void onChartDoubleTapped(MotionEvent me) {}
        @Override
        public void onChartSingleTapped(MotionEvent me) {}
        @Override
        public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {}
        @Override
        public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}
        @Override
        public void onChartTranslate(MotionEvent me, float dX, float dY) {
        }
    }
    private static class chartTranslateListener extends OnChartTranslateListener {
        private LineChart originChart;
        public chartTranslateListener(LineChart chart) { originChart = chart; }
        @Override
        public void onChartTranslate(MotionEvent me, float dX, float dY) {
            autoscroll = (originChart.getHighestVisibleXIndex() == originChart.getXValCount()-1);
            for (LineChart chart:charts) chart.moveViewToX(originChart.getLowestVisibleXIndex()-1);
        }
    }
    private static class chartSelectListener implements OnChartValueSelectedListener {
        @Override
        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
            for (LineChart chart:charts) chart.highlightValue(h.getXIndex(), dataSetIndex);
        }
        public void onNothingSelected() {}
    }

    /** INIT **/

    private void init() {
        charts.clear();
        charts.add(chartTemperature);
        charts.add(chartHumidity);
        charts.add(chartPressure);
        charts.add(chartWind);

        chartTemperature.setData(new LineData(timeData, temperatureData));
        chartHumidity.setData(new LineData(timeData, humidityData));
        chartPressure.setData(new LineData(timeData, pressureData));
        chartWind.setData(new LineData(timeData, windData));

        for (LineChart chart:charts) {
            initChart(chart);
            initDataSet((LineDataSet) chart.getData().getDataSetByIndex(0));
        }

        chartTemperature.getXAxis().setDrawLabels(true);
        chartTemperature.setViewPortOffsets(120, 54, 18, 22);

        updateChart();
    }

    private static void initChart(LineChart chart) {
        chart.setGridBackgroundColor(Color.parseColor("#424242"));
        chart.setDescription("");
        chart.getLegend().setEnabled(false);
        chart.setViewPortOffsets(120, 22, 18, 22);
        chart.setScaleEnabled(false);
        chart.setOnChartGestureListener(new chartTranslateListener(chart));
        chart.setOnChartValueSelectedListener(new chartSelectListener());

        /** X axis **/
        chart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
        chart.getXAxis().setTextSize(10);
        chart.getXAxis().setTextColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.primary_text_dark));
        chart.getXAxis().setGridColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.secondary_text_dark));
        chart.getXAxis().setValueFormatter(new TimeValueFormatter());
        chart.getXAxis().setSpaceBetweenLabels(6);
        //chart.getXAxis().setAvoidFirstLastClipping(true);
        chart.getXAxis().setDrawLabels(false);

        /** Y axis **/
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setTextSize(10);
        chart.getAxisLeft().setTextColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.primary_text_dark));
        chart.getAxisLeft().setGridColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.secondary_text_dark));
        chart.getAxisLeft().setValueFormatter(new WindooValueFormatter());
        chart.getAxisLeft().setStartAtZero(false);
        chart.getAxisLeft().setLabelCount(5, true);
    }

    private static void initDataSet(LineDataSet data) {
        data.setAxisDependency(YAxis.AxisDependency.LEFT);
        data.setValueTextColor(android.R.color.secondary_text_dark);
        data.setCircleRadius(1);
        data.setLineWidth(2);
    }

    /** Chart TIME data **/
    private static long timeOffset = 15000;
    private static long timeFirst() {  return WnObserver.getInstance().getTimeFirstWindooData() - timeOffset; }
    private static long timeNow() { return System.currentTimeMillis(); }
    private static int sec(long time) { return (int) Math.round((time - timeFirst())/1000.0); }
    private static void fillTimeDataFrom(long timeFrom) { for(int i=sec(timeFrom);i<=sec(timeNow());i++) timeData.add(String.valueOf(timeFirst() + i * 1000)); }
    private static void updateTimeData() {
        if (timeFirst() > 0) {
            if (timeData.size() == 0) fillTimeDataFrom(timeFirst());
            else fillTimeDataFrom(Long.valueOf(timeData.get(timeData.size() - 1)) + 1000);
        }
    }
    private static void updateTemperatureData() {
        for (int i = temperatureData.getEntryCount(); i < WnObserver.getInstance().getTemperature().size(); i++)
            temperatureData.addEntry(new Entry(
                    (float) (double) WnObserver.getInstance().getTemperature().get(i),
                    sec(WnObserver.getInstance().getTemperature().getKey(i))));
    }
    private static void updatePressureData() {
        for (int i = pressureData.getEntryCount(); i < WnObserver.getInstance().getPressure().size(); i++)
            pressureData.addEntry(new Entry(
                    (float)(double) WnObserver.getInstance().getPressure().get(i),
                    sec(WnObserver.getInstance().getPressure().getKey(i))));
    }
    private static void updateHumidityData() {
        for (int i = humidityData.getEntryCount(); i < WnObserver.getInstance().getHumidity().size(); i++)
            humidityData.addEntry(new Entry(
                    (float)(double) WnObserver.getInstance().getHumidity().get(i),
                    sec(WnObserver.getInstance().getHumidity().getKey(i))));
    }
    private static void updateWindData() {
        for (int i = windData.getEntryCount(); i < WnObserver.getInstance().getWind().size(); i++)
            windData.addEntry(new Entry(
                    (float)(double) WnObserver.getInstance().getWind().get(i),
                    sec(WnObserver.getInstance().getWind().getKey(i))));
    }

    private static int chartTimeRange = 15;
    private void setTimeRange(int sec) {
        for(LineChart chart:charts) {
            chart.getXAxis().setLabelsToSkip(sec / 3);
            chart.setVisibleXRangeMinimum(sec);
            chart.setVisibleXRangeMaximum(sec);
        }
    }
    private void updateTemperatureChart() {
        updateTemperatureData();
        if (temperatureData.getYVals().size() > 0) {
            chartTemperature.notifyDataSetChanged();
            chartTemperature.invalidate(); // refresh
            float padding = 1.0f;
            chartTemperature.getAxisLeft().setAxisMinValue((float) Math.floor(temperatureData.getYMin()) - padding);
            chartTemperature.getAxisLeft().setAxisMaxValue((float) Math.ceil(temperatureData.getYMax()) + padding);
            if(autoscroll) chartTemperature.moveViewToX(timeData.size()); // move to newest data

            chartTemperature.getAxisLeft().removeAllLimitLines();
            ArrayList<LimitLine> limitLines = new ArrayList<>();
            limitLines.add(new LimitLine(temperatureData.getYMax(), "最高"));
            limitLines.add(new LimitLine((float) (double) WnObserver.getInstance().getAvgTemperature(), "平均"));
            limitLines.add(new LimitLine(temperatureData.getYMin(), "最低"));
            float pos = 0;
            for (LimitLine line:limitLines) {
                line.setLineColor(Color.MAGENTA);
                line.setLineWidth(1f);
                line.setTextColor(Color.WHITE);
                line.setTextSize(11f);
                line.setXOffset(pos += 40);
                chartTemperature.getAxisLeft().addLimitLine(line);
            }
        }
    }
    private void updateHumidityChart() {
        updateHumidityData();
        if (humidityData.getYVals().size() > 0) {
            chartHumidity.notifyDataSetChanged();
            chartHumidity.invalidate(); // refresh
            float padding = 1.0f;
            chartHumidity.getAxisLeft().setAxisMinValue((float) Math.floor(humidityData.getYMin()) - padding);
            chartHumidity.getAxisLeft().setAxisMaxValue((float) Math.ceil(humidityData.getYMax()) + padding);
            if(autoscroll) chartHumidity.moveViewToX(timeData.size()); // move to newest data

            chartHumidity.getAxisLeft().removeAllLimitLines();
            ArrayList<LimitLine> limitLines = new ArrayList<>();
            limitLines.add(new LimitLine(humidityData.getYMax(), "最高"));
            limitLines.add(new LimitLine((float) (double) WnObserver.getInstance().getAvgHumidity(), "平均"));
            limitLines.add(new LimitLine(humidityData.getYMin(), "最低"));
            float pos = 0;
            for (LimitLine line:limitLines) {
                line.setLineColor(Color.MAGENTA);
                line.setLineWidth(1f);
                line.setTextColor(Color.WHITE);
                line.setTextSize(11f);
                line.setXOffset(pos += 40);
                chartHumidity.getAxisLeft().addLimitLine(line);
            }
        }
    }
    private void updatePressureChart() {
        updatePressureData();
        if (pressureData.getYVals().size() > 0) {
            chartPressure.notifyDataSetChanged();
            chartPressure.invalidate(); // refresh
            float padding = 1.0f;
            chartPressure.getAxisLeft().setAxisMinValue((float) Math.floor(pressureData.getYMin()) - padding);
            chartPressure.getAxisLeft().setAxisMaxValue((float) Math.ceil(pressureData.getYMax()) + padding);
            if(autoscroll) chartPressure.moveViewToX(timeData.size()); // move to newest data

            chartPressure.getAxisLeft().removeAllLimitLines();
            ArrayList<LimitLine> limitLines = new ArrayList<>();
            limitLines.add(new LimitLine(pressureData.getYMax(), "最高"));
            limitLines.add(new LimitLine((float) (double) WnObserver.getInstance().getAvgPressure(), "平均"));
            limitLines.add(new LimitLine(pressureData.getYMin(), "最低"));
            float pos = 0;
            for (LimitLine line:limitLines) {
                line.setLineColor(Color.MAGENTA);
                line.setLineWidth(1f);
                line.setTextColor(Color.WHITE);
                line.setTextSize(11f);
                line.setXOffset(pos += 40);
                chartPressure.getAxisLeft().addLimitLine(line);
            }
        }
    }
    private void updateWindChart() {
        updateWindData();
        if (windData.getYVals().size() > 0) {
            chartWind.notifyDataSetChanged();
            chartWind.invalidate(); // refresh
            float padding = 1.0f;
            chartWind.getAxisLeft().setAxisMinValue(0);
            chartWind.getAxisLeft().setAxisMaxValue((float) Math.ceil(windData.getYMax()) + padding);
            if(autoscroll) chartWind.moveViewToX(timeData.size()); // move to newest data

            chartWind.getAxisLeft().removeAllLimitLines();
            ArrayList<LimitLine> limitLines = new ArrayList<>();
            limitLines.add(new LimitLine(windData.getYMax(), "最高"));
            limitLines.add(new LimitLine((float) (double) WnObserver.getInstance().getAvgWind(), "平均"));
            limitLines.add(new LimitLine(windData.getYMin(), "最低"));
            float pos = 0;
            for (LimitLine line:limitLines) {
                line.setLineColor(Color.MAGENTA);
                line.setLineWidth(1f);
                line.setTextColor(Color.WHITE);
                line.setTextSize(11f);
                line.setXOffset(pos += 40);
                chartWind.getAxisLeft().addLimitLine(line);
            }
        }
    }

    private void updateChart() {

        updateTimeData();

        WnObserver.getInstance().calcAverages();

        updateTemperatureChart();
        updateHumidityChart();
        updatePressureChart();
        updateWindChart();

        int[] timeRanges = {15, 30, 60, 120, 300, 600};
        if (autoscroll) {
            if(timeData.size() < timeRanges[1]) chartTimeRange = timeRanges[0];
            for (int timeRange: timeRanges)  if(timeData.size() > timeRange) chartTimeRange = timeRange;
        }
        setTimeRange(chartTimeRange);

    }

    public void onEventMainThread(WnObserver.WindooEvent event) {
        updateChart();
    }
}
