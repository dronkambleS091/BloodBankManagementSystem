
package com.bloodbank.db;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DonorListServlet")
public class DonorListServlet extends HttpServlet {

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String search = request.getParameter("search");
        String term = search == null ? "" : search.trim();

        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<title>Registered Donors</title>");

            out.println("<style>");
            out.println("*{box-sizing:border-box;}");
            out.println("body{font-family:Arial,sans-serif;background:#f4f6f9;padding:25px;color:#333;}");
            out.println("h1{text-align:center;color:#b30000;}");
            out.println(".search-box{text-align:center;margin:20px 0;}");
            out.println("input{padding:11px;width:300px;max-width:90%;border:1px solid #ccc;border-radius:5px;}");
            out.println("button{padding:11px 16px;background:#b30000;color:white;border:none;border-radius:5px;cursor:pointer;}");
            out.println("button:hover{background:#7f0000;}");
            out.println(".table-wrap{overflow-x:auto;background:white;padding:15px;border-radius:10px;}");
            out.println("table{width:100%;border-collapse:collapse;}");
            out.println("th,td{padding:10px;border:1px solid #ddd;text-align:left;}");
            out.println("th{background:#b30000;color:white;}");
            out.println("tr:nth-child(even){background:#f2f2f2;}");
            out.println("a{color:#b30000;}");
            out.println(".clear{margin-left:10px;}");
            out.println("</style></head><body>");

            out.println("<h1>Registered Donors</h1>");

            out.println("<form class='search-box' method='get' action='DonorListServlet'>");
            out.println("<input type='text' name='search' placeholder='Search by name or blood group' value='"
                    + escapeHtml(term) + "'>");
            out.println(" <button type='submit'>Search</button>");
            out.println(" <a class='clear' href='DonorListServlet'>Clear</a>");
            out.println("</form>");

            out.println("<p><a href='index.html'>← Back to Home</a></p>");
            out.println("<div class='table-wrap'>");
            out.println("<table><thead><tr>");
            out.println("<th>ID</th><th>Name</th><th>Age</th>");
            out.println("<th>Gender</th><th>Blood Group</th>");
            out.println("<th>Phone</th><th>Email</th><th>Address</th>");
            out.println("</tr></thead><tbody>");

            String sql = "SELECT * FROM donors "
                    + "WHERE (? = '' OR full_name LIKE ? OR blood_group LIKE ?) "
                    + "ORDER BY donor_id DESC";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, term);
                ps.setString(2, "%" + term + "%");
                ps.setString(3, "%" + term + "%");

                try (ResultSet rs = ps.executeQuery()) {

                    boolean found = false;

                    while (rs.next()) {
                        found = true;

                        out.println("<tr>");
                        out.println("<td>" + rs.getInt("donor_id") + "</td>");
                        out.println("<td>" + escapeHtml(rs.getString("full_name")) + "</td>");
                        out.println("<td>" + rs.getInt("age") + "</td>");
                        out.println("<td>" + escapeHtml(rs.getString("gender")) + "</td>");
                        out.println("<td>" + escapeHtml(rs.getString("blood_group")) + "</td>");
                        out.println("<td>" + escapeHtml(rs.getString("phone")) + "</td>");
                        out.println("<td>" + escapeHtml(rs.getString("email")) + "</td>");
                        out.println("<td>" + escapeHtml(rs.getString("address")) + "</td>");
                        out.println("</tr>");
                    }

                    if (!found) {
                        out.println("<tr><td colspan='8'>No matching donors found.</td></tr>");
                    }
                }

            } catch (SQLException e) {
                log("Error retrieving donor records", e);
                out.println("<tr><td colspan='8'>Unable to load donor records. Please check the database connection.</td></tr>");
            }

            out.println("</tbody></table></div>");
            out.println("</body></html>");
        }
    }
}