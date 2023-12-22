package com.bangkitacademy.nutrace.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkitacademy.nutrace.MainActivity
import com.bangkitacademy.nutrace.databinding.ActivityInputUserBinding

class InputUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menyiapkan data untuk Spinner Jenis Kelamin
        val jenisKelaminOptions = arrayOf("Laki-laki", "Perempuan")
        val jenisKelaminAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jenisKelaminOptions)
        jenisKelaminAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJenisKelamin.adapter = jenisKelaminAdapter

        val tujuanOptions = arrayOf("Diet", "Bulking")
        val tujuanAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tujuanOptions)
        tujuanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTujuan.adapter = tujuanAdapter

        // Menyiapkan data untuk Spinner Tingkat Aktivitas
        val tingkatAktivitasOptions = arrayOf("Rendah", "Sedang", "Tinggi")
        val tingkatAktivitasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tingkatAktivitasOptions)
        tingkatAktivitasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTingkatAktivitas.adapter = tingkatAktivitasAdapter

        // Menangani klik tombol Simpan
        binding.saveButton.setOnClickListener {
            // Ambil nilai dari semua input
            val beratBadan = binding.editBb.text.toString()
            val tinggiBadan = binding.editTb.text.toString()
            val usia = binding.editUsia.text.toString()
            val jenisKelamin = binding.spinnerJenisKelamin.selectedItem.toString()
            val tujuan = binding.spinnerTujuan.selectedItem.toString()
            val tingkatAktivitas = binding.spinnerTingkatAktivitas.selectedItem.toString()

            val isAlergiASelected = binding.checkBoxAlergiA.isChecked
            val isAlergiBSelected = binding.checkBoxAlergiB.isChecked
            val isAlergiCSelected = binding.checkBoxAlergiC.isChecked
            // Lakukan sesuatu dengan data yang diambil, misalnya menyimpan ke database
            // ...

            // Tambahkan logika lain yang diperlukan
            val intent = Intent(this@InputUserActivity, MainActivity::class.java)
            startActivity(intent)

            // Contoh: Tampilkan Toast sebagai umpan balik
            val feedbackMessage = "Data berhasil disimpan:\nBerat Badan: $beratBadan\nTinggi Badan: $tinggiBadan\nUsia: $usia\nJenis Kelamin: $jenisKelamin\nTingkat Aktivitas: $tingkatAktivitas\tatus Alergi A: $isAlergiASelected\\nStatus Alergi B: $isAlergiBSelected\\nStatus Alergi C: $isAlergiCSelected"
            showToast(feedbackMessage)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}