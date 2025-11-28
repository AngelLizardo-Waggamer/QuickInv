package aahl.quickinv.ui.visualize;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import aahl.quickinv.R;
import aahl.quickinv.data.DBOps;
import aahl.quickinv.models.Inventory;
import aahl.quickinv.models.Product;
import aahl.quickinv.ui.visualize.recycler.ProductAdapter;
import aahl.quickinv.utils.EdgeToEdgeHelper;

public class VisualizeFragment extends Fragment {

    private final int MARGIN_EXTRA_BOTTOM_RECYCLERVIEW = 20;

    private RecyclerView recyclerView;
    private Spinner spinner;
    private EditText etSearch;
    private ProductAdapter adapter;
    private List<Inventory> inventories = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>(); // Lista completa sin filtrar

    private DBOps dbOps;
    private boolean isAnyInventoryAvailable = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inicializar la vista
        View view = inflater.inflate(R.layout.fragment_visualize, container, false);

        // Inicializar el contexto de la base de datos
        dbOps = new DBOps(getContext());

        // Inicializar componentes
        recyclerView = view.findViewById(R.id.rvProducts);
        spinner = view.findViewById(R.id.spinnerInventories);
        etSearch = view.findViewById(R.id.etSearch);

        // Poner el margin del recyclerview
        configurarEdgeToEdge(view);

        // Configurar el spinner
        // Dentro de esta función se obtienen los inventarios, y dependiendo de eso lit
        // lo que resta del código hace cositas o nel
        configurarSpinner();

        // Si no hay ningún inventario disponible, mostrar un Toast indicando que no hay inventarios
        if (!isAnyInventoryAvailable) {
            showToast("There are no inventories");
            feedbackNegativoPorNoInventarios();
            return view;
        }

        // Configurar el recyclerview
        configurarRecyclerView();

        // Devolver la vista
        return view;
    }

    private void configurarRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ProductAdapter(products);
        adapter.setListener(this::navegarAEditarProducto);
        recyclerView.setAdapter(adapter);

        // Configurar el TextWatcher para la búsqueda en tiempo real
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementación
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No se necesita implementación
            }

            @Override
            public void afterTextChanged(Editable s) {
                filtrarProductos(s.toString());
            }
        });
    }

    // Agrega margin al recyclerview (Que ya debe de estar asignado) para que no se dibuje detrás de la barra
    // de navegación por gestos.
    private void configurarEdgeToEdge(View view) {
        EdgeToEdgeHelper.applyBottomMargin(view, recyclerView, MARGIN_EXTRA_BOTTOM_RECYCLERVIEW);
    }

    private void configurarSpinner() {
        // Primero obtener si hay inventarios disponibles
        obtenerInventarios();

        // Construir opciones del spinner dependiendo de si hay inventarios o nepe
        ArrayList<String> opciones = isAnyInventoryAvailable ?
                new ArrayList<>(inventories.stream().map(Inventory::getName).toList()):
                new ArrayList<>(List.of("No inventories detected"));

        // Después construir el Adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                opciones
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // Listener para cuando se selecciona un inventario en el spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                manejarInventarioSeleccionado(adapterView, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Pus q se va a hacer aquí si no es posible no seleccionar nada :p
            }
        });
    }

    private void manejarInventarioSeleccionado(AdapterView<?> parent, View view, int position, long id) {

        if (inventories.isEmpty()) return; // Mejor validar si existen o no inventarios directamente

        // Inventario seleccionado
        Inventory selectedInventory = inventories.get(position);

        // Obtener todos los productos de un inventario
        // Actualizar la lista completa (sin filtrar)
        allProducts.clear();
        allProducts.addAll(dbOps.getProductsByInventory(selectedInventory.getId()));

        // Aplicar el filtro actual (si existe texto en la búsqueda)
        String currentQuery = etSearch.getText().toString();
        if (currentQuery.isEmpty()) {
            // Si no hay búsqueda, mostrar todos los productos
            products.clear();
            products.addAll(allProducts);
        } else {
            // Si hay búsqueda activa, re-aplicar el filtro
            filtrarProductos(currentQuery);
        }

        // Lógica para notificar al recyclerView que el dataset de productos cambió
        adapter.notifyDataSetChanged();
    }

    private void navegarAEditarProducto(int position){
        // Validar que la posición es válida
        if (position < 0 || position >= products.size()) return;

        // Obtener el producto seleccionado
        Product selectedProduct = products.get(position);

        // Obtener el inventario actual seleccionado en el spinner
        int selectedInventoryPosition = spinner.getSelectedItemPosition();
        if (selectedInventoryPosition < 0 || selectedInventoryPosition >= inventories.size()) return;

        long selectedInventoryId = inventories.get(selectedInventoryPosition).getId();

        // Crear el Bundle con los datos necesarios
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", selectedProduct);
        bundle.putLong("id_inventory", selectedInventoryId);
        bundle.putString("source", "visualize"); // Indicar el origen de la navegación

        // Navegar a la pantalla de edición
        Navigation.findNavController(requireView())
                .navigate(R.id.action_visualize_to_add_edit_product, bundle);
    }

    /**
     * Filtra la lista de productos basándose en el texto de búsqueda.
     * La búsqueda es case-insensitive y busca coincidencias parciales en el nombre del producto.
     *
     * @param query Texto de búsqueda ingresado por el usuario
     */
    private void filtrarProductos(String query) {
        // Limpiar la lista de productos
        products.clear();

        // Si el query está vacío, mostrar todos los productos
        if (query == null || query.trim().isEmpty()) {
            products.addAll(allProducts);
        } else {
            // Convertir el query a minúsculas para búsqueda case-insensitive
            String queryLowerCase = query.toLowerCase().trim();

            // Filtrar productos que contengan el query en su nombre
            for (Product product : allProducts) {
                if (product.getName().toLowerCase().contains(queryLowerCase)) {
                    products.add(product);
                }
            }
        }

        // Notificar al adapter que los datos han cambiado
        adapter.notifyDataSetChanged();
    }

    private void obtenerInventarios() {
        // SELECT de la base de datos
        inventories = dbOps.getAllInventories();

        // Si no hay inventarios, marcar esa flag
        isAnyInventoryAvailable = !inventories.isEmpty();
    }

    private void feedbackNegativoPorNoInventarios() {
        // Deshabilitar la barra de búsqueda y el recyclerview
        // Ambos se ponen de color gris por el selector
        etSearch.setEnabled(false);
        recyclerView.setEnabled(false);
    }

    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
