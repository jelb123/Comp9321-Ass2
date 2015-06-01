package com.enterprise.dao.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.enterprise.beans.AddressBean;
import com.enterprise.beans.UserBean;
import com.enterprise.common.DBConnectionFactory;
import com.enterprise.common.ServiceLocatorException;
import com.enterprise.dao.DataAccessException;
import com.enterprise.dao.UserDAO;

public class UserDAOImpl implements UserDAO{
	
	private DBConnectionFactory services;
	
	public UserDAOImpl() {
		try {
			services = new DBConnectionFactory();
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		}
	}
	
	public UserDAOImpl(DBConnectionFactory services) {
		this.services = services;
	}

	@Override
	public void insert(UserBean userBean) throws DataAccessException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
				"insert into TBL_USERS (username, password, email, nickname, " +
				"firstname, lastname, date_of_birth, street_address, city, " + 
				"state, country, postcode, credit_card_number, account_state, admin)" +
				" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			ps.setString(1, userBean.getUsername());
			ps.setString(2, userBean.getPassword());
			ps.setString(3, userBean.getEmail());
			ps.setString(4, userBean.getNickname());
			ps.setString(5, userBean.getFirstName());
			ps.setString(6, userBean.getLastName());
			ps.setString(7, userBean.getDateOfBirth());
			ps.setString(8, userBean.getAddress().getStreetAddress());
			ps.setString(9, userBean.getAddress().getCity());
			ps.setString(10, userBean.getAddress().getState());
			ps.setString(11, userBean.getAddress().getCountry());
			ps.setInt(12, userBean.getAddress().getPostCode());
			ps.setInt(13, userBean.getCreditCardNumber());
			ps.setInt(14, userBean.getAccountState());
			ps.setBoolean(15, userBean.getIsAdmin());
			
			
			int rows = ps.executeUpdate();
			if (rows < 1) 
				throw new DataAccessException("UserBean: " + userBean + " not inserted");
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
			throw new DataAccessException("UserBean: " + userBean + " not inserted");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataAccessException("UserBean: " + userBean + " not inserted");
		} finally {
			close(con);
			close(ps);
		}
		
	}

	@Override
	public void delete(int id) throws DataAccessException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
				"delete from TBL_USERS where id=?");
			ps.setInt(1, id);
			int rows = ps.executeUpdate();
			if (rows < 1) 
				throw new DataAccessException("UserBean: " + id + " not deleted");
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(ps);
			close(con);
		}
	}

	@Override
	public UserBean findByLoginDetails(String username, String password) throws DataAccessException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = services.createConnection();
			stmt = con.prepareStatement("select * from TBL_USERS where username=? and password=?");
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				UserBean user = createUserBean(rs);
				stmt.close(); 
				rs.close();
				return user;
			}
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to retrieve connection; " + e.getMessage(), e);
		} catch (SQLException e) {
			throw new DataAccessException("Unable to execute query; " + e.getMessage(), e);
		} finally {
			close(con);
			close(stmt);
		}
		return null;
	}
	
	public List<UserBean> findAllUsers() throws DataAccessException {
		List<UserBean> list = new ArrayList<UserBean>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
					"select * from TBL_USERS");
			rs = ps.executeQuery();
			while (rs.next())
				list.add(createUserBean(rs));
		} catch (SQLException e) {
			throw new DataAccessException("Unable to find all users", e);
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to locate connection", e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		return list;
	}
	
	public UserBean findUserByID(int id) throws DataAccessException {
		UserBean user = null;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement("select * from TBL_USERS where id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (!rs.next())
				return null;
			user = createUserBean(rs);
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to find user from database; " + e.getMessage(), e);
		} catch (SQLException e) {
			throw new DataAccessException("SQLException while finding user;" + e.getMessage(), e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		return user;
	}
	
	public UserBean findUserByUsername(String username) throws DataAccessException {
		UserBean user = null;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			con = services.createConnection();
			ps = con.prepareStatement("select * from TBL_USERS where username = ?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			if (!rs.next())
				return null;
			user = createUserBean(rs);			
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to find user from database; " + e.getMessage(), e);
		} catch (SQLException e) {
			throw new DataAccessException("SQLException while finding user;" + e.getMessage(), e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		return user;
	}
	
	public void updateUserState(int id, int newState) throws DataAccessException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
				"update TBL_USERS set account_state=? where id=?");
			ps.setInt(1, newState);
			ps.setInt(2, id);
			int rows = ps.executeUpdate();
			if (rows < 1) 
				throw new DataAccessException("User State: " + id + " not updated");
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(ps);
			close(con);
		}
	}
	
	public void updateUserRecord(UserBean user) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
				"update TBL_USERS set password = ?, email = ?, nickname = ?, " +
				"firstname = ?, lastname = ?, date_of_birth = ?, street_address = ?, city = ?, " + 
				"state = ?, country = ?, postcode = ?, credit_card_number = ? where id=?");
			
			ps.setString(1, user.getPassword());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getNickname());
			ps.setString(4, user.getFirstName());
			ps.setString(5, user.getLastName());
			ps.setString(6, user.getDateOfBirth());
			ps.setString(7, user.getAddress().getStreetAddress());
			ps.setString(8, user.getAddress().getCity());
			ps.setString(9, user.getAddress().getState());
			ps.setString(10, user.getAddress().getCountry());
			ps.setInt(11, user.getAddress().getPostCode());
			ps.setInt(12, user.getCreditCardNumber());
					
			ps.setInt(13, user.getId());
			
			int rows = ps.executeUpdate();
			if (rows < 1) 
				throw new DataAccessException("User State: " + user.getId() + " not updated");
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(ps);
			close(con);
		}
	}
	
	private UserBean createUserBean(ResultSet rs) throws SQLException {
		UserBean user = new UserBean();
		AddressBean address = new AddressBean();
		
		//Grab address of user
		address.setStreetAddress(rs.getString("street_address"));
		address.setCity(rs.getString("city"));
		address.setState(rs.getString("state"));
		address.setCountry(rs.getString("country"));
		address.setPostCode(rs.getInt("postcode"));
		
		//Grab and set user details
		user.setId(rs.getInt("id"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password"));
		user.setEmail(rs.getString("email"));
		user.setNickname(rs.getString("nickname"));
		user.setFirstName(rs.getString("firstname"));
		user.setLastName(rs.getString("lastname"));
		user.setDateOfBirth(rs.getString("date_of_birth"));
		user.setAddress(address);
		user.setCreditCardNumber(rs.getInt("credit_card_number"));
		user.setIsAdmin(rs.getBoolean("admin"));
		user.setAccountState(rs.getInt("account_state"));
		
		return user;
	}
	
	private void close (Connection con) {
		try {
			if (con != null)
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void close(PreparedStatement ps) {
		try {
			if (ps != null)
				ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
