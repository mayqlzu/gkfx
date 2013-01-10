package com.gkfx;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;


public class Main extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private JList 						m_fileList;
	private JTextArea 					m_statusArea;
	private ArrayList<File> 			m_files;
	private HtmlParser 					m_htmlParser;
	private HtmlWriter 					m_htmlWriter;
	private ExcelParser 				m_excelParser;
	private ExcelWriter					m_excelWriter;
	private ArrayList<Deal>				m_deals;
	private HashMap<String, Customer>	m_customers;  //use HashMap to group deals by customer
	private HashMap<String, IB>			m_ibs;		  //use HashMap to group customers by ib
	private HashMap<String, String>		m_customerIBMap;
	private	HashMap<String,	Markup>		m_customerMarkupMap;
	
	JButton m_button_choose_files;
	JButton m_button_go;
	
	
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


		m_fileList = new JList(listModel);
//		fileList = new JList(data);
//		 fileList = new JList();
		
		m_button_choose_files = new JButton("load file");
		m_button_choose_files.addActionListener(this);
		
		m_button_go = new JButton("go");
		m_button_go.addActionListener(this);
		
		m_statusArea = new JTextArea("status message");
		
		add(m_fileList);
		add(m_button_choose_files);
		add(m_button_go);
		add(m_statusArea);
		
		validate(); // can not show without it.
		
		m_files			=	new ArrayList<File>();
		m_htmlParser 	=	new HtmlParser();
		m_htmlWriter	= 	new HtmlWriter();
		m_excelParser	=	new ExcelParser();
		m_excelWriter	=	new ExcelWriter();
		// do NOT new deals here, return from HtmlParser
		
	}
	
	public void updateStatus(String message) {
		m_statusArea.setText(message);
	}

	/* assume we have only one html file */
	private File getTheRawReport() {
		for(File f : m_files) {
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
		if(e.getSource().equals(m_button_choose_files)) {
			// TODO Auto-generated method stub
			FileChooser fileChooser = new FileChooser();
			File[] files_this_time = fileChooser.getChoosedFiles();
			if(null == files_this_time){
				System.out.println("choosed no files");
			}else{
				System.out.println("choosed some files");
				
				/* add files into list */
				DefaultListModel listModel = (DefaultListModel) m_fileList.getModel();
//				listModel.addElement("test file");
				for(File f : files_this_time){ /* keyword 'for' is so nice :) */
					listModel.addElement(f.getAbsolutePath());
					/* save file in arrayList, todo:exclude the duplicated files */
					m_files.add(f);
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
		else if(e.getSource().equals(m_button_go)) {
				System.out.println("button go clicked");
				
				initAllDeals();
				
				if(false = isInputEnough()){
					return; // do nothing for this event handling loop
				}
				
				initAllCustomers();
				initAllIBs();
				
				// this condition is rough, same parms will be determined after counting vol by individual IB
				setConditionForAllIBs();
				
				computeFeeForEveryIB();
				m_excelWriter.printFinalReport(m_ibs);
				m_htmlWriter.printFinalReport(m_ibs); // can i convert excel result to html result ?
				
				System.out.println("final report generated :)");
		}
	}
	
	private void setConditionForAllIBs(){
		// IB_ID -> Condition
		HashMap<String, Condition> ibConditionMap = m_excelParser.generateIBConditionMap();
		ArrayList<IB> ibList = (ArrayList<IB>) m_ibs.values();
		for(IB ib: ibList){
			Condition c = ibConditionMap.get(ib.getID());
			assert(null != c);
			ib.setCondition(c);
		}
	}
	
	private boolean isInputEnough(){
		todo
		// pop dialog if input is not enough,
		// hint customers to provider all missing input

	}
	
	// deals will be affected
	private void initAllDeals(){
		File rawReportHtmlFile = this.getTheRawReport();
		try {
			// m_deals will be newed by callee
			m_deals =	this.m_htmlParser.readTheRawReport(rawReportHtmlFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	// ibs will be affected
	private void initAllIBs(){
		this.initCustomerIBMap();
		assert(null != m_customers && null != m_customerIBMap);
		
		ArrayList<Customer> cuslist = new ArrayList<Customer>(m_customers.values());
		m_ibs = new HashMap<String, IB>();
		for(Customer c : cuslist)	{
			String IBCode = this.m_customerIBMap.get(c.getLogin());
			
			IB existentIB = m_ibs.get(IBCode);
			if(null == existentIB){
				// not found
				IB newIB = new IB(IBCode);
				newIB.addCustomer(c);
				m_ibs.put(IBCode, newIB);
			}else{
				// found
				existentIB.addCustomer(c);
			}
		}
	}
	
	private void initCustomerIBMap(){
		// excelParser will new object inside
		m_customerIBMap = m_excelParser.generateCustomerIBMap();
	}
	
	private void computeFeeForEveryIB(){
		ArrayList<IB> list = new ArrayList<IB>(m_ibs.values());
		for(IB ib : list){
			ib.computeIncome();
		}
	}
	
	
	
	// customers will be affected, elements of deals will be grouped and passed into elements of customers
	private void initAllCustomers(){
		// we have filtered invalid lines already
		
		assert(null != m_deals);
		
		m_customers = new HashMap<String, Customer>();
		for(Deal thisDeal: m_deals){
			String login = thisDeal.getLogin();
			Customer existentCus = m_customers.get(login);
			if(null == existentCus){
				// not found
				Customer newCustomer = new Customer(login);
				newCustomer.addDeal(thisDeal);
				m_customers.put(login, newCustomer);
			}else{
				// found
				existentCus.addDeal(thisDeal);
			}
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
