package aahl.quickinv.ui.manage.products;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import aahl.quickinv.R;

public class ManageableProductViewHolder extends RecyclerView.ViewHolder{

    public TextView tvProdName, tvProdPrice, tvProdQty;
    private ImageButton btnEditProd, btnDeleteProd;

    public ManageableProductViewHolder(@NonNull View itemView, final onProductItemClicked listener) {
        super(itemView);
        tvProdName = itemView.findViewById(R.id.tvProdName);
        tvProdPrice = itemView.findViewById(R.id.tvProdPrice);
        tvProdQty = itemView.findViewById(R.id.tvProdQty);
        btnEditProd = itemView.findViewById(R.id.btnEditProd);
        btnDeleteProd = itemView.findViewById(R.id.btnDeleteProd);


        if (listener != null) {
            int position = getBindingAdapterPosition();

            btnEditProd.setOnClickListener(v -> {
                if (position != RecyclerView.NO_POSITION){
                    listener.onEditProductClicked(position);
                }
            });

            btnDeleteProd.setOnClickListener(v -> {
                if (position != RecyclerView.NO_POSITION){
                    listener.onDeleteProductClicked(position);
                }
            });
        }
    }
}
