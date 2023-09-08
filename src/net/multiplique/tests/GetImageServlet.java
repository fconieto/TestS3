package net.multiplique.tests;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/TraerImagen", asyncSupported = true)
public class GetImageServlet extends HttpServlet {

    private static final long serialVersionUID = 6842028303135L;
    
    private final static FileManager fileManager = new S3FileManager();
    //private final static FileManager fileManager = new LocalFileManager();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        response.setContentType("image/gif");
        response.setHeader("requesttime", "" + Calendar.getInstance().getTimeInMillis());

        try (OutputStream out = response.getOutputStream()) {
            FileReference fr = new FileReference();
            
            
            fr.id = "prueba.jpg";
            fr.ruta = "test/";
            // fr.id = "prueba.jpg";
            // fr.ruta = "/home/desarrollo/Downloads/";

            Image image = fileManager.getImage(fr.ruta + fr.id); // ImageIO.read(file);
            if (image != null){               
                int heigth = 200;
                int width = 200 * image.getWidth(null) / image.getHeight(null);
                image = image.getScaledInstance(width, heigth, Image.SCALE_SMOOTH);
                new GifEncoder(image, out, true).encode();
            } else {
                BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
                Font f = new Font(Font.MONOSPACED, Font.PLAIN, 20);
                Graphics2D graphics = img.createGraphics();
                graphics.setPaint(Color.darkGray);
                graphics.fillRect(0, 0, 200, 200);
                graphics.setFont(f);
                graphics.setColor(Color.white);
                graphics.drawString("No Disponible", 20, 100);
                new GifEncoder(img, out, true).encode();
            }
        } catch (IOException e) {
            System.out.println("processRequest");
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
        processRequest(request, response);
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
        processRequest(request, response);
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
