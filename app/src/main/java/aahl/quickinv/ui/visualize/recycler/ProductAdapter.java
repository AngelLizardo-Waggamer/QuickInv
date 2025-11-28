package aahl.quickinv.ui.visualize.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import aahl.quickinv.R;
import aahl.quickinv.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder>{

    private List<Product> productList;

    private onProductClickListener listener;

    public void setListener(onProductClickListener listener){
        this.listener = listener;
    }

    public ProductAdapter(List<Product> productList){
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductDetails.setText(generarDetallesDeProducto(product));
        holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), asignarColorDeFondoBasadoEnStock(product)));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * Para que el feedback del usuario sea bueno :p </br>
     * Con Stock:</br>
     * {@code Stock: 45 | Value: $mmm.mm} </br>
     * Sin Stock:</br>
     * {@code No Stock | Last Checked: dd/mm/aaaa} </br>
     * Sí, el feedback es bueno :p </br>
     * Y SI, TAMBIEN NO PONDRE LAS FECHAS EN EL FORMATO EN INGLES. (El IDE quiso decirme que el
     * formato inglés era el correcto y me ardí)
     */
    private String generarDetallesDeProducto(Product p) {
        StringBuilder details = new StringBuilder();

        if (p.getQuantity() < 0) {
            details.append("No Stock | ");
            details.append("Last Checked: ").append(formatearFecha(p.getLastCheckedAt()));
        } else {
            details.append("Stock: ").append(p.getQuantity()).append(" | ");
            details.append("Value: $").append(p.getTotalPrice());
        }

        return details.toString();
    }

    private int asignarColorDeFondoBasadoEnStock(Product p) {
        if (p.getQuantity() > 0) { // Hay stock
            return R.color.status_in_stock;
        } else { // No hay stock
            return R.color.status_out_of_stock;
        }
    }

    private String formatearFecha(long t){
        Instant instant = Instant.ofEpochMilli(t);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateTime.format(formatter);
    }
}
