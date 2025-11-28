package aahl.quickinv.ui.manage.products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import aahl.quickinv.R;
import aahl.quickinv.data.DBOps;
import aahl.quickinv.models.Inventory;

public class ManageProducts extends Fragment {

    private Spinner spinnerInventarios;
    private List<Inventory> inventories = new ArrayList<>();
    private Inventory selectedInventory;
    private CardView cardModifyProducts, cardAuditProducts;
    private boolean isAnyInventoryAvailable = false;

    private DBOps dbOps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.hub_products, container, false);

        // Inicializar el contexto de la base de datos
        dbOps = new DBOps(getContext());

        // Inicializar componentes
        spinnerInventarios = view.findViewById(R.id.spinnerHubInventory);
        cardModifyProducts = view.findViewById(R.id.cardModifyProducts);
        cardAuditProducts = view.findViewById(R.id.cardAuditProducts);

        // Configurar el spinner
        // A como en el visualize, de una correcta inicialización depende
        // el resto del código
        configurarSpinner(view);

        // De base quitar la navegación e implementar los listeners por seguridad, solo
        // hasta después de seleccionar un inventario se configura.
        configurarListenersCards(view);
        deshabilitarNavegacion();

        // Si no hay inventarios mostrarlo en un toast
        if (!isAnyInventoryAvailable) {
            showToast("There are no inventories");
        }

        return view;
    }

    private void configurarSpinner(View view) {
        // Obtener los inventarios de la BD
        obtenerInventarios();

        // Construir las opciones del spinner
        ArrayList<String> opciones = isAnyInventoryAvailable ?
                new ArrayList<>(inventories.stream().map(Inventory::getName).toList()):
                new ArrayList<>(List.of("No inventories detected"));

        // Después construir el adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                opciones
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInventarios.setAdapter(spinnerAdapter);

        // Configuración del listener
        spinnerInventarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                manejarInventarioSeleccionado(adapterView, view, i, l);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nada :p
            }
        });
    }

    private void manejarInventarioSeleccionado(AdapterView<?> parent, View view, int position, long id) {

        if (inventories.isEmpty()) return; // Validar que la lista de inventarios no esté vacía, si sí, no se ejecuta directamente.

        selectedInventory = inventories.get(position);
        habilitarNavegacion();
    }

    private void configurarListenersCards(View view) {
        cardModifyProducts.setOnClickListener(v -> {
            // Lógica de navegación hacia productos detailed

            Bundle bundle = new Bundle();
            bundle.putLong("id_inventory", selectedInventory.getId());
            Navigation.findNavController(view)
                    .navigate(R.id.action_manage_products_to_products_modify, bundle);
        });

        cardAuditProducts.setOnClickListener(v -> {
            // Lógica de navegación hacia audit_products

            Bundle bundle = new Bundle();
            bundle.putLong("id_inventory", selectedInventory.getId());
            Navigation.findNavController(view)
                    .navigate(R.id.action_manage_products_to_audit_products, bundle);
        });
    }

    private void obtenerInventarios() {
        // SELECT de la base de datos
        inventories = dbOps.getAllInventories();

        // Si no hay inventarios, marcar esa flag
        isAnyInventoryAvailable = !inventories.isEmpty();
    }

    private void habilitarNavegacion() {
        cardModifyProducts.setEnabled(true);
        cardModifyProducts.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        cardAuditProducts.setEnabled(true);
        cardAuditProducts.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
    }

    private void deshabilitarNavegacion() {
        cardModifyProducts.setEnabled(false);
        cardModifyProducts.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface_disabled));
        cardAuditProducts.setEnabled(false);
        cardAuditProducts.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface_disabled));
    }

    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
