package com.printer.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.printer.vendor.IPrinterService

class PrinterClient(
    private val context: Context
) {

    private var printerService: IPrinterService? = null
    private var isBound = false
    private var onServiceBound: (() -> Unit)? = null


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            printerService = IPrinterService.Stub.asInterface(service)
            isBound = true

            val connected = printerService?.isPrinterConnected() ?: false
            Log.d("PrinterClient", "Printer connected: $connected")
            onServiceBound?.invoke()
            onServiceBound = null
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            printerService = null
            isBound = false
        }

    }


    fun bindService(onBound: (() -> Unit)? = null) {
        this.onServiceBound = onBound
        val intent = Intent().apply {
            action = "com.printer.vendor.PRINTER_SERVICE"
            setPackage("com.printer.vendor")
        }

        context.bindService(
            intent,
            connection,
            Context.BIND_AUTO_CREATE
        )

    }

    fun printReceipt() {
        if (!isBound) return

        printerService?.printReceipt(
            """
            SHIFT Booking Receipt
            Booking ID: 12345
            Car: Toyota Camry
            Total: 250 SAR
            """.trimIndent()
        )
    }

    fun isServiceBound(): Boolean = isBound
    fun unbindPrinterService() {
        if (isBound) {
            context.unbindService(connection)
            isBound = false
        }
    }


}

/***
 * Customer App calls bindService()
 *         ↓
 * Android looks for service in com.printer.vendor
 *         ↓
 * PrinterRemoteService.onBind() is called
 *         ↓
 * Service returns IBinder
 *         ↓
 * Customer App receives IBinder in onServiceConnected()
 *         ↓
 * Customer App converts IBinder to IPrinterService
 *         ↓
 * Customer App calls printReceipt()
 *         ↓
 * Printer App prints through Bluetooth/Wi-Fi/USB
 *
 * */