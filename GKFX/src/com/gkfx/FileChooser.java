package com.gkfx;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;


public class FileChooser {
	
	private File[] m_files;
	
	FileChooser(){
		// TODO Auto-generated method stub
//		files = null;  java will init it as null automatically :)
//		System.out.println(files);
		
		JFileChooser file_chooser = new JFileChooser("D:/coding/windows/gkfx/tables-from-jeffery/Input_New/forTest");
		//String workingDir = System.getProperty("user.dir");
		//JFileChooser file_chooser = new JFileChooser(workingDir);
		file_chooser.setMultiSelectionEnabled(true);
		file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		file_chooser.setFileFilter(new MyFileFilter());
        int returnValue = file_chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION)
        {  
        	m_files = file_chooser.getSelectedFiles();
        	System.out.println("file choosed: "+ m_files[0].toString());
        }else{
        	m_files = null;
        }
	}
	
	public File[] getChoosedFiles(){
		return m_files;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new FileChooser();
	}
	
}
