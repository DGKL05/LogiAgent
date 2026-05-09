package com.logiagent.common.result;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {

    private List<T> records;
    private Long total;
    private Long page;
    private Long size;

    public PageResult() {
    }

    private PageResult(List<T> records, Long total, Long page, Long size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public static <T> PageResult<T> of(List<T> records, Long total, Long page, Long size) {
        return new PageResult<>(records, total, page, size);
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
