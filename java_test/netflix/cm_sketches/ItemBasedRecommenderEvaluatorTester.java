package netflix.cm_sketches;

import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;

public class ItemBasedRecommenderEvaluatorTester {

	public static void main(String[] args) throws IOException, TasteException {
		// TODO Auto-generated method stub
		ItemBasedRecommenderEvaluator evaluator = new ItemBasedRecommenderEvaluator("/Users/danmac/git/umanoidTyphoon/"	+ 
												  "Netflix-CM_Sketches/in/2016-02-22_19-25-54_mahout_netflix_dataset.csv");
		double score = evaluator.runEvaluation();
		
		System.out.println("IBR EVALUATOR :: Score = " + score);
	}

}
