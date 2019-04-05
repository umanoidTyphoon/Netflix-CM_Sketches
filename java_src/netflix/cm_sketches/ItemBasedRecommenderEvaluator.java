package netflix.cm_sketches;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class ItemBasedRecommenderEvaluator {
	
	private DataModel dataModel = null;
	
	public ItemBasedRecommenderEvaluator(String trainingSetPath) throws IOException {
		this.dataModel = new FileDataModel(new File(trainingSetPath));
	}
	
	public double runEvaluation() throws TasteException {
		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		
		RecommenderBuilder builder = new RecommenderBuilder() {
									 	@Override
									 	public Recommender buildRecommender(DataModel model) throws TasteException {
									 		ItemSimilarity similarity = new LogLikelihoodSimilarity(model);
									 		return new GenericItemBasedRecommender(model, similarity);
									 	}
									 };
		double score = evaluator.evaluate(builder, null, this.dataModel, 0.7, 1.0);
	
		return score;
	}

}
