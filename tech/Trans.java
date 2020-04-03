package com.test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Trans {
	
	/**
	 * Campare to Object，return map with true or false
	 * @param obj1
	 * @param obj2
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public Map<Object,Object> compareObjToObj(Object obj1,Object obj2) throws IllegalArgumentException, IllegalAccessException{
		Map<Object,Object> result = new HashMap<Object,Object>();
		Field[] fs= obj1.getClass().getDeclaredFields();
		for(Field f:fs){
			f.setAccessible(true);
			Object v1 = f.get(obj1);
			Object v2 = f.get(obj2);
			result.put(f.getName(),String.valueOf(equals(v1,v2)));
		}
		return result;
	}
	/**
	 * Campare to Object
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public boolean equals(Object obj1,Object obj2){
		if(obj1==obj2){
			return true;
		}
		//if param both be null,return false
		if(obj1==null||obj2==null){
			return false;
		}
		return obj1.equals(obj2);
	}
}
