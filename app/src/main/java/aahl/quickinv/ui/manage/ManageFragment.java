package aahl.quickinv.ui.manage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import aahl.quickinv.R;

public class ManageFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inicializar la vista
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        // Agregar listeners a los cardviews
        configurarListenersEnBotones(view);

        // Devolver la vista
        return view;
    }

    private void configurarListenersEnBotones(View view) {
        CardView cardManageProducts = view.findViewById(R.id.cardManageProducts);
        CardView cardManageInventories = view.findViewById(R.id.cardManageInventories);

        cardManageProducts.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_nav_manage_to_manage_products);
        });

        cardManageInventories.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_nav_manage_to_manage_inventories);
        });
    }
}
