package com.sfsu.searchengines.model;

import java.util.Set;
import java.util.TreeSet;

public class DocumentOccurance implements Comparable<DocumentOccurance>
{
	private int documentID;
	private Set<Integer> termPositionSet;
	
	public DocumentOccurance(int documentID)
	{
		this.documentID = documentID;
		this.termPositionSet = new TreeSet<>();
	}
	
	public void addTermOccurance(int position)
	{
		this.termPositionSet.add(position);	
	}
	
	@Override
	public int hashCode() {
		return this.documentID;
	}
	
	@Override
	public boolean equals(Object obj) {
		DocumentOccurance obj2 = (DocumentOccurance) obj;
		if (obj2.getDocumentID() == this.documentID)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public int compareTo(DocumentOccurance o) {
		if (o.getDocumentID() == this.documentID)
		{
			return 0;
		} else if (o.getDocumentID() < this.documentID)
		{
			return 1;
		}
		return -1;
	}
	
	@Override
	public String toString() 
	{
		return "[" + documentID 
				+ "," 
				+ termPositionSet.size() 
				+ ": " 
				+ termPositionSet.toString().replaceAll("\\[", "").replaceAll("\\]", "") 
				+ "]";
	}
	
	/**
	 * @return the documentID
	 */
	public int getDocumentID() {
		return documentID;
	}
	/**
	 * @param documentID the documentID to set
	 */
	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}
	/**
	 * @return the termPositionList
	 */
	public Set<Integer> getTermPositionList() {
		return termPositionSet;
	}
	/**
	 * @param termPositionList the termPositionList to set
	 */
	public void setTermPositionList(Set<Integer> termPositionList) {
		this.termPositionSet = termPositionList;
	}
}
