package aahl.quickinv.ui.manage.products.modification;

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

import java.util.List;

import aahl.quickinv.R;
import aahl.quickinv.data.DBOps;
import aahl.quickinv.models.Product;
import aahl.quickinv.ui.manage.products.ManageableProductAdapter;
import aahl.quickinv.ui.manage.products.onProductItemClicked;
import aahl.quickinv.utils.EdgeToEdgeHelper;

public class ProductModify extends Fragment {

    private final int MARGIN_EXTRA_BOTTOM_RECYCLERVIEW = 20;

    private RecyclerView recyclerView;
    private ManageableProductAdapter adapter;
    private List<Product> productList;
    private DBOps dbOps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.products_detailed, container, false);

        // Primero obtener todos los datos del inventario referenciado
        obtenerProductosDelInventario();

        // Configurar el recyclerView
        configurarRecycler(view);

        // Configurar edge-to-edge
        configurarEdgeToEdge(view);

        // Configurar el FAB
        configurarFAB(view);

        return view;
    }

    private void configurarRecycler(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.rvProductsDetailed);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ManageableProductAdapter(productList);
        adapter.setListener(new onProductItemClicked() {
            @Override
            public void onEditProductClicked(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", productList.get(position));
                bundle.putLong("id_inventory", obtenerIdDelInventarioCorrespondiente());
                bundle.putString("source", "modify"); // Indicar que viene de ProductModify

                Navigation.findNavController(view)
                        .navigate(R.id.action_products_modify_to_add_edit_product, bundle);
            }

            @Override
            public void onDeleteProductClicked(int position) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm deletion")
                        .setMessage("Are you sure that you want to delete the product \"" + productList.get(position).getName()
                                + "\" from the database? This action is not reversible")
                        .setPositiveButton("Yes", (dialog, which) -> borrarProducto(position))
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

    private void configurarFAB(View view) {
        FloatingActionButton fabNuevoProducto = view.findViewById(R.id.fabAddProduct);

        fabNuevoProducto.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("id_inventory", obtenerIdDelInventarioCorrespondiente());
            bundle.putString("source", "modify"); // Indicar que viene de ProductModify

            Navigation.findNavController(v)
                    .navigate(R.id.action_products_modify_to_add_edit_product, bundle);
        });
    }

    private void borrarProducto(int position) {

        // Borrar de la BD y de la lista
        dbOps.deleteProduct(productList.get(position).getId());
        productList.remove(position);

        // Luego, avisar al recycler que el elemento ya no existe.
        adapter.notifyItemRemoved(position);
    }

    private void obtenerProductosDelInventario() {
        // Inicializar la clase de DbOps
        dbOps = new DBOps(getContext());

        // ID del inventario que se va a modificar
        long id_inventory = obtenerIdDelInventarioCorrespondiente();

        // Llenar la lista de los productos
        productList = dbOps.getProductsByInventory(id_inventory);
    }

    private long obtenerIdDelInventarioCorrespondiente() {
        return getArguments().getLong("id_inventory");
    }

}
