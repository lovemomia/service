package cn.momia.admin.web.common;

import java.util.List;

/**
 * Created by hoze on 15/6/30.
 */
public class QueryPage {

    private List list;//结果集
    private int totalRecords;//查询记录数
    private int pageSize = 5;//每页多少条数据
    private int pageNo;//第几页
    private int query_type;//查询类型

    private int totalPages;//总页数
    private int topPageNo;//首页
    private int previousPageNo;//上一页
    private int nextPageNo;//下一页
    private int bottomPageNo;//尾页


    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTopPageNo() {
        return topPageNo;
    }

    public void setTopPageNo(int topPageNo) {
        this.topPageNo = topPageNo;
    }

    public int getPreviousPageNo() {
        return previousPageNo;
    }

    public void setPreviousPageNo(int previousPageNo) {
        this.previousPageNo = previousPageNo;
    }

    public int getNextPageNo() {
        return nextPageNo;
    }

    public void setNextPageNo(int nextPageNo) {
        this.nextPageNo = nextPageNo;
    }

    public int getBottomPageNo() {
        return bottomPageNo;
    }

    public void setBottomPageNo(int bottomPageNo) {
        this.bottomPageNo = bottomPageNo;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getQuery_type() {
        return query_type;
    }

    public void setQuery_type(int query_type) {
        this.query_type = query_type;
    }
}
