package com.icht.backfront.model;

import java.io.Serializable;
import java.util.List;

public class Paging<R> implements Serializable {
    //页数
    private int pageNum;
    //每页数量
    private int pageSize;
    //总记录数
    private long totalCount;
    //总页数
    private int totalPage;
    //集合数据
    private List<R> data;

    public Paging() {

    }

    public Paging(int pageNum, int pageSize, int totalPage, long totalCount, List<R> data) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalCount = totalCount;
        this.data = data;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public List<?> getData() {
        return data;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setData(List<R> data) {
        this.data = data;
    }
}
