package aahl.quickinv.ui.visualize.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import aahl.quickinv.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    public TextView tvProductName;
    public TextView tvProductDetails;
    public View statusIndicator;
    public ImageView btnEdit;

    public ProductViewHolder(@NonNull View itemView, final onProductClickListener listener){
        super(itemView);
        statusIndicator = itemView.findViewById(R.id.statusIndicator);
        tvProductName = itemView.findViewById(R.id.tvProductName);
        tvProductDetails = itemView.findViewById(R.id.tvProductDetails);
        btnEdit = itemView.findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(v -> {
           if (listener != null){
               int position = getBindingAdapterPosition();
               if (position != RecyclerView.NO_POSITION){
                   listener.onItemClicked(position);
               }
           }
        });
    }

}
