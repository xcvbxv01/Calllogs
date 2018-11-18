package com.it18zhang.ssm.domain;

/**
 * 处理起始和结束的时间的类
 */
public class CalllogRange {

    //起始点
    private String startPoint ;
    //结束点
    private String endPoint ;

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String toString() {
        return startPoint + " - " + endPoint ;
    }
}
