package com.android.stocknfc.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.stocknfc.Articulo;

import java.util.ArrayList;

/**
 * Created by Victor on 13/06/2017.
 */

public class ArticulosDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ArticulosDatabaseHelper";
    public static final String TABLE_NAME = "articulo";
    private static final int DATABASE_VERSION = 1;

    public ArticulosDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                ConstantesArticulo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ConstantesArticulo.NOMBRE + " TEXT NOT NULL, " +
                ConstantesArticulo.STOCK + " INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            onCreate(db);
        }
    }

    public void insertarArticulo(SQLiteDatabase db, String nombre,
                             int stock){
        ContentValues values = new ContentValues();
        values.put(ConstantesArticulo.NOMBRE, nombre);
        values.put(ConstantesArticulo.STOCK, stock);
        db.insert(TABLE_NAME, null, values);
    }

    public int editArticulo(SQLiteDatabase db, int stock, int id){
        ContentValues values = new ContentValues();
        values.put(ConstantesArticulo.STOCK, stock);

        int cant = db.update(TABLE_NAME, values, "[ID]= " + id, null);

        return cant;
    }

    public int getStock(SQLiteDatabase db, String nombre){
        db  = getReadableDatabase();
        String condicion = ConstantesArticulo.NOMBRE + "= '" + nombre + "'";
        Articulo articulo = new Articulo();
        Cursor c = db.query(ConstantesArticulo.ARTICULOS_TABLE_NAME,
                null, condicion, null, null, null, null);
        if (c.moveToFirst()){
            int nombreIndex = c.getColumnIndex(ConstantesArticulo.NOMBRE);
            int stockIndex = c.getColumnIndex(ConstantesArticulo.STOCK);
            int idIndex = c.getColumnIndex(ConstantesArticulo.ID);
            do {
                String nombreArticulo = c.getString(nombreIndex);
                int stockArticulo= c.getInt(stockIndex);
                int id = c.getInt(idIndex);
                articulo = new Articulo(nombreArticulo, stockArticulo, id);
            } while (c.moveToNext());
        }
        return articulo.getStock();
    }

    public Articulo obtenerArticulo(SQLiteDatabase db, String nombre) {
        db  = getReadableDatabase();
        String condicion = ConstantesArticulo.NOMBRE + "= '" + nombre + "'";
        Articulo articulo = new Articulo();
        Cursor c = db.query(ConstantesArticulo.ARTICULOS_TABLE_NAME,
                null, condicion, null, null, null, null);
        if (c.moveToFirst()){
            int nombreIndex = c.getColumnIndex(ConstantesArticulo.NOMBRE);
            int stockIndex = c.getColumnIndex(ConstantesArticulo.STOCK);
            int idIndex = c.getColumnIndex(ConstantesArticulo.ID);
            do {
                String nombreArticulo = c.getString(nombreIndex);
                int stockArticulo= c.getInt(stockIndex);
                int id = c.getInt(idIndex);
                articulo = new Articulo(nombreArticulo, stockArticulo, id);
            } while (c.moveToNext());
        }
        return articulo;
    }
}
