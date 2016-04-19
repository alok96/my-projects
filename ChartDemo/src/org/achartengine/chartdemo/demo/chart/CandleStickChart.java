package org.achartengine.chartdemo.demo.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class CandleStickChart implements IChart{

	static int lowerBound = 5;
	static int seriesCount = 30;
    static int Range = 5000;
    static int div = 80;
    
	String name = "Wykres œwieczkowy";
	String desc = "Wykres œwieczkowy(by Google Chart API)";
	public String getName() {
		
		// TODO Auto-generated method stub
		return name;
	}

	public String getDesc() {
		// TODO Auto-generated method stub
		return desc;
	}

	public Intent execute(Context context) {
        Double[] _1st = CandleStickChart.generateRandomArray(seriesCount);
        Double[] _2nd = CandleStickChart.incrementArray(_1st, 1000d);
        Double[] _3rd = CandleStickChart.incrementArray(_1st, 2000d);
        Double[] _4th = CandleStickChart.incrementArray(_1st, 3000d);
        ArrayList<Double[]> series = new ArrayList<Double[]>();
        series.add(_1st);
        series.add(_2nd);
        series.add(_3rd);
        series.add(_4th);
		
		String abc = createCandleStickChart(series);
		Uri uri = Uri.parse(abc);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		return intent;
		// TODO Auto-generated method stub
	}
	
    private String createCandleStickChart(ArrayList<Double[]> dataSeries) {
        ArrayList<Integer[]> chd = transformChartSeries(dataSeries);
        StringBuilder sbChart = new StringBuilder();
        //Four series data
        StringBuilder sb1 = new StringBuilder("-1,");
        StringBuilder sb2 = new StringBuilder("-1,");
        StringBuilder sb3 = new StringBuilder("-1,");
        StringBuilder sb4 = new StringBuilder("-1,");
        //Axis Labels
        StringBuilder axis = new StringBuilder("&chl=");
        for (int i = 0; i < chd.get(0).length; i++) {
            sb1.append((chd.get(0)[i]) + ",");
            sb2.append((chd.get(1)[i]) + ",");
            sb3.append((chd.get(2)[i]) + ",");
            sb4.append((chd.get(3)[i]) + ",");
            axis.append(i + "|");
        }

        sb1.append("-1|");
        sb2.append("-1|");
        sb3.append("-1|");
        sb4.append("-1");
        sbChart.append("https://chart.googleapis.com/chart?");//API URL
        sbChart.append("chs=800x200&");//Chart Size
        sbChart.append("cht=lc&chm=F,0000FF,0,,10&");//CandleStick marker
        sbChart.append("chxt=y&");//Visible axis: Left y-axis
        sbChart.append("chd=t0:");//Chart Data t0 - all series are hidden
        String chdd = sbChart.toString() + sb1.toString() + sb2.toString() + sb3.toString() + sb4.toString() + axis.toString();
        System.out.println(chdd);
    	
    	return chdd;
	}

	public static Double[] generateRandomArray(int lenght) {
        Double[] data = new Double[lenght];
        Random r = new Random();
        for (int i = 0; i < lenght; i++) {
            data[i] = (Double) (r.nextInt(Range) + lowerBound + 0.001);
        }
        return data;
    }

    public static void displayArray(Double[] input) {
        for (int i = 0; i < input.length; i++) {
            System.out.print(input[i] + " ");
        }
    }

    public static void displayArray(Integer[] input) {
        for (int i = 0; i < input.length; i++) {
            System.out.print(input[i] + " ");
        }
    }

    /**
     * Metoda przetwarza dane typu ArrayList<Double[]> do
     * typu akceptowanego przez Google Chart API
     * czyli do wartoœci Integer: od 0 do 100.
     * Wartoœci danych musz¹ mieæ 4 serie danych.
     * @param input Dane wejœciowe(4 serie danych!)
     * @return zwraca odpowiednie dane do przetworzenia w google chart api
     */
    public static ArrayList<Integer[]> transformChartSeries(ArrayList<Double[]> input) {
        ArrayList<Integer[]> scaledInput = new ArrayList<Integer[]>();

        double scaleRatio = getScaleRatio(Min(input), Max(input));
        for (int i = 0; i < input.size(); i++) {
            scaledInput.add(scale(input.get(i), scaleRatio));
        }
        return scaledInput;
    }

    /*
     * Metody pomocnicze do skalowania i transformacji
     */
    private static double Max(ArrayList<Double[]> arr) {
        ArrayList<Double> tmp = new ArrayList<Double>();
        for (int i = 0; i < arr.size(); i++) {
            tmp.add(Max(arr.get(i)));
        }
        return Collections.max(tmp);
    }

    private static double Min(ArrayList<Double[]> arr) {
        ArrayList<Double> tmp = new ArrayList<Double>();
        for (int i = 0; i < arr.size(); i++) {
            tmp.add(Min(arr.get(i)));
        }
        return Collections.min(tmp);
    }

    private static Integer Min(Integer[] input) {
        Integer iMin = Collections.min(Arrays.asList(input));
        return iMin;
    }

    private static Integer Max(Integer[] input) {
        Integer iMax = Collections.max(Arrays.asList(input));
        return iMax;
    }

    private static double Min(Double[] input) {
        return Collections.min(Arrays.asList(input));
    }

    private static double Max(Double[] input) {
        return Collections.max(Arrays.asList(input));
    }

    /*
     * Skalowanie
     */
    private static Integer[] scale(Double[] input, double ratio) {
        Integer[] scaledData = new Integer[input.length];
        for (int i = 0; i < input.length; i++) {
            scaledData[i] = new Integer((int) Math.round(input[i] / ratio));
        }
        return scaledData;
    }

    private static double getScaleRatio(double min, double max) {
        double ratio = 0;
        ratio = (max - min) / div;
        return ratio;
    }

    /**
     * Metoda zwiêksza o podany wspó³czynnik tablicê Double[]
     * @param input Tablica wejœciowa Double[]
     * @param value Wspó³czynnik do dodania
     * @return nowa tablica z uwzglêdnionym wspó³czynnikiem.
     */
    public static Double[] incrementArray(Double[] input,Double value){
        Double[] output = new Double[input.length];
        for (int i = 0;i<input.length;i++){
            output[i] = input[i] + value;
        }
        return  output;
    }

}
