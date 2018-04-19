package com.yin.mpandroidchartstudy.mychart;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.yin.mpandroidchartstudy.R;

import java.util.List;

/**
 * Created by glh on 2018/4/18.
 */

public class LineChartEntity extends BaseChartEntity {

    public LineChartEntity(BarLineChartBase chart, List[] entries, String[] labels, int[] chartColor, int valueColor, float textSize) {
        super(chart, entries, labels, chartColor, valueColor, textSize);
    }

    public LineChartEntity(BarLineChartBase chart, List[] entries, String[] labels, int[] chartColor, int valueColor, float textSize, boolean[] hasDotted) {
        super(chart, entries, labels, chartColor, valueColor, textSize, hasDotted);
    }

    @Override
    protected void setChartData() {
        LineDataSet[] lineDataSets = new LineDataSet[mEntries.length];
        if (mChart.getData() != null && mChart.getData().getDataSetCount() == mEntries.length) {
            for (int index = 0, len = mEntries.length; index < len; index++) {
                List<Entry> list = mEntries[index];
                lineDataSets[index] = (LineDataSet) mChart.getData().getDataSetByIndex(index);
                lineDataSets[index].setValues(list);
            }
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            for (int index = 0, len = mEntries.length; index < len; index++) {
                lineDataSets[index] = new LineDataSet(mEntries[index], mLabels[index]);
                //这个地方只设置了左边的y轴，还要考虑右边的轴线
                //设置y轴上的labels的位置
                lineDataSets[index].setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSets[index].setColor(mChartColor[index]);
                lineDataSets[index].setLineWidth(1.5f);
                lineDataSets[index].setCircleRadius(3.5f);
                lineDataSets[index].setCircleColor(mChartColor[index]);
                lineDataSets[index].setFillAlpha(25);
//                    lineDataSet[index].enableDashedLine(10f, 15f, 0f);
//                    lineDataSet[index].enableDashedHighlightLine(10f, 15f, 0f);
                lineDataSets[index].setDrawCircleHole(false);
                lineDataSets[index].setValueTextColor(mChartColor[index]);
//                    lineDataSet[index].setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
                if (mHasDotted != null && mHasDotted[index]) {
                    lineDataSets[index].setDrawCircles(false);
                    lineDataSets[index].setCircleColor(R.color.white);
                    lineDataSets[index].enableDashedLine(10f, 15f, 0f);
                    lineDataSets[index].enableDashedHighlightLine(10f, 15f, 0f);
                }
            }

            LineData data= new LineData(lineDataSets);
            data.setValueTextSize(mTextSize);

            mChart.setData(data);
            //数据设置动画
            mChart.animateX(2000, Easing.EasingOption.EaseInOutQuad);
        }

    }
}