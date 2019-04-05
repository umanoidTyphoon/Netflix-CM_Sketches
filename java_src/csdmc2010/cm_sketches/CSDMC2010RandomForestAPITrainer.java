package csdmc2010.cm_sketches;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class CSDMC2010RandomForestAPITrainer {
	private int numSamples           = 0;
	private int testNumSamples		 = 0;
	
	private String attributeFilename = null;
	private String testAttributeFile = null;
	private String trainingSetDir    = null;
	private String trainingSetFile   = null;
	
	public CSDMC2010RandomForestAPITrainer(String trainingSetDir, String trainingSetFile, String attributeFilename, String testAttributeFilename, int numSamples, int testNumSamples){
		this.trainingSetDir    = trainingSetDir;
		this.trainingSetFile   = trainingSetFile;
		this.attributeFilename = attributeFilename;
		this.testAttributeFile = testAttributeFilename;
		this.numSamples		   = numSamples;
		this.testNumSamples    = testNumSamples;
	}
	
	public void writeARFFfile(String arff_out_file){
		int[] md5_array	   		   = (IntStream.range(0, numSamples)).toArray();

		BufferedWriter writer      = null;
		List<Attribute> attributes = new ArrayList<Attribute>();
		List<Integer> labels		   = new ArrayList<Integer>();
		List<Integer> md5_list	   = new ArrayList<Integer>();
		
		for (int md5 : md5_array){
			 md5_list.add(md5);
		}

		try{
			BufferedReader br = new BufferedReader(new FileReader(this.attributeFilename)); 
		    for(String line; (line = br.readLine()) != null; ) {
		        Attribute attribute = new Attribute(line);
		        attributes.add(attribute);
		    }
		    br.close();
		    br = new BufferedReader(new FileReader(this.attributeFilename.replace("training_set_API","training_set_labels")));
		    for(String line; (line = br.readLine()) != null; ) {
		        Integer label = Integer.parseInt(line);
		        labels.add(label);
		    }
		    br.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Attribute is_malicious = new Attribute("malicious?");
//		attributes.add(0, is_malicious);
		
		System.out.println("CLASSIFIER EVALUATOR :: Computing API count map...");
		
		Map<Integer, Map<String,Integer>> api_count_map = this.get_api_count(md5_list, trainingSetFile, attributes);
		
		System.out.println("CLASSIFIER EVALUATOR :: ComputedIntegrated W API count map...");
		
		FastVector is_malicious_fast_vector = new FastVector(2);
		is_malicious_fast_vector.addElement("malicious");
		is_malicious_fast_vector.addElement("benign");
		Attribute is_malicious = new Attribute("class", is_malicious_fast_vector);
		attributes.add(is_malicious);
		
		FastVector wekaAttributes = new FastVector(attributes.size());
		for (Attribute attribute : attributes) {
			 wekaAttributes.addElement(attribute);
		}
		// System.out.println(api_count_map);
		// System.out.println(attributes);
		// System.out.println(labels);
		
		int wekaAttributesSize = wekaAttributes.size();
		Instances trainingSet  = new Instances("API logs", wekaAttributes, 1);
		// If the class index of the set is less than the number of attributes (class excluded) an exception is thrown.
		trainingSet.setClassIndex(wekaAttributesSize - 1);
		
		for (Integer md5 : md5_list) {
			 Instance trainingSetInstance = new Instance(wekaAttributesSize);
			 // The last attribute is the 'class' attribute
			 for (int i=0; i < wekaAttributes.size() - 1; i++) {
				  trainingSetInstance.setValue((Attribute) wekaAttributes.elementAt(i), api_count_map.get(md5).get(attributes.get(i).name()));
			 }
			 String label = (labels.get(md5) == 0) ? "benign" : "malicious";
			 trainingSetInstance.setValue((Attribute) wekaAttributes.elementAt(wekaAttributesSize - 1), label);
			 trainingSet.add(trainingSetInstance);
		}

		try {
			writer = new BufferedWriter(new FileWriter(arff_out_file));
			writer.write(trainingSet.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("CLASSIFIER EVALUATOR :: Written  ARFF file on " + arff_out_file + "...");
	}
	
	public void writeTestARFFfile(String test_aff_file) {
		int[] md5_array	   		   = (IntStream.range(0, this.testNumSamples)).toArray();

		BufferedWriter writer     	    = null;
		List<Attribute> attributes      = new ArrayList<Attribute>();
		List<Attribute> trainAttributes = new ArrayList<Attribute>();
		List<Integer> md5_list	   	    = new ArrayList<Integer>();
		String testSetFile		  	    = this.trainingSetDir + "/CSDMC2010_API/CSDMC_API_TestData.csv";
				
		for (int md5 : md5_array){
			 md5_list.add(md5);
		}
		// NOTE: The attributes extracted from the test set, used in the evaluation, have to be the same of the training set.
		try{
			BufferedReader br = new BufferedReader(new FileReader(this.testAttributeFile)); 
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
		Map<Integer, Map<String,Integer>> test_api_count_map  = this.get_api_count(md5_list, testSetFile, attributes);
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(this.attributeFilename)); 
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
		// System.out.println(trainAttributes);
		// Map<Integer, Map<String,Integer>> train_api_count_map = this.get_api_count(md5_list, this.trainingSetFile, attributes);
		
		Map<Integer, Map<String,Integer>> training_set_compliant_test_api_count_map = this.get_evaluation_api_count(test_api_count_map, trainAttributes);
		// System.out.println(training_set_compliant_test_api_count_map);
		
		FastVector is_malicious_fast_vector = new FastVector(1);
		// is_malicious_fast_vector.addElement("?");
		is_malicious_fast_vector.addElement("malicious");
		is_malicious_fast_vector.addElement("benign");
		Attribute is_malicious = new Attribute("class", is_malicious_fast_vector);
		// NOTE: The attributes extracted from the test set, used in the evaluation, have to be the same of the training set.
		trainAttributes.add(is_malicious);
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
			 testSetInstance.setValue((Attribute) wekaAttributes.elementAt(wekaAttributesSize - 1), "malicious");
			 testSet.add(testSetInstance);
		}

		try {
			writer = new BufferedWriter(new FileWriter(test_aff_file));
			writer.write(testSet.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public RandomForest trainRandomForestFromARFFFile(String ARFFFile) {
		RandomForest forest = null;
		try {
			DataSource arffFile   = new DataSource(ARFFFile);
			Instances trainingSet = arffFile.getDataSet();
			// setting class attribute if the data format does not provide this information
			// For example, the XRFF format saves the class attribute information as well
			if (trainingSet.classIndex() == -1)
			    trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
			
//			Classifier model = (Classifier) new NaiveBayes();
//			model.buildClassifier(trainingSetInstances); 
			
			forest = new RandomForest();
			forest.setNumTrees(100);
			
			forest.buildClassifier(trainingSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return forest;
	}
	
	public String serializeRandomForestClassifier(RandomForest forest, String outputStreamFile){
		try {
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(outputStreamFile));
			stream.writeObject(forest);
			stream.flush();
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputStreamFile;
	}
	
	@SuppressWarnings("unused")
	private void evaluateClassifier_OLD_VERSION(String out_arff_file, String test_arff_file, String out_stream_file) {
		Instances trainingSet = null;
		Instances testSet	  = null;
		RandomForest forest   = null;
		try {
			DataSource trainARFFfile = new DataSource(out_arff_file);
			DataSource testARFFfile  = new DataSource(test_arff_file);

			forest      = (RandomForest) weka.core.SerializationHelper.read(out_stream_file);
			trainingSet = trainARFFfile.getDataSet();
			testSet     = testARFFfile.getDataSet();
			// setting class attribute if the data format does not provide this information
			// For example, the XRFF format saves the class attribute information as well
			if (trainingSet.classIndex() == -1)
			    trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
			if (testSet.classIndex() == -1)
			    testSet.setClassIndex(testSet.numAttributes() - 1);
			
			Evaluation evaluator = new Evaluation(trainingSet);
			evaluator.evaluateModel(forest, testSet);
			System.out.println(evaluator.toSummaryString("\nResults\n======\n", false));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void evaluateClassifier(String out_arff_file, String out_stream_file) {
		DataSource dataARFFfile = null;
		RandomForest forest    = null;
		Instances dataset      = null;
		try {
			 System.out.println("CLASSIFIER EVALUATOR :: Running evaluation on " + out_stream_file + " model on arff file " + out_arff_file + "...");
			
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
		this.kFoldCrossValidation(dataset, forest, 10);
	}
	
	private void kFoldCrossValidation(Instances data, Classifier classifier, int k) {
		int  runs				   = 1;
		long seed    			   = Double.doubleToLongBits(Math.random());
		Evaluation evaluator		   = null;
		Instances randomizedData	   = null;
		Random randNumberGenerator = null;
				
		for (int i = 0; i < runs; i++) {		
			 randNumberGenerator = new Random(seed);
			 randomizedData      = new Instances(data);
			 randomizedData.randomize(randNumberGenerator);
			 randomizedData.stratify(k);
		
			for (int j = 0; j < k; j++) {
				 Instances trainingSet = randomizedData.trainCV(k, j);
				 Instances testSet	   = randomizedData.testCV(k, j);
				 try {
					 evaluator = new Evaluation(trainingSet);
					 evaluator.evaluateModel(classifier, testSet);
					 System.out.println(evaluator.toSummaryString("\nResults\n======\n", true));
//					 System.out.println(evaluator.toClassDetailsString());
//				     System.out.println("=== Results For Class -1- ===");
//				     System.out.println("Precision=  " + evaluator.precision(0));
//				     System.out.println("Recall=  " + evaluator.recall(0));
//				     System.out.println("F-measure=  " + evaluator.fMeasure(0));
//				     System.out.println("=== Results For Class -2- ===");
//				     System.out.println("Precision=  " + evaluator.precision(1));
//				     System.out.println("Recall=  " + evaluator.recall(1));
//				     System.out.println("F-measure=  " + evaluator.fMeasure(1));
				} catch (Exception e) {
					 // TODO Auto-generated catch block
					 e.printStackTrace();
				 }
			}
		}
		
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
			Set<String> APIs				 = api_map.keySet();
			for (String API : APIs) {
				 if (API_attributes.contains(API)) {
					 training_set_compliant_test_api_count_map.put(md5, api_map);
				 }
			}
		}
		for (Integer md5 : test_map_keys) {
			Map<String, Integer> api_map = training_set_compliant_test_api_count_map.get(md5);
			Set<String> APIs				 = new HashSet<String>(api_map.keySet());
			for (String API : API_attributes) {
				 if (!APIs.contains(API)) {
					 training_set_compliant_test_api_count_map.get(md5).put(API, 0);
				 }
			}
		}
		return training_set_compliant_test_api_count_map;
	}
	
}
