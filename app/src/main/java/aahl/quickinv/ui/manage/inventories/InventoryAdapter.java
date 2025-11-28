package aahl.quickinv.ui.manage.inventories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import aahl.quickinv.R;
import aahl.quickinv.models.Inventory;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryViewHolder> {

    private List<Inventory> inventoryList;
    private onItemActionsClicked listener;

    public InventoryAdapter(List<Inventory> inventoryList){
        this.inventoryList = inventoryList;
    }

    public void setListener(onItemActionsClicked listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Inventory inventory = inventoryList.get(position);
        holder.tvInventoryName.setText(inventory.getName());
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }
}
