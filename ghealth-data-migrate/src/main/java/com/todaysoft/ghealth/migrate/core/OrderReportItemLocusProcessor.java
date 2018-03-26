package com.todaysoft.ghealth.migrate.core;

import com.todaysoft.ghealth.migrate.config.CdcJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.CsmsJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.DetectionReport;
import com.todaysoft.ghealth.migrate.model.OrderEvalItem;
import com.todaysoft.ghealth.migrate.model.OrderEvalItemLocus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderReportItemLocusProcessor implements Processor
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
        List<OrderEvalItemLocus> itemLocusDatas = getReportItemLocusHistoryData();
        migrateItemLocusDatas(itemLocusDatas);
    }
    
    private List<OrderEvalItemLocus> getReportItemLocusHistoryData()
    {
        String sql = "select * from cdc_report_loci";
        return cdc.query(sql, new RowMapper<OrderEvalItemLocus>()
        {
            @Override
            public OrderEvalItemLocus mapRow(ResultSet rs, int i) throws SQLException
            {
                OrderEvalItemLocus data = new OrderEvalItemLocus();
                data.setReportId(rs.getString("REPORT_ID"));
                data.setLocusCode(rs.getString("LOCUS_NAME"));
                data.setRisk(rs.getBigDecimal("RATIO"));
                data.setResult(rs.getString("RESULT"));
                data.setItemId(rs.getString("ITEM_ID"));
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
    
    private Map<String, String> getReportItemMap()
    {
        String sql = "select ID,ITEM_KEY from cdc_report_item";
        Map<String, String> map = new HashMap<>();
        cdc.query(sql, new RowMapper<Object>()
        {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException
            {
                String id = resultSet.getString("Id");
                String itemKey = resultSet.getString("ITEM_KEY");
                map.put(id, itemKey);
                return null;
            }
        });
        return map;
    }
    
    private Map<String, String> getLocusMap()
    {
        String sql = "select ID,NAME from ghealth_locus";
        Map<String, String> map = new HashMap<>();
        ghealth.query(sql, new RowMapper<Object>()
        {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException
            {
                String id = resultSet.getString("Id");
                String name = resultSet.getString("NAME");
                map.put(name, id);
                return null;
            }
        });
        return map;
    }
    
    private void migrateItemLocusDatas(List<OrderEvalItemLocus> itemLocusDatas)
    {
        Map<String, String> detectionReportMap = getDetectionReportMap();
        
        Map<String, String> workorderMap = getWorkorderMap();
        
        Map<String, String> orderMap = getOrderMap();
        
        Map<String, String> itemLocusMap = getLocusMap();
        
        Map<String, String> itemCodeMap = getItemCodeMap();
        
        Map<String, String> reportItemMap = getReportItemMap();
        String sql = "insert into ghealth_order_eval_item_locus(ID,ORDER_ID,ITEM_ID,LOCUS_ID,GENETYPE,EVAL_VALUE) values(?,?,?,?,?,?)";
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
            {
                for (OrderEvalItemLocus data : itemLocusDatas)
                {
                    String reportId = data.getReportId();
                    String workorderCode = detectionReportMap.get(reportId);
                    String syncKey = workorderMap.get(workorderCode);
                    String orderId = orderMap.get(syncKey);
                    if (StringUtils.isEmpty(orderId))
                    {
                        System.out.println("order id null reportId = " + reportId);
                        continue;
                    }
                    String locusId = itemLocusMap.get(data.getLocusCode());
                    if(StringUtils.isEmpty(locusId))
                    {
                        System.out.println("locusId id null locusCode = " + data.getLocusCode());
                        continue;
                    }
                    String geneType = data.getResult().replace("/", "");

                    String itemKey = reportItemMap.get(data.getItemId());
                    String itemId = itemCodeMap.get(itemKey);
                    if (StringUtils.isEmpty(itemId))
                    {
                        System.out.println("itemId id null sourceItemId = " + data.getItemId());
                        continue;
                    }
                    addItemLocusBatch(orderId, locusId, geneType, data.getRisk(), itemId, ps);
                }
                return ps.executeBatch();
            }
        });
    }
    
    private void addItemLocusBatch(String orderId, String locusId, String geneType, BigDecimal risk, String itemId, PreparedStatement ps) throws SQLException
    {
        int index = 1;
        ps.setString(index++, KeyGenerator.uuid());
        ps.setString(index++, orderId);
        ps.setString(index++, itemId);
        ps.setString(index++, locusId);
        ps.setString(index++, geneType);
        ps.setBigDecimal(index++, risk);
        ps.addBatch();
    }
    
}
