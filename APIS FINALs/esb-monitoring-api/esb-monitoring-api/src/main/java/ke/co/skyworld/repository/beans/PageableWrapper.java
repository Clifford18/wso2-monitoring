package ke.co.skyworld.repository.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PageableWrapper<T> {

    private String domain;
    private Integer currentPage;
    private Integer pageSize;
    private Long totalCount;
    private T records;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public T getRecords() {
        return records;
    }

    @JsonIgnore
    public FlexicoreHashMap getSingleRecord() {
        if (records instanceof FlexicoreArrayList) {
            return ((FlexicoreArrayList) records).getRecord(0);
        } else if (records instanceof FlexicoreHashMap)
            return (FlexicoreHashMap) records;
        return null;
    }

    public void setRecords(T records) {
        this.records = records;
    }
}