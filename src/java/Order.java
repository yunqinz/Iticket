/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Cheng Guo
 */
@WebServlet(urlPatterns = {"/Order"})
public class Order extends HttpServlet {

    private Connection conn;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws javax.mail.MessagingException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MessagingException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */

            HttpSession userNameSession = request.getSession(true);
            String row = request.getParameter("row");
            String seat = request.getParameter("seat");
            String id = row + seat;
            String user = (String) userNameSession.getAttribute("currentUser");
            try {
                Class.forName("com.mysql.jdbc.Driver");
                try {
                    //conn = DriverManager.getConnection("jdbc:odbc:iTicket", "root", "");
                    conn = DriverManager.getConnection("jdbc:mysql://localhost/users", "root", "");
                    // set the preparedstatement parameters
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE tickets SET booked = ?, username = ? WHERE id =?")) {
                        // set the preparedstatement parameters
                        ps.setString(1, "Yes");
                        ps.setString(2, user);
                        ps.setString(3, id);
                        ps.executeUpdate();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Recipient's email ID needs to be mentioned.
            final String username = "iticketclemson@gmail.com";
            final String password = "fifa2003";

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {

                HttpSession userSession = request.getSession(true);
                String receiver = (String) userSession.getAttribute("currentUser");
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("iticketclemson@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiver));
                message.setSubject("Order Information");
                message.setText("Dear Student,"
                        + "\n You have successfully booked a ticket for the following event:"
                        + "\n Date: 11/29"
                        + "\n Event: Clemson versus South Carolina"
                        + "\n Time: TBD"
                        + "\n Section: E"
                        + "\n Row: " + row
                        + "\n Seat: " + seat
                        + "\n Please bring your student ID with you at the game day to enter into the stadium");
                Transport.send(message);

                out.println("Done");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (MessagingException ex) {
            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (MessagingException ex) {
            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
