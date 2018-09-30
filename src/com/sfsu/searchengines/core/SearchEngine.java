/**
 * 
 */
package com.sfsu.searchengines.core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lemurproject.kstem.KrovetzStemmer;

/**
 * @author supritha This class runs the search engine program.
 */
public class SearchEngine {
	
	private PositionalIndex positionalIndex;
	
	public SearchEngine()
	{
		positionalIndex = new PositionalIndex();
		Map<Integer, String> documentsMap = parseSearchDocument("documents.txt");
		positionalIndex.buildPositionalIndex(documentsMap);
		positionalIndex.writePositionalIndex("positionalIndex.txt");
	}

	/**
	 * The main driver program for the search engine.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		SearchEngine searchEngine = new SearchEngine();
		Map<Integer, String> queriesMap = searchEngine.parseQueries(searchEngine.parseSearchQueries());

		FileWriter writer = new FileWriter("searchResults.txt");
		for (Integer inputId : queriesMap.keySet()) 
		{
			String query = queriesMap.get(inputId);
			List<String> proximityTerms = searchEngine.getProximityTerms(query);
			List<String> normalTerms = searchEngine.getRegularTerms(query);
			
			System.out.println("Proximity Terms: " + proximityTerms);
			System.out.println("Regular Terms; " + normalTerms);
			
			writer.write("Query: " + query + "\n");
			writer.write("Document Ranking: " + searchEngine.getDocumentRankingMap(proximityTerms, normalTerms));
			writer.write("\n");
		}
		writer.close();
	}
	
	public Map<Integer, Double> getDocumentRankingMap(List<String> proximityTerms, 
			List<String> regularTerms) 
	{
		Set<Integer> documentSet = new HashSet<>();
		Set<String> searchTerms = new LinkedHashSet<>();
		KrovetzStemmer stemmer = new KrovetzStemmer();
		for(String proximityTerm: proximityTerms)
		{
			String[] splitString = proximityTerm.split("\\(");
			int maxDistance = Integer.parseInt(splitString[0]);
			String[] terms = splitString[1].replaceAll("\\)", "").split(" ");
			documentSet.addAll(positionalIndex.getDocumentsForProximityQuery(terms[0], terms[1], maxDistance));	
			searchTerms.add(stemmer.stem(terms[0]));
			searchTerms.add(stemmer.stem(terms[1]));
		}
		for (String regularTerm: regularTerms)
		{
			documentSet.addAll(positionalIndex.getDocumentsForRegularTerm(regularTerm));
			searchTerms.add(stemmer.stem(regularTerm));
		}
		
		Map<Integer, Double> documentRankingMap = new HashMap<>();
		FreeTextQueryEvaluation freeTextQueryEvaluation = new FreeTextQueryEvaluation();
		documentRankingMap = freeTextQueryEvaluation.getDocumentRanking(searchTerms, documentSet, positionalIndex);
		System.out.println(documentRankingMap);
		return documentRankingMap;
	}

	
	public List<String> getProximityTerms(String query)
	{
		Pattern proximityPattern = Pattern.compile("([0-9]*?)\\((.*?)\\)");
		Matcher proximityMatcher = proximityPattern.matcher(query);
		List<String> proximityTerms = new ArrayList<>();
		while(proximityMatcher.find())
		{
			proximityTerms.add(proximityMatcher.group(0));
		}
		return proximityTerms;
	}
	
	public List<String> getRegularTerms(String query)
	{
		List<String> normalTerms = new ArrayList<>();
		for(String normalTerm: query.split("([0-9]*?)\\((.*?)\\)"))
		{
			if (normalTerm.trim().length() > 0)
			{
				normalTerms.addAll(Arrays.asList(normalTerm.trim().split(" ")));
			}
		}
		return normalTerms;
	}

//	/**
//	 * This method takes the list of document numbers and prints the list as
//	 * [DOC1, DOC2, DOC3]
//	 * 
//	 * @param docList
//	 * @return string formatted list of document names.
//	 */
//	private String stringifyDocList(List<Integer> docList) {
//		if (docList == null || docList.size() == 0)
//		{
//			return "[]";
//		}
//		if (docList.size() == 1) {
//			return "[DOC" + docList.get(0) + "]";
//		}
//
//		StringBuffer result = new StringBuffer("[");
//		for (int i = 0; i < docList.size() - 1; i++) {
//			result.append("DOC" + docList.get(i) + ", ");
//		}
//		result.append("DOC" + docList.get(docList.size() - 1));
//		result.append("]");
//		return result.toString();
//	}

	/**
	 * This method returns a static list of search queries.
	 * 
	 * @return list of search queries.
	 */
	private List<String> parseSearchQueries() {
		List<String> queries = new ArrayList<>();
		queries.add("nexus like love happy");
		queries.add("asus repair");
		queries.add("10(touch screen) fix repair");
		queries.add("1(great tablet) 2(tablet fast)");
		queries.add("tablet");
		return queries;
	}

	/**
	 * This method parses the search queries and constructs a map of Query
	 * Number to the actual query String.
	 * 
	 * @param queries
	 * @return Map of Query Number and Query String.
	 */
	private Map<Integer, String> parseQueries(List<String> queries) {
		Map<Integer, String> documents = new HashMap<Integer, String>();
		for (int index = 0; index < queries.size(); index++) {
			documents.put(index + 1, queries.get(index));
		}
		return documents;
	}

	/**
	 * This method reads the contents of the given file as separate documents
	 * delimited by <DOC></DOC> tags and returns a Map of Document Number and
	 * Document content
	 * 
	 * @return Map of Doc number and Document content
	 */
	public Map<Integer, String> parseSearchDocument(String filename) {
		Scanner scanner = null;
		Map<Integer, String> documents = new HashMap<Integer, String>();
		String line = null;
		int documentCounter = 1;
		try {
			scanner = new Scanner(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (scanner.hasNextLine()) {
			String documentContents = "";
			String nextLine;
			line = scanner.nextLine();
			if (line.startsWith("<DOC ")) {
				while (!(nextLine = scanner.nextLine()).equals("</DOC>")) {
					if (nextLine != "</DOC>") {
						if (nextLine.equals("")) {
							documentContents += " ";
						}
						documentContents = documentContents + " " + nextLine;
					} else {
						break;
					}
				}
				documents.put(documentCounter, documentContents);
				documentCounter++;
			} else {
				continue;
			}
		}
		try {
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documents;
	}

	/** constant AND used for separating query params. */
	private static final String AND = "AND";
}
