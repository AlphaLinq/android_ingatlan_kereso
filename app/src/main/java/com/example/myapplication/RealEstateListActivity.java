package com.example.myapplication;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class RealEstateListActivity extends AppCompatActivity {

    private static final String LOG_TAG = RealEstateListActivity.class.getName();
    int gridNumber = 1;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private boolean viewRow = true;

    private RecyclerView mRecyclerView;
    private ArrayList<RealEstateItem> mItemList;
    private RealEstateAdapter mAdapter;

    private FirebaseFirestore mFireStore;
    private CollectionReference mItems;

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_real_estate_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        }else{
            Log.d(LOG_TAG, "Not authenticated user!");
            finish();
        }


        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();
        mAdapter = new RealEstateAdapter(this,mItemList);

        mRecyclerView.setAdapter(mAdapter);

        mFireStore = FirebaseFirestore.getInstance();
        mItems = mFireStore.collection("Items");


        queryData();
        initializeData();

    }

    private void queryData() {
        mItemList.clear();

        //mItems.whereEqualTo()
        mItems.orderBy("name").limit(5).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                RealEstateItem item = doc.toObject(RealEstateItem.class);
                mItemList.add(item);
            }

            if (mItemList.isEmpty()) {
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    private void deleteItem(RealEstateItem item){

    }

    private void updateItem(RealEstateItem item){

    }

    private void initializeData() {
        String[] itemList = getResources().getStringArray(R.array.real_estate_titles);
        String[] itemDescription = getResources().getStringArray(R.array.shopping_item_desc);
        String[] itemPrice = getResources().getStringArray(R.array.shopping_item_price);
        String[] itemRooms = getResources().getStringArray(R.array.shopping_item_rooms);
        String[] itemPhone = getResources().getStringArray(R.array.phoneNums);
        TypedArray itemImageUrl = getResources().obtainTypedArray(R.array.shopping_item_images);

        //mItemList.clear();

        for (int i = 0; i < itemList.length; i++) {
            mItems.add(new RealEstateItem(itemList[i],
                    itemDescription[i],
                    itemPrice[i],
                    itemRooms[i],
                    itemPhone[i],
                    itemImageUrl.getResourceId(i, 0)));
        }

        itemImageUrl.recycle();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.estate_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.log_out) {
            Log.d(LOG_TAG, "Logout clicked!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (itemId == R.id.setting_button) {
            Log.d(LOG_TAG, "Setting clicked!");
            // Consider if you really want to sign out here as well,
            // or if this was a copy-paste error.
            // FirebaseAuth.getInstance().signOut();
            // finish();
            return true;
        } else if (itemId == R.id.view_selector) {
            if (viewRow) {
                changeSpanCount(item, R.drawable.baseline_apps_24, 1);
            } else {
                changeSpanCount(item, R.drawable.baseline_format_align_justify_24, 2);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

}