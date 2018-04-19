package com.yin.mpandroidchartstudy;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.yin.mpandroidchartstudy.mychart.LineChartEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Entry> dottedLineData,solidLineData;
    private List<String> dataList;
    private float maxData = 0;
    private LineChart trendLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        setDataToView();
    }


    //模拟网络获取数据
    private void initData() {
        String data ="{\"exponentList\":[{\"dottedLineData\":\"0.0109\",\"exponentDate\":\"07-04\",\"solidLineData\":\"0.0099\"}," +
                "{\"dottedLineData\":\"0.0102\",\"exponentDate\":\"07-05\",\"solidLineData\":\"0.0039\"}," +
                "{\"dottedLineData\":\"0.0095\",\"exponentDate\":\"07-06\",\"solidLineData\":\"0.0084\"}," +
                "{\"dottedLineData\":\"0.0088\",\"exponentDate\":\"07-07\",\"solidLineData\":\"0.0195\"}," +
                "{\"dottedLineData\":\"0.0081\",\"exponentDate\":\"07-08\",\"solidLineData\":\"0.0148\"}," +
                "{\"dottedLineData\":\"0.0073\",\"exponentDate\":\"07-09\",\"solidLineData\":\"0.0035\"}," +
                "{\"dottedLineData\":\"0.0066\",\"exponentDate\":\"07-10\",\"solidLineData\":\"0.0013\"}],\"overviewName\":\"负面舆情指数\"}";
        //将数据转化成jsonObject
        try {
            JSONObject object = new JSONObject(data);
            //转成jsonObject的格式为
            // {
            //      exponentList:{
            //          dottedLineData:{[0.0109,0.0109, ...]}
            //          exponentDate:{[07-05,07-06, ...]}
            //          solidLineData:{[0.0099,0.0039, ...]}
            //      }
            //      overviewName:负面舆情指数
            // }
            JSONArray jsonArray = object.getJSONArray("exponentList");
            dottedLineData = new ArrayList<>();
            solidLineData = new ArrayList<>();
            dataList = new ArrayList<>();
            // BigDecimal实现精确加减乘除运算，可以构造小数
            //将数据放大100倍（实现百分比）
            BigDecimal scale = new BigDecimal(100);

            for (int i = 0,count = jsonArray.length(); i < count; i++){
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String dottedData = jsonObject.getString("dottedLineData");
                String solidData = jsonObject.getString("solidLineData");
                String exponentDate = jsonObject.getString("exponentDate");

                BigDecimal dotted = new BigDecimal(dottedData);
                BigDecimal solid = new BigDecimal(solidData);

                dottedLineData.add(new Entry(i,scale.multiply(dotted).floatValue()));
                solidLineData.add(new Entry(i,scale.multiply(solid).floatValue()));

                //设置Y轴最大值，（我们将值设置在同一个Y轴上，我们可以设置多个Y轴）
                if ( Float.valueOf(dottedData) > maxData){
                    maxData = Float.valueOf(dottedData);
                }
                if ( Float.valueOf(solidData) > maxData){
                    maxData = Float.valueOf(solidData);
                }

                //设置X轴上的日期
                dataList.add(exponentDate);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDataToView() {
        trendLineChart = findViewById(R.id.trend_lineChart);

        List<Entry>[] entries = new ArrayList[2];
        entries[0] = dottedLineData;
        entries[1] = solidLineData;

        //设置绘画曲线的颜色
        int[] chartColors = {Color.parseColor("#F0C330"),Color.parseColor("#50C4D9")};

        //设置曲线的label
        String[] labels = {"趋势线","TCL"};

        //设置曲线的样式
        boolean[] hasDotted = {true,false};

        //创建图表对象,由于图表的复用性较强，我们进行封装
        final LineChartEntity lineChartEntity = new LineChartEntity(trendLineChart,entries,labels,chartColors,R.color.black99,12f,hasDotted);

    }

}
