package org.achartengine.chartdemo.demo.chart;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class BarChartV2 implements IChart{

	int SeriesSize = 30;
	
	public String getName() {
		return "Wykres s³upkowy";
	}

	public String getDesc() {
		return "Wykres s³upkowy inwestycyjny";
	}

	public Intent execute(Context context) {
		// TODO Auto-generated method stub
		Double[] _1st = CandleStickChart.generateRandomArray(SeriesSize);

        Double[] _2nd = CandleStickChart.incrementArray(_1st, 1000d);
        Double[] _3rd = CandleStickChart.incrementArray(_2nd, 1000d);
        Double[] _4th = CandleStickChart.incrementArray(_3rd, 1000d);
        ArrayList<Double[]> series = new ArrayList<Double[]>();
        series.add(_1st);
        series.add(_2nd);
        series.add(_3rd);
        series.add(_4th);
        ArrayList<Integer[]> _bseries = CandleStickChart.transformChartSeries(series);
        System.out.println("");System.out.println("");
        String chartUrl = BarChartV2.createChartUrl(_bseries);
        Uri uri = Uri.parse(chartUrl);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		return intent;
	}
	
	
	public static String createChartUrl(ArrayList<Integer[]> dataSeries){
        StringBuilder chartSb = new StringBuilder();
        ArrayList<StringBuilder> sbList = new ArrayList<StringBuilder>();
        sbList.add(new StringBuilder());
        sbList.add(new StringBuilder());
        sbList.add(new StringBuilder());
        sbList.add(new StringBuilder());
        for (int i = 0;i < 4; i++){
            StringBuilder sb = sbList.get(i);
            Integer[] data = dataSeries.get(i);
            for (Integer j:data){
                sb.append(j+",");
            }
            System.out.println(sb.toString());
        }
        chartSb.append("http://chart.apis.google.com/chart?");//GOOGLE CHART API
        chartSb.append("cht=bvg&chbh=r,.1,1&");//Typ wykresu i skalowanie
        chartSb.append("chs=1000x300&");//Rozmiar wykresu 1000x300 px
        chartSb.append("chxt=x,y&");//Widocznoœæ osi x i y
        chartSb.append("chf=bg,s,000000&");// Kolor t³a 000000 - czarne
        //Markery dla ka¿dej z serii. Te kreseczki które s¹ widoczne na wykresie
        chartSb.append("chm=E,FF0000,0:1,,1:10|E,FF0000,1:1,,1:10|E,FF0000,2:1,,1:10|E,FF0000,3:1,,1:10&");
        //Kolory poszczególnych serii danych. W tym przypadku nie s¹ widoczne. 
        chartSb.append("chco=600060,006060,606000&");
        //Dane wykresu, t0 oznacza, ¿e ¿adna seria danych nie jest widoczna.
        //Widoczne s¹ tylko markery chm
        chartSb.append("chd=t0:");
        for (StringBuilder sb: sbList){
            chartSb.append(sb.substring(0, sb.length()-1) +"|");
        }
        //System.out.println(chartSb.substring(0, chartSb.length() - 1));
        return chartSb.substring(0, chartSb.length() - 1);
    }

}
