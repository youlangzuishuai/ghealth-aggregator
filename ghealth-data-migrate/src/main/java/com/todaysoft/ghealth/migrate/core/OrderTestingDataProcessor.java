package com.todaysoft.ghealth.migrate.core;

import com.todaysoft.ghealth.migrate.config.CsmsJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.OrderTestingData;
import com.todaysoft.ghealth.migrate.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderTestingDataProcessor implements Processor
{
    @Autowired
    private CsmsJdbcTemplate csms;
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Override
    public void process()
    {
        List<String> orderIds = getDistinctOrderIds();
        Map<String, String> map = getDataByOrderId(orderIds);
          migrate(map);
    }
    
    private List<String> getDistinctOrderIds()
    {
        String sql = "select distinct(ORDER_ID) from csms_detection_data";
        return csms.queryForList(sql, String.class);
    }
    
    private Map<String, String> getDataByOrderId(List<String> orderIds)
    {
        Map<String, String> map = new HashMap<>();
        for (String orderId : orderIds)
        {
            String sql = "select * from csms_detection_data where ORDER_ID = ? AND LOCUS_CODE != '' AND GENETYPE != ''";
            List<OrderTestingData> list = csms.query(sql, new RowMapper<OrderTestingData>()
            {
                @Override
                public OrderTestingData mapRow(ResultSet rs, int i) throws SQLException
                {
                    OrderTestingData data = new OrderTestingData();
                    data.setGenetype(rs.getString("GENETYPE").replace("/",""));
                    data.setLocus(rs.getString("LOCUS_CODE"));
                    return data;
                }
            },new Object[]{orderId});
            String getOrderIdSql = "select ID from ghealth_order where SYNC_KEY = ?";
            List<String> ids = ghealth.queryForList(getOrderIdSql, String.class, new Object[]{orderId});
            if(CollectionUtils.isEmpty(ids))
            {
                continue;
            }
            String jsonString = JsonUtils.toJson(list);
            map.put(ids.get(0), jsonString);
        }
        return map;
    }
    
    private void migrate(Map<String, String> map)
    {
        String sql = "insert into ghealth_order_testing_data(ID,ORDER_ID,UPLOAD_RECORD_ID,DATA_DETAILS) values(?,?,?,?)";
        String uoloadRecoedKey = KeyGenerator.uuid();
        ghealth.update("INSERT INTO ghealth_testing_data_upload_record (ID, TITLE,FILENAME, DOWNLOAD_URL ,UPLOAD_TIME,UPLOADER_NAME) " + "values ('"
            + uoloadRecoedKey + "', '" + "0" + "', '" + "0" + "', '" + "0" + "', '" + new Timestamp(new Date().getTime()) + "', '" + "0" + "')");
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
            {
                for (Map.Entry<String, String> entry : map.entrySet())
                {
                    addDatch(uoloadRecoedKey,entry,ps);
                }
                return ps.executeBatch();
            }
        });
    }
    
    private void addDatch(String key,Map.Entry<String, String> entry, PreparedStatement ps) throws SQLException
    {
        int index = 1;
        ps.setString(index++,KeyGenerator.uuid());
        ps.setString(index++,entry.getKey());
        ps.setString(index++,key);
        ps.setString(index++,entry.getValue());
        ps.addBatch();
    }
}
