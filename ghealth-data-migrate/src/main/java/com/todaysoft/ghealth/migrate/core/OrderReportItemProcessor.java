package com.todaysoft.ghealth.migrate.core;

import com.sun.rowset.internal.Row;
import com.todaysoft.ghealth.migrate.config.CdcJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.CsmsJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.DetectionReport;
import com.todaysoft.ghealth.migrate.model.OrderEvalItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class OrderReportItemProcessor implements Processor
{
    @Autowired
    private CdcJdbcTemplate cdc;
    
    @Autowired
    private CsmsJdbcTemplate csms;
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Override
    public void process()
    {
        
        List<OrderEvalItem> itemdatas = getReportItemHistoryData();
        
        migrateItemdatas(itemdatas);
        
    }
    
    private List<OrderEvalItem> getReportItemHistoryData()
    {
        String sql = "select * from cdc_report_item";
        return cdc.query(sql, new RowMapper<OrderEvalItem>()
        {
            @Override
            public OrderEvalItem mapRow(ResultSet rs, int i) throws SQLException
            {
                OrderEvalItem data = new OrderEvalItem();
                data.setReportId(rs.getString("REPORT_ID"));
                data.setItemKey(rs.getString("ITEM_KEY"));
                data.setRisk(rs.getBigDecimal("RISK"));
                return data;
            }
        });
    }
    
    public Map<String, String> getDetectionReportMap()
    {
        Map<String, String> map = new HashMap<>();
        String sql = "select ID,DETECTION_CODE from cdc_detection_report";
        cdc.query(sql, new RowMapper<DetectionReport>()
        {
            @Override
            public DetectionReport mapRow(ResultSet rs, int i) throws SQLException
            {
                String code = rs.getString("DETECTION_CODE");
                String id = rs.getString("ID");
                map.put(id, code);
                return null;
            }
        });
        return map;
    }
    
    private Map<String, String> getOrderMap()
    {
        String sql = "select ID,SYNC_KEY from ghealth_order";
        Map<String, String> map = new HashMap<>();
        ghealth.query(sql, new RowMapper<Object>()
        {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException
            {
                String id = resultSet.getString("Id");
                String syncKey = resultSet.getString("SYNC_KEY");
                map.put(syncKey, id);
                return null;
            }
        });
        return map;
    }
    
    private Map<String, String> getWorkorderMap()
    {
        String sql = "select ID,SPECIMEN_CODE from csms_workorder";
        Map<String, String> map = new HashMap<>();
        csms.query(sql, new RowMapper<Object>()
        {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException
            {
                String id = resultSet.getString("Id");
                String code = resultSet.getString("SPECIMEN_CODE");
                map.put(code, id);
                return null;
            }
        });
        return map;
    }
    
    private Map<String, String> getItemCodeMap()
    {
        String sql = "select ID,CODE from ghealth_testing_item";
        Map<String, String> map = new HashMap<>();
        ghealth.query(sql, new RowMapper<Object>()
        {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException
            {
                String id = resultSet.getString("Id");
                String code = resultSet.getString("CODE");
                map.put(code, id);
                return null;
            }
        });
        return map;
    }
    
    private void migrateItemdatas(List<OrderEvalItem> datas)
    {
        Map<String, String> detectionReportMap = getDetectionReportMap();
        
        Map<String, String> workorderMap = getWorkorderMap();
        
        Map<String, String> orderMap = getOrderMap();
        
        Map<String, String> itemCodeMap = getItemCodeMap();
        String sql = "insert into ghealth_order_eval_item(ID,ORDER_ID,ITEM_ID,EVAL_VALUE) values(?,?,?,?)";
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
            {
                for (OrderEvalItem data : datas)
                {
                    String itemKey = data.getItemKey();
                    String itemId = itemCodeMap.get(itemKey);
                    if (null == itemId)
                    {
                        System.out.println("itemId id null reportId = " + itemKey);
                        continue;
                    }
                    String reportId = data.getReportId();
                    String workorderCode = detectionReportMap.get(reportId);
                    String syncKey = workorderMap.get(workorderCode);
                    String orderId = orderMap.get(syncKey);
                    if (null == orderId)
                    {
                        System.out.println("order id null reportId = " + reportId);
                        continue;
                    }
                    addItemBatch(orderId, itemId, data, ps);
                }
                return ps.executeBatch();
            }
        });
    }
    
    private void addItemBatch(String orderId, String itemId, OrderEvalItem data, PreparedStatement ps) throws SQLException
    {
        int index = 1;
        ps.setString(index++, KeyGenerator.uuid());
        ps.setString(index++, orderId);
        ps.setString(index++, itemId);
        ps.setBigDecimal(index++, data.getRisk());
        ps.addBatch();
    }
    
}
