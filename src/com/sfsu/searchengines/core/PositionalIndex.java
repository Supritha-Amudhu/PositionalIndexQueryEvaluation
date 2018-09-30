package com.sfsu.searchengines.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.lemurproject.kstem.KrovetzStemmer;

import com.sfsu.searchengines.model.DocumentOccurance;
import com.sfsu.searchengines.model.TermDocumentFrequency;

public class PositionalIndex 
{
	private Map<TermDocumentFrequency, List<DocumentOccurance>> positionalIndex;
	
	public PositionalIndex()
	{
		this.positionalIndex = new HashMap<>();
	}
	
	public void buildPositionalIndex(Map<Integer, String> documentsMap)
	{
		// For each document
		for(Integer key: documentsMap.keySet())
		{
			Map<TermDocumentFrequency, DocumentOccurance> termFrequency = new HashMap<>();
			String document = documentsMap.get(key);
			//Get all the words split by space
			List<String> words = Arrays.asList(document.split(" "));
			
			KrovetzStemmer stemmer = new KrovetzStemmer();
			for(int i=0; i< words.size(); i++)
			{
				String word = words.get(i);
				// don't is split as 'don' and 't'
				List<String> partialWords = Arrays.asList(word.split("\\.|\\'|\\\"|\\-|\\(|\\)|\\[|\\]|\\{|\\}|\\+|\\=|\\_|\\:|\\;|\\<|\\>|\\?|\\/"));
				
				List<String> stemmedWords = new ArrayList<>();
				// stem each partial word
				for(String partialWord: partialWords)
				{
					if (isValidWord(partialWord))
					{
						stemmedWords.add(stemmer.stem(partialWord));
					}
				}
				
				// add stemmedword to idf for the current document
				for(String stemmedWord: stemmedWords)
				{
					TermDocumentFrequency tdfKey = new TermDocumentFrequency(stemmedWord, 0);
					if(termFrequency.containsKey(tdfKey))
					{
						DocumentOccurance idf = termFrequency.get(tdfKey);
						idf.addTermOccurance(i);
					}
					else
					{
						DocumentOccurance idf = new DocumentOccurance(key);
						idf.addTermOccurance(i);
						// intialize doc frequency as 0. We will update this later
						termFrequency.put(tdfKey, idf);
					}
				}
			}
			// after processing all the words in the doc
			// Update positional Index with each term in the doc
			updatePositonalIndex(positionalIndex, termFrequency);
		}
		// Update Key with documentFrequency value
		updateDocumentFrequency(positionalIndex);
		System.out.println(positionalIndex);
	}
	
	public void writePositionalIndex(String filename)
	{
		try {
			FileWriter writer = new FileWriter(filename);
			for (TermDocumentFrequency df: this.positionalIndex.keySet())
			{
				List<DocumentOccurance> tfList = this.positionalIndex.get(df);
				StringBuffer indexEntry = new StringBuffer("[" + df.getDocumentTerm() + ":" + df.getDocumentFrequency() + "]");
				indexEntry.append("->");
				indexEntry.append(tfList);
				indexEntry.append("\n");
				writer.write(indexEntry.toString());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Integer> getDocumentsForProximityQuery(String query1, String query2, int maxDistance)
	{
		
		List<Integer> documentList = new ArrayList<>();
		List<DocumentOccurance> query1TFList = this.positionalIndex.get(new TermDocumentFrequency(query1, 0));
		List<DocumentOccurance> query2TFList = this.positionalIndex.get(new TermDocumentFrequency(query2, 0));
		Set<DocumentOccurance> intersection = getIntersection(query1TFList, query2TFList);
		Map<Integer, List<DocumentOccurance>> queryOccuranceMap = new HashMap<>();
		updateQueryOccuranceMap(queryOccuranceMap, intersection, query1TFList);
		updateQueryOccuranceMap(queryOccuranceMap, intersection, query2TFList);
		// for each document in which both query terms occur
		for (Integer documentID: queryOccuranceMap.keySet())
		{
			List<DocumentOccurance> tfList = queryOccuranceMap.get(documentID);
			// check that the size of this list is exactly 2 since we are comparing distance
			// of exactly two queries
			if (tfList.size() == 2)
			{
				DocumentOccurance query1Occurance = tfList.get(0);
				DocumentOccurance query2Occurance = tfList.get(1);
				if (isCandidateForProximity(query1Occurance, query2Occurance, maxDistance))
				{
					documentList.add(documentID);
				}
			}
		}
//		System.out.println("Doc list for " + query1 + " " + query2 + " " + documentList);
		return documentList;
	}
	
	public List<Integer> getDocumentsForRegularTerm(String regularTerm) 
	{
		List<Integer> documentList = new ArrayList<>();
		List<DocumentOccurance> documentOccuranceList = positionalIndex.get(new TermDocumentFrequency(regularTerm, 0));
		if (documentOccuranceList != null && documentOccuranceList.size() > 0)
		for(DocumentOccurance documentOccurance : documentOccuranceList)
		{
			documentList.add(documentOccurance.getDocumentID());
		}
		return documentList;
	}
	
	public int getTermFrequency(String term, int docId) 
	{
		int termFrequency = 0;
//		System.out.println("Checking term frequence for '" + term + "' in doc: '" + docId + "'");
		List<DocumentOccurance> documentOccuranceList = positionalIndex.get(new TermDocumentFrequency(term, 0));
		for (DocumentOccurance documentOccurance: documentOccuranceList)
		{
			if (documentOccurance.getDocumentID() == docId)
			{
				termFrequency = documentOccurance.getTermPositionList().size();
				break;
			}
		}
		return termFrequency;
	}

	public int getDocumentFrequency(String term) 
	{
		List<DocumentOccurance> documentOccuranceList = positionalIndex.get(new TermDocumentFrequency(term, 0));
		if (documentOccuranceList != null)
		{
			return documentOccuranceList.size();
		}
		return 0;
	}
	
	private boolean isCandidateForProximity(DocumentOccurance query1Occurance, 
			DocumentOccurance query2Occurance,
			int maxDistance) 
	{
		for (Integer occurance: query1Occurance.getTermPositionList())
		{
			int nextHigherIndex = getNextHigherIndex(query2Occurance.getTermPositionList(), occurance);
			if (nextHigherIndex != -1 && (nextHigherIndex - occurance -1) <= maxDistance)
			{
				return true;
			}
		}
		return false;
	}

	private int getNextHigherIndex(Set<Integer> termPositionList, Integer occurance) 
	{
		for(Integer position: termPositionList)
		{
			if (position >= occurance)
			{
				return position;
			}
		}
		return -1;
	}

	private void updateQueryOccuranceMap(Map<Integer, List<DocumentOccurance>> queryOccuranceMap, 
			Set<DocumentOccurance> intersection,
			List<DocumentOccurance> queryTFList)
	{
		for (DocumentOccurance tf: queryTFList)
		{
			if (intersection.contains(tf))
			{
				if (queryOccuranceMap.containsKey(tf.getDocumentID()))
				{
					queryOccuranceMap.get(tf.getDocumentID()).add(tf);
				}
				else
				{
					List<DocumentOccurance> docList = new ArrayList<>();
					docList.add(tf);
					queryOccuranceMap.put(tf.getDocumentID(), docList);
				}
			}
		}
	}
	
	private Set<DocumentOccurance> getIntersection(List<DocumentOccurance> query1TFList, List<DocumentOccurance> query2TFList)
	{
		Set<DocumentOccurance> tfSet1 = new TreeSet<>(query1TFList);
		Set<DocumentOccurance> tfSet2 = new TreeSet<>(query2TFList);
		Set<DocumentOccurance> intersection = new TreeSet<>();
		for(DocumentOccurance tf: query1TFList)
		{
			if (tfSet2.contains(tf))
			{
				intersection.add(tf);
			}
		}
		for(DocumentOccurance tf: query2TFList)
		{
			if (tfSet1.contains(tf))
			{
				intersection.add(tf);
			}
		}
		return intersection;
	}
	
	private void updateDocumentFrequency(Map<TermDocumentFrequency, List<DocumentOccurance>> positionalIndex) 
	{
		for(TermDocumentFrequency df: positionalIndex.keySet())
		{
			List<DocumentOccurance> tfList = positionalIndex.get(df);
			df.setDocumentFrequency(tfList.size());
		}
	}

	private void updatePositonalIndex(Map<TermDocumentFrequency, List<DocumentOccurance>> positionalIndex,
			Map<TermDocumentFrequency, DocumentOccurance> termFrequency) 
	{
		for(TermDocumentFrequency df: termFrequency.keySet())
		{
			DocumentOccurance tf = termFrequency.get(df);
			if (positionalIndex.containsKey(df))
			{
				positionalIndex.get(df).add(tf);
			}
			else
			{
				List<DocumentOccurance> idfList = new ArrayList<>();
				idfList.add(tf);
				positionalIndex.put(df, idfList);
			}
		}
	}

	private boolean isValidWord(String word)
	{
		return word != null && word.trim().length() > 0;
	}

}
