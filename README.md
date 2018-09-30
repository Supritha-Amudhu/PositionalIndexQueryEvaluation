# InvertedSearchIndexQueryEvaluation

Description:

Search Engines Home Work 1 is about building an Inverted Search Index and performing a Boolean Query Evaluation on the query strings to obtain search results.

Input and Output Files Used:

1. Input document file - documents.txt
2. Output 1 - Inverted Index on Document Words - documentTermFrequency.txt
3. Output 2 - Boolean Query Evaluation Results on Search Queries - searchResults.txt
4. Copy of Queries that are being search for - input_1.txt, input_2.txt, input_3.txt

Java Files:

1. com.sfsu.searchengines.model - Package:
	a. SearchWord.java - Class that holds data about the Search Word and the Document it belongs to.
	b. TermDocumentFrequency - Class that holds information about terms from the Inverted Index, along with their Document Frequency.
	
2. com.sfsu.searchengines.core - Package:
	a. InvertedIndex.java - Class that builds an Inverted Index for any set of inputs.
	b. BooleanQueryEvaluation.java -  Class that performs an intersection operation on two lists.
	c. SearchEngine.java - Class that has to be executed, in order for the program to work.
	
3. org.lemurproject.kstem - Package:
	a. This package consists of all files related to the Stemmer algorithm.

Steps to run the program:

1. I have created a zip file of the entire project, as the source file is required for the project to run on Eclipse.
2. Unzip the project contents.
3. Open the unzipped folder using Eclipse (Any version).
4. Go to src -> com.sfsu.searchengines.core -> SearchEngine.java
5. Click on the button 'Run', to run the Main program.
6. In the Console, there will be statements about the Input Query, Documents they are listed in, Interesection of results.
7. The file `documents.txt` is the input file containing the list of 10 documents.
8. The file `documentTermFrequency.txt` is the first output file containing the Inverted Index for the list of words from `documents.txt`.
9. The file `searchResults.txt` has the Search Results for the Queries provided in the program.
10. The files `input_1.txt`, `input_2.txt`, `input_3.txt` are Input files written from the program to have a clear view of what the Input queries to search are.



