package com.vinorsoft.microservices.core.notarization.util.helpers;

import java.util.Collection;

public class PaginationSet<T> {

    public PaginationSet() {
    }

    public PaginationSet(Collection<T> items, Integer totalCount, Integer page, Integer pageSize, String keyword) {
        this.items = items;
        this.totalCount = totalCount;
        this.page = page;
        this.pageSize = pageSize;
        this.keyword = keyword;
    }

    private Integer page;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    private Collection<T> items;

    public Collection<T> getItems() {
        return items;
    }

    public void setItems(Collection<T> items) {
        this.items = items;
    }

    public Integer totalPages() {
        return getPageSize() == 0 ? (int) Math.ceil((double) getTotalCount() / getPageSize()) : 0;
    }

    private Integer totalCount;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
