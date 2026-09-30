
package com.example.dbsederhana;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText etNrp, etNama, etProdi;
    private Button btnSimpan, btnCari, btnUpdate, btnHapus;

    private DatabaseHelper dbHelper;

    // ListView dan Adapter
    private ListView listMahasiswa;
    private ArrayList<String> daftarMahasiswa;
    private ArrayAdapter<String> adapter;

    // SharedPreferences
    private static final String PREFS_NAME = "prefs_mahasiswa";
    private static final String KEY_LAST_NRP = "last_nrp";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi komponen form
        etNrp = findViewById(R.id.etNrp);
        etNama = findViewById(R.id.etNama);
        etProdi = findViewById(R.id.etProdi);

        btnSimpan = findViewById(R.id.btnSimpan);
        btnCari = findViewById(R.id.btnCari);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnHapus = findViewById(R.id.btnHapus);

        // Inisialisasi database dan preferences
        dbHelper = new DatabaseHelper(this);

        sharedPreferences = getSharedPreferences(
                PREFS_NAME, MODE_PRIVATE);

        // Inisialisasi ListView
        listMahasiswa = findViewById(R.id.listMahasiswa);

        daftarMahasiswa = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                R.layout.item_mahasiswa,
                R.id.tvItemMahasiswa,
                daftarMahasiswa
        );

        listMahasiswa.setAdapter(adapter);

        // Tampilkan data satu kali saat aplikasi dibuka
        tampilkanSemuaMahasiswa();

        // Muat NRP terakhir yang dicari
        String lastNrp = sharedPreferences.getString(
                KEY_LAST_NRP, "");

        if (!TextUtils.isEmpty(lastNrp)) {
            etNrp.setText(lastNrp);
        }

        // Listener tombol
        btnSimpan.setOnClickListener(v -> simpanData());
        btnCari.setOnClickListener(v -> cariData());
        btnUpdate.setOnClickListener(v -> updateData());
        btnHapus.setOnClickListener(v -> hapusData());
    }

    // Validasi NRP
    private boolean inputKosong(String nrp) {
        if (TextUtils.isEmpty(nrp)) {
            Toast.makeText(this,
                    "NRP tidak boleh kosong",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    // READ: Tampilkan seluruh mahasiswa di ListView
    private void tampilkanSemuaMahasiswa() {

        Cursor cursor = dbHelper.getAllMahasiswa();

        daftarMahasiswa.clear();

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {

                    String nrp = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_NRP));

                    String nama = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_NAMA));

                    String prodi = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_PRODI));

                    String data = "NRP: " + nrp
                            + "\nNama: " + nama
                            + "\nProdi: " + prodi;

                    daftarMahasiswa.add(data);
                }
            } finally {
                cursor.close();
            }
        }

        adapter.notifyDataSetChanged();
    }

    // CREATE: Simpan mahasiswa
    private void simpanData() {

        String nrp = etNrp.getText().toString().trim();
        String nama = etNama.getText().toString().trim();
        String prodi = etProdi.getText().toString().trim();

        if (inputKosong(nrp)) return;

        if (TextUtils.isEmpty(nama)
                || TextUtils.isEmpty(prodi)) {

            Toast.makeText(this,
                    "Nama dan Prodi tidak boleh kosong",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean berhasil = dbHelper.insertMahasiswa(
                nrp, nama, prodi);

        if (berhasil) {

            tampilkanSemuaMahasiswa();

            Toast.makeText(this,
                    "Data " + nama + " berhasil disimpan",
                    Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this,
                    "Gagal simpan. NRP mungkin sudah terdaftar",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // READ: Cari mahasiswa berdasarkan NRP
    private void cariData() {

        String nrp = etNrp.getText().toString().trim();

        if (inputKosong(nrp)) return;

        Cursor cursor = dbHelper.getMahasiswaByNrp(nrp);

        if (cursor != null && cursor.moveToFirst()) {

            String nama = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COL_NAMA));

            String prodi = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COL_PRODI));

            etNama.setText(nama);
            etProdi.setText(prodi);

            cursor.close();

            SharedPreferences.Editor editor =
                    sharedPreferences.edit();

            editor.putString(KEY_LAST_NRP, nrp);
            editor.apply();

            Toast.makeText(this,
                    "Data ditemukan: " + nama,
                    Toast.LENGTH_SHORT).show();

        } else {

            if (cursor != null) {
                cursor.close();
            }

            Toast.makeText(this,
                    "Data dengan NRP " + nrp
                            + " tidak ditemukan",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // UPDATE: Ubah data mahasiswa
    private void updateData() {

        String nrp = etNrp.getText().toString().trim();
        String namaBaru = etNama.getText().toString().trim();
        String prodiBaru = etProdi.getText().toString().trim();

        if (inputKosong(nrp)) return;

        if (TextUtils.isEmpty(namaBaru)
                || TextUtils.isEmpty(prodiBaru)) {

            Toast.makeText(this,
                    "Nama dan Prodi tidak boleh kosong",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int rowsAffected = dbHelper.updateMahasiswa(
                nrp, namaBaru, prodiBaru);

        if (rowsAffected > 0) {

            tampilkanSemuaMahasiswa();

            Toast.makeText(this,
                    "Data NRP " + nrp
                            + " berhasil diupdate",
                    Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this,
                    "Data dengan NRP " + nrp
                            + " tidak ditemukan",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // DELETE: Hapus mahasiswa
    private void hapusData() {

        String nrp = etNrp.getText().toString().trim();

        if (inputKosong(nrp)) return;

        int rowsAffected = dbHelper.deleteMahasiswa(nrp);

        if (rowsAffected > 0) {

            tampilkanSemuaMahasiswa();

            Toast.makeText(this,
                    "Data NRP " + nrp
                            + " berhasil dihapus",
                    Toast.LENGTH_SHORT).show();

            etNrp.setText("");
            etNama.setText("");
            etProdi.setText("");

        } else {
            Toast.makeText(this,
                    "Data dengan NRP " + nrp
                            + " tidak ditemukan",
                    Toast.LENGTH_SHORT).show();
        }
    }
}