package aahl.quickinv.ui.manage.inventories.modification;

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
import aahl.quickinv.models.Inventory;
import aahl.quickinv.utils.EdgeToEdgeHelper;

public class add_edit_inventory extends Fragment {

    private final int MARGIN_EXTRA_BOTTOM_BUTTONS = 20;

    private Inventory bundledInventory;
    private boolean isNewInventory = false;
    private TextInputEditText etInventoryName;
    private DBOps dbOps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_edit_inventory, container, false);

        dbOps = new DBOps(getContext());

        // Obtener los datos que pudieron haberse mandado (Para actualización se mandan, para agregar no)
        obtenerDatosDelBundle();

        // Mostrar los datos obtenidos, aunque si es un nuevo inventario no muestra nada.
        mostrarLosDatosObtenidos(view);

        // Cambiar el nombre de la ventana dependiendo de si bundledInventory es nulo o no
        cambiarTituloDeVentana();

        // Configuración de los botones
        configurarBotones(view);

        return view;
    }

    private void obtenerDatosDelBundle() {
        if (getArguments() == null) return;
        bundledInventory = getArguments().getSerializable("inventory", Inventory.class);

        if (bundledInventory == null) isNewInventory = true;
    }

    private void cambiarTituloDeVentana() {
        // Cambiar el título según si estamos agregando o editando
        if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                if (isNewInventory) {
                    // Modo agregar
                    activity.getSupportActionBar().setTitle("Add Inventory");
                } else {
                    // Modo editar
                    activity.getSupportActionBar().setTitle("Edit Inventory");
                }
            }
        }
    }

    private void mostrarLosDatosObtenidos(View view) {
        etInventoryName = view.findViewById(R.id.etInventoryName);

        if (!isNewInventory) {
            etInventoryName.setText(bundledInventory.getName());
        }
    }

    private void configurarEdgeToEdge(View view) {
        View layoutButtons = view.findViewById(R.id.layoutButtons);
        EdgeToEdgeHelper.applyBottomMargin(view, layoutButtons, MARGIN_EXTRA_BOTTOM_BUTTONS);
    }

    private void configurarBotones(View view) {
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            if (!isNewInventory) {
                dbOps.updateInventory(bundledInventory.getId(), etInventoryName.getText().toString());
                navegarAListaDeInventarios(view);
            } else {
                dbOps.addInventory(etInventoryName.getText().toString());
                navegarAListaDeInventarios(view);
            }
        });

        btnCancel.setOnClickListener(v -> {
            navegarAListaDeInventarios(view);
        });
    }

    private void navegarAListaDeInventarios(View view) {
        Navigation.findNavController(view)
                .navigate(R.id.action_addedit_to_manage_inventories);
    }

}
