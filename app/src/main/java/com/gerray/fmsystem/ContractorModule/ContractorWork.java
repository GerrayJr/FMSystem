package com.gerray.fmsystem.ContractorModule;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gerray.fmsystem.LesseeModule.LesseeDetailsClass;
import com.gerray.fmsystem.ManagerModule.WorkOrder.DetailsClass;
import com.gerray.fmsystem.ManagerModule.WorkOrder.WorkViewHolder;
import com.gerray.fmsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ContractorWork extends Fragment {

    private DatabaseReference reference, dbRef;
    FirebaseRecyclerAdapter<DetailsClass, WorkViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<LesseeDetailsClass, WorkViewHolder> adapter;
    FirebaseRecyclerOptions<LesseeDetailsClass> firebaseRecyclerOptions;
    FirebaseRecyclerOptions<DetailsClass> options;
    FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public ContractorWork() {
    }

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
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = auth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consultant_work, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Work Orders");

        options = new FirebaseRecyclerOptions.Builder<DetailsClass>().setQuery(databaseReference, DetailsClass.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DetailsClass, WorkViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final WorkViewHolder holder, int position, @NonNull final DetailsClass model) {
                if (model.getConsultantID().equals(firebaseUser.getUid())) {
                    holder.tvStatus.setText(model.getStatus());
                    holder.tvWork.setText(model.getWorkDescription());
                    holder.tvWorkDate.setText(model.getWorkDate());
                    String fManagerID = model.getfManagerID();
                    if (fManagerID != null) {
                        reference = FirebaseDatabase.getInstance().getReference().child("Facilities").child(model.getfManagerID());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.tvConsultant.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), EditWorkDetails.class);
                    intent.putExtra("workID", model.getWorkID());
                    startActivity(intent);
                });


            }

            @NonNull
            @Override
            public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new WorkViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.cons_work_card, parent, false));
            }
        };

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<LesseeDetailsClass>().setQuery(databaseReference, LesseeDetailsClass.class).build();
        adapter = new FirebaseRecyclerAdapter<LesseeDetailsClass, WorkViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull WorkViewHolder holder1, int position, @NonNull LesseeDetailsClass model) {
                if (model.getConsultantID().equals(firebaseUser.getUid())) {
                    holder1.tvStatus.setText(model.getStatus());
                    holder1.tvWork.setText(model.getWorkDescription());
                    holder1.tvWorkDate.setText(model.getWorkDate());
                    String lesseeID = model.getLesseeID();
                    if (lesseeID != null) {
                        dbRef = FirebaseDatabase.getInstance().getReference().child("Lessees").child(model.getLesseeID());
                        dbRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder1.tvConsultant.setText(Objects.requireNonNull(snapshot.child("contactName").getValue()).toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    holder1.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), EditWorkDetails.class);
                        intent.putExtra("workID", model.getWorkID());
                        startActivity(intent);
                    });
                }
            }

            @NonNull
            @Override
            public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new WorkViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.cons_work_card, parent, false));
            }
        };


        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_consWork);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


        return view;
    }
}