package aahl.quickinv.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import aahl.quickinv.R;
import aahl.quickinv.databinding.FragmentHomeBinding;
import aahl.quickinv.utils.BackBlurHandler;

public class HomeFragment extends Fragment {

    private ImageView ivFondoHome;
    private Button btnManage, btnVisualize;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       // Inicializar la vista
       View view = inflater.inflate(R.layout.fragment_home, container, false);

       // Inicializar el fondo
       aplicarBlurAlFondo(view);

       // Configurar listeners para que se pueda navegar a las otras pantallas.
       configurarListenersEnBotones(view);

       // Devolver la vista inicializada
       return view;
    }

    private void aplicarBlurAlFondo(View view){
        ivFondoHome = view.findViewById(R.id.ivFondoHome);

        Bitmap imgFondo = BackBlurHandler.getBlurredBackground(requireContext());
        ivFondoHome.setImageBitmap(imgFondo);
    }


    private void configurarListenersEnBotones(View view){
        btnManage = view.findViewById(R.id.btnNavegarManage);
        btnVisualize = view.findViewById(R.id.btnNavegarVisualizer);

        // setup para navegar a manage con el comportamiento correcto definido en el XML
        btnManage.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_nav_home_to_nav_manage);
        });

        // setup para navegar a visualize con el comportamiento correcto definido en el XML
        btnVisualize.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_nav_home_to_nav_visualize);
        });

    }
}