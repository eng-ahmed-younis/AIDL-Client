package com.printer.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.printer.vendor.IPrinterService
import com.printer.vendor.model.ReceiptRequest

/**
 * A client-side wrapper to manage the AIDL connection to the Printer Service.
 * This class handles binding, unbinding, and calling remote methods on the service.
 */
class PrinterClient(
    private val context: Context
) {

    // The interface generated from IPrinterService.aidl
    private var printerService: IPrinterService? = null
    private var isBound = false
    private var onServiceBound: (() -> Unit)? = null

    /**
     * ServiceConnection callback to handle the lifecycle of the connection.
     */
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            // Convert the raw IBinder into the IPrinterService interface
            printerService = IPrinterService.Stub.asInterface(service)
            isBound = true

            // Example of calling a remote method immediately after connection
            val connected = printerService?.isPrinterConnected() ?: false
            Log.d("PrinterClient", "Printer connected: $connected")
            
            // Notify the caller that the service is ready
            onServiceBound?.invoke()
            onServiceBound = null
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // This is called when the connection to the service has been unexpected lost,
            // such as when the process hosting the service has crashed.
            printerService = null
            isBound = false
        }
    }

    /**
     * Initiates binding to the remote service.
     * @param onBound Optional callback to execute when the connection is established.
     */
    fun bindService(onBound: (() -> Unit)? = null) {
        this.onServiceBound = onBound
        val intent = Intent().apply {
            // The action must match the <intent-filter> in the Printer App's AndroidManifest.xml
            action = "com.printer.vendor.PRINTER_SERVICE"
            // Explicitly set the package name for security and to target the correct app
            setPackage("com.printer.vendor")
        }

        context.bindService(
            intent,
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    /**
     * Sends a receipt print request to the remote service.
     * @param request The parcelable receipt data.
     */
    fun printReceipt(request: ReceiptRequest) {
        if (!isBound) {
            Log.e("PrinterClient", "Cannot print: Service not bound")
            return
        }

        try {
            // Remote call - this might throw RemoteException if the service crashes
            printerService?.printReceipt(request)
        } catch (e: Exception) {
            Log.e("PrinterClient", "Remote call failed", e)
        }
    }

    /**
     * Returns true if the service is currently bound and ready to use.
     */
    fun isServiceBound(): Boolean = isBound

    /**
     * Cleans up the connection. Should be called when the activity or component is destroyed.
     */
    fun unbindPrinterService() {
        if (isBound) {
            context.unbindService(connection)
            isBound = false
            printerService = null
        }
    }
}

/***
 * 1. Server app installed
 *    ↓
 * PackageManager reads manifest
 *    ↓
 * Stores service + intent-filter info
 *
 * 2. Client calls bindService(intent)
 *    ↓
 * AMS receives binding request
 *    ↓
 * AMS asks PackageManager to resolve service
 *
 * 3. PackageManager finds matching service
 *    ↓
 * com.printer.vendor.PrinterRemoteService
 *
 * 4. AMS checks security
 *    ↓
 * exported / permission / package
 *
 * 5. AMS starts server process if needed
 *    ↓
 * Server Service created
 *
 * 6. Server onBind() called
 *    ↓
 * Server returns IPrinterService.Stub Binder
 *
 * 7. Client onServiceConnected() called
 *    ↓
 * Client receives IBinder
 *
 * 8. Client calls AIDL method
 *    ↓
 * Proxy → Parcel → Binder driver → Stub
 *
 * 9. Server receives method call
 *    ↓
 * printReceipt(request)
 *
 *
 * */