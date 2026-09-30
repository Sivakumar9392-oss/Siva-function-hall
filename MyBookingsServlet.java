package com.hall;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/mybookings")
public class MyBookingsServlet extends HttpServlet {

    // Search bookings by phone number
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String phone = req.getParameter("phone");
        List<String[]> list = new ArrayList<>();
        if (phone != null && !phone.isEmpty()) {
            try (Connection c = DBUtil.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                   "SELECT b.id,h.name,b.event_date,b.guests,b.status FROM bookings b " +
                   "JOIN halls h ON h.id=b.hall_id WHERE b.phone=? ORDER BY b.event_date")) {
                ps.setString(1, phone);
                ResultSet rs = ps.executeQuery();
                while (rs.next())
                    list.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3),
                                          rs.getString(4), rs.getString(5)});
            } catch (Exception e) { req.setAttribute("msg", "Error: " + e.getMessage()); }
        }
        req.setAttribute("list", list);
        req.setAttribute("phone", phone == null ? "" : phone);
        req.getRequestDispatcher("/bookings.jsp").forward(req, res);
    }

    // Cancel a booking
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String phone = req.getParameter("phone");
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
               "UPDATE bookings SET status='CANCELLED' WHERE id=? AND phone=?")) {
            ps.setInt(1, Integer.parseInt(req.getParameter("id")));
            ps.setString(2, phone);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        res.sendRedirect("mybookings?phone=" + phone);
    }
}
