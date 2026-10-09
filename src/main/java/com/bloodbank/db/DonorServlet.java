package com.bloodbank.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DonorServlet")
public class DonorServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("full_name");
        String ageText = request.getParameter("age");
        String gender = request.getParameter("gender");
        String bloodGroup = request.getParameter("blood_group");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        response.setContentType("text/html;charset=UTF-8");

        try {
            int age = Integer.parseInt(ageText);

            if (age < 18 || age > 100) {
                response.sendError(400, "Age must be between 18 and 100.");
                return;
            }

            String sql = "INSERT INTO donors "
                    + "(full_name, age, gender, blood_group, phone, email, address) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, fullName);
                ps.setInt(2, age);
                ps.setString(3, gender);
                ps.setString(4, bloodGroup);
                ps.setString(5, phone);
                ps.setString(6, email);
                ps.setString(7, address);

                ps.executeUpdate();

                response.getWriter().println(
                    "<h2>Donor registered successfully!</h2>"
                    + "<a href='register-donor.html'>Register another donor</a>"
                    + "<br><a href='index.html'>Back to Home</a>"
                );
            }

        } catch (NumberFormatException e) {
            response.sendError(400, "Please enter a valid age.");
        } catch (java.sql.SQLException e) {
            throw new ServletException("Could not save donor information.", e);
        }
    }
}

