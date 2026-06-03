package com.example.giaodien.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.giaodien.data.model.DanhGia
import com.example.giaodien.data.repository.DanhGiaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DanhGiaViewModel(private val repo: DanhGiaRepository) : ViewModel() {

    private val _danhGiaList = MutableStateFlow<List<DanhGia>>(emptyList())
    val danhGiaList: StateFlow<List<DanhGia>> = _danhGiaList

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadDanhGia(thucDonId: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _danhGiaList.value = repo.getDanhGia(thucDonId)
            } finally {
                _loading.value = false
            }
        }
    }

    fun addDanhGia(thucDonId: Long, userEmail: String, soSao: Int) {
        viewModelScope.launch {
            val newRating = repo.addDanhGia(thucDonId, userEmail, soSao)
            _danhGiaList.value = _danhGiaList.value + newRating
        }
    }


    fun averageRating(): Float {
        val list = _danhGiaList.value
        if (list.isEmpty()) return 0f
        return list.map { it.soSao }.average().toFloat()
    }
}
