package com.printer.vendor.model;

parcelable ReceiptRequest;

/*
 * Because the AIDL compiler does not read your Kotlin class directly.
 *
 * Your Kotlin Parcelable class is used by the Android runtime.
 *
 * But this AIDL file:
 *
 * package com.printer.vendor.model;
 *
 * parcelable ReceiptRequest;
 *
 * is used by the AIDL compiler.
 *
 * It tells AIDL:
 *
 * "There is a Parcelable type called ReceiptRequest.
 * You are allowed to use it inside IPrinterService.aidl."
 */
