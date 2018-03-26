package com.todaysoft.ghealth.migrate.core;

import com.todaysoft.ghealth.migrate.model.Locus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.todaysoft.ghealth.migrate.config.CdcJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class LocusProcessor implements Processor
{
    @Autowired
    private CdcJdbcTemplate cdc;
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Override
    public void process()
    {
        List<Locus> list = getHsitorData();
        Map<String, String> map = insertGene(list);
        migrate(list, map);
    }
    
    private List<Locus> getHsitorData()
    {
        return cdc.query("select * from cdc_locus", new RowMapper<Locus>()
        {
            @Override
            public Locus mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                Locus locus = new Locus();
                locus.setLocusCode(rs.getString("LOCUS_CODE"));
                locus.setGene(rs.getString("GENE_SEGMENT"));
                locus.setId(rs.getString("ID"));
                return locus;
            }
        });
    }
    
    private Map<String, String> insertGene(List<Locus> list)
    {
        Map<String, String> map = new HashMap<>();
        Set<String> symbols = new HashSet<>();
        for (Locus data : list)
        {
            if (symbols.contains(data.getGene()))
            {
                continue;
            }
            symbols.add(data.getGene());
            String geneId = KeyGenerator.uuid();
            String geneName = data.getGene();
            String createName = "管理员";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String id = data.getId();
            ghealth.update("INSERT INTO GHEALTH_GENE (ID, SYMBOL,  CREATE_TIME ,CREATOR_NAME, DELETED) " + "values ('" + geneId + "', '" + geneName + "', '"
                + timestamp + "', '" + createName + "', '" + 0 + "')");
            map.put(data.getGene(), geneId);
        }
        return map;
    }
    
    private void migrate(List<Locus> list, Map<String, String> map)
    {
        String sql = "INSERT INTO GHEALTH_LOCUS(ID, NAME, GENE_ID, CREATE_TIME ,CREATOR_NAME, DELETED, SYNC_KEY) VALUES (?, ?, ?, ?, ?, ?, ?)";
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
            {
                for (Locus data : list)
                {
                    addBatch(data, map.get(data.getGene()), ps);
                }
                return ps.executeBatch();
            }
        });
    }
    
    private void addBatch(Locus data, String geneId, PreparedStatement ps) throws SQLException
    {
        int index = 1;
        ps.setString(index++, KeyGenerator.uuid());
        ps.setString(index++, data.getLocusCode());
        ps.setString(index++, geneId);
        ps.setTimestamp(index++, new Timestamp(new Date().getTime()));
        ps.setString(index++, "管理员");
        ps.setInt(index++, 0);
        ps.setString(index++, data.getId());
        ps.addBatch();
    }
}
