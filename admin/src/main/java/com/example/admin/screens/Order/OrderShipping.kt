package com.example.admin.screens.Order

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.admin.R
import com.example.admin.model.Order
import com.example.admin.model.OrderStatus
import com.example.admin.screens.Home
import com.example.admin.ui.theme.OrderTheme
import com.example.admin.ui.theme.Pink80
import com.example.admin.ui.theme.blue
import com.example.admin.ui.theme.blue2
import com.example.admin.ui.theme.blue3
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderShipping : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    var pendingList by remember { mutableStateOf(listOf<Order>()) }

                    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                    val dbPending: CollectionReference = db.collection("orders")

                    dbPending.whereIn("status", listOf("Shipping", "Shipped")).get().addOnSuccessListener { queryDocumentSnapshot ->
                        if (!queryDocumentSnapshot.isEmpty) {
                            val list = queryDocumentSnapshot.documents.mapNotNull { d ->
                                d.toObject<Order>()?.apply { id = d.id }
                            }
                            pendingList = list
                        } else {
                            Toast.makeText(
                                this@OrderShipping,
                                "No data found in Database",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@OrderShipping,
                            "Fail to get the data.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    ShippingScreen(context, pendingList)
                }
            }
        }
    }
}


@Composable
fun ShippingScreen(context: Context, pendingList: List<Order>) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { context.startActivity(Intent(context, Home::class.java)) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "",
                    tint = blue,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(55.dp))
            Text(
                text = "Order Shipping", style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }

        LazyColumn {
            items(pendingList) { order ->
                Surface(
                    color = blue2,
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = {}
                ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Row() {
                                Surface(
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(top = 3.dp),
                                    color = blue3
                                ) {
                                    order.date?.let { date ->
                                        val formattedDate = SimpleDateFormat(
                                            "dd/MM/yyyy HH:mm",
                                            Locale.getDefault()
                                        ).format(date)
                                        Text(
                                            text = formattedDate,
                                            fontSize = 12.sp,
                                            style = MaterialTheme.typography.titleSmall,
                                            modifier = Modifier.padding(
                                                vertical = 4.dp,
                                                horizontal = 8.dp
                                            ),
                                            color = Color.Black
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(top = 3.dp),
                                    color = Pink80
                                ) {
                                    order.status?.let {
                                        Text(
                                            text = it,
                                            fontSize = 12.sp,
                                            style = MaterialTheme.typography.titleSmall,
                                            modifier = Modifier.padding(
                                                vertical = 4.dp,
                                                horizontal = 8.dp
                                            ),
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${order.restaurant}",
                                modifier = Modifier.padding(top = 5.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            order.total?.let {
                                val formattedPrice = NumberFormat.getCurrencyInstance(
                                    Locale("vi", "VN")
                                ).format(it)
                                Text(
                                    text = "Total: $formattedPrice",
                                    modifier = Modifier.padding(top = 5.dp),
                                    fontSize = 20.sp,
                                )
                            }
                            Text(
                                text = "Customer: ${order.custumerName}",
                                modifier = Modifier.padding(top = 5.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            IconRowWithDashedLine(LocalContext.current,order.status, order.id)
                        }

                }
            }
        }
    }
}
@Composable
fun IconRowWithDashedLine(context: Context,status: String?, orderId: String) {
    var phase by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            phase += 3f
            kotlinx.coroutines.delay(100)
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 65.dp)) {
        Icon(
            painter = painterResource(id = R.drawable.img_1),
            contentDescription = "",
            modifier = Modifier.size(40.dp)
        )

        Canvas(modifier = Modifier
            .height(1.dp)
            .width(150.dp)) {
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Red, Color.Blue),
                    start = Offset.Zero,
                    end = Offset(size.width, 0f),
                    tileMode = TileMode.Repeated
                ),
                start = Offset(0f, center.y),
                end = Offset(size.width, center.y),
                strokeWidth = 5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), phase)
            )
        }

        IconButton(
            onClick = {
                updateOrderStatus(orderId ?: "", OrderStatus.Delivered)
                context.startActivity(Intent(context, OrderShipping::class.java))
            },
            enabled = status == "Shipped", // Enable button only if status is "Shipped"
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.img_2),
                contentDescription = "",
                modifier = Modifier.size(35.dp)
            )
        }
    }
}