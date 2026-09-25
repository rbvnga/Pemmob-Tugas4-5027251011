package com.example.dbsederhana;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "db_mahasiswa.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_MHS = "mhs";
    public static final String COL_NRP = "nrp";
    public static final String COL_NAMA = "nama";
    public static final String COL_PRODI = "prodi";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_MHS + " (" +
                COL_NRP + " TEXT PRIMARY KEY, " +
                COL_NAMA + " TEXT, " +
                COL_PRODI + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MHS);
        onCreate(db);
    }

    // CREATE
    public boolean insertMahasiswa(String nrp, String nama, String prodi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NRP, nrp);
        cv.put(COL_NAMA, nama);
        cv.put(COL_PRODI, prodi);
        long result = db.insert(TABLE_MHS, null, cv);
        db.close();
        return result != -1;
    }

    // READ (cari berdasarkan NRP)
    public Cursor getMahasiswaByNrp(String nrp) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_MHS,
                new String[]{COL_NRP, COL_NAMA, COL_PRODI},
                COL_NRP + "=?",
                new String[]{nrp},
                null, null, null);
    }

    // UPDATE
    public int updateMahasiswa(String nrp, String namaBaru, String prodiBaru) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAMA, namaBaru);
        cv.put(COL_PRODI, prodiBaru);
        int rows = db.update(TABLE_MHS, cv, COL_NRP + "=?", new String[]{nrp});
        db.close();
        return rows;
    }

    // DELETE
    public int deleteMahasiswa(String nrp) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_MHS, COL_NRP + "=?", new String[]{nrp});
        db.close();
        return rows;
    }
}