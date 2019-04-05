package csdmc2010.cm_sketches;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class CSDMC2010RandomForestAPIClassifier {
	int testNumSamples = -1;
	
	String ARFFfileAbsolutePath			 = null;
	String randomForestModelAbsolutePath = null;
	String testSetAbsolutePath 			 = null;
	String testSetAttributeAbsolutePath	 = null;
	String trainSetAttributeAbsolutePath = null;
	
	public CSDMC2010RandomForestAPIClassifier(String testSetAbsolutePath, String randomForestModelAbsolutePath, String testSetAttributeAbsolutePath,
											  String trainSetAttributeAbsolutePath, String ARFFfileAbsolutePath, int testNumSamples) {
		this.ARFFfileAbsolutePath          = ARFFfileAbsolutePath;
		this.randomForestModelAbsolutePath = randomForestModelAbsolutePath;
		this.testNumSamples				   = testNumSamples;
		this.testSetAbsolutePath 		   = testSetAbsolutePath;
		this.testSetAttributeAbsolutePath  = testSetAttributeAbsolutePath;
		this.trainSetAttributeAbsolutePath = trainSetAttributeAbsolutePath;
	}	

	public void writeTestARFFfile() {
		int[] md5_array	   		   = (IntStream.range(0, this.testNumSamples)).toArray();

		BufferedWriter writer     	    = null;
		List<Attribute> attributes      = new ArrayList<Attribute>();
		List<Attribute> trainAttributes = new ArrayList<Attribute>();
		List<Attribute> testAttributes  = new ArrayList<Attribute>();
		List<Integer> md5_list	   	    = new ArrayList<Integer>();
				
		for (int md5 : md5_array){
			 md5_list.add(md5);
		}
		// NOTE: The attributes extracted from the test set, used in the evaluation, have to be the same of the training set.
		try{
			BufferedReader br = new BufferedReader(new FileReader(this.testSetAttributeAbsolutePath)); 
		    for(String line; (line = br.readLine()) != null; ) {
		        Attribute attribute = new Attribute(line);
		        attributes.add(attribute);
		    }
		    br.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<Integer, Map<String,Integer>> test_api_count_map  = this.get_api_count(md5_list, this.testSetAbsolutePath, attributes);
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(this.trainSetAttributeAbsolutePath)); 
		    for(String line; (line = br.readLine()) != null; ) {
		        Attribute attribute = new Attribute(line);
		        trainAttributes.add(attribute);
		    }
		    br.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<Integer, Map<String,Integer>> training_set_compliant_test_api_count_map = this.get_evaluation_api_count(test_api_count_map, trainAttributes);
		
		/*FastVector is_malicious_fast_vector = new FastVector(1);
		is_malicious_fast_vector.addElement("unknown");
		Attribute is_malicious = new Attribute("class", is_malicious_fast_vector);*/
		FastVector is_malicious_fast_vector = new FastVector(2);
		is_malicious_fast_vector.addElement("benign");
		is_malicious_fast_vector.addElement("malicious");
		Attribute is_malicious = new Attribute("class", is_malicious_fast_vector);
		
		// NOTE: The attributes extracted from the test set, used in the evaluation, have to be the same of the training set.
		testAttributes.add(is_malicious);
		//attributes.add(is_malicious);
		
		// NOTE: The attributes extracted from the test set, used in the evaluation, have to be the same of the training set.
//		FastVector wekaAttributes = new FastVector(attributes.size());
//		for (Attribute attribute : attributes) {
//			 wekaAttributes.addElement(attribute);
//		}
		FastVector wekaAttributes = new FastVector(trainAttributes.size());
		for (Attribute attribute : trainAttributes) {
			 wekaAttributes.addElement(attribute);
		}
		wekaAttributes.addElement(is_malicious);

		// System.out.println(api_count_map);
		// System.out.println(attributes);
		
		int wekaAttributesSize = wekaAttributes.size();
		Instances testSet  = new Instances("TEST API logs", wekaAttributes, 1);
		// If the class index of the set is less than the number of attributes (class excluded) an exception is thrown.
		testSet.setClassIndex(wekaAttributesSize - 1);
		
		for (Integer md5 : md5_list) {
			 Instance testSetInstance = new Instance(wekaAttributesSize);
			 // The last attribute is the 'class' attribute
			 for (int i=0; i < wekaAttributes.size() - 1; i++) {
				  // NOTE: The attributes extracted from the test set, used in the evaluation, have to be the same of the training set.
				  // testSetInstance.setValue((Attribute) wekaAttributes.elementAt(i), training_set_compliant_test_api_count_map.get(md5).get(attributes.get(i).name()));
				  // System.out.println(wekaAttributes.elementAt(i));
				  // System.out.println(training_set_compliant_test_api_count_map.get(md5).get(trainAttributes.get(i).name()));
				  testSetInstance.setValue((Attribute) wekaAttributes.elementAt(i), training_set_compliant_test_api_count_map.get(md5).get(trainAttributes.get(i).name()));
			 }
			 testSetInstance.setValue((Attribute) wekaAttributes.elementAt(wekaAttributesSize - 1), "benign");
			 testSet.add(testSetInstance);
		}

		try {
			writer = new BufferedWriter(new FileWriter(this.ARFFfileAbsolutePath));
			writer.write(testSet.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public String classify() {
		 Instances unlabeled 		  = null;
		 RandomForest forest 		  = null;
		 String out_labeled_ARFF_file = null;
		try {
			 unlabeled = new Instances(new BufferedReader(new FileReader(this.ARFFfileAbsolutePath)));
			 forest    = (RandomForest) weka.core.SerializationHelper.read(this.randomForestModelAbsolutePath);;
			 // set class attribute
			 unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
			 // create copy
			 Instances labeled = new Instances(unlabeled);
			 // label instances
			 for (int i = 0; i < unlabeled.numInstances(); i++) {
				  double clsLabel = forest.classifyInstance(unlabeled.instance(i));
				  labeled.instance(i).setClassValue(clsLabel);
			 }
			 String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
			 // save labeled data
			 out_labeled_ARFF_file = this.ARFFfileAbsolutePath.split("test.arff")[0] + "labeled_test" + timestamp +".arff";
			 BufferedWriter writer = new BufferedWriter(new FileWriter(out_labeled_ARFF_file));
			 writer.write(labeled.toString());
			 writer.newLine();
			 writer.flush();
			 writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return out_labeled_ARFF_file;
	}
	
	public void evaluateClassifier(String out_arff_file, String out_stream_file) {
		DataSource dataARFFfile = null;
		RandomForest forest    = null;
		Instances dataset      = null;
		try {
			 System.out.println("CLASSIFIER EVALUATOR :: Running evaluation on " + out_stream_file + "model on arff file " + out_arff_file + "...");
			
			 dataARFFfile = new DataSource(out_arff_file);
			 forest       = (RandomForest) weka.core.SerializationHelper.read(out_stream_file);
			 dataset      = dataARFFfile.getDataSet();
			 
			 // setting class attribute if the data format does not provide this information
			 // For example, the XRFF format saves the class attribute information as well
			 if (dataset.classIndex() == -1)
			     dataset.setClassIndex(dataset.numAttributes() - 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// this.kFoldCrossValidation(dataset, forest, 10);
	}
	
	private Map<Integer, Map<String,Integer>> get_api_count(List<Integer> md5_list, String logs, List<Attribute> attributes){
		List<String> log_rows 						    = new ArrayList<String>(); 
		Map<Integer, Map<String,Integer>> api_count_map = new HashMap<Integer, Map<String,Integer>>();
		for (Integer md5 : md5_list) {
			 api_count_map.put(md5, new HashMap<String,Integer>());
			 for (Attribute attribute : attributes) {
				  String API 		      = attribute.name();
				  Map<String,Integer> map = api_count_map.get(md5);
				  map.put(API, 0);
			 }
		}
		try{
			BufferedReader br = new BufferedReader(new FileReader(logs)); 
		    for(String line; (line = br.readLine()) != null; ) {
		        log_rows.add(line);
		    }
		    br.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Integer md5 : md5_list) {
			 List<String> log = Arrays.asList(log_rows.get(md5).split(",")[1].split(" "));
			 for (String API : log) {
				  int api_count 			  = api_count_map.get(md5).get(API);
				  api_count              += 1; 
				  Map<String,Integer> map = api_count_map.get(md5);
				  map.put(API, api_count);
			 }
		}
		return api_count_map;
	}

	private Map<Integer, Map<String, Integer>> get_evaluation_api_count(Map<Integer, Map<String, Integer>> test_api_count_map,
																		List<Attribute> attributes) {
		Map<Integer, Map<String, Integer>> training_set_compliant_test_api_count_map = new HashMap<Integer, Map<String, Integer>>();
		
		Set<String> API_attributes = new HashSet<String>();
		for (Attribute attribute : attributes) {
			 API_attributes.add(attribute.name());
		}
//		System.out.println(attributes);
//		System.out.println(API_attributes);

		Set<Integer> test_map_keys = test_api_count_map.keySet();
		for (Integer md5 : test_map_keys) {
			Map<String, Integer> api_map = test_api_count_map.get(md5);
			Set<String> APIs			 = api_map.keySet();
			for (String API : APIs) {
				 if (API_attributes.contains(API)) {
					 training_set_compliant_test_api_count_map.put(md5, api_map);
				 }
			}
		}
		for (Integer md5 : test_map_keys) {
			Map<String, Integer> api_map = training_set_compliant_test_api_count_map.get(md5);
			Set<String> APIs			 = new HashSet<String>(api_map.keySet());
			for (String API : API_attributes) {
				 if (!APIs.contains(API)) {
					 training_set_compliant_test_api_count_map.get(md5).put(API, 0);
				 }
			}
		}
		return training_set_compliant_test_api_count_map;
	}

	public static void getSummary(String classifiedARFFfile, String groundTruthARFFfile) {
		int differentLabels = 0; 
		int falsePositives  = 0;
		int falseNegatives  = 0;
		int sameLabels 		= 0;
		
		try {
			BufferedReader clsBr = new BufferedReader(new FileReader(classifiedARFFfile));
			BufferedReader gtBr  = new BufferedReader(new FileReader(groundTruthARFFfile));
			
			for(String clsLine, gtLine; (clsLine = clsBr.readLine()) != null && (gtLine = gtBr.readLine()) != null;) {
		    	if (clsLine.startsWith("@") && gtLine.startsWith("@") || 
		    		clsLine.equals("")      && gtLine.equals("")) {
		    		continue;
		    	}
		    	// System.out.println(clsLine);
		    	// System.out.println(gtLine);
		    	
		    	System.out.println();
		    	
		    	String[] clsLineSplits = clsLine.split(",");
		    	String[] gtLineSplits  = gtLine.split(",");
		    	
		    	String clsLabel = clsLineSplits[clsLineSplits.length - 1];
				String gtLabel  = gtLineSplits[gtLineSplits.length - 1];
				
				if (clsLabel.equals(gtLabel)) {
					sameLabels++;
				}
				else {
					if (gtLabel.equals("benign") && clsLabel.equals("malicious")) {
						falsePositives++;
					}
					if (gtLabel.equals("malicious") && clsLabel.equals("benign")) {
						falseNegatives++;
						System.out.println(clsLine);
				    	System.out.println(gtLine);
					}
					differentLabels++;
				}
			}
			clsBr.close();
			gtBr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("CLASSIFIER :: Number of identically classified samples - " + sameLabels);
		System.out.println("CLASSIFIER :: Number of differently classified samples - " + differentLabels);
		System.out.println("CLASSIFIER :: FP - " + falsePositives);
		System.out.println("CLASSIFIER :: FN - " + falseNegatives);
	}
	
}
