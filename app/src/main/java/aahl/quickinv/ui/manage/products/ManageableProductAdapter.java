package aahl.quickinv.ui.manage.products;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import aahl.quickinv.R;
import aahl.quickinv.models.Product;

public class ManageableProductAdapter extends RecyclerView.Adapter<ManageableProductViewHolder>{

    private List<Product> productList;
    private onProductItemClicked listener;

    public ManageableProductAdapter(List<Product> productList){
        this.productList = productList;
    }

    public void setListener(onProductItemClicked listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ManageableProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_detailed, parent, false);
        return new ManageableProductViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageableProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProdName.setText(product.getName());
        holder.tvProdQty.setText("|  Qty: " + product.getQuantity());
        holder.tvProdPrice.setText("$" + product.getUnitPrice());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
