
package org.achartengine.chartdemo.demo.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;


public class LineChart extends AbstractDemoChart implements IChartable {
	String title = "Wykres spó³ek";
	String xTitle = "Dzieñ miesi¹ca";
	String yTitle ="Wartoœæ";
	Integer arraySize = 32;
	Integer seriesSize = 2;
	/*
	 * Obiekt implementuj¹cy interfejst IChartable zawieraj¹cy
	 * potrzebne dane do wyrysowania wykresu. W tym przypadku
	 * jest to obiekt typu LineChart dlatego referencja na this.
	 * Nie przypuszcza³bym, ¿e to przejdzie a tu pozytywne zaskoczenie.
	 */
	IChartable data = this;
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "Wykres liniowy";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "Wykres liniowy przyk³ad.";
	}

	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	public Intent execute(Context context) {
		ArrayList<String> chartTitles = data.getChartSeriesTitle();
		/*
		 * Tablica potrzebna do przekazania jako
		 * parametr ArrayList.toArray(T[])
		 */
		String[] E = new String[]{"",""};
		List<Double[]> x = data.getChartSeries();
		

		List<Double[]> values = data.getChartSeries2();

		int[] colors = new int[] { Color.BLUE, Color.GREEN };
		PointStyle[] styles = new PointStyle[] { null,	null };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}
		
		setChartSettings(renderer, title, xTitle,yTitle, 0.5, arraySize, 0, 120, Color.LTGRAY,
				Color.LTGRAY);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setXLabels(10);
		renderer.setYLabels(10);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
		renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
		Intent intent = ChartFactory.getLineChartIntent(context,
				buildDataset(chartTitles.toArray(E),x,values), renderer,
				"Wykres s³upkowy");
		return intent;
	}

	public ArrayList<Double[]> getChartSeries() {
		if (isTestData()){
			ArrayList<Double[]> x = new ArrayList<Double[]>();

			for (int k = 0; k < seriesSize; k++) {
				Double[] month = new Double[arraySize];
				for (int i = 0; i < month.length; i++) {
					month[i] = (double) i;
				}
				x.add(month);
			}
			return x;
		}
		else{
			//TODO
		}
		
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
	public ArrayList<Double[]> getChartSeries2(){
		if (isTestData()){
			Random r = new Random();
			ArrayList<Double[]> values = new ArrayList<Double[]>();
			for (int k = 0; k < seriesSize; k++) {
				Double[] tmpVal = new Double[arraySize];
				for (int i = 0; i < tmpVal.length; i++) {
					tmpVal[i] = new Double(r.nextDouble() * 100);
				}
				values.add(tmpVal);
			}
			return values;
		}
		else{
			//TODO
		}
		return null;
	}
	
	public Boolean isTestData(){
		Boolean test = Boolean.TRUE;
		return test;
	}

}
