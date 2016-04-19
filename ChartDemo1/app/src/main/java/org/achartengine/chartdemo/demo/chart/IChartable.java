package org.achartengine.chartdemo.demo.chart;

import java.util.ArrayList;

/*
 * Podstawowy interfejs dla dowolnego Ÿród³a danych wykresu (Portfel, Instrument, itp)
 */
public interface IChartable {

/*
 * Metoda zwracaj¹ca serie danych wykresu. Dane mog¹ byæ
 * pobierane z zewnêtrznych Ÿróde³ danych. 	
 */
 public ArrayList<Double[]> getChartSeries();
 
 /*
  * Metoda zwracaj¹ca dodatkow¹ seriê danych 
  * (Zazwyczaj OŒ Y)
  */
 public ArrayList<Double[]> getChartSeries2();
 
 /*
  * Metoda zwracaj¹ca Tytu³ ka¿dej serii
  */
 public ArrayList<String> getChartSeriesTitle();
 
 /*
  * Kolor serii danych
  */
 public ArrayList<Integer> getChartSeriesColor();
 
 /*
  * Zakres danych. Zazwyczaj d³ugoœæ równa d³ugoœci serii danych
  */
 public Integer getChartSeriesRange();
 
 /*
  * Metoda sprawdzaj¹ca czy dane nale¿y wygenerowaæ testowo. 
  */
 public Boolean isTestData();
}
