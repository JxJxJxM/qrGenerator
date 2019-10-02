package com.ana.qrGenerator.Controller;


import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.util.Optional;

import static com.google.zxing.BarcodeFormat.*;

@RestController
public class QRController
{
    private static final String QR_CODE_IMAGE_PATH = "./MyQRCode.png";
    private static final Logger logger = LoggerFactory.getLogger(QRController.class);
    @RequestMapping("/generateQR")
    @CrossOrigin(origins = "*",allowedHeaders = "*")
    public void generateQRCode(HttpServletResponse response, @RequestParam(value = "qrSize", required = false) Optional<String> qrSize, @RequestParam("qrData")String qrData) throws IOException, WriterException {

        logger.info("qrSize: {}",qrSize.orElse("not present"));
        logger.info("qrData: {}",qrData);
        Integer[] size = extractSizeFromString(qrSize.orElse("128x128"));
        ByteArrayOutputStream in = generateQRCodeImage(qrData,size[0],size[1],QR_CODE_IMAGE_PATH);

        byte[] media = in.toByteArray();
        InputStream i = new ByteArrayInputStream(media);

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        response.setHeader("x-content-type-options","nosniff");
        response.setHeader("x-frame-options","ALLOWALL");
        response.setStatus(200);

        IOUtils.copy(i, response.getOutputStream());
    }


    private Integer[] extractSizeFromString(String sizeString) throws ArrayIndexOutOfBoundsException{
        Integer[] size = new Integer[2];
        String[] stringSizes;
        stringSizes = sizeString.split("x",2);
        size[0] = Integer.valueOf(stringSizes[0]);
        size[1] = Integer.valueOf(stringSizes[1]);

        return size;
    }

    private static ByteArrayOutputStream generateQRCodeImage(String text, int width, int height, String filePath)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, QR_CODE, width, height);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "jpeg", stream);
        stream.flush();



        return  stream;


    }

}
