package aahl.quickinv.ui.manage.products.modification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;

import aahl.quickinv.R;
import aahl.quickinv.data.DBOps;
import aahl.quickinv.models.Product;
import aahl.quickinv.utils.EdgeToEdgeHelper;

public class add_edit_product extends Fragment {

    private final int MARGIN_EXTRA_BOTTOM_BUTTONS = 20;

    private Product bundledProduct;
    private long id_inventory;
    private String source; // Origen de la navegación: "visualize" o "modify"
    private boolean isNewProduct = false;
    private TextInputEditText etProdName, etProdQty, etProdPrice;
    private DBOps dbOps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_edit_product, container, false);

        dbOps = new DBOps(getContext());

        // Obtener los posibles datos que pudieron haberse mandado
        obtenerDatosDelBundle();

        // Mostrar los datos obtenidos si es que se mandaron datos
        mostrarLosDatosObtenidos(view);

        // Configuración del título de la ventana
        cambiarTituloDeVentana();

        // Configuración de los botones
        configurarBotones(view);

        return view;
    }

    private void obtenerDatosDelBundle() {
        if (getArguments() == null) return;
        bundledProduct = getArguments().getSerializable("product", Product.class);
        id_inventory = getArguments().getLong("id_inventory");
        source = getArguments().getString("source"); // Obtener el origen

        if (bundledProduct == null) isNewProduct = true;
    }

    private void mostrarLosDatosObtenidos(View view) {
        etProdName = view.findViewById(R.id.etProdName);
        etProdQty = view.findViewById(R.id.etProdQty);
        etProdPrice = view.findViewById(R.id.etProdPrice);

        if (!isNewProduct) {
            etProdName.setText(bundledProduct.getName());
            etProdQty.setText(String.valueOf(bundledProduct.getQuantity()));
            etProdPrice.setText(String.valueOf(bundledProduct.getUnitPrice()));
        }
    }

    private void cambiarTituloDeVentana() {
        // Cambiar el título según si estamos agregando o editando
        if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                if (isNewProduct) {
                    // Modo agregar
                    activity.getSupportActionBar().setTitle("Add Product");
                } else {
                    // Modo editar
                    activity.getSupportActionBar().setTitle("Edit Product");
                }
            }
        }
    }

    private void configurarEdgeToEdge(View view) {
        View layoutButtons = view.findViewById(R.id.layoutButtons);
        EdgeToEdgeHelper.applyBottomMargin(view, layoutButtons, MARGIN_EXTRA_BOTTOM_BUTTONS);
    }

    private void configurarBotones(View view) {
        Button btnSave = view.findViewById(R.id.btnSaveProd);
        Button btnCancel = view.findViewById(R.id.btnCancelProd);

        btnSave.setOnClickListener(v -> {
            if (!isNewProduct) {
                dbOps.updateProduct(
                        bundledProduct.getId(),
                        etProdName.getText().toString(),
                        Integer.parseInt(etProdQty.getText().toString()),
                        Double.parseDouble(etProdPrice.getText().toString())
                );
                navegarAListaDeProductos(view, id_inventory);
            } else {
                dbOps.addProduct(
                        etProdName.getText().toString(),
                        Integer.parseInt(etProdQty.getText().toString()),
                        Double.parseDouble(etProdPrice.getText().toString()),
                        id_inventory
                );
                navegarAListaDeProductos(view, id_inventory);
            }
        });

        btnCancel.setOnClickListener(v -> {
            navegarAListaDeProductos(view, id_inventory);
        });
    }

    private void navegarAListaDeProductos(View view, long id_inventory) {
        Bundle bundle = new Bundle();
        bundle.putLong("id_inventory", id_inventory);

        // Determinar a dónde navegar según el origen
        if ("visualize".equals(source)) {
            // Si viene de VisualizeFragment, regresar allí
            Navigation.findNavController(view)
                    .navigate(R.id.action_addedit_products_to_visualize, bundle);
        } else {
            // Si viene de ProductModify o cualquier otro origen, usar el comportamiento original
            Navigation.findNavController(view)
                    .navigate(R.id.action_addedit_products_to_products_modify, bundle);
        }
    }

}
