package com.yin.mpandroidchartstudy.mychart;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;

import java.util.List;

/**
 * Created by YIN on 2018/4/18.
 * 图表的样式基类
 */

public abstract class BaseChartEntity<T extends Entry> {
    //图表
    protected BarLineChartBase mChart;

    //数据
    protected List<T>[] mEntries;
    //绘制的曲线描述
    protected String[] mLabels;
    //图表的颜色
    protected int[] mChartColor;
    //字体的大小
    protected float mTextSize;
    //颜色值
    protected int mValueColor;
   /*为true表示需要设置成虚线*/
    protected boolean[] mHasDotted;

    protected BaseChartEntity(BarLineChartBase chart,List<T>[] entries, String[] labels,int [] chartColor,int valueColor,float textSize){
            mChart = chart;
            mEntries = entries;
            mLabels = labels;
            mChartColor = chartColor;
            mValueColor = valueColor;
            mTextSize = textSize;
            initChart();

    }

    protected BaseChartEntity(BarLineChartBase chart,List<T>[] entries,String[] labels,int [] chartColor, int valueColor, float textSize,boolean[] hasDotted){
        mChart = chart;
        mEntries = entries;
        mLabels = labels;
        mChartColor = chartColor;
        mValueColor = valueColor;
        mTextSize = textSize;
        mHasDotted = hasDotted;
        initChart();
    }

    /**
     * 初始化chart
     * (暂时没考虑右边的Y轴)
     */
    private void initChart() {
     initProperties();
     setChartData();
     initLegend(Legend.LegendForm.LINE,mTextSize,mValueColor);
     initXAxis(mValueColor,mTextSize);
     initLeftAXis(mValueColor,mTextSize);
    }

    protected abstract void setChartData();

    /**
     * <p>设置图表属性初始化属性<p/>
     */
    private void initProperties() {
        //设置告诉用户没有可用数据来绘制图表的文本。
        mChart.setNoDataText("");
        //设置描述文字
        mChart.getDescription().setEnabled(false);
        //enable touch gestures
        mChart.setTouchEnabled(true);
        //设置摩擦系数
        mChart.setDragDecelerationFrictionCoef(0.9f);
        //enable scaling and dragging
        mChart.setDragEnabled(true);
        //能在水平方向展开
        mChart.setScaleXEnabled(true);
        //如果设为真，x轴和y轴都可以用2个手指同时缩放，如果是假的，x和y轴可以分开缩放。默认值:假
        mChart.setPinchZoom(false);
        //设置该区域的大小（x轴上的范围）应该是最大的立即可见（不允许进一步放大）。如果这是设置为在x轴上的10个范围内不超过10个，可以同时查看滚动
        mChart.setVisibleXRangeMaximum(6);
        //不能在水平方向拉伸
        mChart.setScaleYEnabled(false);
        //把这个设为true来绘制网格背景
        mChart.setDrawGridBackground(false);
        //将其设置为true，当它完全缩小时，允许在chartSurface上突出显示。
        mChart.setHighlightPerDragEnabled(false);
        //if disabled,scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

    }

    /**
     * <p>初始化Legend展示信息 即x轴和y轴旁的数据</p>
     * 当 setDrawAxisLine(true)时才起作用
     * @param form
     * @param mTextSize
     * @param mValueColor
     */

    private void initLegend(Legend.LegendForm form, float mTextSize, int mValueColor) {
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(form);

        l.setTextSize(mTextSize);
        l.setTextColor(mValueColor);
        //l.setYOffset(11f);
        // p>图例说明</p>
        //垂直方向位置 默认底部
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        //水平方向位置 默认右边
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        //显示方向 默认水平展示
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        // 确定图例是否会在图表中或外部绘制
        l.setDrawInside(false);
    }

    /**
     *
     * @param mValueColor
     * @param mTextSize
     */
    private void initXAxis(int mValueColor, float mTextSize) {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(mTextSize);
        xAxis.setTextColor(mValueColor);
        //设置x轴的最小值
        //为这个轴设置一个自定义的最小值。如果设置，这个值将不会根据所提供的数据自动计算。
        // 使用resetAxisMinValue（）来撤销这个。如果您使用这种方法，请不要忘记调用setStartAtZero（false）。否则，轴最小值仍将被强制为0。
//        xAxis.setAxisMinimum(0);
        //设置为true时绘制网格
        xAxis.setDrawGridLines(false);
        //如果沿着轴的线应该被画出来，就把它设为true。
        xAxis.setDrawAxisLine(false);
        //设置轴标签
        xAxis.setDrawLabels(true);

        xAxis.setAxisLineWidth(1f);
        //设置标签数量
        xAxis.setLabelCount(8);
        //设置界限线
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setAxisLineColor(mValueColor);
        //把轴的标签是否放在中心位置
        xAxis.setCenterAxisLabels(false);
        //设置x轴标签的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void initLeftAXis(int mValueColor, float mTextSize) {
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(mValueColor);
        leftAxis.setTextSize(mTextSize);
        float yMax = mChart.getData().getYMax() == 0 ? 100f : mChart.getData().getYMax();
        leftAxis.setAxisMaximum(yMax + yMax*0.0007f);

        leftAxis.setDrawAxisLine(false);
        //启用/禁用轴值间隔的粒度控制。如果启用，axis间隔不允许低于一定的粒度。默认值:假
        leftAxis.setGranularityEnabled(false);
        //将其设置为true，无论是否启用其他网格线，都要画出零线。默认值:假
        leftAxis.setDrawZeroLine(false);
        leftAxis.setLabelCount(6);
        leftAxis.setAxisLineWidth(1f);
        leftAxis.setAxisLineWidth(mValueColor);

        //设置右边y轴不可见
        mChart.getAxisRight().setEnabled(false);

    }

}
