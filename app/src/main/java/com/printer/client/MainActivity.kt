package com.printer.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.printer.client.ui.theme.AIDLClientTheme

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
                        printerClient.printReceipt()
                    }

                    onDispose {
                        printerClient.unbindPrinterService()
                    }
                }

                Greeting("Printer Client")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AIDLClientTheme {
        Greeting("Android")
    }
}