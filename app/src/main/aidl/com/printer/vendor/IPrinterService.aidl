package com.printer.vendor;

import com.printer.vendor.model.ReceiptRequest;

interface IPrinterService {
    boolean isPrinterConnected();
    void printReceipt(in ReceiptRequest request);
}
// in means: -> Data goes from client → server