package org.achartengine.chartdemo.demo.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Sales demo bar chart.
 */
public class BarChart extends AbstractDemoChart implements IChartable {
	String name = "";
	List<Double[]> values;
	int seriesSize = 2;
	int arraySize = 10;
	int randomSeed = 2000;
	//Orientacja wykresu
	Orientation chartOrientation = Orientation.HORIZONTAL;
	//Wyœwietlanie liczb przy s³upkach
	Boolean displayChartValues = Boolean.FALSE;
	
	public BarChart() {
		name = "Wykres s³upkowy";
	}
	public BarChart(Orientation orientation) {
		chartOrientation = orientation;
		name = "Wykres s³upkowy(" + orientation.toString() +")";
	} 
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return name;
  }
  public void setName(String _name){
	  name = _name;
  }

  public String getDesc() {
    return "Wykres s³upkowy. Testowe dane";
  }

  public Intent execute(Context context) {
	String[] E = new String[0];
    String[] titles = getChartSeriesTitle().toArray(E);
    values = getChartSeries();
    Double minX,maxX,minY,maxY;
    maxY = GetMaxY(values) ;
    maxY += 200d;
    minY = 0d;
    minX = 0d;
    maxX = (double) (arraySize);
    int[] colors = new int[] { Color.CYAN, Color.BLUE };
    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
    renderer.setOrientation(chartOrientation);
    setChartSettings(renderer, "Wykres s³upkowy", "S³upki", "Wartoœæ spó³ki", 0.5,
        maxX, minY, maxY, Color.GRAY, Color.LTGRAY);

    renderer.setXLabels(arraySize);
    renderer.setYLabels(10);
    
    for (int i =0; i < arraySize; i++){
    	if (i % 2 == 0){
    		renderer.addTextLabel(i,i+"");
    	}
    }
    renderer.setDisplayChartValues(displayChartValues);
    return ChartFactory.getBarChartIntent(context, buildBarDataset(titles, values), renderer,
        Type.DEFAULT);
  }

  public ArrayList<Double[]> getChartSeries() {
		if (isTestData()){
			Random r = new Random();
			ArrayList<Double[]> seriesList = new ArrayList<Double[]>();

			for (int k = 0; k < seriesSize; k++) {
				Double[] series = new Double[arraySize];
				for (int i = 0; i < series.length; i++) {
					series[i] =  (double)(i * Math.abs(r.nextInt(randomSeed)));
				}
				seriesList.add(series);
			}
			return seriesList;
		}
		else{
			//TODO
		}
		
		return null;
	}


public ArrayList<Double[]> getChartSeries2() {
	// TODO Auto-generated method stub
	return null;
}

public ArrayList<String> getChartSeriesTitle() {
	if (isTestData()){
		ArrayList<String> charTitles = new ArrayList<String>();
		for (int i = 0;i<seriesSize ;i++){
			charTitles.add("Test " + i);
		}
		
		return charTitles;
	}
	else{
		//TODO
	}
	
	return null;
}

public ArrayList<Integer> getChartSeriesColor() {
	// TODO Auto-generated method stub
	return null;
}

public Integer getChartSeriesRange() {
	// TODO Auto-generated method stub
	return null;
}

public Boolean isTestData() {
	// TODO Auto-generated method stub
	return true;
}
public Double GetMaxY(List<Double[]> values){
	Double max = 0d;
	ArrayList<Double> tmpMax = new ArrayList<Double>();
	
	for (int i=0;i< values.size();i++){
		Double[] seriesRow = values.get(i);
		//Dodanie maksymalnej wartoœci z serii do tymczasowej tablicy
		tmpMax.add(Collections.max(Arrays.asList(seriesRow)));
	}
	//Pobranie najwiêkszej wartoœci z wszystkich serii.
	max = Collections.max(tmpMax);
	return max;
}

}
