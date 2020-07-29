package com.benz.dhanyaassignment.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.benz.dhanyaassignment.models.parkingspots.Items;

@Component
@Service
public class SortByDistanceParkingSpots implements Comparator<Items> {

	@Override
	public int compare(Items a, Items b) 
	    { 
	        return a.getDistance() - b.getDistance(); 
	    }
	
	public static List<Items> sortItemsandReturnResult(List<Items> list) {
		Collections.sort(list, new SortByDistanceParkingSpots());
		if (list.size() < 3 && !list.isEmpty()) {
			return list.subList(0, list.size());
		}

		return list.subList(0, 3);
	}

}
