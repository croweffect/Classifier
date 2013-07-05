package at.rovo.classifier.naiveBayes;

import java.io.Serializable;

public class SmoothedNaiveBayes<F extends Serializable, C extends Serializable> extends NormalNaiveBayes<F,C>
{
	private double smoothingPrior = 0.0;
	
	protected SmoothedNaiveBayes(TrainingDataStorageMethod method)
	{
		super(method);
	}
	
	protected SmoothedNaiveBayes(String name)
	{
		super(name);
	}
	
	public void setSmoothingPrior(double smoothingPrior)
	{
		this.smoothingPrior = smoothingPrior;
	}
	
	public double getSmoothingPrior()
	{
		return this.smoothingPrior;
	}
	
	/**
	 * <p>Returns the conditional probability of a word given its 
	 * classification-category <em>[Pr(word|classification)]</em>.</p>
	 * <p>This method allows passing an additional argument, the <em>smoothingPrior</em>
	 * which prevents zero probabilities in further computations. A <em>smoothingPrior</em>
	 * of 0 does not affect the outcome while a <em>smoothingPrior</em> between
	 * 0 and 1 is called a Lidstone smoothing while a <em>smoothingPrior</em> of
	 * 1 is called a Laplace smoothing.</p>
	 * 
	 * @param feature Feature or word the probability should be calculated for
	 * @param category The category the feature/word have to be in
	 * @param smoothingPrior accounts for features not present in the learning 
	 *                       samples and prevents zero probabilities in further 
	 *                       computations
	 * @return The probability of the feature given its category
	 */
	@Override
	public double getConditionalProbability(F feature, C category)
	{
		// P(F|C)
		String output = null;
		if (logger.isDebugEnabled())
			output = "   P('"+feature+"'|'"+category+"') = ";
		long samplesForCategory = this.trainingData.getNumberOfSamplesForCategory(category);
		if (samplesForCategory == 0)
			return 0.;
		// The total number of times this feature appeared in this
		// category divided by the total number of items in this category
		// application of the multinomial event model
		int featCount = this.trainingData.getFeatureCount(feature, category);
		double result = ((double)featCount+this.smoothingPrior)/(samplesForCategory+this.smoothingPrior*this.trainingData.getTotalNumberOfFeatures());
		if (logger.isDebugEnabled())
			logger.debug(output+featCount+"/"+samplesForCategory+" = "+result);
		return result;
	}
	
	/**
	 * <p>Returns the conditional probability for words given their 
	 * classification-category <em>[Pr(word1,word2|classification)]</em>.</p>
	 * <p>This method allows passing an additional argument, the <em>smoothingPrior</em>
	 * which prevents zero probabilities in further computations. A <em>smoothingPrior</em>
	 * of 0 does not affect the outcome while a <em>smoothingPrior</em> between
	 * 0 and 1 is called a Lidstone smoothing while a <em>smoothingPrior</em> of
	 * 1 is called a Laplace smoothing.</p>
	 * 
	 * @param features Features or words the probability should be calculated for
	 * @param category The category the features/words have to be in
	 * @param smoothingPrior accounts for features not present in the learning 
	 *                       samples and prevents zero probabilities in further 
	 *                       computations
	 * @return The probability of the features given their category
	 */
	@Override
	public double getConditionalProbability(F[] features, C category)
	{
		// http://en.wikipedia.org/wiki/Naive_Bayes_classifier
		// P(F1,F2|C) = P(F1|C)*P(F2|C,F1) but as F1 and F2 are statistically 
		//                                 independent (naive assumption)
		//              P(F1|C)*P(F2|C)
		String output = null;
		if (logger.isDebugEnabled())
		{
			output = "   P(";
			for (F feature : features)
				output += "'"+feature+"',";
			output = output.substring(0, output.length()-1);
			output += "|'"+category+"') = ";
		}
		double prob = 1;
		for (F feature : features)
			prob *= this.getConditionalProbability(feature, category);
		if (logger.isDebugEnabled())
			logger.debug(output+prob);
		return prob;
	}
}