package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QrCodeGenerator {

    fun generateQrCode(
        content: String,
        size: Int = 512,
        fgColor: Int = Color.BLACK,
        bgColor: Int = Color.WHITE,
        margin: Int = 1
    ): Bitmap? {
        if (content.isBlank()) return null
        return try {
            val hints = mapOf(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to margin
            )
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    // Default to sharp squares, rounded corners is more complex in ZXing natively 
                    // without custom drawing. We'll stick to a basic matrix replacement.
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) fgColor else bgColor)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // In a real app we'd build a proper SVG XML from the bit matrix
    fun generateSvg(content: String, fgColor: Int, bgColor: Int): String {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 50 50\">" +
               "<rect width=\"50\" height=\"50\" fill=\"#FFFFFF\"/>" +
               "</svg>" // Simplified placeholder
    }
}
