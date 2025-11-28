package aahl.quickinv.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import aahl.quickinv.models.Inventory;
import aahl.quickinv.models.Product;

public class DBOps {

    private AdminBD dbHelper;
    private SQLiteDatabase db;

    public DBOps(Context context) {
        dbHelper = new AdminBD(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    // =========================================================================
    // INVENTARIOS
    // =========================================================================

    /**
     * Crea un nuevo inventario.
     * @param name Nombre del inventario (Debe ser único).
     * @return El ID del nuevo inventario o -1 si hubo error (ej. nombre duplicado).
     */
    public long addInventory(String name) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put("name", name);

            // Retorna el ID de la fila insertada o -1 si falla
            return db.insert("inventories", null, values);
        } finally {
            close();
        }
    }

    /**
     * Obtiene todos los inventarios para mostrarlos en una lista.
     * @return Lista de inventarios ordenados alfabéticamente por nombre.
     */
    public List<Inventory> getAllInventories() {
        open();
        List<Inventory> inventories = new ArrayList<>();
        Cursor cursor = null;

        try {
            String[] columns = {"id", "name"};
            cursor = db.query("inventories", columns, null, null, null, null, "name ASC");

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                inventories.add(new Inventory(id, name));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }

        return inventories;
    }

    /**
     * Actualiza el nombre de un inventario.
     */
    public boolean updateInventory(long id, String newName) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put("name", newName);

            // Retorna true si se actualizó al menos una fila
            return db.update("inventories", values, "id = ?", new String[]{String.valueOf(id)}) > 0;
        } finally {
            close();
        }
    }

    /**
     * Borra un inventario. Todos sus productos igual desaparecen por el constraint.
     */
    public boolean deleteInventory(long id) {
        open();
        try {
            return db.delete("inventories", "id = ?", new String[]{String.valueOf(id)}) > 0;
        } finally {
            close();
        }
    }

    // =========================================================================
    // PRODUCTOS
    // =========================================================================

    /**
     * Agrega un producto a un inventario específico.
     * Se guardan automáticamente la fecha de creación y actualización.
     */
    public long addProduct(String name, int quantity, double unitPrice, long inventoryId) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("quantity", quantity);
            values.put("unit_price", unitPrice);
            values.put("inventory_id", inventoryId);

            long currentTime = System.currentTimeMillis();
            values.put("created_at", currentTime);
            values.put("updated_at", currentTime);
            // last_checked_at inicia en NULL

            return db.insert("products", null, values);
        } finally {
            close();
        }
    }

    /**
     * Obtiene productos filtrados por inventario.
     * @param inventoryId ID del inventario del cual obtener los productos.
     * @return Lista de productos ordenados alfabéticamente por nombre.
     */
    public List<Product> getProductsByInventory(long inventoryId) {
        open();
        List<Product> products = new ArrayList<>();
        Cursor cursor = null;

        try {
            String selection = "inventory_id = ?";
            String[] selectionArgs = {String.valueOf(inventoryId)};

            cursor = db.query("products", null, selection, selectionArgs, null, null, "name ASC");

            while (cursor.moveToNext()) {
                products.add(extractProductFromCursor(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }

        return products;
    }

    /**
     * Busca productos dentro de un inventario por nombre.
     * @param inventoryId ID del inventario donde buscar.
     * @param queryText Texto a buscar en el nombre del producto.
     * @return Lista de productos que coinciden con la búsqueda.
     */
    public List<Product> searchProductsInInventory(long inventoryId, String queryText) {
        open();
        List<Product> products = new ArrayList<>();
        Cursor cursor = null;

        try {
            String selection = "inventory_id = ? AND name LIKE ?";
            // Los % son comodines para buscar "que contenga" el texto
            String[] selectionArgs = {String.valueOf(inventoryId), "%" + queryText + "%"};

            cursor = db.query("products", null, selection, selectionArgs, null, null, "name ASC");

            while (cursor.moveToNext()) {
                products.add(extractProductFromCursor(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }

        return products;
    }

    /**
     * Modificar producto completo.
     * Actualiza automáticamente el campo 'updated_at'.
     */
    public boolean updateProduct(long id, String name, int quantity, double unitPrice) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("quantity", quantity);
            values.put("unit_price", unitPrice);
            values.put("updated_at", System.currentTimeMillis());

            return db.update("products", values, "id = ?", new String[]{String.valueOf(id)}) > 0;
        } finally {
            close();
        }
    }

    /**
     * Lógica del Checklist.
     * Solo actualiza la fecha de verificación.
     */
    public boolean checkProduct(long id, long millis){
        open();
        try {
            ContentValues values = new ContentValues();
            values.put("last_checked_at", millis);

            return db.update("products", values, "id = ?", new String[]{String.valueOf(id)}) > 0;
        } finally {
            close();
        }
    }

    /**
     * Eliminar un producto individual.
     */
    public boolean deleteProduct(long id) {
        open();
        try {
            return db.delete("products", "id = ?", new String[]{String.valueOf(id)}) > 0;
        } finally {
            close();
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================

    /**
     * Extrae un objeto Product desde un Cursor.
     * @param cursor Cursor posicionado en una fila de producto.
     * @return Objeto Product con todos los datos.
     */
    private Product extractProductFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
        double unitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("unit_price"));
        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"));
        long updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"));

        // last_checked_at puede ser NULL
        int lastCheckedIndex = cursor.getColumnIndexOrThrow("last_checked_at");
        Long lastCheckedAt = cursor.isNull(lastCheckedIndex) ? null : cursor.getLong(lastCheckedIndex);

        long inventoryId = cursor.getLong(cursor.getColumnIndexOrThrow("inventory_id"));

        return new Product(id, name, quantity, unitPrice, createdAt, updatedAt, lastCheckedAt, inventoryId);
    }
}
