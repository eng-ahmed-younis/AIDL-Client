package com.printer.vendor.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReceiptRequest(
    val receiptNo: String,
    val customerName: String,
    val totalAmount: Double,
    val lines: List<String>
) : Parcelable