package com.todaysoft.ghealth.migrate.core;

import com.todaysoft.ghealth.migrate.config.CdcJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.DetectionItem;
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
import java.util.List;

@Service
public class TestingItemProcessor implements Processor
{
    @Autowired
    private CdcJdbcTemplate cdc;
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Override
    public void process()
    {
        List<DetectionItem> list = getHsitorData();
        migrate(list);
    }
    
    private List<DetectionItem> getHsitorData()
    {
        return cdc.query("select * from cdc_detection_item WHERE is_single = 0 AND type != 2", new RowMapper<DetectionItem>()
        {
            @Override
            public DetectionItem mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                DetectionItem data = new DetectionItem();
                data.setId(rs.getString("ID"));
                data.setCancerId(rs.getString("CANCER_ID"));
                data.setName(rs.getString("NAME"));
                data.setType(rs.getString("TYPE"));
                data.setSemantic(rs.getString("SEMANTIC"));
                return data;
            }
        });
    }
    
    private void migrate(List<DetectionItem> list)
    {
        String sql =
            "INSERT INTO ghealth_testing_item(ID, NAME, CODE,SEX_RESTRAINT,CATEGORY,CATEGORY_MAPPING,ENABLED, CREATE_TIME ,CREATOR_NAME, DELETED, SYNC_KEY) VALUES (?,?, ?, ?,?, ?, ?, ?, ?, ?, ?)";
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
            {
                for (DetectionItem data : list)
                {
                    if (null != data.getCancerId())
                    {
                        String getCancerId = "select ID from ghealth_cancer where SYNC_KEY = ?";
                        String cancerId = ghealth.queryForObject(getCancerId, String.class, new Object[] {data.getCancerId()});
                        data.setCancerId(cancerId);
                    }
                    data.setGender(getGender(data));
                    addBatch(data, ps);
                }
                return ps.executeBatch();
            }
        });
    }
    
    private void addBatch(DetectionItem data, PreparedStatement ps) throws SQLException
    {
        int index = 1;
        ps.setString(index++, KeyGenerator.uuid());
        ps.setString(index++, data.getName());
        ps.setString(index++, data.getSemantic());
        ps.setString(index++, data.getGender());
        String type = getType(data);
        ps.setString(index++, type);
        ps.setString(index++, data.getCancerId());
        ps.setInt(index++, 1);
        ps.setTimestamp(index++, new Timestamp(new Date().getTime()));
        ps.setString(index++, "管理员");
        ps.setInt(index++, 0);
        ps.setString(index++, data.getId());
        ps.addBatch();
    }
    
    private String getGender(DetectionItem data)
    {
        String getGenders = "select DISTINCT(GENDER) from cdc_detection_packages_item where ITEM_ID = ?";
        List<String> genders = cdc.queryForList(getGenders, String.class, new Object[] {data.getId()});
        if (CollectionUtils.isEmpty(genders) || genders.size() > 1)
        {
            return "0";
        }
        return genders.get(0);
    }
    
    private String getType(DetectionItem data)
    {
        String type = null;
        if ("0".equals(data.getType()))
        {
            type = DetectionItem.ITEM_CATEGORY_JB;
        }
        else if ("1".equals(data.getType()))
        {
            type = DetectionItem.ITEM_CATEGORY_ET;
        }
        else if ("2".equals(data.getType()))
        {
            type = DetectionItem.ITEM_CATEGORY_YY;
        }
        return type;
    }
}
