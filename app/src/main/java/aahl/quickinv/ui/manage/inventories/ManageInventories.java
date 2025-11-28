package aahl.quickinv.ui.manage.inventories;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import aahl.quickinv.R;
import aahl.quickinv.data.DBOps;
import aahl.quickinv.models.Inventory;
import aahl.quickinv.utils.EdgeToEdgeHelper;

public class ManageInventories extends Fragment {

    private final int MARGIN_EXTRA_BOTTOM_RECYCLERVIEW = 20;

    private List<Inventory> inventoryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private FloatingActionButton fabNuevoInventory;
    private DBOps dbOps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventories, container, false);

        dbOps = new DBOps(getContext());

        // Obtener los inventarios disponibles
        obtenerInventarios();

        // Configuración del recycler
        configurarRecyclerView(view);

        // Configuracion del FAB
        configurarFAB(view);

        return view;
    }

    private void obtenerInventarios() {
        inventoryList = dbOps.getAllInventories();
    }

    private void configurarRecyclerView(View view){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.rvInventories);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new InventoryAdapter(inventoryList);
        adapter.setListener(new onItemActionsClicked() {
            @Override
            public void onEditInventoryClicked(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("inventory", inventoryList.get(position));

                Navigation.findNavController(view)
                        .navigate(R.id.action_manage_inventories_to_add_edit_inventory, bundle);
            }

            @Override
            public void onDeleteInventoryClicked(int position) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm deletion")
                        .setMessage("Are you sure that you want to delete the inventory \"" + inventoryList.get(position).getName()
                        + "\" from the database? This action is not reversible and all the items contained within it will be erased.")
                        .setPositiveButton("Yes", (dialog, which) -> borrarInventario(position))
                        .setNegativeButton("No", null)
                        .setCancelable(false)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void configurarEdgeToEdge(View view) {
        EdgeToEdgeHelper.applyBottomMargin(view, recyclerView, MARGIN_EXTRA_BOTTOM_RECYCLERVIEW);
    }

    private void borrarInventario(int position) {

        // Borrar de la BD y de la lista
        dbOps.deleteInventory(inventoryList.get(position).getId());
        inventoryList.remove(position);

        // Luego, avisar al recycler que el elemento ya no existe.
        adapter.notifyItemRemoved(position);
    }

    private void configurarFAB(View view) {
        fabNuevoInventory = view.findViewById(R.id.fabAddInventory);

        fabNuevoInventory.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_manage_inventories_to_add_edit_inventory, null);
        });
    }
}
