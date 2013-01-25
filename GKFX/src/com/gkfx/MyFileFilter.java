package com.gkfx;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/* accept html and excel files only */
public class MyFileFilter extends FileFilter {

	private String getExtension(File f){
		String name = f.getName();
		int index = name.lastIndexOf('.');  

		if (index == -1)
		{  
			return "";
		}  
		else  
		{  
			String extension = name.substring(index + 1);
			return extension;
		} 
	}

	@Override
	public boolean accept(File f) {
		// TODO Auto-generated method stub
		if(f.isDirectory())
			return true;
		
		String extension = getExtension(f);

		if(extension.equalsIgnoreCase("htm") || extension.equalsIgnoreCase("xlsx"))
			return true;
		else 
			return false;

	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
