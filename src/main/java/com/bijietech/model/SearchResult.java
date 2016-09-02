package com.bijietech.model;

import java.io.Serializable;
import java.util.List;

public class SearchResult<T> implements Serializable {

	private static final long serialVersionUID = -5873223111136887489L;

	/**
     * 总记录条数
     */
    private long total;

    /**
     * 查询结果集
     */
    private List<T> rows;
    
    public SearchResult() {
        super();
    }

    public SearchResult(List<T> rows, long total) {
        super();
        this.rows = rows;
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        if (total == 0 && rows != null) {
            return rows.size();
        }
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

}