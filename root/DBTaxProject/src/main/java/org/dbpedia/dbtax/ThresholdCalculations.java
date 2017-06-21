package org.dbpedia.dbtax;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.dbpedia.dbtax.database.CategoryDB;

/*
*
* This class has all functions related to threshold calculations. 
* We calculated threshold to avoid any administrative categories during the leaf extraction.
*
*/

public class ThresholdCalculations {
	
	private ThresholdCalculations() { }
	
	//Gets the list of categories along with their slopes.
	private static List<Point> calculuateNoOfCategories(){
		ArrayList<Point> points = new ArrayList();
		for(int i=0;i<500;i++){
			Point p = new Point(i,CategoryDB.getCategoryPageCount(i));
			points.add(p);
		}
		return points;
	}
	
	//This function calculates and returns the Threshold. 
	public static int findThreshold(){
		List<Point> points = calculuateNoOfCategories();
		int count =0;
		int i=0;
		while(count<5){
			if(caluculateSlope(points.get(i),points.get(i+1))<4)
				count++;
			i++;
		}
		return i;
	}
	
	//Helper Function: Calculates the slope, given two points 
	private static double caluculateSlope(Point p1, Point p2){
		double deltaX = p2.getX()-p1.getX();
		double deltaY = p2.getY()-p1.getY();
		return deltaY/deltaX;
	}
	
	public static void main(String[] argv){
		int threshold =ThresholdCalculations.findThreshold();
		System.out.println(threshold);
	}
}