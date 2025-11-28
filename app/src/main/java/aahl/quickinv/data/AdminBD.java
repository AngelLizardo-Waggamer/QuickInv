package aahl.quickinv.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminBD extends SQLiteOpenHelper {

    // Configuraciones iniciales
    private static final String DB_NOMBRE = "QuickInvAlphaOne.db";
    private static final int DB_VERSION = 1;

    /**
     * Tabla de inventarios
     * Cuenta con una validación "UNIQUE" en `name` para que no haya dos productos con el mismo nombre.
     */
    private static final String TABLE_INVENTORIES = "CREATE TABLE inventories (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL UNIQUE)";

    /**
     * Tabla de productos
     * Cuenta con una llave foránea en `inventory_id` para referenciar al inventario al que pertenece,
     * y se le implementó la opción de ON DELETE CASCADE para que, en caso de que un inventario
     * se borre, todos sus productos también desaparezcan.
     */
    private static final String TABLE_PRODUCTS = "CREATE TABLE products (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "quantity INTEGER NOT NULL DEFAULT 0, " +
            "unit_price REAL NOT NULL DEFAULT 0.0, " +
            "created_at INTEGER, " +
            "updated_at INTEGER, " +
            "last_checked_at INTEGER, " +
            "inventory_id INTEGER NOT NULL, " +
            "FOREIGN KEY(inventory_id) REFERENCES inventories(id) ON DELETE CASCADE)";

    public AdminBD(Context context){
        super(context, DB_NOMBRE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Crear las dos tablas
        db.execSQL(TABLE_INVENTORIES);
        db.execSQL(TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        // Como es un alpha, la vdd si cambia de versión solo se borran todas las tablas y se
        // vuelven a inicializar. Ni que fuera prod :p
        db.execSQL("DROP TABLE IF EXISTS inventories");
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // Esque sino no funciona el "ON DELETE CASCADE"
    }
}
