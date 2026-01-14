package com.antiscalping.tickets.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Component
public class QRCodeGenerator {
    
    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;
    
    public String generateQRCode(String data) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();
        
        return Base64.getEncoder().encodeToString(imageData);
    }
    
    public String generateTicketQR(String ticketNumber, String eventName) throws WriterException, IOException {
        String qrData = String.format("TICKET:%s|EVENT:%s|TIME:%d", ticketNumber, eventName, System.currentTimeMillis());
        return generateQRCode(qrData);
    }
}
