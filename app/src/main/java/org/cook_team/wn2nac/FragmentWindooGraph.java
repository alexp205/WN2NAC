package org.cook_team.wn2nac;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

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

public class FragmentWindooGraph extends android.support.v4.app.Fragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private static EventBus bus = EventBus.getDefault();

    /** OPTIONs **/
    public boolean controlsBarVisible = true;
    public boolean chartBarVisible = true;

    public static boolean autoScroll = true;
    private static int[] timeRanges = {15, 30, 60, 120, 300, 600};
    public static int chartTimeRange = timeRanges[2];
    public static boolean drawLimitLines = false;
    private static int timeFormat = 1;

    private static int limitLineColor = ContextCompat.getColor(WnApp.getContext(), android.R.color.holo_orange_dark);
    private static float limitLineWidth = 0.7f;
    private static int limitLineTextColor = Color.WHITE;
    private static float limitLineTextSize = 10f;

    /** FRAGMENT **/
    public ToggleButton temperatureToggleButton, humidityToggleButton, pressureToggleButton, windToggleButton;
    private TableRow temperature, humidity, pressure, wind;
    private static Switch autoScrollSwitch;
    private CheckBox drawLimitLinesCheckbox;
    private Spinner timeRangeSpinner;
    private RadioGroup timeFormatRadio;
    private RadioButton timeFormatRadio1, timeFormatRadio2;
    private LinearLayout controlsBar, chartBar;

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

        controlsBar = (LinearLayout) rootView.findViewById(R.id.controlBar);
        chartBar = (LinearLayout) rootView.findViewById(R.id.chartBar);

        temperature = (TableRow) rootView.findViewById(R.id.temperature);
        humidity = (TableRow) rootView.findViewById(R.id.humidity);
        pressure = (TableRow) rootView.findViewById(R.id.pressure);
        wind = (TableRow) rootView.findViewById(R.id.wind);

        chartTemperature = (LineChart) rootView.findViewById(R.id.chartTemperature);
        chartHumidity = (LineChart) rootView.findViewById(R.id.chartHumidity);
        chartPressure = (LineChart) rootView.findViewById(R.id.chartPressure);
        chartWind = (LineChart) rootView.findViewById(R.id.chartWind);

        temperatureToggleButton = (ToggleButton) rootView.findViewById(R.id.temperatureToggleButton);
        humidityToggleButton = (ToggleButton) rootView.findViewById(R.id.humidityToggleButton);
        pressureToggleButton = (ToggleButton) rootView.findViewById(R.id.pressureToggleButton);
        windToggleButton = (ToggleButton) rootView.findViewById(R.id.windToggleButton);
        temperatureToggleButton.setOnClickListener(this);
        humidityToggleButton.setOnClickListener(this);
        pressureToggleButton.setOnClickListener(this);
        windToggleButton.setOnClickListener(this);

        drawLimitLinesCheckbox = (CheckBox) rootView.findViewById(R.id.drawLimitLinesCheckbox);
        drawLimitLinesCheckbox.setOnCheckedChangeListener(this);

        autoScrollSwitch = (Switch) rootView.findViewById(R.id.autoScrollSwitch);
        autoScrollSwitch.setOnCheckedChangeListener(this);

        timeRangeSpinner = (Spinner) rootView.findViewById(R.id.timeRangeSpinner);
        ArrayList<String> timeRangeStrings = new ArrayList<>();
        for (int timeRange : timeRanges) timeRangeStrings.add(timeRange<60 ? String.valueOf(timeRange)+"秒" : String.valueOf(timeRange/60)+"分");
        ArrayAdapter<String> timeRangeList = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, timeRangeStrings);
        timeRangeSpinner.setAdapter(timeRangeList);
        timeRangeSpinner.setOnItemSelectedListener(this);

        timeFormatRadio = (RadioGroup) rootView.findViewById(R.id.timeDisplayGroup);
        timeFormatRadio.setOnCheckedChangeListener(this);
        timeFormatRadio1 = (RadioButton) rootView.findViewById(R.id.timeDisplay1);
        timeFormatRadio2 = (RadioButton) rootView.findViewById(R.id.timeDisplay2);

        timeRangeSpinner.setSelection(2);
        autoScrollSwitch.setChecked(autoScroll);

        initView();

        return rootView;
    }

    @Override
    public void onClick(View v) {

        chartTemperature.setViewPortOffsets(120, 22, 18, 22);
        chartHumidity.setViewPortOffsets(120, 22, 18, 22);
        chartPressure.setViewPortOffsets(120, 22, 18, 22);
        chartWind.setViewPortOffsets(120, 22, 18, 22);
        chartTemperature.getXAxis().setDrawLabels(false);
        chartHumidity.getXAxis().setDrawLabels(false);
        chartPressure.getXAxis().setDrawLabels(false);
        chartWind.getXAxis().setDrawLabels(false);

        if (temperatureToggleButton.isChecked()) {
            chartTemperature.getXAxis().setDrawLabels(true);
            chartTemperature.setViewPortOffsets(120, 54, 18, 22);
        } else if (humidityToggleButton.isChecked()) {
            chartHumidity.getXAxis().setDrawLabels(true);
            chartHumidity.setViewPortOffsets(120, 54, 18, 22);
        } else if (pressureToggleButton.isChecked()) {
            chartPressure.getXAxis().setDrawLabels(true);
            chartPressure.setViewPortOffsets(120, 54, 18, 22);
        } else if (windToggleButton.isChecked()) {
            chartWind.getXAxis().setDrawLabels(true);
            chartWind.setViewPortOffsets(120, 54, 18, 22);
        }

        chartTemperature.invalidate();
        chartHumidity.invalidate();
        chartPressure.invalidate();
        chartWind.invalidate();

        temperature.setVisibility(temperatureToggleButton.isChecked() ? View.VISIBLE : View.GONE);
        humidity.setVisibility(humidityToggleButton.isChecked() ? View.VISIBLE : View.GONE);
        pressure.setVisibility(pressureToggleButton.isChecked() ? View.VISIBLE : View.GONE);
        wind.setVisibility(windToggleButton.isChecked() ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        autoScroll = autoScrollSwitch.isChecked();
        drawLimitLines = drawLimitLinesCheckbox.isChecked();
    }

    public static void setAutoScroll(boolean enabled) {
        autoScroll = enabled;
        autoScrollSwitch.setChecked(enabled);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
        chartTimeRange = timeRanges[position];
        clear();
        updateView();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId) {
            case R.id.timeDisplay1:
                timeFormat = 1;
                break;
            case R.id.timeDisplay2:
                timeFormat = 2;
                break;
        }
        updateView();
    }

    /** WINDOO CHARTs**/
    public static class WindooChart {

        /** MEMBERs **/
        private static List<String> timeData = new ArrayList<>();
        LineChart chart;
        LineDataSet dataSet;
        IndexedMap<Long, Double> source;
        private float defaultVal;

        public LineChart getChart() {
            return chart;
        }

        /** CONSTRUCTOR **/
        public WindooChart(String name, LineChart chart, IndexedMap<Long, Double> source, float defaultVal) {
            this.chart = chart;
            this.dataSet = new LineDataSet(new ArrayList<Entry>(), name);
            this.source = source;
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

            chart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
            chart.getXAxis().setTextSize(10);
            chart.getXAxis().setTextColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.primary_text_dark));
            chart.getXAxis().setGridColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.secondary_text_dark));
            chart.getXAxis().setValueFormatter(new TimeValueFormatter());
            chart.getXAxis().setSpaceBetweenLabels(6);
            //chart.getXAxis().setAvoidFirstLastClipping(true);
            chart.getXAxis().setDrawLabels(false);

            chart.getAxisRight().setEnabled(false);
            //chart.getAxisLeft().setTextSize(10);
            chart.getAxisLeft().setTextColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.primary_text_dark));
            chart.getAxisLeft().setGridColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.secondary_text_dark));
            chart.getAxisLeft().setValueFormatter(new WindooValueFormatter());
            chart.getAxisLeft().setStartAtZero(false);
            chart.getAxisLeft().setLabelCount(5, true);
        }

        /** INIT DATASET **/
        private void initDataSet() {
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setValueTextColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.primary_text_dark));
            dataSet.setValueFormatter(new WindooValueFormatter());
            dataSet.setValueTextSize(8);
            dataSet.setCircleRadius(1);
            dataSet.setLineWidth(2);
            dataSet.setColor(ContextCompat.getColor(WnApp.getContext(), android.R.color.holo_blue_dark));
        }

        /** Chart TIME data **/
        private static long timeFirst() {  return WnObserver.getInstance().getTimeFirstWindooData() - chartTimeRange*1000; }
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
        private void setTimeRange(int sec) {
            chartTimeRange = sec;
            chart.getXAxis().setLabelsToSkip(sec / 3 - 1);
            chart.setVisibleXRangeMinimum(sec);
            chart.setVisibleXRangeMaximum(sec);
            dataSet.setDrawValues(sec < 60);
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
        private float chartYMin, chartYMax;
        private void updateChart() {
            setTimeRange(chartTimeRange);
            if (dataSet.getYVals().size() > 0) {

                drawLimitLines();

                float padding = 0.25f;
                float min = chart.getYMin(), max = chart.getYMax();
                if (dataSet.getEntryCount() == 0) min = max = defaultVal;
                chartYMin = defaultVal == 0 ? 0 : (float) (Math.floor(2*(min-padding))/2.0f);
                chartYMax = (float) (Math.ceil(2*(max+padding))/2.0f);
                chart.getAxisLeft().setAxisMinValue(chartYMin);
                chart.getAxisLeft().setAxisMaxValue(chartYMax);

                if(autoScroll) chart.moveViewToX(timeData.size()); // move to newest data
            }
        }

        private void drawLimitLines() {
            chart.getAxisLeft().removeAllLimitLines();
            if (drawLimitLines) {
                LimitLine   high = new LimitLine(dataSet.getYMax(), WnApp.getContext().getString(R.string.limitLine_high)),
                            avg = new LimitLine((float)(double) source.getAvg(), WnApp.getContext().getString(R.string.limitLine_avg)),
                            low = new LimitLine(dataSet.getYMin(), WnApp.getContext().getString(R.string.limitLine_low));
                List<LimitLine> limitLines = Arrays.asList(high, avg, low);
                float chartHeight = chartYMax - chartYMin;
                high.setLabelPosition((chartYMax - dataSet.getYMax()) / chartHeight < 0.25 ? LimitLine.LimitLabelPosition.RIGHT_BOTTOM : LimitLine.LimitLabelPosition.RIGHT_TOP);
                low.setLabelPosition((dataSet.getYMin() - chartYMin) / chartHeight < 0.25 ? LimitLine.LimitLabelPosition.LEFT_TOP : LimitLine.LimitLabelPosition.LEFT_BOTTOM);
                avg.setLabelPosition((source.getAvg() - chartYMin) / chartHeight < 0.25 ? LimitLine.LimitLabelPosition.LEFT_TOP : LimitLine.LimitLabelPosition.LEFT_BOTTOM);
                avg.setXOffset(chart.getWidth()/8.0f);
                for (LimitLine limitLine:limitLines) {
                    limitLine.setLineColor(limitLineColor);
                    limitLine.setLineWidth(limitLineWidth);
                    limitLine.setTextColor(limitLineTextColor);
                    limitLine.setTextSize(limitLineTextSize);
                    chart.getAxisLeft().addLimitLine(limitLine);
                }
            }
        }

        public static void clearTimeData() { timeData.clear(); }
        public void clearDataSet() { dataSet.clear(); }

        /** Chart VALUE FORMATTERs **/
        public static class TimeValueFormatter implements XAxisValueFormatter {
            private static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
            @Override
            public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
                switch(timeFormat) {
                    default:
                    case 1:
                        int secsAgo = Math.round((System.currentTimeMillis()-Long.valueOf(original))/1000.0f);
                        return secsAgo < 60 ?
                                String.valueOf(secsAgo) + "秒前" :
                                String.valueOf(secsAgo/60) + "分" + String.valueOf(secsAgo%60) + "秒前";
                    case 2:
                        return timeFormatter.format(new Date(Long.valueOf(original)));
                }
            }
        }

        public static class WindooValueFormatter implements YAxisValueFormatter, ValueFormatter  {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return value<100 || yAxis.getAxisMaxValue()-yAxis.getAxisMinValue()<10 ? String.format("%.1f", value) : String.format("%.0f", value);
            }
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return value != 0 ? String.format("%.1f", value) : "";
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
                setAutoScroll(originChart.getHighestVisibleXIndex() == originChart.getXValCount()-1);
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
    public void initView() {

        controlsBar.setVisibility(controlsBarVisible ? View.VISIBLE : View.GONE);
        chartBar.setVisibility(chartBarVisible ? View.VISIBLE : View.GONE);

        charts = Arrays.asList(chartTemperature, chartHumidity, chartPressure, chartWind);
        windoo = Arrays.asList(
                new WindooChart("Temperature", chartTemperature, WnObserver.getInstance().getTemperature(), 25.0f),
                new WindooChart("Humidity", chartHumidity, WnObserver.getInstance().getHumidity(), 50.0f),
                new WindooChart("Pressure", chartPressure, WnObserver.getInstance().getPressure(), 1013.0f),
                new WindooChart("Wind", chartWind, WnObserver.getInstance().getWind(), 0.0f)
        );
        chartTemperature.getAxisLeft().setAxisMinValue(20);
        chartTemperature.getAxisLeft().setAxisMaxValue(30);
        chartHumidity.getAxisLeft().setAxisMinValue(20);
        chartHumidity.getAxisLeft().setAxisMaxValue(30);
        chartPressure.getAxisLeft().setAxisMinValue(20);
        chartPressure.getAxisLeft().setAxisMaxValue(30);
        chartWind.getAxisLeft().setAxisMinValue(20);
        chartWind.getAxisLeft().setAxisMaxValue(30);

        chartTemperature.getXAxis().setDrawLabels(true);
        chartTemperature.setViewPortOffsets(120, 54, 18, 22);

        updateView();
    }

    private void updateView() {

        if (WindooChart.updateTime())
            for (WindooChart windooChart: windoo) {
                windooChart.updateDataSet();
                windooChart.updateChart();
            }
    }

    private void clear() {
        WindooChart.clearTimeData();
        for(WindooChart windooChart: windoo) windooChart.clearDataSet();
    }


    public void onEventMainThread(WnObserver.WindooEvent event) {
        updateView();
    }
}
