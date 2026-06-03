package com.example.giaodien.data.repository

import com.example.giaodien.data.model.DanhGia
import com.example.giaodien.data.network.ApiService
import com.example.giaodien.data.network.model.DanhGiaRequest
class DanhGiaRepository(private val api: ApiService) {

    suspend fun getDanhGia(thucDonId: Long): List<DanhGia> {
        return api.getDanhGia(thucDonId)
    }

    suspend fun addDanhGia(thucDonId: Long, userEmail: String, soSao: Int): DanhGia {
        return api.addDanhGia(thucDonId, userEmail, soSao)
    }
}

