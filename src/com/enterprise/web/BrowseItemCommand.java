package com.enterprise.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enterprise.beans.ItemBean;
import com.enterprise.dao.DataAccessException;
import com.enterprise.dao.ItemDAO;
import com.enterprise.dao.support.ItemDAOImpl;

public class BrowseItemCommand implements Command {
	private static ItemDAO itemDAO;
	
	public BrowseItemCommand() {
		itemDAO = new ItemDAOImpl();
	}
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		try {
			int itemID = Integer.parseInt(request.getParameter("item"));
			System.out.println(request.getParameter("item"));
			ItemBean item = itemDAO.getItemById(itemID);
			
			String specialMsg = null;
			if (item.getBidAccepted() == 1) {
				specialMsg = "You have already accepted this bid";
			} else if (item.getBidAccepted() == 2) {
				specialMsg = "You have already rejected this bid";
			}
			
			System.out.println("reserve: " + item.getReservePrice().getPrice());
			request.setAttribute("item", item);
			request.setAttribute("specialMsg", specialMsg);
			return "/displayItem.jsp";
			
			
		}  catch (NumberFormatException e) {
			e.printStackTrace();
			String msg = "Item doesnt exist (id is invalid)";
			request.setAttribute("msg", msg);
			return "/displayMsg.jsp";
		} catch(DataAccessException e) {
			e.printStackTrace();
			return "/welcome.jsp";
		}
		
		//return null;
	}

}
