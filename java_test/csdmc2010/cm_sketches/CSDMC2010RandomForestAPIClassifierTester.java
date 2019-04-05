package csdmc2010.cm_sketches;

public class CSDMC2010RandomForestAPIClassifierTester {
	
	public static void main(String[] args) {
		int testNumSamples  					 = 378;
		
		String ARFFfileAbsolutePath          = args[0];
		String randomForestModelAbsolutePath = args[1];
		String testSetAbsolutePath 			 = args[2];
		String testSetAttributeAbsolutePath  = args[3];
		String trainSetAttributeAbsolutePath = args[4];
		String groundTruthARFFfile			 = args[5];
				
		CSDMC2010RandomForestAPIClassifier classifier = new CSDMC2010RandomForestAPIClassifier(testSetAbsolutePath, randomForestModelAbsolutePath, testSetAttributeAbsolutePath, 
																							   trainSetAttributeAbsolutePath, ARFFfileAbsolutePath, testNumSamples);
		classifier.writeTestARFFfile();
		String classifiedARFFfile  = classifier.classify();
		
		CSDMC2010RandomForestAPIClassifier.getSummary(classifiedARFFfile, groundTruthARFFfile);
	}
}
