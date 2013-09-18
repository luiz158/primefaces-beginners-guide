package com.packtpub.techbuzz.repositories.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.packtpub.techbuzz.entities.User;
import com.packtpub.techbuzz.repositories.UserRepository;
import com.packtpub.techbuzz.repositories.rowmappers.UserRowMapper;

/**
 * @author Siva
 *
 */
@Repository
public class UserRepositoryImpl implements UserRepository
{
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public User login(String loginId, String pwd)
	{
		String sql = "SELECT * FROM USERS  WHERE (USERNAME=? OR EMAIL_ID=?) AND PASSWORD=? AND (DISABLED IS NULL OR DISABLED=0)";
		Object[] args = new Object[]{loginId,loginId, pwd};
		List<User> users = jdbcTemplate.query(sql, args, new UserRowMapper());
		if(users != null && !users.isEmpty()){
			return users.get(0);
		}
		return null;
	}

	@Override
	public User findByUserName(String userName)
	{
		String sql = "SELECT * FROM USERS WHERE USERNAME=?";
		Object[] args = new Object[]{userName};
		List<User> users = jdbcTemplate.query(sql, args, new UserRowMapper());
		if(users != null && !users.isEmpty()){
			return users.get(0);
		}
		return null;
	}

	@Override
	public User findByEmailId(String email)
	{
		String sql = "SELECT * FROM USERS WHERE EMAIL_ID=?";
		Object[] args = new Object[]{email};
		List<User> users = jdbcTemplate.query(sql, args, new UserRowMapper());
		if(users != null && !users.isEmpty()){
			return users.get(0);
		}
		return null;
	}
	
	@Override
	public User create(final User user)
	{
		final String sql = "INSERT INTO USERS (EMAIL_ID,USERNAME,PASSWORD,FIRSTNAME,LASTNAME,GENDER,PHONE,DOB,BIO,DISABLED)"+
				 	 		" VALUES (?,?,?,?,?,?,?,?,?,?)";
		
		KeyHolder holder = new GeneratedKeyHolder();

		jdbcTemplate.update(new PreparedStatementCreator() {           
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getEmailId());
                    ps.setString(2, user.getUserName());
                    ps.setString(3, user.getPassword());
                    ps.setString(4, user.getFirstName());
                    ps.setString(5, user.getLastName());
                    ps.setString(6, user.getGender());
                    ps.setString(7, user.getPhone());
                    if(user.getDob() != null)
                    {
                    	ps.setDate(8, new java.sql.Date(user.getDob().getTime()));
                    }else {
                    	ps.setDate(8, null);
                    }
                    ps.setString(9, user.getBio());
                    ps.setBoolean(10, user.getDisabled());
                    return ps;
                }
            }, holder);

		int newUserId = holder.getKey().intValue();
		user.setId(newUserId);
		return user;
	}
	
	@Override
	public int changePassword(String loginId, String oldPwd, String newPwd) {
		String sql = "UPDATE USERS SET PASSWORD=? WHERE (EMAIL_ID=? OR USERNAME=?) AND PASSWORD=?";
		Object[] args = new Object[]{newPwd, loginId, loginId, oldPwd};
		return jdbcTemplate.update(sql, args);
	}

	@Override
	public void update(User user) {
		final String sql = "UPDATE USERS SET FIRSTNAME=?,LASTNAME=?,GENDER=?,PHONE=?,DOB=?,DISABLED=?, BIO=? WHERE EMAIL_ID=?";
		
		Object[] args = {
				user.getFirstName(),
				user.getLastName(),
				user.getGender(),
				user.getPhone(),
				user.getDob(),
				user.getDisabled(),
				user.getBio(),
				user.getEmailId()
		};
		jdbcTemplate.update(sql, args);
		
	}

	@Override
	public List<User> findAll() {
		String sql = "SELECT * FROM USERS";
		List<User> users = jdbcTemplate.query(sql, new UserRowMapper());
		return users;
	}
	
	@Override
	public User findById(Integer key) {
		String sql = "SELECT * FROM USERS WHERE USER_ID=?";
		List<User> users = jdbcTemplate.query(sql, new Object[]{key}, new UserRowMapper());
		if(!users.isEmpty()){
			return users.get(0);
		}
		return null;
	}



	@Override
	public void delete(Integer key) {
		
	}

	@Override
	public void updateUsersStatus(List<User> users, String status) {
		final String sql = "UPDATE USERS SET DISABLED=? WHERE EMAIL_ID=?";
		boolean disabled = (status != null && "DISABLED".equalsIgnoreCase(status));
		for (User user : users) 
		{
			Object[] args = {					
					disabled,					
					user.getEmailId()
			};
			jdbcTemplate.update(sql, args);
		}
				
	}

}


