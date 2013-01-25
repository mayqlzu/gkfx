package com.gkfx;

public class Tools
{

	/**
	 * Return the suffix of the passed file name.
	 *
	 * @param  fileName  File name to retrieve suffix for.
	 *
	 * @return Suffix for <TT>fileName</TT> or an empty string
	 *       if unable to get the suffix.
	 *
	 * @throws IllegalArgumentException  if <TT>null</TT> file name passed.
	 */
	public static String getFileNameSuffix(String fileName)
	{
		if (fileName == null)
		{
			throw new IllegalArgumentException("file name == null");
		}
		int pos = fileName.lastIndexOf('.');
		if (pos > 0 && pos < fileName.length() - 1)
		{
			return fileName.substring(pos + 1);
		}
		return "";
	}

	public static boolean isNumber(String str) {
		return str.matches("^-?[0-9]+(\\.[0-9]+)?$");
	}
	
	/*
	 * quota: pei e
	 * quote: bao jia
	 * 
	 * return BBB from AAABBB
	 */
	public static String getQuoteCurrency(String pair){
		assert(6 == pair.length());
		return pair.substring(3);
	}
	
	public static String lastTwoCharsOf(String symbol){
		return  symbol.substring(symbol.length() - 2);
	}
}