package com.jabat.personal.unipiplishopping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

// Αυτή η κλάση είναι ένας RecyclerView Adapter για την εμφάνιση μιας λίστας προϊόντων.
// Κάθε προϊόν εμφανίζεται σε ένα στοιχείο λίστας που περιέχει τίτλο, περιγραφή, τιμή, κωδικό προϊόντος και ημερομηνία κυκλοφορίας μαζί με ένα κουμπί που επιτρέπει στους χρήστες να παραγγείλουν το προϊόν.

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;

    // Constructor για την αρχικοποίηση της λίστας προϊόντων και του context από το οποίο καλείται η ProductAdapter.
    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.items, parent, false);
        return new ProductViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Παίρνει το τρέχον προϊόν από τη λίστα και ενημερώνει τα δεδομένα της εμφάνισης με τις τιμές του προϊόντος.
        Product product = productList.get(position);
        holder.title.setText(product.getTitle());
        holder.description.setText(product.getDescription());
        holder.price.setText(context.getString(R.string.price_label) + " " + product.getPrice());
        holder.code.setText(product.getCode());
        holder.releaseDate.setText(product.getReleaseDate());

        //αν το προϊόν δεν είναι διαθέσιμο αλλάζει την λειτουργία και το χρώμα
        if (!product.isAvailable()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.btnOrder.setEnabled(false);
            holder.btnOrder.setAlpha(0.5f);
        }

        //αν πατηθεί το κουμπί παραγγελίας του συγκεκριμένου προϊόντος
        holder.btnOrder.setOnClickListener(v -> {
            String productCode = product.getCode();
            String username = User.getUsername();
            long timestamp = System.currentTimeMillis();

            //δημιουργεί ένα νέο αντικείμενο παραγγελίας.
            Order order = new Order(productCode, username, timestamp);

            //αποθηκεύει στην βάση και αλλάζει το availability του product σε false
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ordersRef = database.getReference("orders");

            String orderKey = ordersRef.push().getKey();
            if (orderKey != null) {
                ordersRef.child(orderKey).setValue(order)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(context, R.string.toast_order_success, Toast.LENGTH_SHORT).show();

                            // Update product availability
                            DatabaseReference productRef = database.getReference("products").child(product.getKey());
                            productRef.child("available").setValue(false)
                                    .addOnSuccessListener(unused2 -> {
                                        product.setAvailable(false);
                                        notifyItemChanged(position);
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Failed to update product availability: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Failed to create order: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView code, title, description, releaseDate, price;
        ImageView btnOrder;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.txtCode);
            title = itemView.findViewById(R.id.txtTitle);
            description = itemView.findViewById(R.id.txtDescription);
            releaseDate = itemView.findViewById(R.id.txtReleaseDate);
            price = itemView.findViewById(R.id.txtPrice);
            btnOrder = itemView.findViewById(R.id.btnOrder);
        }
    }
}
