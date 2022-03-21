package uk.ac.ebi.spot.ols.controller.api;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SKOSConceptNode<T> {
	
	private Collection<SKOSConceptNode<T>> children = new ArrayList<SKOSConceptNode<T>>();
	private Collection<SKOSConceptNode<T>> related = new ArrayList<SKOSConceptNode<T>>();
    private Collection<SKOSConceptNode<T>> parent = new ArrayList<SKOSConceptNode<T>>();
    private String index;
    private T data = null;
    
    public SKOSConceptNode(T data) {
        this.data = data;
    }
    
    public SKOSConceptNode(T data, Collection<SKOSConceptNode<T>> parent) {
        this.data = data;
        this.parent = parent;
    }

	public Collection<SKOSConceptNode<T>> getChildren() {
		return children;
	}
	public void setChildren(Collection<SKOSConceptNode<T>> children) {
		this.children = children;
	}
	
    public void addChild(T data) {
        SKOSConceptNode<T> child = new SKOSConceptNode<T>(data);
//        child.addParent(this);
        this.children.add(child);
    }

    public void addChild(SKOSConceptNode<T> child) {
//        child.addParent(this);
        this.children.add(child);
    }
    
    public void addRelated(T data) {
        SKOSConceptNode<T> related = new SKOSConceptNode<T>(data);
        this.related.add(related);
    }

    public void addRelated(SKOSConceptNode<T> related) {
        this.related.add(related);
    }
    
    public void addParent(T data) {
        SKOSConceptNode<T> parent = new SKOSConceptNode<T>(data);
//        parent.addChild(this);
        this.parent.add(parent);
    }

    public void addParent(SKOSConceptNode<T> parent) {
//    	parent.addChild(this);
        this.parent.add(parent);
    }
	
	public Collection<SKOSConceptNode<T>> getRelated() {
		return related;
	}
	public void setRelated(Collection<SKOSConceptNode<T>> related) {
		this.related = related;
	}
	public Collection<SKOSConceptNode<T>>  getParent() {
		return parent;
	}
	public void setParent(Collection<SKOSConceptNode<T>>  parent) {
		this.parent = parent;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	
    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
	
   public boolean isRoot() {
        return this.parent.size() == 0;
    }

    public boolean isLeaf() {
        return this.children.size() == 0;
    }

    public void resetParent() {
        this.parent = new ArrayList<SKOSConceptNode<T>>();
    }
    
    public void resetChildren() {
        this.children = new ArrayList<SKOSConceptNode<T>>();
    }
    
    public void resetRelated() {
        this.related = new ArrayList<SKOSConceptNode<T>>();
    }
}
