package com.sfsu.searchengines.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sfsu.searchengines.model.DocumentRank;

public class FreeTextQueryEvaluation 
{

	public List<DocumentRank> getDocumentRanking(Set<String> searchTerms, 
			Set<Integer> documentSet,
			PositionalIndex positionalIndex) 
	{
		System.out.println(searchTerms);
		System.out.println(documentSet);
		List<DocumentRank> documentRanking = new ArrayList<>();
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
			documentRanking.add(new DocumentRank(docId, docScore));
		}
		return documentRanking;
	}
}
