package com.calllog.coprossor;


import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 协处理器
 */
public class CalllogRegion<postScannerNext> extends BaseRegionObserver {

    //被叫引用id
    private static final String REF_ROW_ID = "refrowid" ;
    //通话记录表名
    private static final String CALL_LOG_TABLE_NAME = "call:calllogs" ;

    /**
     * 每次put数据之后调用
     */
    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
        super.postPut(e, put, edit, durability);

        TableName tName = TableName.valueOf("call:calllogs");

        TableName tName1 = e.getEnvironment().getRegion().getRegionInfo().getTable();

        if (tName.equals(tName1)) {
            //得到rowkey
            String rowKey = Bytes.toString(put.getRow());

            String[] strs = rowKey.split(",");
            //如果是被叫，直接返回
            if (strs[3].equals("1")) {
                return;
            }
            //66,15733218888,20181002071335,0,18332561111,063
            //取出相应的值
            String caller = strs[1];
            String time = strs[2];
            String callee = strs[4];
            String duration = strs[5];
            //计算区域号
            String hash = CalllogUtil.getHashcode(callee, time, 100);
            String newRowKey = hash + "," + callee + "," + time + "," + "1" + "," + caller + "," + duration;

            //开始put数据
            Put p = new Put(Bytes.toBytes(newRowKey));
            p.addColumn(Bytes.toBytes("f2"), Bytes.toBytes("refrowid"), Bytes.toBytes(rowKey));

            Table tb = e.getEnvironment().getTable(tName);
            tb.put(p);
            System.out.println("put over");
        }
    }

    /**
     * get之后调用 -- 实现被叫查询时，直接返回主叫的记录
     * 将之前get的结果替换
     */
    @Override
    public void postGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
        //获得表名
        String tableName = e.getEnvironment().getRegion().getRegionInfo().getTable().getNameAsString();

        //判断表名是否是ns1:calllogs
        if(!tableName.equals("call:calllogs")){
            super.preGetOp(e, get, results);
        }
        else{
            //得到rowkey
            String rowkey = Bytes.toString(get.getRow());
            //
            String[] arr = rowkey.split(",");
            //主叫
            if(arr[3].equals("0")){
                super.postGetOp(e, get, results);
            }
            //被叫
            else{
                //得到主叫方的rowkey
                String refrowid = Bytes.toString(CellUtil.cloneValue(results.get(0)));
                //
                Table tt = e.getEnvironment().getTable(TableName.valueOf("call:calllogs"));
                Get g = new Get(Bytes.toBytes(refrowid));
                Result r = tt.get(g);
                List<Cell> newList = r.listCells();
                results.clear();
                results.addAll(newList);
            }
        }
    }


    /**
     * 扫描之后调用
     */
    @Override
    public boolean postScannerNext(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s, List<Result> results, int limit, boolean hasMore) throws IOException {

        boolean b = super.postScannerNext(e, s, results, limit, hasMore);

        //新集合
        List<Result> newList = new ArrayList<Result>();

        //获得表名
        String tableName = e.getEnvironment().getRegion().getRegionInfo().getTable().getNameAsString();

        //判断表名是否是ns1:calllogs
        if (tableName.equals(CALL_LOG_TABLE_NAME)) {
            Table tt = e.getEnvironment().getTable(TableName.valueOf(CALL_LOG_TABLE_NAME));
            for(Result r : results){
                //rowkey
                String rowkey = Bytes.toString(r.getRow());
                String flag = rowkey.split(",")[3] ;
                //主叫
                if(flag.equals("0")){
                    newList.add(r) ;
                }
                //被叫
                else{
                    //取出主叫号码
                    byte[] refrowkey = r.getValue(Bytes.toBytes("f2"),Bytes.toBytes(REF_ROW_ID)) ;
                    Get newGet = new Get(refrowkey);
                    newList.add(tt.get(newGet));
                }
            }
            results.clear();
            results.addAll(newList);
        }
        return b ;
    }
}
