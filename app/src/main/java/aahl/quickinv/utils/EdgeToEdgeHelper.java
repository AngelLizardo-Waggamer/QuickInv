package aahl.quickinv.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Clase utilitaria para manejar edge-to-edge en los elementos de la UI.
 * Aplica márgenes inferiores a las vistas para evitar que se oculten detrás
 * de la barra de navegación por gestos.
 */
public class EdgeToEdgeHelper {

    /**
     * Aplica un margen inferior a una vista basándose en los insets de la barra de navegación.
     *
     * @param rootView Vista raíz donde se aplicará el listener de insets
     * @param targetView Vista a la que se le aplicará el margen inferior
     * @param extraMargin Margen adicional en píxeles a agregar además del inset
     */
    public static void applyBottomMargin(View rootView, View targetView, int extraMargin) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets navBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) targetView.getLayoutParams();
            params.setMargins(
                    params.leftMargin,
                    params.topMargin,
                    params.rightMargin,
                    navBarInsets.bottom + extraMargin
            );
            targetView.setLayoutParams(params);

            return windowInsets;
        });
    }

    /**
     * Aplica un padding inferior a una vista basándose en los insets de la barra de navegación.
     *
     * @param rootView Vista raíz donde se aplicará el listener de insets
     * @param targetView Vista a la que se le aplicará el padding inferior
     * @param extraPadding Padding adicional en píxeles a agregar además del inset
     */
    public static void applyBottomPadding(View rootView, View targetView, int extraPadding) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets navBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());

            targetView.setPadding(
                    targetView.getPaddingLeft(),
                    targetView.getPaddingTop(),
                    targetView.getPaddingRight(),
                    navBarInsets.bottom + extraPadding
            );

            return windowInsets;
        });
    }
}

