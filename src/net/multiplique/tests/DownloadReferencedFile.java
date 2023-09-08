package net.multiplique.tests;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/BajarArchivo", asyncSupported = true)
public class DownloadReferencedFile extends HttpServlet {

    private static final long serialVersionUID = 6474504805277L;

//    private final static FileManager fileManager = new S3FileManager();
    private final static FileManager fileManager = new LocalFileManager();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        FileReference fr = new FileReference();
//        fr.id = "prueba.jpg";
//        fr.ruta = "test/";
        fr.id = "sakura.jpeg";
        fr.ruta = "/home/desarrollo/Downloads/";

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fr.id);

        try (ServletOutputStream out = response.getOutputStream()) {
            try (InputStream fileIn = fileManager.read(fr.ruta + fr.id)) {
                byte[] outputByte = new byte[1024];
                int readLen = 0;
                while ((readLen = fileIn.read(outputByte)) > 0) {
                    out.write(outputByte, 0, readLen);
                }
            }
        } catch (IOException ex) {
            System.out.println("" + ex);
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
