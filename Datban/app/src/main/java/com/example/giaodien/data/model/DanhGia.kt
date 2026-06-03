package com.example.giaodien.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DanhGia(
    val id: Long,
    val thucDon: ThucDonRef,
    val userEmail: String,
    val soSao: Int,      // 1-5
    val createdAt: String
)
