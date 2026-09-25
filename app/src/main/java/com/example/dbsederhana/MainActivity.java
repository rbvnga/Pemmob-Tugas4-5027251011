package com.example.dbsederhana;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etNrp, etNama, etProdi;
    private Button btnSimpan, btnCari, btnUpdate, btnHapus;

    private DatabaseHelper dbHelper;

    // SharedPreferences: menyimpan NRP terakhir yang dicari
    private static final String PREFS_NAME = "prefs_mahasiswa";
    private static final String KEY_LAST_NRP = "last_nrp";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNrp = findViewById(R.id.etNrp);
        etNama = findViewById(R.id.etNama);
        etProdi = findViewById(R.id.etProdi);

        btnSimpan = findViewById(R.id.btnSimpan);
        btnCari = findViewById(R.id.btnCari);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnHapus = findViewById(R.id.btnHapus);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Muat otomatis NRP terakhir yang dicari
        String lastNrp = sharedPreferences.getString(KEY_LAST_NRP, "");
        if (!TextUtils.isEmpty(lastNrp)) {
            etNrp.setText(lastNrp);
        }

        btnSimpan.setOnClickListener(v -> simpanData());
        btnCari.setOnClickListener(v -> cariData());
        btnUpdate.setOnClickListener(v -> updateData());
        btnHapus.setOnClickListener(v -> hapusData());
    }

    private boolean inputKosong(String nrp) {
        if (TextUtils.isEmpty(nrp)) {
            Toast.makeText(this, "NRP tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    // CREATE
    private void simpanData() {
        String nrp = etNrp.getText().toString().trim();
        String nama = etNama.getText().toString().trim();
        String prodi = etProdi.getText().toString().trim();

        if (inputKosong(nrp)) return;
        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(prodi)) {
            Toast.makeText(this, "Nama dan Prodi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean berhasil = dbHelper.insertMahasiswa(nrp, nama, prodi);
        if (berhasil) {
            Toast.makeText(this, "Data " + nama + " berhasil disimpan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal simpan. NRP mungkin sudah terdaftar", Toast.LENGTH_SHORT).show();
        }
    }

    // READ
    private void cariData() {
        String nrp = etNrp.getText().toString().trim();
        if (inputKosong(nrp)) return;

        Cursor cursor = dbHelper.getMahasiswaByNrp(nrp);
        if (cursor != null && cursor.moveToFirst()) {
            String nama = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAMA));
            String prodi = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODI));
            etNama.setText(nama);
            etProdi.setText(prodi);
            cursor.close();

            // Simpan NRP ini sebagai "NRP terakhir dicari" ke SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_LAST_NRP, nrp);
            editor.apply();

            Toast.makeText(this, "Data ditemukan: " + nama, Toast.LENGTH_SHORT).show();
        } else {
            if (cursor != null) cursor.close();
            Toast.makeText(this, "Data dengan NRP " + nrp + " tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    // UPDATE
    private void updateData() {
        String nrp = etNrp.getText().toString().trim();
        String namaBaru = etNama.getText().toString().trim();
        String prodiBaru = etProdi.getText().toString().trim();

        if (inputKosong(nrp)) return;
        if (TextUtils.isEmpty(namaBaru) || TextUtils.isEmpty(prodiBaru)) {
            Toast.makeText(this, "Nama dan Prodi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        int rowsAffected = dbHelper.updateMahasiswa(nrp, namaBaru, prodiBaru);
        if (rowsAffected > 0) {
            Toast.makeText(this, "Data NRP " + nrp + " berhasil diupdate", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Data dengan NRP " + nrp + " tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    // DELETE
    private void hapusData() {
        String nrp = etNrp.getText().toString().trim();
        if (inputKosong(nrp)) return;

        int rowsAffected = dbHelper.deleteMahasiswa(nrp);
        if (rowsAffected > 0) {
            Toast.makeText(this, "Data NRP " + nrp + " berhasil dihapus", Toast.LENGTH_SHORT).show();
            etNrp.setText("");
            etNama.setText("");
            etProdi.setText("");
        } else {
            Toast.makeText(this, "Data dengan NRP " + nrp + " tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }
}