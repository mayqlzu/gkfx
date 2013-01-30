package com.gkfx;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultCaret;




public class Main extends JPanel implements ActionListener, PropertyChangeListener{
	/**
	 * 
	 */
	private ArrayList<File> 			m_files;
	private HtmlParser 					m_htmlParser;
	private HtmlWriter 					m_htmlWriter;
	private ExcelIO 					m_excelIO;
	
	private ArrayList<Deal>				m_deals;
	private HashMap<String, String>		m_clientGroupMap;
	private HashMap<String, String>		m_groupIBMap;
	private HashMap<String, Condition>	m_groupConditionMap;
	private HashSet<String>				m_activeClients; // which appears in deal record
	private HashSet<String>				m_activeGroups;   // you know :)
	private HashSet<String>				m_activeIBs;   // you know :)
	private FxGroups					m_fxGroupMap; // symbol -> enum ProductyTypes
	
	private JList 						m_fileList;
	private JPanel 						m_buttonsContainer;
	private JButton 					m_button_choose_files;
	private JButton 					m_button_clear_list;
	private JButton 					m_button_go;
	private JTextArea 					m_statusArea;
	
	private ArrayList<Line>				m_resultTable;
	private	HashMap<String,	IBParam>	m_ibParamMap;
	private ExRateTable					m_exRateTable;
	private FxPIPtable					m_fxPIPtable;
	private IndexPIPtable				m_indexPIPtable;
	private IndexValueTable				m_indexValTable;
	private StockValueTable				m_stockValTable;
	
    private Task 						task;
    private static String				ACTION = "Action";
    private static String				CANCEL = "Cancel";
    
    class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
			if(false == checkFileListCompletion())
				return null;
            
			if(false == parseInputFiles())
				return null;
            
			appendLog("checking data completion");
			if(false == checkInputDataCompletion())
				return null;
		
			initPIPtablesAndValuePerSlotTables();
			
			// all map completion is checked
			tagGroupAndIBto(m_deals);
			m_activeIBs = collectAllActiveIBs();
			
			appendLog("computing comission");
			handleAllActiveIBs(m_activeIBs);
			
			appendLog("outputing final report");
			outputFinalReport();
			
			appendLog("final report generated");
			System.out.println("final report generated :)");
			
            return null;
        }

        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            m_button_go.setText(ACTION);
            m_button_choose_files.setEnabled(true);
            m_button_clear_list.setEnabled(true);
        }
        
    	private boolean checkFileListCompletion(){
    		String[] expectedFileNames = new String[] {
    				FILE_NAMES.DEAL_RECORD,
    				FILE_NAMES.EX_RATES, 
    				FILE_NAMES.GROUP_CLIENT, 
    				FILE_NAMES.GROUP_CONDITION,
    			//	FILE_NAMES.IB_GROUP todo open it
    				};
    		
    		ArrayList<String> providedFileNames = new ArrayList<String>();
    		for(File f: m_files){
    			providedFileNames.add(f.getName()); // getName returns "foo.xxx"
    		}
    		
    		ArrayList<String> missingFileNames = new ArrayList<String>();
    		for(String s: expectedFileNames){
    			if(!providedFileNames.contains(s))
    				missingFileNames.add(s);
    		}
    		
    		if(missingFileNames.size() > 0){
    			for(String s: missingFileNames){
    				System.out.println("file: " + s +" not found!");
    				this.appendLog("ERROR: file: " + s + " not found!");
    			}
    			return false;
    		}
    		
    		return true;
    	}
    	
    	private void addTableHeaderForFinalReport(){
    		Object[] line = new Object[]{"IB", "Group", "Client", "Type", "Symbol", "Slot",
    				"Rebate", "Markup", "Commission", "Total"};
    		this.insertLineAtTopIntoResultTable(line);
    	}
    	
    	private boolean checkGroupConditionMapCompletion(){
    		/*
    		 * just in case
    		 * in normal cases, m_activeGroups has been initialized by checkGroupIBMapCompletion() already
    		 */
    		if(null == m_activeGroups){
    			m_activeGroups = collectAllActiveGroupsFrom(m_deals);
    		}
    		
    		// check and collect
    		HashSet<String> groupsNoCondition = new HashSet<String>();
    		Iterator<String> itor = m_activeGroups.iterator();
    		while(itor.hasNext()){
    			String group = itor.next();
    			Condition condition = m_groupConditionMap.get(group);
    			if(null == condition){
    				groupsNoCondition.add(group);
    			}
    		}
    		
    		// print
    		if(groupsNoCondition.isEmpty()){
    			return true;
    		}else{
    			Iterator<String> gItor = groupsNoCondition.iterator();
    			while(gItor.hasNext()){
    				String group = gItor.next();
    				System.out.println("ERROR: map group to Condition failed for: " + group );
    				appendLog("ERROR: Condition info for group: " + group + " not found" );
    			}
    			return false;
    		}
    	}
    	
    	/* 
    	 * map every group to an IB with a same name, ex: group AAA => IB AAA
    	 * todo: do REAL check
    	 */
    	private boolean checkGroupIBMapCompletion(){
    		m_activeGroups = collectAllActiveGroupsFrom(m_deals);
    		Iterator<String> itor = m_activeGroups.iterator();
    		while(itor.hasNext()){
    			String group = itor.next();
    			/*
    			String ib = m_groupIBMap.get(group);
    			if(null == ib){
    				System.out.println("ERROR: map group to IB failed for: " + group );
    				appendLog("ERROR: IB info for group: " + group + "not found" );
    				return false;
    			}
    			*/
    			
    			String aMockIBname = group;
    			m_groupIBMap.put(group, aMockIBname);
    		}
    		return true;
    	}
    	
    	HashSet<String> collectAllActiveGroupsFrom(ArrayList<Deal> deals){
    		HashSet<String> set = new HashSet<String>();
    		for(Deal d: deals){
    			String client = d.getLogin();
    			String group = m_clientGroupMap.get(client);
    			assert(null != group); // checked client-group map before
    			//System.out.println(client + " " + group);
    			set.add(group);
    		}
    		//System.exit(ABORT);
    		return set;
    	}
    	

    	
    	HashSet<String> collectAllClientsAppearIn(ArrayList<Deal> deals){
    		HashSet<String> set = new HashSet<String>();
    		
    		for(Deal d: deals){
    			set.add(d.getLogin());
    			//System.out.println(d.getLogin()); 
    		}
    		//System.exit(ABORT);
    		
    		return set;
    	}
    	
    	private void tagGroupAndIBto(ArrayList<Deal> deals){
    		for(Deal d: deals){
    			String group = getGroupOf(d.getLogin());
    			String ib = getIBof(group);
    			d.setGroup(group);
    			d.setIB(ib);
    		}
    	}
    	
    	private String getGroupOf(String client){
    		return m_clientGroupMap.get(client);
    	}
    	
    	private String getIBof(String group){
    		return m_groupIBMap.get(group);
    	}
    	

    	
    	private HashSet<String> collectNeededExRates(){
    		HashSet<String> neededExRates = new HashSet<String>();
    		for(Deal d: m_deals){
    			if(d.isFx()){
    				String quoteCurrency = Tools.getQuoteCurrency(d.getSymbol());
    				if(quoteCurrency.contains(".")){
    					System.out.println(d.getSymbol());
    					System.exit(0);
    				}
    					
    				if(quoteCurrency.equals("usd")){
    					continue;
    				}else if(quoteCurrency.equals("eur") // forward offer
    						|| quoteCurrency.equals("gbp")
    						|| quoteCurrency.equals("nzd")
    						|| quoteCurrency.equals("aud")){
    					neededExRates.add(quoteCurrency + "usd"); // use AAABBB in file, use aaabbb in program
    				}else{ // reverse offer
    					//System.out.println(quoteCurrency+"usd");
    					neededExRates.add("usd" + quoteCurrency);
    				}
    			}else if(d.isIndex()){
    				String symbol = d.getSymbol();
    				if(symbol.equals("uk100")){
    					neededExRates.add("gbpusd");
    				}else if(symbol.equals("esx50")
    						|| symbol.equals("dax30")
    						|| symbol.equals("sp500")
    						|| symbol.equals("aex25")
    						|| symbol.equals("cac40")
    						){
    					neededExRates.add("eurusd");
    				}else if(symbol.equals("smi20")){
    					neededExRates.add("usdchf");
    				}else if(symbol.equals("swe30")){
    					neededExRates.add("usdsek");
    				}else if(symbol.equals("ise30")){
    					neededExRates.add("usdtry");
    				}else if(symbol.equals("sp500")
    						|| symbol.equals("ws30")
    						|| symbol.equals("nas100")
    						|| symbol.equals("russ")){
    					continue;
    				}else{
    					System.out.println("jeffery's index list is not completed!, unknown index symbol: " + symbol);
    				}
    			}else if(d.isStock()){
    				String symbol = d.getSymbol();
    				String lastTwoChars = symbol.substring(symbol.length()-2);
    				if(lastTwoChars.equals("uk")){
    					neededExRates.add("gbpusd");
    				}else if(lastTwoChars.equals("de")
    						|| lastTwoChars.equals("fr")
    						|| lastTwoChars.equals("nl")
    						|| lastTwoChars.equals("it")
    						){
    					neededExRates.add("eurusd");
    				}else if(lastTwoChars.equals("ch")){
    					neededExRates.add("usdchf");
    				}else if(lastTwoChars.equals("us")){
    					continue;
    				}else{
    					System.out.println("jeffery's stock market list is not completed!");
    				}
    			}
    		}
    		return neededExRates;
    	}
    	

    	
    	private HashMap<String, Float> sumRates(ExRateTable exist, HashSet<String> missing){
    		// put exist items first
    		HashMap<String, Float> sum = new HashMap<String, Float>(exist.getTheMap());
    		
    		// append the missing items
    		Iterator<String> itor = missing.iterator();
    		while(itor.hasNext()){
    			String key = itor.next();
    			Float val = new Float(0); // fill 0 means empty, ask user to fill it
    			sum.put(key, val);
    		}
    		
    		return sum;
    	}
    	
    	private void writeRatesIntoExRateFile(HashMap<String, Float> rates){
    		ArrayList<Line> table = new ArrayList<Line>();
    		Iterator<Entry<String, Float>> itor = rates.entrySet().iterator();
    		while(itor.hasNext()){
    			Entry<String, Float> entry = itor.next();
    			Object key = entry.getKey();
    			Object val = entry.getValue();
    			table.add(new Line(new Object[] {key, val}));
    		}
    		m_excelIO.writeTable(table, getFile(FILE_NAMES.EX_RATES));
    	}
    	
    	
    	private void initPIPtablesAndValuePerSlotTables(){
    		m_fxPIPtable = new FxPIPtable(m_exRateTable);
    		m_indexPIPtable = new IndexPIPtable(m_exRateTable);
    		
    		m_indexValTable = new IndexValueTable(m_exRateTable);
    		m_stockValTable = new StockValueTable(m_exRateTable);
    	}
    	
    	HashSet<String> collectAllActiveIBs(){
    		HashSet<String> set = new HashSet<String>();
    		/* todo: collect from m_activeGroups to save time, and m_activeGroups is ready now,
    		 * to ensure correctness, collect from m_deals for now 
    		 * m_deals has IB tag now
    		 */
    		for(Deal d: m_deals){
    			String IB = d.getIB();
    			assert(null != IB);
    			set.add(IB);
    		}
    		
    		return set;
    	}
    	
    	private void outputFinalReport(){
    		String workingDir = System.getProperty("user.dir");
    		File newFile = new File( workingDir + "/" + FILE_NAMES.FINAL_REPORT);
    		this.addTableHeaderForFinalReport();
    		m_excelIO.writeTable(m_resultTable, newFile);
    	}
    	
    	private boolean checkInputDataCompletion(){
    		if(false == checkClientGroupMapCompletion())
    			return false;
    		
    		if(false == checkGroupIBMapCompletion())
    			return false;
    		
    		if(false == checkGroupConditionMapCompletion())
    			return false;
    		
    		if(false == checkExRateCompletion())
    			return false;
    		
    		return true;
    	}
    	
    	private HashSet<String> diffRateTable(HashSet<String> needed, ExRateTable provided){
    		HashSet<String> missing = new HashSet<String>();
    		
    		Iterator<String> itor = needed.iterator();
    		while(itor.hasNext()){
    			String needThisOne = itor.next();
    			if(!provided.containsKey(needThisOne)){
    				missing.add(needThisOne);
    			}
    		}
    		
    		return missing;
    	}
    	
    	private boolean parseInputFiles(){
			appendLog("reading " + FILE_NAMES.DEAL_RECORD);
    		if(false == parseTheDealRecordFile())
    			return false;
    		if(this.isCancelled())
    			return false;
    		
			appendLog("reading " + FILE_NAMES.GROUP_CLIENT);
    		if(false == parseClientGroupFile())
    			return false;
    		if(this.isCancelled())
    			return false;
    		
			appendLog("reading " + FILE_NAMES.GROUP_CONDITION);
    		if(false == parseTheGroupConditionFile())
    			return false;
    		if(this.isCancelled())
    			return false;
    		
			appendLog("reading " + FILE_NAMES.IB_GROUP);
    		if(false == parseTheGroupIBFile())
    			return false;
    		if(this.isCancelled())
    			return false;
    		
			appendLog("reading " + FILE_NAMES.EX_RATES);
    		if(false == parseTheExRateFile())
    			return false;
    		if(this.isCancelled())
    			return false;
    		
    		return true;
    	}
    	
    	/* 
    	 * count fee for every group for this version, instead of IB
    	 * map every group to a IB with the same name, ex: group AAA => IB AAA 
    	 * todo: take care of IB
    	 */
    	private boolean parseTheGroupIBFile(){
    		/*
    		File f = getFile(FILE_NAMES.IB_GROUP);
    		this.m_groupIBMap = m_excelIO.readIBGroupFile(f);
    		if(null == m_groupIBMap){
    			System.out.println("parse group ib file failed!");
    			appendLog("ERROR: parse " + FILENAMES.IB_GROUP" + " failed");
    			return false;
    		}
    		*/
    		m_groupIBMap = new HashMap<String, String>(); //todo: delete it, callee will new obj
    		return true; // todo: check exception
    	}
    	
    	
    	private boolean parseTheExRateFile(){
    		File f = getFile(FILE_NAMES.EX_RATES);
    		m_exRateTable = m_excelIO.readExRateFile(f);
    		if(null == m_exRateTable){
    			System.out.println("parse exRate file failed!");
    			appendLog("ERROR: parse " + FILE_NAMES.EX_RATES + " failed!");
    			return false;
    		}
    		return true;
    	}
    	
    	private boolean parseTheGroupConditionFile(){
    		File f = getFile(FILE_NAMES.GROUP_CONDITION);
    		m_groupConditionMap = m_excelIO.readGroupConditonFile(f);
    		if(null == m_groupConditionMap){
    			System.out.println("parse group condition file failed!");
    			appendLog("ERROR: parse " + FILE_NAMES.GROUP_CONDITION + " failed!");
    			return false;
    		}
    		return true;
    	}
    	
    	private boolean parseClientGroupFile(){
    		File f = getFile(FILE_NAMES.GROUP_CLIENT);
    		m_clientGroupMap = m_excelIO.readClientGroupFile(f);
    		if(null == m_clientGroupMap){
    			System.out.println("parseClientGroupFile failed!");
    			appendLog("ERROR: parse " + FILE_NAMES.GROUP_CLIENT + " failed!");
    			return false;
    		}
    		return true;
    	}
    	
    	private File getFile(String fileName){
    		for(File f: m_files){
    			if(f.getName().equals(fileName))
    				return f;
    		}
    		return null;
    	}
    	
    	private void handleAllActiveIBs(HashSet<String> ibs){
    		Iterator<String> itor = ibs.iterator();
    		while(itor.hasNext()){
    			String ib = itor.next();
    			ArrayList<Deal> dealsOfThisIB = getAllDealsOfThisIBFrom(ib, m_deals); //filter for callee for efficiency
    			handleThisIB(ib, dealsOfThisIB);
    		}
    	}
    	
    	private ArrayList<Deal> getAllDealsOfThisIBFrom(String ib, ArrayList<Deal> from){
    		ArrayList<Deal> result = new ArrayList<Deal>();
    		for(Deal d: from){
    			if(d.getIB().equals(ib)){
    				result.add(d);
    			}
    		}
    		return result;
    	}
    	
    	private void handleThisIB(String ib, ArrayList<Deal> dealsOfThisIB){
    		computeAndSaveSomeParamsForQueringCondtionLater(ib, dealsOfThisIB); // use later
    		
    		HashSet<String> groups = getAllActiveGroupsOfThisIB(ib);
    		Iterator<String> itor = groups.iterator();
    		Summary summary = new Summary();
    		while(itor.hasNext()){
    			String group = itor.next();
    			ArrayList<Deal> dealsOfThisGroup = getAllDealsOfThisGroupFrom(group, dealsOfThisIB);
    			Summary s = handleThisGroup(ib, group, dealsOfThisGroup);
    			summary.add(s);
    		}
    		
    		Object[] line = new Object[]{ib, "", "", "", "", summary.getSlotSum(),
    				summary.getRebateSum(), summary.getMarkupSum(), summary.getCommSum(), 
    				summary.getTotalFee()};
    		this.insertLineAtTopIntoResultTable(line);
    	}
    	
    	ArrayList<Deal> getAllDealsOfThisGroupFrom(String group, ArrayList<Deal> from){
    		ArrayList<Deal> sub = new ArrayList<Deal>();
    		for(Deal d: from){
    			if(d.getGroup().equals(group)){
    				sub.add(d);
    			}
    		}
    		return sub;
    	}
    	
    	/* should collect in reverse direction: active group -> ib
    	 * NOT in forward direction: ib -> group, because that may find inactive groups
    	 */
    	HashSet<String> getAllActiveGroupsOfThisIB(String ib){
    		HashSet<String> set = new HashSet<String>();
    		Iterator<String> itor = m_activeGroups.iterator();
    		while(itor.hasNext()){
    			String group = itor.next();
    			if(getIBof(group).equals(ib)){
    				set.add(group);
    			}
    		}
    		return set;
    	}
    	
    	// use redundant params to make code more readable :)
    	private Summary handleThisGroup(String ib, String group, ArrayList<Deal> dealsOfThisGroup){
    		Summary summary = new Summary();
    		HashSet<String> clients = getAllActiveClientsOfThisGroup(group);
    		Iterator<String> itor = clients.iterator();
    		while(itor.hasNext()){
    			String client = itor.next();
    			ArrayList<Deal> dealsOfThisClient = getAllDealsOfThisClientFrom(client, dealsOfThisGroup);
    			Summary s = handleThisClient(ib, group, client, dealsOfThisClient);
    			summary.add(s);
    		}
    		
    		Object[] line = new Object[]{"",group,  "", "", "", summary.getSlotSum(),
    				summary.getRebateSum(), summary.getMarkupSum(), summary.getCommSum(), 
    				summary.getTotalFee()};
    		this.insertLineAtTopIntoResultTable(line);
    		
    		return summary;
    	}
    	
    	ArrayList<Deal> getAllDealsOfThisClientFrom(String client, ArrayList<Deal> from){
    		ArrayList<Deal> sub = new ArrayList<Deal>();
    		for(Deal d: from){
    			if(d.getLogin().equals(client)){
    				sub.add(d);
    			}
    		}
    		return sub;
    	}
    	
    	HashSet<String> getAllActiveClientsOfThisGroup(String group){
    		HashSet<String> set = new HashSet<String>();
    		Iterator<String> itor = m_activeClients.iterator();
    		while(itor.hasNext()){
    			String client = itor.next();
    			if(getGroupOf(client).equals(group)){
    				set.add(client);
    			}
    		}
    		return set;
    	}
    	
    	private void computeAndSaveSomeParamsForQueringCondtionLater(
    			String ib, ArrayList<Deal> dealsOfThisIB){
    		float fxGoldSlotSum = 0;
    		float silverOilIndexValueSum = 0;
    		for(Deal d: dealsOfThisIB){
    			if(d.isFx() || d.isGold()){
    				fxGoldSlotSum += d.getSlot();
    			}else if(d.isSilver() || d.isOil()){
    				float val = d.getSlot() * getValPerSlotForThisSilverOrOilDeal(d);
    				silverOilIndexValueSum += val;
    			}else if(d.isIndex()){
    				float slot = d.getSlot();
    				float valPerSlot = m_indexValTable.getValuePerSlot(d.getSymbol(), d.getClosePrice());
    				float val = slot * valPerSlot;
    				silverOilIndexValueSum += val;
    			}
    		}
    		
    		/* 
    		 * convert to million
    		 * in condition file: index_rebate_volume_million_USD=1 means 1 million USD
    		 * so they match
    		 */
    		float silverOilIndexValueSumMillion = silverOilIndexValueSum/1000000f;
    		// save
    		m_ibParamMap.put(ib, new IBParam(fxGoldSlotSum, silverOilIndexValueSumMillion));
    	}
    	
    	private float getValPerSlotForThisSilverOrOilDeal(Deal silverOrOilDeal){
    		return 100 * silverOrOilDeal.getClosePrice();
    	}
    	
    	private void insertLineAtTopIntoResultTable(Object[] lineData){
    			Line line = new Line(lineData);
    			m_resultTable.add(0, line);
    	}
    	
    	private Summary handleThisClient(String ib, String group, String client, 
    			ArrayList<Deal> dealsOfThisClient){
    		Summary summary = new Summary();
    		Summary s;
    		ArrayList<Deal> fxDeals = getAllFxDealsFrom(dealsOfThisClient);
    		s = handleFxDealsOf(ib, group, client, fxDeals);
    		summary.add(s);
    		
    		ArrayList<Deal> commDeals = getAllCommodityDealsFrom(dealsOfThisClient);
    		s = handleCommodityDealsOf(ib, group, client, commDeals);
    		summary.add(s);
    		
    		ArrayList<Deal> indexDeals = getAllIndexDealsFrom(dealsOfThisClient);
    		s = handleIndexDealsOf(ib, group, client, indexDeals);
    		summary.add(s);
    		
    		ArrayList<Deal> stockDeals = getAllStockDealsFrom(dealsOfThisClient);
    		s = handleStockDealsOf(ib, group, client, stockDeals);
    		summary.add(s);
    		
    		Object[] line = new Object[]{"", "", client, "", "", summary.getSlotSum(),
    				summary.getRebateSum(), summary.getMarkupSum(), summary.getCommSum(),
    				summary.getTotalFee()};
    		this.insertLineAtTopIntoResultTable(line);
    		
    		return summary;
    	}
    	
    	
    	private Summary handleFxDealsOf(String ib, String group, String client, ArrayList<Deal> fxDeals){
    		Summary summary = new Summary();
    		HashSet<String> symbols = getAllSymbolsFrom(fxDeals);
    		Iterator<String> itor = symbols.iterator();
    		while(itor.hasNext()){
    			String sym = itor.next();
    			ArrayList<Deal> dealsWithSymbol = getAllDealsWithThisSymbolFrom(sym, fxDeals);
    			Summary s = handleFxDealsWithSymbolOf(ib, group, client, sym, dealsWithSymbol);
    			summary.add(s);
    		}
    		
    		Object[] line = new Object[]{"", "", "", "fx", "", summary.getSlotSum(),
    				summary.getRebateSum(), summary.getMarkupSum(), summary.getCommSum(),
    				summary.getTotalFee()};
    		this.insertLineAtTopIntoResultTable(line);
    		
    		return summary;
    	}
    	
    	private Summary handleFxDealsWithSymbolOf(String ib, String group, String client, String symbol, 
    			ArrayList<Deal> deals){
    		Summary summary = new Summary();
    		// can NOT combine multi deals with the same symbol, because they may have different close price
    		for(Deal d: deals){
    			Summary s = handleOneFxDeal(d); //use less param from now, meaning is clear enough already
    			summary.add(s);
    		}
    		
    		Object[] line = new Object[]{"", "", "", "", symbol, summary.getSlotSum(),
    				summary.getRebateSum(), summary.getMarkupSum(), summary.getCommSum(),
    				summary.getTotalFee()};
    		this.insertLineAtTopIntoResultTable(line);
    		
    		return summary;
    	}
    	
    	private Summary handleCommodityDealsWithSymbolOf(String ib, String client, String symbol, 
    			ArrayList<Deal> deals){
    		Summary summary = new Summary();
    		for(Deal d: deals){
    			Summary s = handleOneCommodityDeal(d);
    			summary.add(s);
    		}
    		
    		Object[] line = new Object[]{"", "", "", "", symbol, summary.getSlotSum(),
    				summary.getRebateSum(), summary.getMarkupSum(), summary.getCommSum(),
    				summary.getTotalFee()};
    		this.insertLineAtTopIntoResultTable(line);
    		
    		return summary;
    	}
    	
    	
    	private Summary handleIndexDealsWithSymbolOf(String ib, String client, String symbol, 
    			ArrayList<Deal> deals){
    		Summary summary = new Summary();
    		for(Deal d: deals){
    			Summary s = handleOneIndexDeal(d);
    			summary.add(s);
    		}
    		
    		Object[] line = new Object[]{"", "", "", "", symbol, summary.getSlotSum(),
    				summary.getRebateSum(), summary.getMarkupSum(), summary.getCommSum(),
    				summary.getTotalFee()};
    		this.insertLineAtTopIntoResultTable(line);
    		
    		return summary;
    	}
    	
    	private Summary handleStockDealsWithSymbolOf(String ib, String client, String symbol, 
    			ArrayList<Deal> deals){
    		Summary summary = new Summary();
    		for(Deal d: deals){
    			Summary s = handleOneStockDeal(d);
    			summary.add(s);
    		}
    		
    		Object[] line = new Object[]{"", "", "", "", symbol, summary.getSlotSum(),
    				summary.getRebateSum(), summary.getMarkupSum(), summary.getCommSum(),
    				summary.getTotalFee()};
    		this.insertLineAtTopIntoResultTable(line);
    		
    		return summary;
    	}
    	
    	private Summary handleOneFxDeal(Deal deal){ // use less param from now, deals include all info we need
    		float rebate = computeRebateForThisFxDeal(deal);
    		float markup = computeMarkupForThisFxDeal(deal);
    		float comm = computeCommForThisFxDeal(deal);
    		
    		Summary summary = new Summary(deal.getSlot(), rebate, markup, comm);
    		return summary;
    	}
    	
    	private Summary handleOneCommodityDeal(Deal deal){
    		float rebate = computeRebateForThisCommodityDeal(deal);
    		float markup = computeMarkupForThisCommodityDeal(deal);
    		float comm = computeCommForThisCommodityDeal(deal);
    		
    		Summary summary = new Summary(deal.getSlot(), rebate, markup, comm);
    		return summary;
    	}
    	
    	private Summary handleOneIndexDeal(Deal deal){
    		float rebate = computeRebateForThisIndexDeal(deal);
    		float markup = computeMarkupForThisIndexDeal(deal);
    		float comm = computeCommForThisIndexDeal(deal);
    		
    		Summary summary = new Summary(deal.getSlot(), rebate, markup, comm);
    		return summary;
    	}
    	
    	private Summary handleOneStockDeal(Deal deal){
    		float rebate = computeRebateForThisStockDeal(deal);
    		float markup = computeMarkupForThisStockDeal(deal);
    		float comm = computeCommForThisStockDeal(deal);
    		
    		Summary summary = new Summary(deal.getSlot(), rebate, markup, comm);
    		return summary;
    	}
    	
    	/* ******************* fx ******************** */
    	private float computeRebateForThisFxDeal(Deal deal){
    		float rebate;
    		WAY way = getFxRebateComputationWay(deal);
    		if(WAY.POINT == way){
    			float slot = deal.getSlot();
    			float pointPerSlot = getFxRebateQuotaOrPointPerSlot(deal);
    			float PIP = m_fxPIPtable.getPIPbySymbol(deal.getSymbol());
    			rebate = slot * pointPerSlot * PIP;
    		}else{ // QUOTA
    			float slot = deal.getSlot();
    			float quotaPerSlot = getFxRebateQuotaOrPointPerSlot(deal);
    			rebate = slot * quotaPerSlot;
    		}
    		return rebate;
    	}
    	
    	private float getFxRebateQuotaOrPointPerSlot(Deal deal){
    		PRODUCT_TYPE groupEnum		= getGroupEnumForThisFxDeal(deal);
    		float fxGoldSlotSum 		= getFxGoldSlotSumForThisDeal(deal);
    		float quotaOrPointPerSlot 	= getRebateQuotaOrPointPerSlotForThisFxDeal(
    											deal, groupEnum, fxGoldSlotSum);
    		return quotaOrPointPerSlot;
    	}
    	
    	private PRODUCT_TYPE getGroupEnumForThisFxDeal(Deal deal){
    		return m_fxGroupMap.getABCDEF(deal.getSymbol());
    	}
    	
    	// generic for all fx and commod deal
    	private float getFxGoldSlotSumForThisDeal(Deal deal){
    		String ib = deal.getIB();
    		IBParam param = m_ibParamMap.get(ib);
    		return param.getM_fxGoldSlotSum();
    	}
    	
    	private float getRebateQuotaOrPointPerSlotForThisFxDeal(
    			Deal deal, PRODUCT_TYPE groupEnum, float fxGoldVolSum){
    		String group = deal.getGroup();
    		Condition cond = m_groupConditionMap.get(group);
    		float[] vals = null;
    		float[] range = cond.getFxCommVol(); //yes, use column fxCommVol in condition file as fxGoldVol
    		// todo: can i use enum as int ?
    		switch(groupEnum){
    		case A:
    			vals = cond.getFxARebate(); // be careful, call corresponding getXRebate()
    			break;
    		case B:
    			vals = cond.getFxBRebate();
    			break;
    		case C:
    			vals = cond.getFxCRebate();
    			break;
    		case D:
    			vals = cond.getFxDRebate();
    			break;
    		case E:
    			vals = cond.getFxERebate();
    			break;
    		case F:
    			vals = cond.getFxFRebate();
    			break;
    		default:
    			break;
    		}
    		
    		return getValByRange(vals, range, fxGoldVolSum);
    	}
    	
    	/*
    	 * example:
    	 * 										range[] 	vals[]
    	 * 										0			5
    	 * val=600, in this range, return 6 ->	500			6
    	 * 										1000		7
    	 * 
    	 * what if val == range
    	 * answer: if val == 500 in this example, return the value of the next bigger rang, 7
    	 */
    	private float getValByRange(float[] vals, float[] range, float val){
    		assert(3 == vals.length && 3 == range.length);
    		if(val < range[1])
    			return vals[0];
    		else if(val < range[2])
    			return vals[1];
    		else
    			return vals[2];
    	}
    	
    	private WAY getFxRebateComputationWay(Deal deal){
    		String group = deal.getGroup();
    		Condition cond = m_groupConditionMap.get(group);
    		return cond.getFxRebateType();
    	}

    	private float computeMarkupForThisFxDeal(Deal deal){
    		float slot = deal.getSlot();
    		float pointPerSlot = getMarkupPointPerSlotForThisFxDeal(deal);
    		float PIP = m_fxPIPtable.getPIPbySymbol(deal.getSymbol());
    		float markup = slot * pointPerSlot * PIP;
    		return markup;
    	}
    	
    	private float getMarkupPointPerSlotForThisFxDeal(Deal fxDeal){
    		String group = fxDeal.getGroup();
    		Condition cond = m_groupConditionMap.get(group);
    		PRODUCT_TYPE groupEnum = this.getGroupEnumForThisFxDeal(fxDeal);
    		float[] vals = null;
    		switch(groupEnum){
    		case A:	//AB as one group when counting markup
    		case B:
    			vals = cond.getFxABMarkupPIP();
    			break;
    		case C:
    			vals = cond.getFxCMarkupPIP();
    			break;
    		case D:
    			vals = cond.getFxDMarkupPIP();
    			break;
    		case E:
    			vals = cond.getFxEMarkupPIP();
    			break;
    		case F:
    			vals = cond.getFxFMarkupPIP();
    			break;
    		default:
    			break;
    		}
    		
    		return vals[0]; // it contains 3 equal value, because it's not related to trading vol
    	}
    	
    	private float computeCommForThisFxDeal(Deal deal){
    		float slot = deal.getSlot();
    		float quotaPerSlot = getFxCommQuotaPerSlot(deal);
    		float comm = slot * quotaPerSlot;
    		return comm;
    	}
    	
    	private float getFxCommQuotaPerSlot(Deal deal){
    		String group = deal.getGroup();
    		Condition cond = m_groupConditionMap.get(group);
    		PRODUCT_TYPE groupEnum = this.getGroupEnumForThisFxDeal(deal);
    		float[] vals = null;
    		switch(groupEnum){
    		case A:	//AB as one group when counting commission
    		case B:
    			vals = cond.getFxABCommUSD();
    			break;
    		case C:
    			vals = cond.getFxCCommUSD();
    			break;
    		case D:
    			vals = cond.getFxDCommUSD();
    			break;
    		case E:
    			vals = cond.getFxECommUSD();
    			break;
    		case F:
    			vals = cond.getFxFCommUSD();
    			break;
    		default:
    			break;
    		}
    		
    		/* 
    		 * comm/markup = f(x); x do NOT contain trading volume
    		 * so, vals contains 3 equal values, return whichever you want
    		 */
    		return vals[0];
    	}
    	
    	/* ******************* commodity ******************** */
    	private float computeRebateForThisCommodityDeal(Deal deal){
    		float rebate = 0;
    		if(deal.isGold()){
    			rebate = computeRebateForThisGoldDeal(deal);
    		}
    		else if(deal.isSilver()){
    			rebate = computeRebateForThisSilverDeal(deal);
    		}
    		else if(deal.isOil()){
    			rebate = computeRebateForThisOilDeal(deal);
    		}
    		else{
    			System.out.println(
    					"Main:computeRebateForThisCommodityDeal(): invalid commodity type" + 
    							deal.getSymbol());
    		}
    		return rebate;
    	}
    	
    	private float computeRebateForThisOilDeal(Deal oilDeal){
    		float rebate;
    		WAY way = getRebateComputationWayForThisCommodDeal(oilDeal);
    		if(WAY.QUOTA == way){
    			float slot = oilDeal.getSlot();
    			float quotaPerSlot = getRebateQuotaPerSlotOrPercentForThisOilDeal(oilDeal);
    			rebate = slot * quotaPerSlot;
    		}else if(WAY.PERCENT == way){
    			float slot = oilDeal.getSlot();
    			float valPerSlot = getValPerSlotForThisOilDeal(oilDeal);
    			float percent = getRebateQuotaPerSlotOrPercentForThisOilDeal(oilDeal);
    			float cookedPercent = percent/100;
    			rebate = slot * valPerSlot * cookedPercent;
    		}else{
    			printError("invalid computation way for this oil deal");
    			rebate = 0;
    		}
    		return rebate;
    	}
    	
    	private float computeRebateForThisSilverDeal(Deal silverDeal){
    		float rebate;
    		WAY way = getRebateComputationWayForThisCommodDeal(silverDeal);
    		if(WAY.QUOTA == way){
    			float slot = silverDeal.getSlot();
    			float quotaPerSlot = getRebateQuotaPerSlotOrPercentForThisSilverDeal(silverDeal);
    			rebate = slot * quotaPerSlot;
    		}else if(WAY.PERCENT == way){
    			float slot = silverDeal.getSlot();
    			float valPerSlot = getValPerSlotForThisSilverDeal(silverDeal);
    			float percent = getRebateQuotaPerSlotOrPercentForThisSilverDeal(silverDeal);
    			float cookedPercent = percent/100;
    			rebate = slot * valPerSlot * cookedPercent;
    		}else{
    			printError("invalid computation way for this silver deal");
    			rebate = 0;
    		}
    		return rebate;
    	}
    	
    	private float getValPerSlotForThisSilverDeal(Deal silverDeal){
    		// call generic function :)
    		return this.getValPerSlotForThisSilverOrOilDeal(silverDeal);
    	}
    	
    	private float getValPerSlotForThisOilDeal(Deal oilDeal){
    		// call generic function :)
    		return this.getValPerSlotForThisSilverOrOilDeal(oilDeal);
    	}
    	
    	private float getRebateQuotaPerSlotOrPercentForThisSilverDeal(Deal silverDeal){
    		// call a more generic function :)
    		return getRebateQuotaPerSlotOrPercentForThisSilverOrOilDeal(silverDeal);
    	}
    	
    	private float getRebateQuotaPerSlotOrPercentForThisOilDeal(Deal oilDeal){
    		// call a more generic function :)
    		return getRebateQuotaPerSlotOrPercentForThisSilverOrOilDeal(oilDeal);
    	}
    	
    	private float getRebateQuotaPerSlotOrPercentForThisSilverOrOilDeal(Deal silverOrOilDeal){
    		Condition cond = this.getConditionByGroupID(silverOrOilDeal.getGroup());
    		float[] vals;
    		if(silverOrOilDeal.isSilver()){
    			vals = cond.getCommSilverRebateFixUSDorPercent();
    		}
    		else{
    			// must be oil
    			vals = cond.getCommOilRebateFixUSDorPercent();
    		}	
    			
    		// yes, use indexRebateVolMillionUSD from table as silverOilIndexValSum range
    		float[] range = cond.getIndexRebateVolMillionUSD();
    		float silverOilIndexValueSumMillion = getSilverOilIndexValueSumMillionForThisSilverOilOrIndexDeal(
    											silverOrOilDeal);
    		float quotaOrPercent = this.getValByRange(vals, range, silverOilIndexValueSumMillion);
    		return quotaOrPercent;
    	}
    	
    	private float getSilverOilIndexValueSumMillionForThisSilverOilOrIndexDeal(Deal silverOilOrIndexDeal){
    		return m_ibParamMap.get(silverOilOrIndexDeal.getIB()).getM_silverOilIndexValueSumMillion();
    	}
    	
    	private WAY getRebateComputationWayForThisCommodDeal(Deal commodDeal){
    		Condition cond = this.getConditionByGroupID(commodDeal.getGroup());
    		return cond.getCommRebateType();
    	}
    	
    	private void printError(String str){
    		System.out.println(str);
    	}
    	
    	private float computeRebateForThisGoldDeal(Deal goldDeal){
    		float slot = goldDeal.getSlot();
    		float quotaPerSlot = getRebateQuotaPerSlotForThisGoldDeal(goldDeal);
    		float rebate = slot * quotaPerSlot;
    		return rebate;
    	}
    	
    	private float getRebateQuotaPerSlotForThisGoldDeal(Deal goldDeal){
    		float fxGoldSlotSum = getFxGoldSlotSumForThisDeal(goldDeal);
    		String group = goldDeal.getGroup();
    		Condition cond = getConditionByGroupID(group);
    		float[] vals = cond.getCommGoldRebatefixUSD();
    		float[] range = cond.getFxCommVol(); //yes, use FxCommVol from condition file as fxGoldSlotSum
    		float quota = getValByRange(vals, range, fxGoldSlotSum);
    		return quota;
    	}
    	
    	private Condition getConditionByGroupID(String groupID){
    		return m_groupConditionMap.get(groupID);
    	}
    	
    	private float computeMarkupForThisCommodityDeal(Deal deal){
    		float slot = deal.getSlot();
    		float quotaPerSlot = getMarkupQuotaPerSlotForThisCommodDeal(deal);
    		float markup = slot * quotaPerSlot;
    		return markup;
    	}
    	
    	private float getMarkupQuotaPerSlotForThisCommodDeal(Deal commodDeal){
    		Condition cond = this.getConditionByGroupID(commodDeal.getGroup());
    		float[] threeEqualVals;
    		if(commodDeal.isGold()){
    			//yes, condition file only has PIP(point), we will convert it to quota later
    			threeEqualVals = cond.getCommGoldMarkupPIP();
    		}else if(commodDeal.isSilver()){
    			threeEqualVals = cond.getCommSilverMarkupPIP();
    		}else{// must be oil
    			threeEqualVals = cond.getCommOilMarkupPIP();
    		}
    		float whichever = threeEqualVals[0];
    		float quota = convertMarkupPointToQuotaForCommodDeal(whichever);
    		return quota;
    	}
    	
    	// for call commodity deals, assume slot has been cooked(/10), 1 point => 10 USD
    	private float convertMarkupPointToQuotaForCommodDeal(float point){
    		return point * 10;
    	}
    	
    	private float computeCommForThisCommodityDeal(Deal deal){
    		float slot = deal.getSlot();
    		float quotaPerSlot = getCommQuotaPerSlotForThisCommodDeal(deal);
    		float comm = slot * quotaPerSlot;
    		return comm;
    	}
    	
    	private float getCommQuotaPerSlotForThisCommodDeal(Deal commodDeal){
    		Condition cond = this.getConditionByGroupID(commodDeal.getGroup());
    		float[] threeEqualVals = null;
    		if(commodDeal.isGold()){
    			threeEqualVals = cond.getCommGoldCommUSD();
    		}else if(commodDeal.isSilver()){
    			threeEqualVals = cond.getCommSilverCommUSD();
    		}else if(commodDeal.isOil()){
    			threeEqualVals = cond.getCommOilCommUSD();
    		}else{
    			printError("Main.java : getCommQuotaPerSlotForThisCommodDeal()"
    					+ "invalid product type" + commodDeal.getSymbol());
    		}
    		
    		float whichever = threeEqualVals[0];
    		return whichever;
    	}
    	
    	
    	/* ******************* index ******************** */
    	private float computeRebateForThisIndexDeal(Deal deal){
    		float slot = deal.getSlot();
    		float valPerSlot = m_indexValTable.getValuePerSlot(deal.getSymbol(), deal.getClosePrice());
    		float percent = getRebatePercentForThisIndexDeal(deal);
    		float cookedPercent = percent/100; //50 means 50% => 50/100
    		float rebate = slot * valPerSlot * cookedPercent;
    		return rebate;
    	}
    	
    	/* 
    	 * return percent number like 0.0035, means 0.0035%, yes, 0.0035%!
    	 * so, you should remember to divide 0.0035 by 100 when using
    	 */
    	private float getRebatePercentForThisIndexDeal(Deal indexDeal){
    		float silverOilIndexValueSumMillion = this.getSilverOilIndexValueSumMillionForThisSilverOilOrIndexDeal(indexDeal);
    		Condition cond = this.getConditionByGroupID(indexDeal.getGroup());
    		float[] vals = cond.getIndexRebatePercent();
    		float[] range = cond.getIndexRebateVolMillionUSD();
    		float percent = this.getValByRange(vals, range, silverOilIndexValueSumMillion);
    		return percent;
    	}
    	
    	private float computeMarkupForThisIndexDeal(Deal deal){
    		float slot = deal.getSlot();
    		float pointPerSlot = getMarkupPointPerSlotForThisIndexDeal(deal);
    		float PIP = m_indexPIPtable.getPIPbySymbol(deal.getSymbol());
    		float markup = slot * pointPerSlot * PIP;
    		return markup;
    	}
    	
    	private float getMarkupPointPerSlotForThisIndexDeal(Deal indexDeal){
    		Condition cond = this.getConditionByGroupID(indexDeal.getGroup());
    		float[] threeEqualVals = cond.getIndexMarkupPIP();
    		return threeEqualVals[0];
    	}
    	
    	private float computeCommForThisIndexDeal(Deal deal){
    		float slot = deal.getSlot();
    		float quotaPerSlot = getCommQuotaPerSlotForThisIndexDeal(deal);
    		float comm = slot * quotaPerSlot;
    		return comm;
    	}
    	
    	private float getCommQuotaPerSlotForThisIndexDeal(Deal indexDeal){
    		Condition cond = this.getConditionByGroupID(indexDeal.getGroup());
    		float[] threeEqualVals = cond.getIndexCommUSD();
    		return threeEqualVals[0];
    	}
    	
    	/* ******************* stock ******************** */
    	private float computeRebateForThisStockDeal(Deal deal){
    		float slot = deal.getSlot();
    		float valPerSlot = m_stockValTable.getValuePerSlot(deal.getSymbol(), deal.getClosePrice());
    		float percent = getRebatePercentForThisStockDeal(deal);
    		float cookedPercent = percent / 100;  //percent = 50 means 50% => 50/100
    		float magicFactor = 0.0001f; // 0.1%, jeffery told me, check doc
    		float rebate = slot * valPerSlot * magicFactor * cookedPercent;
    		return rebate;
    	}
    	
    	// return percent number like 50, 60, means 50%, 60%, remember to divide it by 100 when using
    	private float getRebatePercentForThisStockDeal(Deal stockDeal){
    		Condition cond = this.getConditionByGroupID(stockDeal.getGroup());
    		float[] threeEqualVals = cond.getStockRebatePercent();
    		return threeEqualVals[0];
    	}
    	
    	private float computeMarkupForThisStockDeal(Deal deal){
    		return 0; // no markup for stock deal
    	}
    	
    	private float computeCommForThisStockDeal(Deal deal){
    		float slot = deal.getSlot();
    		float quotaPerSlot = getCommQuotaPerSlotForThisStockDeal(deal);
    		float comm = slot * quotaPerSlot;
    		return comm;
    	}
    	
    	private float getCommQuotaPerSlotForThisStockDeal(Deal stockDeal){
    		Condition cond = this.getConditionByGroupID(stockDeal.getGroup());
    		float[] threeEqualVals = cond.getStockCommUSD();
    		return threeEqualVals[0];
    	}
    	
    	private Summary handleCommodityDealsOf(String ib, String group, String client, ArrayList<Deal> commodityDeals){
    		Summary summary = new Summary();
    		HashSet<String> symbols = getAllSymbolsFrom(commodityDeals);
    		Iterator<String> itor = symbols.iterator();
    		while(itor.hasNext()){
    			String sym = itor.next();
    			ArrayList<Deal> dealsWithSymbol = getAllDealsWithThisSymbolFrom(sym, commodityDeals);
    			Summary s = handleCommodityDealsWithSymbolOf(ib, client, sym, dealsWithSymbol);
    			summary.add(s);
    		}
    		
    		Object[] line = new Object[]{"", "", "", "commodity", "",
    				summary.getSlotSum(), summary.getRebateSum(), summary.getMarkupSum(),
    				summary.getCommSum(), summary.getTotalFee()};
    		this.insertLineAtTopIntoResultTable(line);
    		
    		return summary;
    	}
    	
    	private Summary handleIndexDealsOf(String ib, String group, String client, ArrayList<Deal> indexDeals){
    		Summary summary = new Summary();
    		HashSet<String> symbols = getAllSymbolsFrom(indexDeals);
    		Iterator<String> itor = symbols.iterator();
    		while(itor.hasNext()){
    			String sym = itor.next();
    			ArrayList<Deal> dealsWithSymbol = getAllDealsWithThisSymbolFrom(sym, indexDeals);
    			Summary s = handleIndexDealsWithSymbolOf(ib, client, sym, dealsWithSymbol);
    			summary.add(s);
    		}
    		
    		Object[] line = new Object[]{"", "", "", "index", "",
    				summary.getSlotSum(), summary.getRebateSum(), summary.getMarkupSum(),
    				summary.getCommSum(), summary.getTotalFee()};
    		this.insertLineAtTopIntoResultTable(line);
    		
    		return summary;
    	}
    	
    	private Summary handleStockDealsOf(String ib, String group, String client, ArrayList<Deal> stockDeals){
    		Summary summary = new Summary();
    		HashSet<String> symbols = getAllSymbolsFrom(stockDeals);
    		Iterator<String> itor = symbols.iterator();
    		while(itor.hasNext()){
    			String sym = itor.next();
    			ArrayList<Deal> dealsWithSymbol = getAllDealsWithThisSymbolFrom(sym, stockDeals);
    			Summary s = handleStockDealsWithSymbolOf(ib, client, sym, dealsWithSymbol);
    			summary.add(s);
    		}
    		
    		Object[] line = new Object[]{"", "", "", "stock", "",
    				summary.getSlotSum(), summary.getRebateSum(), summary.getMarkupSum(),
    				summary.getCommSum(), summary.getTotalFee()};
    		this.insertLineAtTopIntoResultTable(line);
    		
    		return summary;
    	}
    	
    	private ArrayList<Deal> getAllDealsWithThisSymbolFrom(String symbol, ArrayList<Deal> from){
    		ArrayList<Deal> result = new ArrayList<Deal>();
    		for(Deal d: from){
    			if(d.getSymbol().equals(symbol)){
    				result.add(d);
    			}
    		}
    		return result;
    	}
    	
    	private HashSet<String> getAllSymbolsFrom(ArrayList<Deal> from){
    		HashSet<String> set = new HashSet<String>();
    		for(Deal d: from){
    			set.add(d.getSymbol());
    		}
    		return set;
    	}
    	
    	private ArrayList<Deal> getAllFxDealsFrom(ArrayList<Deal> from){
    		ArrayList<Deal> result = new ArrayList<Deal>();
    		for(Deal d: from){
    			if(d.isFx()){
    				result.add(d);
    			}
    		}
    		return result;
    	}

    	private ArrayList<Deal> getAllCommodityDealsFrom(ArrayList<Deal> from){
    		ArrayList<Deal> result = new ArrayList<Deal>();
    		for(Deal d: from){
    			if(d.isCommodity()){
    				result.add(d);
    			}
    		}
    		return result;
    	}
    	
    	private ArrayList<Deal> getAllIndexDealsFrom(ArrayList<Deal> from){
    		ArrayList<Deal> result = new ArrayList<Deal>();
    		for(Deal d: from){
    			if(d.isIndex()){
    				result.add(d);
    			}
    		}
    		return result;
    	}
    	
    	private ArrayList<Deal> getAllStockDealsFrom(ArrayList<Deal> from){
    		ArrayList<Deal> result = new ArrayList<Deal>();
    		for(Deal d: from){
    			if(d.isStock()){
    				result.add(d);
    			}
    		}
    		return result;
    	}
    	
    	private boolean parseTheDealRecordFile(){
    		File dealRecordFile = this.getFile(FILE_NAMES.DEAL_RECORD);
    		try {
    			// m_deals will be newed by callee
    			m_deals =	Main.this.m_htmlParser.readTheDealRecordFile(dealRecordFile);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			appendLog("ERROR: parse " + FILE_NAMES.DEAL_RECORD + " failed!");
    			return false;
    		}
    		return true;
    	}
    	
    	// check if user provider all exchange rate we need ?
    	private boolean checkExRateCompletion(){
    		HashSet<String> neededExRates = collectNeededExRates();
    		HashSet<String>	missingRates = diffRateTable(neededExRates, m_exRateTable);
    		
    		if(0 == missingRates.size()){
    			return true;
    		}else{
    			/* no matter rate file exist, create a new one, write ALL rates
    			 * GBPUSD 100
    			 * USDJPY 200
    			 *  ...
    			 * HUFUSD 0     means empty, ask user to fill it, contract: do NOT read it when reading exRate file again
    			 */
    			HashMap<String, Float> providedAndMissing = sumRates(m_exRateTable, missingRates);
    			writeRatesIntoExRateFile(providedAndMissing);
    			System.out.println("complete the Ex Rates Table, and try again!");
    			appendLog("ERROR: " + FILE_NAMES.EX_RATES + " not completed!, complete it and try again");
    			return false;
    		}
    	}
    	
    	public void appendLog(String message) {
    		m_statusArea.append(message + "\n");
    	}
    	
    	private void clearLog(){
    		m_statusArea.setText(null);
    	}
    	
    	private boolean checkClientGroupMapCompletion(){
    		m_activeClients = collectAllClientsAppearIn(m_deals);
    		Iterator<String> itor = m_activeClients.iterator();
    		HashSet<String>	clientsNoGroupInfo = new HashSet<String>();
    		// check and collect
    		while(itor.hasNext()){
    			String client = itor.next();
    			String group = m_clientGroupMap.get(client);
    			if(null == group){
    				clientsNoGroupInfo.add(client);
    			}
    		}
    		
    		// print
    		if(clientsNoGroupInfo.isEmpty()){
    			return true;
    		}else{
    			Iterator<String> clientsItor = clientsNoGroupInfo.iterator();
    			while(clientsItor.hasNext()){
    				String client = clientsItor.next();
    				System.out.println("ERROR: map client to group failed for: " + client );
    				appendLog("ERROR: Group info for client: " + client + " not found" );
    			}
    			return false;
    		}
    		
    	}
    }
    
    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        //if ("progress" == evt.getPropertyName() ) {
            if (task.isDone()) {
                Toolkit.getDefaultToolkit().beep();
                m_button_go.setText(ACTION);
                m_button_choose_files.setEnabled(true);
                m_button_clear_list.setEnabled(true);
            }
        //}

    }
    
	Main(){
		super(new BorderLayout());
        JPanel panel = new JPanel(); // use it to make a border
        panel.setLayout(new GridLayout(3, 1, 5, 5));
        add(panel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));
        
		DefaultListModel listModel = new DefaultListModel();
        m_fileList = new JList(listModel);
        panel.add(new JScrollPane(m_fileList));
        
        m_statusArea = new JTextArea(3, 20);
        m_statusArea.setMargin(new Insets(5,5,5,5));
        DefaultCaret caret = (DefaultCaret)m_statusArea.getCaret();  // auto scroll
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        m_statusArea.setEditable(false);
        panel.add(new JScrollPane(m_statusArea));
        
        this.m_buttonsContainer = new JPanel();
        this.m_buttonsContainer.setLayout(new FlowLayout());
        
		m_button_choose_files = new JButton("Load");
		m_button_choose_files.addActionListener(this);
		m_button_clear_list = new JButton("Clear");
		m_button_clear_list.addActionListener(this);
		m_button_go = new JButton("Action");
		m_button_go.addActionListener(this);
		
		m_buttonsContainer.add(m_button_choose_files);
		m_buttonsContainer.add(m_button_clear_list);
		m_buttonsContainer.add(m_button_go);
		
        panel.add(m_buttonsContainer);

		m_files			=	new ArrayList<File>();
		m_htmlParser 	=	new HtmlParser();
		m_htmlWriter	= 	new HtmlWriter();
		m_excelIO		=	new ExcelIO();
		// do NOT new deals here, return from HtmlParser
		
		m_resultTable 	= 	new ArrayList<Line>();
		m_ibParamMap	= 	new HashMap<String, IBParam>();
		m_fxGroupMap  	= 	new FxGroups();
		
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
				for(File f : files_this_time){
					if(!m_files.contains(f)){
						m_files.add(f);
						listModel.addElement(f.getAbsolutePath());
					}
				}
				
				/* debug: check the arrayList 
				System.out.println("debug start:");
				for(File f: files){
					System.out.println(f);
				}
				System.out.println("debug done");
				*/
			}
		}
		else if(e.getSource().equals(m_button_clear_list)) {
			DefaultListModel listModel = (DefaultListModel) m_fileList.getModel();
			listModel.removeAllElements();
			
			while(!m_files.isEmpty())
				m_files.remove(0);
			
			clearLog();
		}
		else if(e.getSource().equals(m_button_go)) {
			if(m_button_go.getText().equals(ACTION)){
				System.out.println("button go clicked");
				clearLog();
				this.m_button_go.setText(CANCEL);
				this.m_button_choose_files.setEnabled(false);
				this.m_button_clear_list.setEnabled(false);
				
				task = new Task();
				task.addPropertyChangeListener(this);
				task.execute();
			}else{
				// must be CANCEL
				System.out.println("button Cancel clicked");
				task.cancel(true); // must cooperate with SwingWorker's this.isCancelled()
				appendLog("Cancelled");
			}

		}
	}
	

	public void appendLog(String message) {
		m_statusArea.append(message + "\n");
	}
	
	private void clearLog(){
		m_statusArea.setText(null);
	}

	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("GKFX Commission Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new Main();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.setPreferredSize(new Dimension(400, 300));
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
	
}
