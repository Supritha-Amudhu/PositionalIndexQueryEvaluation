package com.sfsu.searchengines.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FreeTextQueryEvaluation 
{

	public Map<Integer, Double> getDocumentRanking(Set<String> searchTerms, 
			Set<Integer> documentSet,
			PositionalIndex positionalIndex) 
	{
		System.out.println(searchTerms);
		System.out.println(documentSet);
		Map<Integer, Double> documentRanking = new HashMap<>();
		for (Integer docId: documentSet)
		{
			double docScore = 0;
			for (String term: searchTerms)
			{
				int tf = positionalIndex.getTermFrequency(term, docId);
				double df = positionalIndex.getDocumentFrequency(term);
				if (tf > 0)
				{
					double tfLog = Math.log(tf)/Math.log(2);
					double dfLog = Math.log(10/df)/Math.log(2);
					double termWeight = (1 + tfLog) * (dfLog);
					docScore += termWeight;
				}
			}
			documentRanking.put(docId, docScore);
		}
		return documentRanking;
	}
}
