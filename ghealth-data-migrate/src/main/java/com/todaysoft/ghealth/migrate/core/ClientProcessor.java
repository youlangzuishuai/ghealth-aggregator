package com.todaysoft.ghealth.migrate.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaysoft.ghealth.migrate.config.CsmsJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.target.Customer;

@Service
public class ClientProcessor implements Processor
{
    @Autowired
    private CsmsJdbcTemplate csms;
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    private Map<String, String> nations = new HashMap<String, String>();
    
    private Map<String, String> agencyMappings = new HashMap<String, String>();
    
    private Map<String, String> userAgencyMappings = new HashMap<String, String>();
    
    @PostConstruct
    private void init()
    {
        nations.put("傣族", "18");
        nations.put("回族", "2");
        nations.put("土家族", "15");
        nations.put("壮族", "7");
        nations.put("朝鲜族", "10");
        nations.put("汉族", "1");
        nations.put("满族", "11");
        nations.put("羌族", "53");
        nations.put("苗族", "5");
        nations.put("蒙古族", "9");
    }
    
    @Override
    public void process()
    {
        int limit = 200;
        int offset = 0;
        List<Customer> customers;
        
        while (true)
        {
            customers = fromHistory(offset, limit);
            
            if (customers.isEmpty())
            {
                System.out.println(MessageFormat.format("Sync customers finished.", offset));
                break;
            }
            
            migrate(customers);
            offset += limit;
            System.out.println(MessageFormat.format("Processed {0} records for sync customers.", offset));
        }
    }
    
    private List<Customer> fromHistory(int offset, int limit)
    {
        String sql = String.format("select * from csms_client where creator_time>= '2018-01-18 00:00:00' order by creator_time limit %1$d offset %2$d", limit, offset);
        
        return csms.query(sql, new RowMapper<Customer>()
        {
            @Override
            public Customer mapRow(ResultSet rs, int rowNum)
                throws SQLException
            {
                Customer customer = new Customer();
                customer.setId(KeyGenerator.uuid());
                customer.setName(rs.getString("NAME"));
                customer.setPhone(rs.getString("TEL"));
                customer.setEmail(rs.getString("EMAIL"));
                customer.setSex(rs.getString("SEX"));
                customer.setBirthday(rs.getString("BIRTH"));
                customer.setAddress(rs.getString("ADDRESS"));
                customer.setRemark(rs.getString("NOTES"));
                customer.setCompany(rs.getString("UNIT"));
                customer.setVocation(rs.getString("OCCUPATION"));
                customer.setMaritalStatus(asTargetMaritalStatus(rs.getString("MARITAL_STATUS")));
                customer.setNation(asTargetNation(rs.getString("FAMILY_NAME")));
                fillTargetDistrict(rs.getString("HOMETOWN"), customer);
                customer.setCreateTime(rs.getTimestamp("CREATOR_TIME"));
                customer.setCreatorName(rs.getString("CREATOR_NAME"));
                customer.setDeleted(asTargetDeleted(rs.getString("IS_DELETE")));
                customer.setAgencyId(asTargetAgencyId(rs.getString("AGENT_ID"), rs.getString("CREATOR_ID")));
                customer.setSyncKey(rs.getString("ID"));
                
                if (null == customer.getCreateTime())
                {
                    try
                    {
                        customer.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").parse("2017-12-31"));
                    }
                    catch (ParseException e)
                    {
                        //
                    }
                }
                
                if (customer.isDeleted())
                {
                    try
                    {
                        customer.setDeleteTime(new SimpleDateFormat("yyyy-MM-dd").parse("2017-12-31"));
                    }
                    catch (ParseException e)
                    {
                        //                       、
                    }
                    
                    customer.setDeletorName("管理员");
                }
                return customer;
            }
        });
    }
    
    private void migrate(List<Customer> customers)
    {
        String sql =
            "INSERT INTO GHEALTH_CUSTOMER (ID, NAME, PHONE, EMAIL, SEX, NATION, PROVINCE, CITY, COUNTY, ADDRESS, REMARK, VOCATION, BIRTHDAY, MARITAL_STATUS, COMPANY, AGENCY_ID, CREATE_TIME, CREATOR_NAME, DELETED, DELETOR_NAME, DELETE_TIME, SYNC_KEY) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                for (Customer customer : customers)
                {
                    addBatch(customer, ps);
                }
                return ps.executeBatch();
            }
        });
    }
    
    private void addBatch(Customer data, PreparedStatement ps)
        throws SQLException
    {
        int index = 1;
        ps.setString(index++, data.getId());
        ps.setString(index++, data.getName());
        ps.setString(index++, StringUtils.isEmpty(data.getPhone()) ? "-" : data.getPhone());
        ps.setString(index++, data.getEmail());
        ps.setString(index++, data.getSex());
        ps.setString(index++, data.getNation());
        ps.setString(index++, data.getProvince());
        ps.setString(index++, data.getCity());
        ps.setString(index++, data.getCounty());
        ps.setString(index++, data.getAddress());
        ps.setString(index++, data.getRemark());
        ps.setString(index++, data.getVocation());
        ps.setString(index++, data.getBirthday());
        ps.setString(index++, data.getMaritalStatus());
        ps.setString(index++, data.getCompany());
        ps.setString(index++, data.getAgencyId());
        ps.setTimestamp(index++, null == data.getCreateTime() ? null : new Timestamp(data.getCreateTime().getTime()));
        ps.setString(index++, StringUtils.isEmpty(data.getCreatorName()) ? "管理员" : data.getCreatorName());
        ps.setBoolean(index++, data.isDeleted());
        ps.setString(index++, data.getDeletorName());
        ps.setTimestamp(index++, null == data.getDeleteTime() ? null : new Timestamp(data.getDeleteTime().getTime()));
        ps.setString(index++, data.getSyncKey());
        ps.addBatch();
    }
    
    private String asTargetMaritalStatus(String status)
    {
        if ("2".equals(status))
        {
            return "1";
        }
        else if ("1".equals(status))
        {
            return "2";
        }
        
        return null;
    }
    
    private String asTargetNation(String nation)
    {
        if (StringUtils.isEmpty(nation))
        {
            return null;
        }
        
        return nations.get(nation);
    }
    
    private void fillTargetDistrict(String district, Customer customer)
    {
        if (StringUtils.isEmpty(district))
        {
            return;
        }
    }
    
    private boolean asTargetDeleted(String deleted)
    {
        return "1".equals(deleted);
    }
    
    private String asTargetAgencyId(String agencyId, String creatorId)
    {
        if (StringUtils.isEmpty(agencyId) && StringUtils.isEmpty(creatorId))
        {
            return null;
        }
        
        if (StringUtils.isEmpty(agencyId))
        {
            agencyId = getAgencyIdByUserId(creatorId);
        }
        
        if (StringUtils.isEmpty(agencyId))
        {
            return null;
        }
        
        return getTargetAgencyId(agencyId);
    }
    
    private String getAgencyIdByUserId(String userId)
    {
        if (StringUtils.isEmpty(userId))
        {
            return null;
        }
        
        String id = userAgencyMappings.get(userId);
        
        if (null == id)
        {
            String sql = "select distinct agent_id from csms_agent_account where user_id = ?";
            
            List<String> agencies = csms.queryForList(sql, new Object[] {userId}, String.class);
            
            if (agencies.size() > 1)
            {
                System.out.println(MessageFormat.format("Creator {0} is not unique agency.", userId));
                throw new IllegalStateException();
            }
            else if (agencies.size() == 1)
            {
                id = agencies.get(0);
            }
            
            if (StringUtils.isEmpty(id))
            {
                id = "";
            }
            
            userAgencyMappings.put(userId, id);
        }
        
        return StringUtils.isEmpty(id) ? null : id;
    }
    
    private String getTargetAgencyId(String syncKey)
    {
        if (StringUtils.isEmpty(syncKey))
        {
            return null;
        }
        
        String id = agencyMappings.get(syncKey);
        
        if (null == id)
        {
            id = ghealth.queryForObject("select id from ghealth_agency where SYNC_KEY = ?", new Object[] {syncKey}, String.class);
            
            if (StringUtils.isEmpty(id))
            {
                id = "";
            }
            
            agencyMappings.put(syncKey, id);
        }
        
        return StringUtils.isEmpty(id) ? null : id;
    }
}
