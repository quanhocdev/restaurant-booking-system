package com.example.giaodien.ui.screens

import DanhGiaViewModelFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.giaodien.data.model.BinhLuan
import com.example.giaodien.data.model.ThucDon
import com.example.giaodien.data.network.RetrofitInstance
import com.example.giaodien.data.repository.DanhGiaRepository
import com.example.giaodien.viewmodel.BinhLuanViewModel
import com.example.giaodien.viewmodel.DanhGiaViewModel
import com.example.giaodien.viewmodel.ThucDonViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MoTaScreen(
    id: Long,
    navController: NavController,
    onBack: () -> Unit, // Thêm lambda để xử lý sự kiện quay lại
    thucDonViewModel: ThucDonViewModel = viewModel(),
    binhLuanViewModel: BinhLuanViewModel = viewModel(),
    // <-- thêm ViewModel đánh giá

) {
    val danhGiaRepo = DanhGiaRepository(RetrofitInstance.api)
    val danhGiaViewModel: DanhGiaViewModel = viewModel(
        factory = DanhGiaViewModelFactory(danhGiaRepo)
    )
    // Load danh sách món và bình luận
    LaunchedEffect(id) {
        thucDonViewModel.loadThucDon()
        binhLuanViewModel.loadBinhLuan(id)
        danhGiaViewModel.loadDanhGia(id) // <-- load đánh giá

    }


    val thucDonList by thucDonViewModel.thucDonList.collectAsState()
    val mon = thucDonList.find { it.idThucDon == id }

    val binhLuanList by binhLuanViewModel.binhLuanList.collectAsState()
    val loadingBinhLuan by binhLuanViewModel.loading.collectAsState()

    val danhGiaList by danhGiaViewModel.danhGiaList.collectAsState()
    val loadingDanhGia by danhGiaViewModel.loading.collectAsState()

// Lấy đánh giá của user hiện tại, nếu có
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    val userRating = danhGiaList.find { it.userEmail == currentUserEmail }?.soSao ?: 0
    val userRated = userRating > 0

// Dùng state để giữ số sao nếu người dùng chưa đánh giá
    var newRating by remember { mutableStateOf(0) }

    // Lấy các món cùng nhóm (khác chính món đang xem)
    val monCungNhom = mon?.let { current ->
        thucDonList.filter { it.nhom == current.nhom && it.idThucDon != current.idThucDon }
    } ?: emptyList()

    if (mon == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    var newComment by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Phần Header chứa ảnh món và nút Quay lại
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Tăng chiều cao ảnh lên một chút
            ) {
                Image(
                    painter = rememberAsyncImagePainter(mon.anh),
                    contentDescription = mon.tenMon,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                )

                // Nút Quay lại
                Card(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .clickable(onClick = onBack) // Sử dụng lambda onBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Phần nội dung thông tin món ăn và bình luận
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Tên món và Giá món (Trình bày nổi bật)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mon.tenMon,
                        style = MaterialTheme.typography.headlineLarge, // Dùng headlineLarge cho nổi bật
                        fontSize = 32.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${mon.gia} VND",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Mô tả món
                if (!mon.moTa.isNullOrEmpty()) {
                    Text(
                        text = "Mô tả chi tiết",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = mon.moTa,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                // Các món cùng nhóm
                if (monCungNhom.isNotEmpty()) {
                    Text(
                        text = "Các món cùng nhóm",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(monCungNhom) { item ->
                            MonCungNhomItem(item = item, onClick = {
                                navController.navigate("mo_ta/${item.idThucDon}") {
                                    launchSingleTop = false
                                    restoreState = false
                                }
                            })
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
                // Hiển thị trung bình đánh giá
                val trungBinhSao = if (danhGiaList.isEmpty()) 0f
                else danhGiaList.map { it.soSao }.average().toFloat()



                Text(
                    text = "Đánh giá trung bình: ${"%.1f".format(trungBinhSao)} sao",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )


                val scope = rememberCoroutineScope()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    (1..5).forEach { i ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (userRated) {
                                if (i <= userRating) Color.Yellow else Color.Gray
                            } else {
                                if (i <= newRating) Color.Yellow else Color.Gray
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clickable(enabled = !userRated) { // Nếu đã đánh giá thì không click
                                    newRating = i
                                }
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    if (!userRated) {
                        val scope = rememberCoroutineScope()
                        Button(onClick = {
                            if (newRating > 0) {
                                scope.launch {
                                    danhGiaViewModel.addDanhGia(id, currentUserEmail, newRating)
                                    danhGiaViewModel.loadDanhGia(id) // reload danh sách từ backend
                                }
                            }
                        }) {
                            Text("Gửi đánh giá")
                        }
                    } else {
                        Text("Bạn đã đánh giá: $userRating sao")
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))

                // Bình luận
                Text(
                    text = "Bình luận (${binhLuanList.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (loadingBinhLuan) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    if (binhLuanList.isEmpty()) {
                        Text(
                            text = "Chưa có bình luận nào. Hãy là người đầu tiên!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            binhLuanList.forEach { bl ->
                                BinhLuanItem(binhLuan = bl)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Thêm bình luận
                Text(
                    text = "Thêm bình luận của bạn",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        placeholder = { Text("Viết bình luận...") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (newComment.isNotBlank()) {
                                binhLuanViewModel.addBinhLuan(id, newComment)
                                newComment = ""
                            }
                        },
                        enabled = newComment.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text("Gửi")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MonCungNhomItem(item: ThucDon, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp) // Giảm kích thước item để gọn hơn
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Màu nền sáng hơn
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(item.anh),
                contentDescription = item.tenMon,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(70.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.tenMon,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                //overflow = androidx.compose.ui.text.TextOverflow.Ellipsis,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun BinhLuanItem(binhLuan: BinhLuan) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // Góc bo tròn hơn
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) // Màu nền nhẹ, dễ nhìn
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = binhLuan.userEmail,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary // Tên người dùng nổi bật
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = binhLuan.noiDung,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}