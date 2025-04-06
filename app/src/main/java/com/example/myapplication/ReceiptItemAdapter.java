package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ReceiptItemAdapter extends RecyclerView.Adapter<ReceiptItemAdapter.ViewHolder> {

    public static class ReceiptItem {
        public String name;
        public int quantity;
        public String unit;
        public boolean isSelected;

        public ReceiptItem(String name) {
            this.name = name;
            this.quantity = 1;
            this.unit = "개";
            this.isSelected = true;
        }
    }

    private final List<ReceiptItem> items;

    public ReceiptItemAdapter(List<String> itemNames) {
        this.items = new ArrayList<>();
        for (String name : itemNames) {
            if (name.trim().isEmpty()) continue;
            name = name.replaceAll("[^가-힣a-zA-Z0-9\\s]", "");  // 특수기호 제거
            if (!name.isEmpty()) {
                this.items.add(new ReceiptItem(name));
            }
        }
    }

    public List<ReceiptItem> getSelectedItems() {
        List<ReceiptItem> selected = new ArrayList<>();
        for (ReceiptItem item : items) {
            if (item.isSelected) selected.add(item);
        }
        return selected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReceiptItem item = items.get(position);

        holder.checkBox.setChecked(item.isSelected);
        holder.editText.setText(item.name);
        holder.spinnerQuantity.setSelection(item.quantity - 1);
        holder.spinnerUnit.setSelection(holder.unitAdapter.getPosition(item.unit));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.isSelected = isChecked;
        });

        holder.editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                item.name = holder.editText.getText().toString();
            }
        });

        holder.spinnerQuantity.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                item.quantity = pos + 1;
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        holder.spinnerUnit.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                item.unit = parent.getItemAtPosition(pos).toString();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        EditText editText;
        Spinner spinnerQuantity;
        Spinner spinnerUnit;
        ArrayAdapter<String> unitAdapter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_item);
            editText = itemView.findViewById(R.id.edit_text_name);
            spinnerQuantity = itemView.findViewById(R.id.spinner_quantity);
            spinnerUnit = itemView.findViewById(R.id.spinner_unit);

            ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(
                    itemView.getContext(), android.R.layout.simple_spinner_item, getQuantityOptions());
            quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerQuantity.setAdapter(quantityAdapter);

            unitAdapter = new ArrayAdapter<>(
                    itemView.getContext(), android.R.layout.simple_spinner_item, getUnitOptions());
            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerUnit.setAdapter(unitAdapter);
        }

        private List<Integer> getQuantityOptions() {
            List<Integer> options = new ArrayList<>();
            for (int i = 1; i <= 10; i++) options.add(i);
            return options;
        }

        private List<String> getUnitOptions() {
            List<String> units = new ArrayList<>();
            units.add("개");
            units.add("팩");
            units.add("ml");
            units.add("l");
            units.add("g");
            return units;
        }
    }
}
