package com.printer.vendor;

interface IPrinterService {
    boolean isPrinterConnected();
    void printReceipt(String text);
}
