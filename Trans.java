package com.test;

public class Trans {
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
