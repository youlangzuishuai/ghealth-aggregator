package com.todaysoft.ghealth.migrate.core;

import com.todaysoft.ghealth.migrate.config.CsmsJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.AgentPackages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class AgentPackagesProcessor implements Processor
{
    @Autowired
    private CsmsJdbcTemplate csms;
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Override
    public void process()
    {
        List<AgentPackages> list = getHistoryData();
        migrate(list);
    }
    
    private List<AgentPackages> getHistoryData()
    {
        String sql = "select * from csms_agent_packages";
        return csms.query(sql, new RowMapper<AgentPackages>()
        {
            @Override
            public AgentPackages mapRow(ResultSet rs, int i) throws SQLException
            {
                AgentPackages data = new AgentPackages();
                data.setAgentId(rs.getString("AGENT_ID"));
                data.setPackageId(rs.getString("PACKAGES_ID"));
                return data;
            }
        });
    }
    
    private void migrate(List<AgentPackages> agents)
    {
        String sql = "INSERT INTO ghealth_agency_product (ID,AGENCY_ID, PRODUCT_ID,AGENCY_PRICE) VALUES (?,?,?,?)";
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            
            @Override
            public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException
            {
                for (AgentPackages data : agents)
                {
                    String findByAgentId = "select id from ghealth_agency where SYNC_KEY = ?";
                    List<String> agenctIds = ghealth.queryForList(findByAgentId, String.class, new Object[] {data.getAgentId()});
                    if (CollectionUtils.isEmpty(agenctIds))
                    {
                        continue;
                    }
                    String agencyId = agenctIds.get(0);
                    String findByItemId = "select id from ghealth_testing_product where SYNC_KEY = ?";
                    List<String> productIds = ghealth.queryForList(findByItemId, String.class, new Object[] {data.getPackageId()});
                    if (CollectionUtils.isEmpty(productIds))
                    {
                        continue;
                    }
                    for (String productId : productIds)
                    {
                        AgentPackages ap = new AgentPackages();
                        ap.setAgentId(agencyId);
                        ap.setPackageId(productId);
                        addBatch(ap, preparedStatement);
                    }
                }
                return preparedStatement.executeBatch();
            }
        });
    }
    
    private void addBatch(AgentPackages data, PreparedStatement ps) throws SQLException
    {
        int index = 1;
        ps.setString(index++, KeyGenerator.uuid());
        ps.setString(index++, data.getAgentId());
        ps.setString(index++, data.getPackageId());
        ps.setBigDecimal(index++, new BigDecimal(0));
        ps.addBatch();
    }
}
