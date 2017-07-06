package org.dbpedia.dbtax;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.dbpedia.dbtax.database.CategoryDB;
import org.slf4j.LoggerFactory;

/*
*
* This class has all functions related to threshold calculations. 
* We calculated threshold to avoid any administrative categories during the leaf extraction.
*
*/

public class ThresholdCalculations {

//	private static final Logger logger = LogManager.getRootLogger();//.getLogger(ThresholdCalculations.class);
	private ThresholdCalculations() { }
	
	//Gets the list of categories along with their slopes.
	private static List<Point> calculuateNoOfCategories(){
		ArrayList<Point> points = new ArrayList();
		for(int i=0;i<1000;i++){
			Point p = new Point(i,CategoryDB.getCategoryPageCount(i));
			points.add(p);
			System.out.println(p.getX()+","+p.getY());
		}
		return points;
	}
	
	//This function calculates and returns the Threshold. 
	public static int findThreshold(){
		List<Point> points = calculuateNoOfCategories();
		int count =0;
		int i=0;
        while(count<5){
            double slope = caluculateSlope(points.get(i), points.get(i+1));
		    System.out.println("heree"+ i+" "+slope);
		    if(slope<10)
				count++;
			i++;
		}
//        logger.info("Calculated threshold: "+ i);
		return i;
	}
	
	//Helper Function: Calculates the slope, given two points 
	private static double caluculateSlope(Point p1, Point p2){
		double deltaX = p2.getX()-p1.getX();
		double deltaY = p2.getY()-p1.getY();
		return deltaY/deltaX;
	}
	
	public static void main(String[] argv){
        org.slf4j.Logger logger = LoggerFactory.getLogger(ThresholdCalculations.class);


        logger.debug("Hello World");

        logger.info("Hello World");

        logger.warn("Hello World");

        logger.error("Hello World");
		int threshold =ThresholdCalculations.findThreshold();
		System.out.println(threshold);
	}
}