package com.printer.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.printer.client.ui.theme.AIDLClientTheme
import com.printer.vendor.model.ReceiptRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIDLClientTheme {
                val context = LocalContext.current
                val printerClient = remember { PrinterClient(context = context) }

                DisposableEffect(Unit) {
                    printerClient.bindService {
                        val request = ReceiptRequest(
                            receiptNo = "12345",
                            customerName = "Ahmed Ali",
                            totalAmount = 250.0,
                            lines = listOf(
                                "SHIFT Booking Receipt",
                                "Car: Toyota Camry",
                                "Total: 250 SAR"
                            )
                        )
                        printerClient.printReceipt(request)
                    }

                    onDispose {
                        printerClient.unbindPrinterService()
                    }
                }
            }
        }
    }
}

