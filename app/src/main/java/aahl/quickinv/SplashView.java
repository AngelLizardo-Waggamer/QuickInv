package aahl.quickinv;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

import aahl.quickinv.R;

public class SplashView extends AppCompatActivity {

    private static final int SPLASH_DURATION = 5000; // Longitud máxima de la animación (ms)
    private Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnableNavegarAMain = new Runnable() {
        @Override
        public void run() {
            navigateToMain();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Objeto de lottie
        LottieAnimationView animationView = findViewById(R.id.animationView);

        // Listener para cuando se acaba la animación o algo pasa.
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Cancelar el handler para que no navegue otra vez a main después
                handler.removeCallbacks(runnableNavegarAMain);

                navigateToMain();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                // No se necesita implementación
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                navigateToMain();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // No se necesita implementación
            }
        });

        // Si la animación pasa del tiempo que se estableció, se navega directamente a la otra pantalla.
        handler.postDelayed(runnableNavegarAMain, SPLASH_DURATION);
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashView.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
