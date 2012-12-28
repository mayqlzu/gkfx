package com.gkfx;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;


public class Main extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JList fileList;
	private JTextArea statusArea;
	private ArrayList<File> files = new ArrayList<File>();
	private HtmlParser htmlParser;
	private ExcelParser excelParser;
	
	
	JButton button_choose_files;
	JButton button_go;
	
	
	Main(){
		super("GKFX");
		setBounds(100,100,400,300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); 
		
		this.setLayout(new FlowLayout());
		
//		String[] data = {"one", "two", "three", "four"};
		/* i need a listModel to insert items later */
		DefaultListModel listModel = new DefaultListModel();
		listModel.addElement("test file1");
		listModel.addElement("test file2");
		listModel.addElement("test file3");


		fileList = new JList(listModel);
//		fileList = new JList(data);
//		 fileList = new JList();
		
		button_choose_files = new JButton("load file");
		button_choose_files.addActionListener(this);
		
		button_go = new JButton("go");
		button_go.addActionListener(this);
		
		statusArea = new JTextArea("status message");
		
		add(fileList);
		add(button_choose_files);
		add(button_go);
		add(statusArea);
		
		validate(); // can not show without it.
		
		htmlParser = new HtmlParser();
	}
	
	public void updateStatus(String message) {
		statusArea.setText(message);
	}

	/* assume we have only one html file */
	private File getTheRawReport() {
		for(File f : files) {
			String suffix = Tools.getFileNameSuffix(f.getName());
			if(suffix.equals("html")) {
				return f;
			}
		}
		System.out.println("error: Main.getTheRawReport: no html files found");
		return null;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(button_choose_files)) {
			// TODO Auto-generated method stub
			FileChooser fileChooser = new FileChooser();
			File[] files_this_time = fileChooser.getChoosedFiles();
			if(null == files_this_time){
				System.out.println("choosed no files");
			}else{
				System.out.println("choosed some files");
				
				/* add files into list */
				DefaultListModel listModel = (DefaultListModel) fileList.getModel();
//				listModel.addElement("test file");
				for(File f : files_this_time){ /* keyword 'for' is so nice :) */
					listModel.addElement(f.getAbsolutePath());
					/* save file in arrayList, todo:exclude the duplicated files */
					files.add(f);
				}
				updateStatus("load file, done.");
				
				/* debug: check the arrayList 
				System.out.println("debug start:");
				for(File f: files){
					System.out.println(f);
				}
				System.out.println("debug done");
				*/
			}
		}
		else if(e.getSource().equals(button_go)) {
				System.out.println("button go clicked");
				/* user contact: source files are named as:
				 * Raw Report CAH.html
				 * IB_customer.excel
				 * IB_commission.excel
				 * ...
				 */
	
				try {
					/* assume we have only one html file */
					htmlParser.parseTheRawReport(getTheRawReport());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
				
//				excel.parse(file1);
//				excel.parse(file2);
//				excel.parse(file3);
				
				
				/* query and create table in DB */
				
				/* output the table in DB as excel or html */
				
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main();
	}
	
}
