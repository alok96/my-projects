package org.achartengine.chartdemo.demo.chart;

import java.util.ArrayList;

/*
 * Podstawowy interfejs dla dowolnego �r�d�a danych wykresu (Portfel, Instrument, itp)
 */
public interface IChartable {

/*
 * Metoda zwracaj�ca serie danych wykresu. Dane mog� by�
 * pobierane z zewn�trznych �r�de� danych. 	
 */
 public ArrayList<Double[]> getChartSeries();
 
 /*
  * Metoda zwracaj�ca dodatkow� seri� danych 
  * (Zazwyczaj O� Y)
  */
 public ArrayList<Double[]> getChartSeries2();
 
 /*
  * Metoda zwracaj�ca Tytu� ka�dej serii
  */
 public ArrayList<String> getChartSeriesTitle();
 
 /*
  * Kolor serii danych
  */
 public ArrayList<Integer> getChartSeriesColor();
 
 /*
  * Zakres danych. Zazwyczaj d�ugo�� r�wna d�ugo�ci serii danych
  */
 public Integer getChartSeriesRange();
 
 /*
  * Metoda sprawdzaj�ca czy dane nale�y wygenerowa� testowo. 
  */
 public Boolean isTestData();
}
