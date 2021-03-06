package com.enterprise.web;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enterprise.beans.ItemBean;
import com.enterprise.beans.UserBean;
import com.enterprise.business.ItemService;
import com.enterprise.business.exception.ItemServiceException;
import com.enterprise.business.support.ItemServiceImpl;

public class FindUserBids implements Command {

	private static ItemService itemService;
	
	public FindUserBids() {
		itemService = new ItemServiceImpl();
	}
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		if (request.getSession().getAttribute("user") == null) {
			String msg = "Invalid request (User not logged in)";
			request.setAttribute("msg", msg);
			return "/displayMsg.jsp";
		}
		String msg = null;
		try {
			UserBean user = (UserBean) request.getSession().getAttribute("user");
			int id = user.getId();
			List<ItemBean> allItems = itemService.getItemsByBidderID(id);	//getting all the items in the database
			List<ItemBean> items = null;
			
			
			if (allItems.size() > 10) {
				Collections.shuffle(allItems);
			    items = allItems.subList(1, 11);
			} else {
				items = allItems;
			}
			if (items == null || items.isEmpty()) {
				msg = "You have not bid on anything";
			} else {
				msg = "Items you have bidded on: ";
			}
			
			request.setAttribute("msg", msg);
			request.setAttribute("items", items);
			return "/welcome.jsp";
			
		} catch(ItemServiceException e) {
			msg = "Cant display item bids";
			request.setAttribute("msg", msg);
			e.printStackTrace();
			return "/welcome.jsp";
		}
	
		//return null;
	}

}
