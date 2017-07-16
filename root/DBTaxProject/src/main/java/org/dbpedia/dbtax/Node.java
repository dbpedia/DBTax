package org.dbpedia.dbtax;

/**
 * Created by shashank on 8/7/17.
 */
public class Node {


    private int nodeId;
    private String categoryName;
    private int interLangScore;

    public Node(int nodeId, String categoryName) {
        this.nodeId = nodeId;
        this.categoryName = categoryName;
    }

    public Node(int nodeId, int interLangScore) {
        this.nodeId = nodeId;
        this.interLangScore = interLangScore;
    }

    public int getInterLangScore() {
        return interLangScore;
    }

    public void setInterLangScore(int interLangScore) {
        this.interLangScore = interLangScore;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
