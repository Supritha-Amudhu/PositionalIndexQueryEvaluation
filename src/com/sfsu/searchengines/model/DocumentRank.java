package com.sfsu.searchengines.model;

public class DocumentRank implements Comparable<DocumentRank>
{
	private int documentID;
	private double rank;
	
	public DocumentRank(int documentID, double rank)
	{
		this.documentID = documentID;
		this.rank = rank;
	}
	
	@Override
	public int compareTo(DocumentRank o) {
		if (o.getRank() == this.rank)
		{
			return 0;
		}
		else if (o.getRank() > this.rank)
		{
			return 1;
		}
		return -1;
	}
	
	@Override
	public String toString() {
		return "{DocumentID: " + documentID + ", score: " + rank + "}";
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
	 * @return the rank
	 */
	public double getRank() {
		return rank;
	}
	/**
	 * @param rank the rank to set
	 */
	public void setRank(double rank) {
		this.rank = rank;
	}
	

}
