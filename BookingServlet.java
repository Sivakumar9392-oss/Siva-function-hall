package com.hall;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/book")
public class BookingServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        List<String[]> halls = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,name,capacity,price FROM halls")) {
            while (rs.next())
                halls.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
        } catch (Exception e) { req.setAttribute("msg", "DB error: " + e.getMessage()); }
        req.setAttribute("halls", halls);
        req.getRequestDispatcher("/book.jsp").forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String msg;
        try (Connection c = DBUtil.getConnection()) {
            int hallId = Integer.parseInt(req.getParameter("hallId"));
            String date = req.getParameter("date");
            PreparedStatement chk = c.prepareStatement(
                "SELECT COUNT(*) FROM bookings WHERE hall_id=? AND event_date=? AND status='CONFIRMED'");
            chk.setInt(1, hallId); chk.setString(2, date);
            ResultSet rs = chk.executeQuery(); rs.next();
            if (rs.getInt(1) > 0) {
                msg = "Sorry, that hall is already booked on " + date;
            } else {
                PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO bookings (hall_id,customer_name,phone,event_date,guests) VALUES (?,?,?,?,?)");
                ps.setInt(1, hallId);
                ps.setString(2, req.getParameter("name"));
                ps.setString(3, req.getParameter("phone"));
                ps.setString(4, date);
                ps.setInt(5, Integer.parseInt(req.getParameter("guests")));
                ps.executeUpdate();
                msg = "Booking confirmed for " + date + "!";
            }
        } catch (Exception e) { msg = "Error: " + e.getMessage(); }
        req.setAttribute("msg", msg);
        doGet(req, res);
    }
}
