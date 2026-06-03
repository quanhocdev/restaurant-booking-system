import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.giaodien.data.repository.DanhGiaRepository
import com.example.giaodien.viewmodel.DanhGiaViewModel

class DanhGiaViewModelFactory(
    private val repo: DanhGiaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DanhGiaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DanhGiaViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
