package com.gerray.fmsystem.ManagerModule;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gerray.fmsystem.ManagerModule.WorkOrder.DetailsClass;

import com.gerray.fmsystem.ManagerModule.WorkOrder.WorkViewHolder;
import com.gerray.fmsystem.R;


import com.gerray.fmsystem.Transactions.Mpesa.MPESAExpressActivity;
import com.gerray.fmsystem.Transactions.PayPal.PaypalActivity;
import com.gerray.fmsystem.Transactions.TransactionDetails;
import com.gerray.fmsystem.Transactions.TransactionViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TransactionActivity extends AppCompatActivity {
    private DatabaseReference reference;
    FirebaseRecyclerAdapter<DetailsClass, WorkViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<DetailsClass> options;
    FirebaseRecyclerOptions<TransactionDetails> firebaseRecyclerOptions;
    FirebaseRecyclerAdapter<TransactionDetails, TransactionViewModel> adapter;
    FirebaseUser firebaseUser;

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
        firebaseRecyclerAdapter.notifyDataSetChanged();

        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessee_pay);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Work Orders");
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Transactions");

        options = new FirebaseRecyclerOptions.Builder<DetailsClass>().setQuery(databaseReference, DetailsClass.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DetailsClass, WorkViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final WorkViewHolder holder, int position, @NonNull final DetailsClass model) {
                if (model.getfManagerID() != null) {
                    if (model.getfManagerID().equals(firebaseUser.getUid())) {
                        holder.tvStatus.setText(model.getStatus());
                        holder.tvWork.setText(model.getWorkDescription());
                        holder.tvWorkDate.setText(model.getWorkDate());
                        reference = FirebaseDatabase.getInstance().getReference().child("Contractor").child(model.getConsultantID());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.tvConsultant.setText(Objects.requireNonNull(snapshot.child("consultantName").getValue()).toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }

                        });
                        holder.itemView.setOnClickListener(v -> {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(TransactionActivity.this);
                            alertDialog.setMessage("Choose the payment method you will use")
                                    .setCancelable(false)
                                    .setPositiveButton("Lipa na Mpesa", (dialog, which) -> startActivity(new Intent(TransactionActivity.this, MPESAExpressActivity.class)
                                            .putExtra("workID", model.getWorkID())))
                                    .setNegativeButton("Pay with PayPal", (dialog, which) -> {
                                        startActivity(new Intent(TransactionActivity.this, PaypalActivity.class)
                                                .putExtra("amount", model.getWorkID()));
                                    });

                            AlertDialog alert = alertDialog.create();
                            alert.setTitle("Payment Method");
                            alert.show();
                        });

                    } else {
                        holder.itemView.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                        params.height = 0;
                        params.width = 0;
                        holder.itemView.setLayoutParams(params);
                    }
                } else {
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }


            }

            @NonNull
            @Override
            public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new WorkViewHolder(LayoutInflater.from(TransactionActivity.this).inflate(R.layout.work_card, parent, false));
            }
        };

        RecyclerView recyclerView = findViewById(R.id.recycler_view_transWork);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<TransactionDetails>().setQuery(reference1, TransactionDetails.class).build();
        adapter = new FirebaseRecyclerAdapter<TransactionDetails, TransactionViewModel>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull TransactionViewModel holder, int position, @NonNull TransactionDetails model) {
                if (model.getPayerID().equals(firebaseUser.getUid())) {
                    holder.transDate.setText(model.getTransactionDate());
                    holder.transDesc.setText(model.getTransactionDescription());
                    holder.transAmount.setText(model.getCost());
                    holder.personType.setText("Paid to:");

                    String consID = model.getPayeeID();
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(consID);
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String firstName = null, secondName = null;
                            if (snapshot.child("firstName").exists()) {
                                firstName = Objects.requireNonNull(snapshot.child("firstName").getValue()).toString().trim();
                            }
                            if (snapshot.child("secondName").exists()) {
                                secondName = Objects.requireNonNull(snapshot.child("secondName").getValue()).toString().trim();
                            }

                            final String payeeName = firstName + " " + secondName;
                            holder.payee.setText(payeeName);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if (model.getPayeeID().equals(firebaseUser.getUid())) {
                    holder.transDate.setText(model.getTransactionDate());
                    holder.transDesc.setText(model.getTransactionDescription());
                    holder.transAmount.setText(model.getCost());
                    holder.personType.setText("Received from:");

                    String lesseeID = model.getPayerID();
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(lesseeID);
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String firstName = null, secondName = null;
                            if (snapshot.child("firstName").exists()) {
                                firstName = Objects.requireNonNull(snapshot.child("firstName").getValue()).toString().trim();
                            }
                            if (snapshot.child("secondName").exists()) {
                                secondName = Objects.requireNonNull(snapshot.child("secondName").getValue()).toString().trim();
                            }

                            final String payeeName = firstName + " " + secondName;
                            holder.payee.setText(payeeName);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }
            }

            @NonNull
            @Override
            public TransactionViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new TransactionViewModel(LayoutInflater.from(TransactionActivity.this).inflate(R.layout.transaction_card, parent, false));
            }
        };

        RecyclerView recyclerView1 = findViewById(R.id.recycler_view_transactions);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(adapter);
        adapter.startListening();

    }

}