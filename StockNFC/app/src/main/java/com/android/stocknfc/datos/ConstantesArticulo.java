package com.android.stocknfc.datos;

import android.provider.BaseColumns;

/**
 * Created by Victor on 15/12/2016.
 */

public class ConstantesArticulo implements BaseColumns {

    private static final String TAG = "ColumnasArticulo";

    /**
     * Travel table name
     */
    public static final String ARTICULOS_TABLE_NAME = "Articulo";

    /**
     * The city of the travel
     * <P>Type: TEXT</P>
     */
    public static final String NOMBRE = "nombre";

    /**
     * The city of the travel
     * <P>Type: TEXT</P>
     */
    public static final String STOCK = "stock";

    /**
     * The city of the travel
     * <P>Type: TEXT</P>
     */
    public static final String ID = "id";

}