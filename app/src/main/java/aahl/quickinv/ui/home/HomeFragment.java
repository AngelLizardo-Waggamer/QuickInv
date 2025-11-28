package aahl.quickinv.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import aahl.quickinv.R;
import aahl.quickinv.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private final int PADDING_BOTTOM_DISCLAIMER = 20;
    private ImageView ivFondoHome;
    private Button btnManage, btnVisualize;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inicializar la vista
       View view = inflater.inflate(R.layout.fragment_home, container, false);

       // Inicializar el fondo
       aplicarBlurAlFondo(view);

       // Configurar edge-to-edge para que el contenido se dibuje detrás de la barra de navegación
       configurarEdgeToEdge(view);

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

    private void configurarEdgeToEdge(View view) {
        // Obtener el TextView del disclaimer
        TextView tvDisclaimer = view.findViewById(R.id.tvDisclaimerBottomHome);

        // Aplicar listener de insets para manejar solo la barra de navegación inferior
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            // Solo obtener los insets de la barra de navegación, NO del status bar
            Insets navBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());

            // Aplicar padding solo en la parte inferior para el disclaimer
            // Esto hará que el texto tenga un padding adicional para no quedar escondido
            // completamente detrás de la barra de navegación por gestos
            tvDisclaimer.setPadding(
                tvDisclaimer.getPaddingLeft(),
                tvDisclaimer.getPaddingTop(),
                tvDisclaimer.getPaddingRight(),
                navBarInsets.bottom + PADDING_BOTTOM_DISCLAIMER
            );

            return windowInsets;
        });
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