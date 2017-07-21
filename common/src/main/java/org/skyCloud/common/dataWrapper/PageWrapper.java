package org.skyCloud.common.dataWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页包装类
 */
public class PageWrapper<T> {
    private long total;
    private List<T> rows;
    private  boolean success=true;
    private String message;

    public PageWrapper() {
    }

    public PageWrapper(Boolean success,String message) {
        this.success=false;
        this.message=message;
    }

    public PageWrapper(long total,List< T> rows) {
        this.total = total;
        this.rows = rows;
    }
    public PageWrapper( T rows) {
        this.total = 1;
        this.rows = new ArrayList<>();
        this.rows.add(rows);
    }

    public long getTotal() {
        return total;
    }

    public List<T> getRows() {
        return rows;
    }

    public boolean isSuccess() {
        return success;
    }
}
