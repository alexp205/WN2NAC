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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

        initView();

        return rootView;
    }

    /** WINDOO CHARTs**/
    public static class WindooChart {

        /** OPTIONs **/
        public static boolean autoScroll = true;
        public static boolean autoTimeRange = true;
        public static long timeOffset = 15000;
        private static int[] timeRanges = {15, 30, 60, 120, 300, 600};

        /** MEMBERs **/
        private static List<String> timeData = new ArrayList<>();
        LineChart chart;
        LineDataSet dataSet;
        IndexedMap<Long, Double> source;
        Double avgSource;
        private float defaultVal;

        public LineChart getChart() {
            return chart;
        }

        /** CONSTRUCTOR **/
        public WindooChart(String name, LineChart chart, IndexedMap<Long, Double> source, Double avgSource, float defaultVal) {
            this.chart = chart;
            this.dataSet = new LineDataSet(new ArrayList<Entry>(), name);
            this.source = source;
            this.avgSource = avgSource;
            this.defaultVal = defaultVal;
            initChart();
            initDataSet();
        }

        /** INIT CHART **/
        private void initChart() {
            chart.setGridBackgroundColor(Color.parseColor("#424242"));
            chart.setDescription("");
            chart.getLegend().setEnabled(false);
            chart.setViewPortOffsets(120, 22, 18, 22);
            chart.setScaleEnabled(false);
            chart.setOnChartGestureListener(new chartTranslateListener(chart));
            chart.setOnChartValueSelectedListener(new chartSelectListener());
            chart.setDragDecelerationEnabled(false);

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

        /** INIT DATASET **/
        private void initDataSet() {
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setValueTextColor(android.R.color.secondary_text_dark);
            dataSet.setCircleRadius(1);
            dataSet.setLineWidth(2);
            dataSet.setColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.holo_blue_dark));
        }

        /** Chart TIME data **/
        private static long timeFirst() {  return WnObserver.getInstance().getTimeFirstWindooData() - timeOffset; }
        private static long timeNow() { return System.currentTimeMillis(); }
        private static int sec(long time) { return (int) Math.round((time - timeFirst())/1000.0); }
        private static void fillTimeDataFrom(long timeFrom) { for(int i=sec(timeFrom);i<=sec(timeNow());i++) timeData.add(String.valueOf(timeFirst() + i * 1000)); }
        private static boolean updateTime() {
            if (timeFirst() > 0) {
                if (timeData.size() == 0) fillTimeDataFrom(timeFirst());
                else fillTimeDataFrom(Long.valueOf(timeData.get(timeData.size() - 1)) + 1000);
                return true;
            } else return false;
        }
        private static int chartTimeRange = 15;
        private void setTimeRange(int sec) {
                chart.getXAxis().setLabelsToSkip(sec / 3);
                chart.setVisibleXRangeMinimum(sec);
                chart.setVisibleXRangeMaximum(sec);
        }

        /** UPDATE DATASET **/
        private void updateDataSet() {
            WnObserver.getInstance().calcAverages();
            for (int i = dataSet.getEntryCount(); i < source.size(); i++)
                dataSet.addEntry(new Entry(
                        (float) (double) source.get(i),
                        sec(source.getKey(i))));
            if (timeFirst() > 0 && dataSet.getEntryCount() == 0) // no data
                chart.setData(new LineData(timeData, new LineDataSet(Arrays.asList(new Entry(0, timeData.size()-1)), "")));
            else
                chart.setData(new LineData(timeData, dataSet));
            chart.notifyDataSetChanged();
            chart.invalidate(); // refresh
        }

        /** UPDATE CHART **/
        private void updateChart() {
            setTimeRange(chartTimeRange);
            if (dataSet.getYVals().size() > 0) {

                updateLimitLines();

                float padding = 1.0f;
                float min = chart.getYMin(), max = chart.getYMax();
                if (dataSet.getEntryCount() == 0) min = max = defaultVal;
                if (defaultVal == 0) min = padding;
                chart.getAxisLeft().setAxisMinValue((float) Math.floor( min - padding));
                chart.getAxisLeft().setAxisMaxValue((float) Math.ceil( max + padding));

                if(autoScroll) chart.moveViewToX(timeData.size()); // move to newest data

                if (autoTimeRange) {
                    if(timeData.size() < timeRanges[1]) chartTimeRange = timeRanges[0];
                    for (int timeRange: timeRanges)  if(timeData.size() > timeRange) chartTimeRange = timeRange;
                }
            }
        }

        private void updateLimitLines() {
            chart.getAxisLeft().removeAllLimitLines();
            List<LimitLine> limitLines = Arrays.asList(
                new LimitLine(dataSet.getYMax(),        "最高"),
                //new LimitLine((float)(double) avgSource, "平均"),
                new LimitLine(dataSet.getYMin(),        "最低")
            );
            float pos = 0;
            for (LimitLine limitLine:limitLines) {
                limitLine.setLineColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.holo_orange_dark));
                limitLine.setLineWidth(0.5f);
                limitLine.setTextColor(Color.WHITE);
                limitLine.setTextSize(11f);
                limitLine.setXOffset(pos += 40);
                chart.getAxisLeft().addLimitLine(limitLine);
            }
        }

        /** Chart VALUE FORMATTERs **/
        public static class TimeValueFormatter implements XAxisValueFormatter {
            private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            @Override
            public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
                int ago = Math.round((System.currentTimeMillis()-Long.valueOf(original))/1000.0f);
                return ago < 60 ?
                        String.valueOf(ago) + "秒前" :
                        String.valueOf(ago/60) + "分" + String.valueOf(ago%60) + "秒前"; // + (timeFormat2).format(new Date(Long.valueOf(original))) + ")";
            }
        }

        public static class WindooValueFormatter implements YAxisValueFormatter {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return value<100 || yAxis.getAxisMaxValue()-yAxis.getAxisMinValue()<10 ? String.format("%.1f", value) : String.format("%.0f", value);
            }
        }

        /** Chart LISTENERs **/
        private abstract class chartGestureListener implements OnChartGestureListener {
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
            public void onChartTranslate(MotionEvent me, float dX, float dY) {}
        }
        private class chartTranslateListener extends chartGestureListener {
            private LineChart originChart;
            public chartTranslateListener(LineChart chart) { originChart = chart; }
            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                autoScroll = (originChart.getHighestVisibleXIndex() == originChart.getXValCount()-1);
                for (LineChart chart:charts) chart.moveViewToX(originChart.getLowestVisibleXIndex()-1);
            }
        }
        private class chartSelectListener implements OnChartValueSelectedListener {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                for (LineChart chart:charts) chart.highlightValue(h.getXIndex(), dataSetIndex);
            }
            public void onNothingSelected() {
                for (LineChart chart:charts) chart.highlightValues(null);
            }
        }

    }

    /** WINDOO CHARTs **/
    private static List<LineChart> charts;
    private LineChart chartTemperature, chartHumidity, chartPressure, chartWind;
    private List<WindooChart> windoo;

    /** INIT **/
    private void initView() {
        charts = Arrays.asList(chartTemperature, chartHumidity, chartPressure, chartWind);
        windoo = Arrays.asList(
                new WindooChart("Temperature", chartTemperature, WnObserver.getInstance().getTemperature(), WnObserver.getInstance().getAvgTemperature(), 25.0f),
                new WindooChart("Humidity", chartHumidity, WnObserver.getInstance().getHumidity(), WnObserver.getInstance().getAvgHumidity(), 50.0f),
                new WindooChart("Pressure", chartPressure, WnObserver.getInstance().getPressure(), WnObserver.getInstance().getAvgPressure(), 1013.0f),
                new WindooChart("Wind", chartWind, WnObserver.getInstance().getWind(), WnObserver.getInstance().getAvgWind(), 0.0f)
        );
        chartTemperature.getXAxis().setDrawLabels(true);
        chartTemperature.setViewPortOffsets(120, 54, 18, 22);
        chartTemperature.getAxisLeft().setAxisMinValue(20);
        chartTemperature.getAxisLeft().setAxisMaxValue(30);
        chartHumidity.getAxisLeft().setAxisMinValue(20);
        chartHumidity.getAxisLeft().setAxisMaxValue(30);
        chartPressure.getAxisLeft().setAxisMinValue(20);
        chartPressure.getAxisLeft().setAxisMaxValue(30);
        chartWind.getAxisLeft().setAxisMinValue(20);
        chartWind.getAxisLeft().setAxisMaxValue(30);
        updateView();
    }

    private void updateView() {
        if (WindooChart.updateTime())
            for(WindooChart windooChart: windoo) {
                windooChart.updateDataSet();
                windooChart.updateChart();
            }
    }

    public void onEventMainThread(WnObserver.WindooEvent event) {
        updateView();
    }
}
