package aahl.quickinv.ui.manage.products.audit;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import aahl.quickinv.R;
import aahl.quickinv.models.Product;

public class AuditingProductAdapter extends RecyclerView.Adapter<AuditingProductViewHolder> {

    private List<Product> products;
    private CompoundButton.OnCheckedChangeListener listener;
    private TextWatcher textChangeListener;
    private RecyclerView recyclerView;

    public AuditingProductAdapter(List<Product> products) {
        this.products = products;
    }

    public void setListener(CompoundButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    public void setTextChangeListener(TextWatcher textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public AuditingProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_checklist, parent, false);
        return new AuditingProductViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AuditingProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvAuditName.setText(product.getName());
        holder.etAuditQty.setText(Integer.toString(product.getQuantity()));

        // Remover listeners anteriores para evitar duplicados
        holder.etAuditQty.setTag(null);

        // Crear un TextWatcher que tenga acceso al ViewHolder actual
        TextWatcher editTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No necesitamos hacer nada aquí
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Marcar automáticamente la checkbox cuando se modifica el texto
                // Solo se hace una vez, cuando aún no está marcada
                if (!holder.cbAudit.isChecked()) {
                    holder.cbAudit.setChecked(true);
                }

                // Notificar al listener externo después de marcar la checkbox
                // Esto asegura que hasAnyChangeBeenMade se actualice
                if (textChangeListener != null) {
                    textChangeListener.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No necesitamos hacer nada aquí
            }
        };

        // Agregar el listener al EditText
        holder.etAuditQty.addTextChangedListener(editTextWatcher);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    /**
     * Obtiene el producto en la posición especificada con la cantidad actualizada desde el EditText.
     * @param position La posición del producto en la lista
     * @return El producto con la cantidad actualizada, o null si no se encuentra el ViewHolder
     */
    public Product getProductWithUpdatedQuantity(int position) {
        if (recyclerView == null) return null;

        AuditingProductViewHolder holder = (AuditingProductViewHolder)
                recyclerView.findViewHolderForAdapterPosition(position);

        if (holder != null) {
            Product product = products.get(position);
            int newQuantity = holder.getQuantityFromEditText();
            product.setQuantity(newQuantity);
            return product;
        }

        return null;
    }

    /**
     * Obtiene la lista de productos con las cantidades actualizadas de todos los EditText visibles.
     */
    public List<Product> getProducts() {
        return products;
    }
}
