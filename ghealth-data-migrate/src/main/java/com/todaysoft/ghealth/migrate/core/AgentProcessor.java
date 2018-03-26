package com.todaysoft.ghealth.migrate.core;

import com.todaysoft.ghealth.migrate.config.CsmsJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.Agent;
import com.todaysoft.ghealth.migrate.model.User;
import com.todaysoft.ghealth.migrate.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AgentProcessor implements Processor
{
    @Autowired
    private CsmsJdbcTemplate csms;
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void process()
    {

        List<Agent> list = getAgentUsers(getHistroyData());
        migrate(list);
    }
    
    private List<Agent> getHistroyData()
    {
        String sql = "select * from csms_agent";
        return csms.query(sql, new RowMapper<Agent>()
        {
            @Override
            public Agent mapRow(ResultSet resultSet, int i) throws SQLException
            {
                Agent data = new Agent();
                data.setId(resultSet.getString("ID"));
                data.setName(resultSet.getString("NAME"));
                data.setCode(resultSet.getString("CODE"));
                data.setAbbr(resultSet.getString("SHORT_NAME"));
                return data;
            }
        });
    }
    
    private List<Agent> getAgentUsers(List<Agent> agents)
    {
        for (Agent data : agents)
        {
            String findagentAccount = "select USER_ID from csms_agent_account where AGENT_ID = ?";
            List<String> userIds = csms.queryForList(findagentAccount, String.class, new Object[] {data.getId()});
            if (!CollectionUtils.isEmpty(userIds))
            {
                List<User> users = new ArrayList<>();
                for (String userId : userIds)
                {
                    String sql = "select * from csms_user where id = ?";
                    User user = csms.queryForObject(sql, new RowMapper<User>()
                    {
                        @Override
                        public User mapRow(ResultSet rs, int i) throws SQLException
                        {
                            User data = new User();
                            String userId = rs.getString("ID");
                            String userName = rs.getString("USERNAME");
                            data.setUserName(userName);
                            String getUserArchiveSql = "select NAME from csms_user_archive where id = ?";
                            List<String> names = csms.queryForList(getUserArchiveSql, String.class, new Object[] {userId});
                            String userArchiveName = null;
                            if (CollectionUtils.isEmpty(names))
                            {
                                userArchiveName = userName;
                            }
                            else
                            {
                                userArchiveName = names.get(0);
                            }
                            data.setId(userId);
                            data.setName(userArchiveName);
                            String disabled = rs.getString("DISABLED");
                            data.setEnable("0".equals(disabled)?"1":"0");
                            return data;
                        }
                    }, new Object[] {userId});
                    users.add(user);
                }
                data.setUsers(users);
            }
        }
        return agents;
    }
    
    private void migrate(List<Agent> agents)
    {
        for (Agent data : agents)
        {
            String agencyId = KeyGenerator.uuid();
            List<User> users = data.getUsers();
            String primaryUserName = null;
            if (!CollectionUtils.isEmpty(users))
            {
                for (int i = 0; i < users.size(); i++)
                {
                    User user = users.get(i);
                    String isPrimaryAccount = User.PA_FALSE;
                    if (i == 0)
                    {
                        isPrimaryAccount = User.PA_TRUE;
                        primaryUserName = user.getName();
                    }
                    String userId = KeyGenerator.uuid();
                    String userName = user.getUserName();
                    String password = passwordEncoder.encode(userName);
                    ghealth.update("INSERT INTO ghealth_agency_account (ID, AGENCY_ID,SYNC_KEY,NAME, USERNAME ,PASSWORD, ENABLED,PRIMARY_ACCOUNT) "
                        + "values ('" + userId + "', '" + agencyId + "', '" + user.getId() + "', '" + user.getName() + "', '" + user.getUserName() + "', '" + password
                        + "', '" + user.getEnable() + "','" + isPrimaryAccount + "')");
                }
            }
            if (CollectionUtils.isEmpty(users))
            {
                continue;
            }
            data.setAgentAccount(primaryUserName);
            ghealth.update(
                "INSERT INTO ghealth_agency(ID,NAME,ABBR,CODE,ACCOUNT_AMOUNT,AMOUNT_SIGNATURE,PRIMARY_USERNAME,PREAUTHORIZED_AMOUNT,CREATE_TIME,CREATOR_NAME,DELETED, SYNC_KEY) "
                    + "values ('" + agencyId + "', '" + data.getName() + "', '" + data.getAbbr() + "', '" + data.getCode() + "', '" + new BigDecimal(0) + "', '"
                    + "A" + "', '" + data.getAgentAccount() + "','" + new BigDecimal(0) + "','" + new Timestamp(new Date().getTime()) + "','" + "管理员" + "','"
                    + 0 + "','" + data.getId() + "')");
        }
        
    }
    
}
