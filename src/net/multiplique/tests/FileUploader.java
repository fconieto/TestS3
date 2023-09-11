package net.multiplique.tests;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploader {

    private static final Logger logger = Logger.getLogger(FileUploader.class.getName());

    private final static FileManager fileManager = new S3FileManager();
    // private final static FileManager fileManager = new LocalFileManager();
    
    private FileUploader() {

    }

    public static void uploadFile(HttpServletRequest request, String entityPath) {
        try {
            FileItemFactory fileFactory = new DiskFileItemFactory();
            ServletFileUpload servletUp = new ServletFileUpload(fileFactory);
            java.util.List<FileItem> items = servletUp.parseRequest(request);

            if (items != null && !items.isEmpty()) {
                FileReference fr = new FileReference();

                /* Crea la estructura de carpetas*/
                String path = entityPath.replace(" ", "_").replace("-", "_").replace(":", "_").replace(".", "_");
                File files = new File(path);
                if (!files.exists()) {
                    files.mkdirs();
                }
                fr.ruta = path;

                String PRE = "EXAMPLE_";
                String idObject = "100";

                fr.id = PRE + idObject.replace(":", "_") + "_" + Calendar.getInstance().getTimeInMillis();
                fr.type = "pdf";

                fr.idEntidad = idObject;

                for (FileItem item : items) {
                    if (item.isFormField()) {
                        if (item.getFieldName().equals("descripcionArchivo")) {
                            fr.comentarios = item.getString("UTF-8").trim();
                        }

                        if (item.getFieldName().equals("SubirArchivo")) {
                            fr.idArchivoTipoEntidad = item.getString();
                        }

                        if (item.getFieldName().equals("idEntidad") && idObject.isEmpty()) {
                            fr.idEntidad = item.getString();
                        }

                        if (item.getFieldName().equals("idCalificacionArchivo")) {
                            fr.idCalificacionArchivo = item.getString();
                        }

                    }
                }

                for (FileItem item : items) {
                    if (!item.isFormField() && item.getFieldName().equals("archivo")) {
                        fr.id = PRE + idObject.replace(":", "_") + "_" + Calendar.getInstance().getTimeInMillis();
                        fr.type = item.getContentType();
                        String ext = item.getName();

                        int nLastPoint = ext.lastIndexOf(".");
                        if (nLastPoint > 0) {
                            ext = ext.substring(nLastPoint);
                            fr.id += ext;
                        }

                        String fileName = path + fr.id;
                        fileManager.write(item, fileName);

                        if (fr.type.equals("image/jpeg")) {
                            double factor25 = 0.25;
                            double factor50 = 0.50;

                            if (item.getSize() < 600000) {
                                factor25 = 0.50;
                                factor50 = 1;
                            }

                            if (item.getSize() < 300000) {
                                factor25 = 1;
                                factor50 = 1;
                            }
                            ImageTool it = new ImageTool(fileManager);
                            it.createStamp(fr, factor25, "R25_");
                            it.createStamp(fr, factor50, "R50_");

                            fr.reducida = "1";
                        }

//                        FileReferenceMgr.mgr.save(fr, idUser);
                    }
                }
            }

        } catch (UnsupportedEncodingException | FileUploadException ex) {
            logger.log(Level.SEVERE, "uploadFileCode", ex);
        } finally {
            request.getSession().removeAttribute("ITEMS_TO_UPLOAD");
        }
    }

}
