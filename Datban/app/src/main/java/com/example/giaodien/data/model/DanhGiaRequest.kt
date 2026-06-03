package com.example.giaodien.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class DanhGiaRequest(
    val thucDonId: Long,
    val soSao: Int
)
