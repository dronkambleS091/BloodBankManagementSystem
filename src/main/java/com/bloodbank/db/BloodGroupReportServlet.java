
package com.bloodbank.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/BloodGroupReportServlet")
public class BloodGroupReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String sql = "SELECT blood_group, COUNT(*) AS total "
                   + "FROM donors GROUP BY blood_group";

        response.setContentType("text/html;charset=UTF-8");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            java.util.Map<String, Integer> counts =
                    new java.util.LinkedHashMap<>();

            String[] groups = {
                "A+", "A-", "B+", "B-",
                "AB+", "AB-", "O+", "O-"
            };

            for (String group : groups) {
                counts.put(group, 0);
            }

            while (rs.next()) {
                String group = rs.getString("blood_group");
                if (counts.containsKey(group)) {
                    counts.put(group, rs.getInt("total"));
                }
            }

            var out = response.getWriter();

            out.println("<!DOCTYPE html><html><head>");
            out.println("<title>Blood Group Report</title>");
            out.println("<meta charset='UTF-8'>");
            out.println("<style>");
            out.println("body{font-family:Arial;margin:30px;background:#f5f6fa}");
            out.println("h1{background:#c62828;color:white;padding:20px}");
            out.println("table{border-collapse:collapse;width:100%;background:white}");
            out.println("th,td{padding:12px;border:1px solid #ddd;text-align:center}");
            out.println("th{background:#c62828;color:white}");
            out.println("</style></head><body>");

            out.println("<h1>Blood Group Report</h1>");
            out.println("<h2>Registered Donor Summary</h2>");
            out.println("<table><tr><th>Blood Group</th>");
            out.println("<th>Number of Donors</th></tr>");

            for (String group : groups) {
                out.println("<tr><td>" + group + "</td><td>"
                        + counts.get(group) + "</td></tr>");
            }

            out.println("</table><p><a href='index.html'>Back to Home</a></p>");
            out.println("</body></html>");

        } catch (java.sql.SQLException e) {
            throw new ServletException(
                    "Could not generate the blood group report.", e);
        }
    }
}
