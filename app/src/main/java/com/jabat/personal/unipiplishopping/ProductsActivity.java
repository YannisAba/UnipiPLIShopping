package com.jabat.personal.unipiplishopping;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//εμφανίζει μια λίστα προϊόντων από το Realtime Database.
//χρησιμοποιεί RecyclerView για την προβολή των δεδομένων και ProductAdapter για την σύνδεση των δεδομένων με το ui.

public class ProductsActivity extends BaseActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    List<Product> productList = new ArrayList<>();
    RecyclerView recyclerView;
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_products);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        applyBackgroundColor(R.id.main);

        recyclerView = findViewById(R.id.rvProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //σύνδεση του adapter με τη λίστα των προϊόντων.
        adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("products");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //δύο ξεχωριστές λίστες για διαθέσιμα και μη διαθέσιμα προϊόντα για διαφορετική ταξινόμηση.
                List<Product> availableProducts = new ArrayList<>();
                List<Product> unavailableProducts = new ArrayList<>();

                //αδειάζει τη λίστα για να φορτωθούν τα νέα δεδομένα.
                productList.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null && product.isAvailable()) {
                        product.setKey(productSnapshot.getKey());
                        availableProducts.add(product);
                    } else {
                        unavailableProducts.add(product);
                    }
                }
                //συνδυάζει τα διαθέσιμα και μη διαθέσιμα προϊόντα στη λίστα για να τα εμφανίσει.
                if(unavailableProducts.isEmpty()){
                    productList.addAll(availableProducts);
                }else{
                    productList.addAll(availableProducts);
                    productList.addAll(unavailableProducts);
                }
                //ενημερώνει τον RecyclerView όταν γίνουν αλλαγές στην βάση για να αλλάξουν στο ui.
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductsActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}