package com.gkfx;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;


public class FileChooser extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JButton button_choose_files;
	
	
	FileChooser(){
		super("GKFX");
		setBounds(100,100,800,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); 
		
		this.setLayout(new FlowLayout());
		
		button_choose_files = new JButton("choose files");
		button_choose_files.addActionListener(this);
		add(button_choose_files);
		validate();

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		JFileChooser file_chooser = new JFileChooser("D:/coding/gkfx");
		file_chooser.setMultiSelectionEnabled(true);
		file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		file_chooser.setFileFilter(new HtmlFileFilter());
        int returnValue = file_chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION)
        {  
        	File[] files = file_chooser.getSelectedFiles();
        	System.out.println(files[0].toString());
        }
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new FileChooser();
	}
	
}
