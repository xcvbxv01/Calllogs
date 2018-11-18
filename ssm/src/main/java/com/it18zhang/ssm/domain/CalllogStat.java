package com.it18zhang.ssm.domain;

/**
 * 指定号码一年内每个月通话统计的结果
 */
public class CalllogStat {

    private String yearMonth;
    private int count;

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
