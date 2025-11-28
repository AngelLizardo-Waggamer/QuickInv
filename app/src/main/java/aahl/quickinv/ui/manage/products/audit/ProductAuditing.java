package aahl.quickinv.ui.manage.products.audit;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import aahl.quickinv.R;
import aahl.quickinv.data.DBOps;
import aahl.quickinv.models.Product;
import aahl.quickinv.utils.EdgeToEdgeHelper;

public class ProductAuditing extends Fragment {

    private final int MARGIN_EXTRA_BOTTOM_RECYCLERVIEW = 20;

    private RecyclerView recyclerView;
    private AuditingProductAdapter adapter;
    private List<Product> products;
    private int changedElementsCount = 0;
    private boolean hasAnyChangeBeenMade = false;
    private long id_inventory;
    private DBOps dbOps;

    // Antes de crear la vista
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_audit_products, container, false);

        // Obtener el ID del inventario
        obtenerIdDeInventario();

        // Inicializar la base de datos
        dbOps = new DBOps(getContext());

        // Obtener los productos
        obtenerProductos();

        // Configurar el RecyclerView
        configurarRecyclerView(view);

        // Configurar los botones
        configurarBotones(view);

        return view;
    }

    // Justo después de su creación
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar un listener para cuando se quiere navegar fuera de la pantalla
        configurarListenerDeNavegacionBack();

        // Configurar otro listener pero para cuando se presiona el botón de la toolbar para ir atrás
        configurarListenerDeBotonDelToolbar();

    }

    private void obtenerIdDeInventario() {
        if (getArguments() == null) return;
        id_inventory = getArguments().getLong("id_inventory");
    }

    private void obtenerProductos() {
        products = dbOps.getProductsByInventory(id_inventory);
    }

    private void configurarRecyclerView(View view) {
        // Fetch al elemento y asignar un layoutManager
        recyclerView = view.findViewById(R.id.rvChecklist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Crear el adapter y definir el listener para la checkbox
        adapter = new AuditingProductAdapter(products);
        adapter.setListener(((compoundButton, isChecked) -> {
            // Activar la flag de que hay cambios para mostrar el diálogo de confirmación de salirse
            hasAnyChangeBeenMade = true;

            if (isChecked) {
                changedElementsCount++;
            } else {
                changedElementsCount--;
            }
        }));

        // Agregar listener para detectar cambios en el EditText
        adapter.setTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No necesitamos hacer nada aquí
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Marcar que se ha hecho un cambio
                hasAnyChangeBeenMade = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No necesitamos hacer nada aquí
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void configurarEdgeToEdge(View view) {
        EdgeToEdgeHelper.applyBottomMargin(view, recyclerView, MARGIN_EXTRA_BOTTOM_RECYCLERVIEW);
    }

    private void configurarBotones(View view) {
        Button btnCancelAudit = view.findViewById(R.id.btnCancelAudit);
        Button btnSaveAudit = view.findViewById(R.id.btnSaveAudit);

        // Descartar
        btnCancelAudit.setOnClickListener(v -> {
            if (hasAnyChangeBeenMade) {
                dialogoDeConfirmacionDeSalida(getContext());
            } else {
                navegarAlHubDeProductos(view);
            }
        });

        // Guardar
        btnSaveAudit.setOnClickListener(v -> {
            if (!hasAnyChangeBeenMade) {
                showToast("No changes have been made");
                return;
            }

            if (changedElementsCount != products.size()) {
                showToast("Please review all products");
                return;
            }

            long currentTime = System.currentTimeMillis();

            // Recorrer todos los productos y actualizar con las cantidades de los EditText
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);

                // Obtener el ViewHolder para acceder al EditText
                AuditingProductViewHolder holder = (AuditingProductViewHolder)
                        recyclerView.findViewHolderForAdapterPosition(i);

                if (holder != null) {
                    // Obtener la cantidad del EditText
                    int newQuantity = holder.getQuantityFromEditText();

                    // Actualizar el producto en la BD con la nueva cantidad
                    dbOps.updateProduct(
                            product.getId(),
                            product.getName(),
                            newQuantity,
                            product.getUnitPrice()
                    );
                }

                // Actualizar la fecha de verificación
                dbOps.checkProduct(product.getId(), currentTime);
            }

            showToast("Changes saved correctly");
            navegarAlHubDeProductos(view);
        });
    }

    private void navegarAlHubDeProductos(View view) {
        Navigation.findNavController(view)
                .navigate(R.id.action_audit_products_to_manage_products);
    }

    private void configurarListenerDeNavegacionBack() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (hasAnyChangeBeenMade) {
                    dialogoDeConfirmacionDeSalida(getContext());
                } else {
                    navegarAlHubDeProductos(getView());
                }
            }
        });
    }

    private void configurarListenerDeBotonDelToolbar() {
        // Usar MenuProvider para interceptar el clic en el botón "atrás" de la toolbar
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // No necesitamos inflar ningún menú adicional
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // Interceptar el clic en el botón "atrás" de la toolbar (android.R.id.home)
                if (menuItem.getItemId() == android.R.id.home) {
                    if (hasAnyChangeBeenMade) {
                        dialogoDeConfirmacionDeSalida(getContext());
                        return true; // Indicar que hemos manejado el evento
                    }
                    // Si no hay cambios, dejar que la navegación normal ocurra
                    return false;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private AlertDialog dialogoDeConfirmacionDeSalida(Context context) {
        return new AlertDialog.Builder(context)
                .setTitle("Discard changes")
                .setMessage("Are you sure you want to discard the changes?")
                .setPositiveButton("Yes", (dialog, which) -> navegarAlHubDeProductos(getView()))
                .setNegativeButton("No", null)
                .setCancelable(false)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
