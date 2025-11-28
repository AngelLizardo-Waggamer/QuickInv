package aahl.quickinv.ui.manage.inventories;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import aahl.quickinv.R;

public class InventoryViewHolder extends RecyclerView.ViewHolder {

    public TextView tvInventoryName;
    public ImageButton btnEditInventory;
    public ImageButton btnDeleteInventory;
    public onItemActionsClicked listener;

    public InventoryViewHolder(@NonNull View itemView, final onItemActionsClicked listener) {
        super(itemView);
        tvInventoryName = itemView.findViewById(R.id.tvInventoryName);
        btnEditInventory = itemView.findViewById(R.id.btnEditInventory);
        btnDeleteInventory = itemView.findViewById(R.id.btnDeleteInventory);

        btnEditInventory.setOnClickListener(v -> {
            if (listener != null) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    listener.onEditInventoryClicked(position);
                }
            }
        });

        btnDeleteInventory.setOnClickListener(v -> {
            if (listener != null) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    listener.onDeleteInventoryClicked(position);
                }
            }
        });
    }
}
