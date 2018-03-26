package com.todaysoft.ghealth.migrate.core;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.todaysoft.ghealth.migrate.config.CdcJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.Disease;

@Service
public class DiseaseProcessor implements Processor
{
    @Autowired
    private CdcJdbcTemplate cdc;
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Override
    public void process()
    {
        List<Disease> diseases = getHistoryData();
        migrate(diseases);
    }
    
    private List<Disease> getHistoryData()
    {
        return cdc.query("select * from cdc_cancer", new RowMapper<Disease>()
        {
            @Override
            public Disease mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                Disease disease = new Disease();
                disease.setId(rs.getString("ID"));
                disease.setName(rs.getString("NAME"));
                disease.setMaleRisk(rs.getBigDecimal("MALE_RISK_AVG"));
                disease.setFemaleRisk(rs.getBigDecimal("FEMALE_RISK_AVG"));
                return disease;
            }
        });
    }
    
    private void migrate(List<Disease> diseases)
    {
        String sql =
            "INSERT INTO GHEALTH_CANCER (ID, NAME, RISK_MALE, RISK_FEMALE, CREATE_TIME, CREATOR_NAME, DELETED, SYNC_KEY) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
            {
                for (Disease disease : diseases)
                {
                    addBatch(disease, ps);
                }
                
                return ps.executeBatch();
            }
        });
    }
    
    private void addBatch(Disease disease, PreparedStatement ps) throws SQLException
    {
        int index = 1;
        ps.setString(index++, KeyGenerator.uuid());
        ps.setString(index++, disease.getName());
        ps.setBigDecimal(index++, disease.getMaleRisk().divide(BigDecimal.valueOf(100)));
        ps.setBigDecimal(index++, disease.getFemaleRisk().divide(BigDecimal.valueOf(100)));
        ps.setTimestamp(index++, new Timestamp(new Date().getTime()));
        ps.setString(index++, "管理员");
        ps.setInt(index++, 0);
        ps.setString(index++, disease.getId());
        ps.addBatch();
    }
}
