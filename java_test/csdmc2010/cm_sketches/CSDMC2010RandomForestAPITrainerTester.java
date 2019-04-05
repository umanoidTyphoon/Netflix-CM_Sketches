package csdmc2010.cm_sketches;

import weka.classifiers.trees.RandomForest;


public class CSDMC2010RandomForestAPITrainerTester {

	public static void main(String[] args) {
		int numSamples = 195;
// 		numSamples = 98;
// 		numSamples = 65;
//		numSamples = 49;
//		numSamples = 39;
//		numSamples = 388;
		int testNumSamples = 378;
/*********************************************************************** CODE VERSION RUNNABLE DIRECTLY FROM THIS MAIN ***********************************************************************		 
		String trainingSetDir  		  = args[0];
		String trainAttributeFilename = args[1];
		String testAttributeFilename	  = args[2];
//		String out_arff_file          = "./out/" + numSamples + "_train.arff";
		String out_arff_file			  = "./out/train.arff";
//		String out_stream_file        = "./out/" + numSamples + "_rf.p";
		String out_stream_file		  = "./out/rf.p";
//		String test_arff_file		  = "./out/test.arff";
		String reducedTrainingSetDir  = trainingSetDir + "/out/" + numSamples + "_CSDMC2010_API_Train"; 

		String trainingSetFile 		 = reducedTrainingSetDir + "/" + numSamples + "_CSDMC_API_Train_2016-06-07_19-32-36.csv";
//		trainingSetFile 		 		 = reducedTrainingSetDir + "/" + numSamples + "_CSDMC_API_Train_2016-06-07_19-37-43.csv";
//		trainingSetFile 		 		 = reducedTrainingSetDir + "/" + numSamples + "_CSDMC_API_Train_2016-06-07_19-41-41.csv";
//		trainingSetFile 		 		 = reducedTrainingSetDir + "/" + numSamples + "_CSDMC_API_Train_2016-06-07_19-42-20.csv";
//		trainingSetFile 		 		 = reducedTrainingSetDir + "/" + numSamples + "_CSDMC_API_Train_2016-06-07_19-48-25.csv";
//		trainingSetFile 				 = trainingSetDir + "/CSDMC2010_API/CSDMC_API_Train.csv";
		
		CSDMC2010RandomForestAPITrainer trainer = new CSDMC2010RandomForestAPITrainer(trainingSetDir, trainingSetFile, trainAttributeFilename, testAttributeFilename, numSamples, testNumSamples);
		// trainer.writeARFFfile(out_arff_file);
		// trainer.writeTestARFFfile(test_arff_file);
		// RandomForest trainedForest = trainer.trainRandomForestFromARFFFile(out_arff_file);
		// String serializedRFObject  = trainer.serializeRandomForestClassifier(trainedForest, out_stream_file);
		
		trainer.evaluateClassifier(out_arff_file, out_stream_file);
******************************************************************** END CODE VERSION RUNNABLE DIRECTLY FROM THIS MAIN *********************************************************************/
		
/****************************************************************** CODE VERSION RUNNABLE DIRECTLY FROM THE PYTHON SCRIPT ******************************************************************/		
		
		String trainingSetDir  		  = args[0];
		String trainAttributeFilename = args[1];
		String testAttributeFilename	  = args[2];
		String out_arff_file          = args[3];
		String out_stream_file        = args[4];
		String trainingSetFile 		  = args[5];
		
		numSamples					  = Integer.parseInt(args[6]);
		
		CSDMC2010RandomForestAPITrainer trainer = new CSDMC2010RandomForestAPITrainer(trainingSetDir, trainingSetFile, trainAttributeFilename, testAttributeFilename, numSamples, testNumSamples);
		trainer.writeARFFfile(out_arff_file);
		RandomForest trainedForest = trainer.trainRandomForestFromARFFFile(out_arff_file);
		String serializedRFObject  = trainer.serializeRandomForestClassifier(trainedForest, out_stream_file);
		
		trainer.evaluateClassifier(out_arff_file, out_stream_file);
/**************************************************************** END CODE VERSION RUNNABLE DIRECTLY FROM THE PYTHON SCRIPT ****************************************************************/
		
	}
}
