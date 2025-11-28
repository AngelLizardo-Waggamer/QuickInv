package aahl.quickinv.ui.manage.products.audit;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import aahl.quickinv.R;

public class AuditingProductViewHolder extends RecyclerView.ViewHolder{

    public CheckBox cbAudit;
    public TextView tvAuditName;
    public EditText etAuditQty;

    public AuditingProductViewHolder(@NonNull View itemView, CompoundButton.OnCheckedChangeListener listener) {
        super(itemView);
        cbAudit = itemView.findViewById(R.id.cbAudit);
        tvAuditName = itemView.findViewById(R.id.tvAuditName);
        etAuditQty = itemView.findViewById(R.id.etAuditQty);

        if (listener != null) {
            cbAudit.setOnCheckedChangeListener(listener);
        }
    }

    /**
     * Obtiene la cantidad actual ingresada en el EditText.
     * Retorna 0 si el campo está vacío o tiene un valor inválido.
     */
    public int getQuantityFromEditText() {
        String text = etAuditQty.getText().toString().trim();
        if (text.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
