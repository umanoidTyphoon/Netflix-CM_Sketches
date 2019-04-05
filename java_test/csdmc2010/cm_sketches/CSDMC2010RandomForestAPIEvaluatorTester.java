package csdmc2010.cm_sketches;

public class CSDMC2010RandomForestAPIEvaluatorTester {
	
/****************************************************************** CODE VERSION RUNNABLE DIRECTLY FROM THE PYTHON SCRIPT ******************************************************************/		
	public static void main(String[] args) {
		
		String trainingSetDir  		  = args[0];
		String trainAttributeFilename = args[1];
		String testAttributeFilename  = args[2];
		String out_arff_file          = args[3];
		String out_stream_file        = args[4];
		String trainingSetFile 		  = args[5];
		
		int numSamples				  = Integer.parseInt(args[6]);
		int testNumSamples			  = -1;
		
		CSDMC2010RandomForestAPITrainer trainer = new CSDMC2010RandomForestAPITrainer(trainingSetDir, trainingSetFile, trainAttributeFilename, testAttributeFilename, numSamples, testNumSamples);
		trainer.writeARFFfile(out_arff_file);
		
		trainer.evaluateClassifier(out_arff_file, out_stream_file);
	}
/**************************************************************** END CODE VERSION RUNNABLE DIRECTLY FROM THE PYTHON SCRIPT ****************************************************************/
}
