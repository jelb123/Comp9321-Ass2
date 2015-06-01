package com.enterprise.dao.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.enterprise.beans.AddressBean;
import com.enterprise.beans.ItemBean;
import com.enterprise.beans.PriceBean;
import com.enterprise.beans.UserBean;
import com.enterprise.common.DBConnectionFactory;
import com.enterprise.common.ServiceLocatorException;
import com.enterprise.dao.DataAccessException;
import com.enterprise.dao.ItemDAO;

public class ItemDAOImpl implements ItemDAO {
	
	/*
	 * Initialisation variables that are needed for connected to the Database
	 */
	private DBConnectionFactory services;
	public ItemDAOImpl() {
		try {
			services = new DBConnectionFactory();
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		}
	}
	public ItemDAOImpl(DBConnectionFactory services) {
		this.services = services;
	}
	
	
	
	/*
	 * This prepares the SQL statements that will read user input and convert it into SQL insert statements
	 * into the AUCTION_ITEM table
	 */
	@Override
	public void insert(ItemBean itemBean) throws DataAccessException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
				"insert into TBL_ITEMS (ownerID, title, category, " +
				"picture, description, address, reservePrice, startPrice, " + 
				"bidIncrements, endTime, highestBid, highest_bid_user_ID, isActive, bidAccepted)" +
				" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			/*
			 * read values in from input form
			 */
			//ps.setInt(11, itemBean.getId()); the item_id is automatically generated
			
			String address = itemBean.getAddress().getCity() + ", " + itemBean.getAddress().getState() + ", " + itemBean.getAddress().getCountry();
			System.out.println(address);
			ps.setInt(1, itemBean.getOwnerID());
			ps.setString(2, itemBean.getTitle());
			ps.setString(3, itemBean.getCategory());
			ps.setString(4, itemBean.getPicture());
			ps.setString(5, itemBean.getDescription());
			//ps.setString(6, itemBean.getAddress().getStreetAddress());
			ps.setString(6, address);
			ps.setFloat(7, itemBean.getReservePrice().getPrice());
			ps.setFloat(8, itemBean.getStartPrice().getPrice());
			ps.setFloat(9, itemBean.getBidIncrements());
			ps.setTimestamp(10, itemBean.getEndTime());
			ps.setFloat(11, itemBean.getStartPrice().getPrice());
			ps.setInt(12, itemBean.getOwnerID());
			ps.setBoolean(13, true);
			ps.setInt(14, 0); 
			
			int rows = ps.executeUpdate();
			if (rows < 1) 
				throw new DataAccessException("ItemBean: " + itemBean + " not inserted");
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
			throw new DataAccessException("ItemBean: " + itemBean + " not inserted");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataAccessException("ItemBean: " + itemBean + " not inserted");
		}finally {
			close(ps);
			close(con);
		}
	}

	/*
	 * delete item that is pointed to by the item_id that was passed in
	 */
	public void delete(int id) throws DataAccessException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
				"delete from TBL_ITEMS where item_id=?");
			ps.setInt(1, id);
			int rows = ps.executeUpdate();
			if (rows < 1) 
				throw new DataAccessException("ItemBean: " + id + " not deleted");
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(ps);
			close(con);
		}
	}
	
	
	/*
	 * output the whole list of items in the database
	 */
	public List<ItemBean> showAllItems() throws DataAccessException {
		List<ItemBean> list = new ArrayList<ItemBean>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
					"select * from TBL_ITEMS");
			rs = ps.executeQuery();
			while (rs.next())
				list.add(createItemBean(rs));
		} catch (SQLException e) {
			throw new DataAccessException("Unable to find all items", e);
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to locate connection", e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		return list;
	}
	
	public List<ItemBean> getActiveItems() throws DataAccessException {
		List<ItemBean> list = new ArrayList<ItemBean>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
					"select * from TBL_ITEMS where isActive = ?");	
			ps.setBoolean(1, true);
			rs = ps.executeQuery();
			while (rs.next())
				list.add(createItemBean(rs));
		} catch (SQLException e) {
			throw new DataAccessException("Unable to find all items", e);
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to locate connection", e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		return list;
		
	}
	/*
	 * Finds and returns the itemBean that contains "string/catagory/desc"
	 */
	public List<ItemBean> findItemByString(String searchString){
		/*
		int i, found;
		List<ItemBean> list = new ArrayList<ItemBean>();
		ItemBean item = new ItemBean();
		
		//for now just get a list of all the items and iterate through it
		list = showAllItems();												
		for (i = 0, found = 0; i < list.size(); i++){
			item = list.get(i);
			
			//check all substrings
			if(item.getTitle().contains(searchString) ){
				found = 1;
			}
			if(item.getCategory().contains(searchString)){
				found = 1;
			}
			if(item.getDescription().contains(searchString)){
				found = 1;
			}
			if(found == 0){
				//remove the item from the list if it wasn't found
				list.remove(i);
				i--;
			}
		}
		*/
		
		List<ItemBean> list = new ArrayList<ItemBean>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = services.createConnection();
			String wildCard = "%";
			searchString = wildCard + searchString + wildCard;
			//CHECK IF CORRECT %?% might throw errors hard here!!
			ps = con.prepareStatement(
					"select * from TBL_ITEMS where lower(title) like lower(?) or lower(description) like lower(?) or lower(category) like lower(?)");
			ps.setString(1, searchString);
			ps.setString(2, searchString);
			ps.setString(3, searchString);
			
			rs = ps.executeQuery();
			while (rs.next())
				list.add(createItemBean(rs));
		} catch (SQLException e) {
			throw new DataAccessException("Unable to find all items", e);
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to locate connection", e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		
		return list;
	}
	
	
	
	/*
	 * ADVANCED SEARCH!
	 * we don't "OR" in there "where sql statement" since it's a very narrowed search
	 */
	public List<ItemBean> advancedSearch(String name, String desc, String category, String addr, float start_price){
		
		List<ItemBean> list = new ArrayList<ItemBean>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = services.createConnection();
			
			String wildCard = "%";
			name 		= wildCard + name + wildCard;
			desc 		= wildCard + desc + wildCard;
			category 	= wildCard + category + wildCard;
			addr 		= wildCard + addr + wildCard;
			
			ps = con.prepareStatement(
					"select * from TBL_ITEMS where lower(title) like lower(?) and lower(description) like lower(?) and lower(category) like lower(?) "
					+ "and lower(address) like lower(?) and highestBid < ? and highestBid > ?" );
			ps.setString(1, name);
			ps.setString(2, desc);
			ps.setString(3, category);
			ps.setString(4, addr);
			ps.setFloat(5, start_price + 10);
			ps.setFloat(6, start_price - 10);
			
			rs = ps.executeQuery();
			while (rs.next())
				list.add(createItemBean(rs));
		} catch (SQLException e) {
			throw new DataAccessException("Unable to find all items", e);
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to locate connection", e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		
		return list;
	}
	
	public List<ItemBean> getItemsByUserID(int id) throws DataAccessException {
		List<ItemBean> list = new ArrayList<ItemBean>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
					"select * from TBL_ITEMS where ownerID = ?");	
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while (rs.next())
				list.add(createItemBean(rs));
		} catch (SQLException e) {
			throw new DataAccessException("Unable to find all items", e);
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to locate connection", e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		return list;
		
	}	
	
	public List<ItemBean> getItemsByBidderID(int id) throws DataAccessException {
		List<ItemBean> list = new ArrayList<ItemBean>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ItemBean item = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
					"select * from TBL_ITEMS where highest_bid_user_ID = ?");	
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				item = createItemBean(rs);
				if (item.getOwnerID() != item.getHighestBidUserID()) {
					list.add(item);
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException("Unable to find all items", e);
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to locate connection", e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		return list;
		
	}	
	
	public ItemBean getItemById(int id){
		ItemBean item = null;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
					"select * from TBL_ITEMS where item_id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (!rs.next())
				return null;
			item = createItemBean(rs);
		} catch (SQLException e) {
			throw new DataAccessException("Unable to find item pointed by id", e);
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to locate connection", e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		return item;
	}
	
	
	/*
	 * updating to the new highest bidder value
	 */
	public int updateBid(int item_id, float bid_value, int bidder_id){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int update = 0;
		try {
			con = services.createConnection();
			
				
			
				//update to the new database value
				ps = con.prepareStatement(
						"update TBL_ITEMS "
						+ "set highestBid = ?, highest_bid_user_ID = ? "
						+ "where item_id = ? ");
				ps.setFloat(1, bid_value);
				ps.setInt(2, bidder_id);
				ps.setInt(3, item_id);
				ps.executeUpdate();
				//flag that we have update so we return = 1
				update = 1;							
			//if it's not, do nothing and return 0;
		} catch (SQLException e) {
			throw new DataAccessException("Unable to update the bid value and user_id", e);
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to locate connection", e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		return update;						//we did not update the bid value
	}
	
	/**
	 * 
	 * @param item_id
	 * @param bid_value
	 * @param bidder_id
	 * @return
	 */
	public int updateBidAccepted(int item_id, int bidAccepted) throws DataAccessException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int update = 0;
		try {
			con = services.createConnection();
			
				
			
				//update to the new database value
				ps = con.prepareStatement(
						"update TBL_ITEMS "
						+ "set bidAccepted = ?"
						+ "where item_id = ? ");
				ps.setInt(1, bidAccepted);
				ps.setInt(2, item_id);
				ps.executeUpdate();
				//flag that we have update so we return = 1
				update = 1;							
			//if it's not, do nothing and return 0;
		} catch (SQLException e) {
			throw new DataAccessException("Unable to update the bid value and user_id", e);
		} catch (ServiceLocatorException e) {
			throw new DataAccessException("Unable to locate connection", e);
		} finally {
			close(rs);
			close(ps);
			close(con);
		}
		return update;						//we did not update the bid value
	}
	
	
	/*
	 * Halt the auction ie. adding more time to the endTime column
	 */
	public ItemBean haltAuction(int item_id){
		Connection con = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ItemBean item = null;
		try {
			con = services.createConnection();
			ps1 = con.prepareStatement(
				"update TBL_ITEMS "
				+ "set isActive = false where item_id = ? ");
			ps1.setInt(1, item_id);
			ps1.executeUpdate();
			
			ps2 = con.prepareStatement(
				"select * from TBL_ITEMS "
				+ "where item_id = ? ");
			ps2.setInt(1, item_id);
			rs = ps2.executeQuery();
			
			if (!rs.next())
				return null;
			item = createItemBean(rs);
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
			throw new DataAccessException("Couldnt locate database service");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataAccessException("Couldnt update isActive");
		} finally {
			close(rs);
			close(ps1);
			close(ps2);
			close(con);
		}
		return item;
	}
	
	
	
	
	//FUNCTIONS BELOW ARE FOR WISHLIST
	
		public void insertToWishlist(int item_id, int user_id ) throws DataAccessException{
			Connection con = null;
			PreparedStatement ps = null;
			try {
				con = services.createConnection();
				ps = con.prepareStatement(
					"insert into TBL_WISHLIST (item_id, owner_id)" +
					" values (?,?)");
				
				/*
				 * read values in from input form
				 */		
				ps.setInt(1, item_id);
				ps.setInt(2, user_id);
		
				
				int rows = ps.executeUpdate();
				if (rows < 1) 
					throw new DataAccessException("item_id: " + item_id + " not inserted in wishlist");
			} catch (ServiceLocatorException e) {
				e.printStackTrace();
				throw new DataAccessException("item_id: " + item_id + " not inserted in wishlist");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DataAccessException("item_id: " + item_id + " not inserted in wishlist");
			} finally {
				close(con);
				close(ps);
			}
		}
		
		/* 
		 * delete item from Wishlist by item_id
		 */
		public void deleteFromWishlist(int item_id) throws DataAccessException{
			Connection con = null;
			PreparedStatement ps = null;
			try {
				con = services.createConnection();
				ps = con.prepareStatement(
					"delete from TBL_WISHLIST where item_id=?");
				ps.setInt(1, item_id);
				int rows = ps.executeUpdate();
				System.out.println("number of rows: " + rows);
				if (rows < 1) 
					System.out.println("ItemBean: " + item_id + " not deleted from wishlist");
			} catch (ServiceLocatorException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				close(ps);
				close(con);
			}
		} 
		
		
		public List<ItemBean> showWishlist(int user_id) throws DataAccessException{
			List<ItemBean> wishList = new ArrayList<ItemBean>();	//for the actual wishList<ItemBean>
			List<Integer> itemsToSearch = new ArrayList<Integer>();
			int i;
			
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				con = services.createConnection();
				ps = con.prepareStatement(
						"select * from TBL_WISHLIST where owner_id = ?");
				ps.setInt(1, user_id);
				rs = ps.executeQuery();
				while (rs.next()){
					itemsToSearch.add(rs.getInt("item_id"));
				}
				
				//create iterator for loop below
				Iterator<Integer> myListIterator = itemsToSearch.iterator(); 
				
				//add ItemBeans to the actually wishList, previously we were returning only item_id's etc.
				for(i = 0; i < itemsToSearch.size() && myListIterator.hasNext(); i++){
					wishList.add(getItemById(myListIterator.next()));
				}
				
			} catch (SQLException e) {
				throw new DataAccessException("Unable to show all items in wishlist", e);
			} catch (ServiceLocatorException e) {
				throw new DataAccessException("Unable to locate connection", e);
			} finally {
				close(rs);
				close(ps);
				close(con);
			}
			return wishList;
		}
		
	
	public boolean isInWishlist(int itemID, int userID) throws DataAccessException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = services.createConnection();
			ps = con.prepareStatement(
					"select * from TBL_WISHLIST where owner_id = ? and item_id = ?");
			ps.setInt(1, userID);
			ps.setInt(2,  itemID);
			rs = ps.executeQuery();
			if (rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataAccessException("Cant execute sql", e);
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
			throw new DataAccessException("Unable to locate connection", e);
		} finally {
			close(con);
			close(ps);
			close(rs);
		}
		return false;
	}
	
	private ItemBean createItemBean(ResultSet rs) throws SQLException {
		ItemBean item = new ItemBean();
		AddressBean address = new AddressBean();
		PriceBean resPrice = new PriceBean();
		PriceBean startPrice = new PriceBean();
		
		//Grab address of item and package it up
		address.setStreetAddress(rs.getString("address"));
		item.setAddress(address);
		
		//Grab and set item details
		item.setItemID(rs.getInt("item_id"));
		item.setOwnerID(rs.getInt("ownerID"));
		item.setTitle(rs.getString("title"));
		item.setCategory(rs.getString("category"));
		item.setPicture(rs.getString("picture"));
		item.setDescription(rs.getString("description"));
		
		//Grab price of item and package to Bean
		resPrice.setPrice(rs.getFloat("reservePrice"));
		item.setReservePrice(resPrice);
		
		startPrice.setPrice(rs.getFloat("startPrice"));
		item.setStartPrice(startPrice);
		
		
		item.setBidIncrements(rs.getFloat("bidIncrements"));
		item.setEndTime(rs.getTimestamp("endTime"));
		item.setHighestBid(rs.getFloat("highestBid"));
		item.setHighestBidUserID(rs.getInt("highest_bid_user_ID"));
		item.setIsActive(rs.getBoolean("isActive"));
		item.setBidAccepted(Integer.parseInt(rs.getString("bidAccepted")));
		
		return item;
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
