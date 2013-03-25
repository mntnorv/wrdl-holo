package com.mntnorv.wrdl_holo.db;

public class LoaderIdDistributor {
	private static final int MAX_IDS_PER_LOADER = 1000;
	private static int nextIdStart = 0; 
	
	public static int getNextId() {
		int returnValue = nextIdStart;
		nextIdStart += MAX_IDS_PER_LOADER;
		return returnValue;
	}
	
	public static int getMaxIdsPerLoader() {
		return MAX_IDS_PER_LOADER;
	}
}
