<!DOCTYPE html>
<html>
    <head>
        <title>Service is not available</title>
        <link rel="icon" href="images/logo_ico.png">
        <script src="resizer.js"></script>
    </head>
    <body>
        <h2>Subir un archivo</h2>
        <hr/>
        <form autocomplete="off" accept-charset="UTF-8" method="post" name="FRM_VztT0I" target="_self" action="UploadFileServlet" enctype="multipart/form-data">
            <table width="450px" cellpadding="4" cellspacing="0">
                <tr>
                    <td colspan="2" class="st1">Cargar Archivo (Tamaño Máximo 5Mb, la operación puede tardar unos minutos)</td>
                </tr>
                <tr></tr>
                <tr>
                    <td class="st3" width="200px">Tipo Documento (*):</td>
                    <td class="st4">
                        <select class="w3-input w3-border mwk-input" name="SubirArchivo" id="SubirArchivo" required style="font-weight: normal; min-width: 100px;">
                            <option value="30">Foto de Perfil</option>
                            <option value="31">Soporte Estudios</option>
                            <option value="32">Soporte Experiencia Laboral</option>
                            <option value="33">Otros Documentos</option>
                            <option value="49">Hoja de Vida</option>
                            <option value="50">Soporte Seguridad Social</option>
                            <option value="" selected>--------------</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="st3" width="200px">Seleccione el Archivo:</td>
                    <td class="st4"><input type="file" id="archivo" name="archivo" onchange='loadFiles("archivo");' size="40" multiple /></td>
                </tr>
                <tr>
                    <td class="st3" width="200px">Descripción (*):</td>
                    <td class="st4"><textarea name="descripcionArchivo" cols="50" rows="3" required></textarea></td>
                </tr>
                <tr>
                    <td class="st3" colspan="2" align="right"><button type="submit">Subir</button></td>
                </tr>
            </table>
            <input type="hidden" name="to_do" value="subirArchivo" /><input type="hidden" name="KF_TOKEN" value="Dp4une21mKDzSDgY" /><input type="hidden" name="IDX" value="1" />
            <input type="hidden" name="IDS" value="REG_INFOCV" />
        </form>
        <hr/>
        <a href="index.jsp">Volver</a>
    </body>
</html>
