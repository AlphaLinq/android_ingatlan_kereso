package com.example.myapplication;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


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

    private NotificationHandler mNotificationHandler;

    private JobScheduler mJobScheduler;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_estate_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            Log.e(LOG_TAG, "Toolbar is null!");
        } else {
            setSupportActionBar(toolbar);
        }

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

        mNotificationHandler = new NotificationHandler(this);
        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        setJobScheduler();


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setJobScheduler() {
        int networkType = JobInfo.NETWORK_TYPE_UNMETERED;
        int hardDeadLine = 5000;

        ComponentName componentName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(0, componentName);
        builder.setRequiredNetworkType(networkType);
        builder.setOverrideDeadline(hardDeadLine);
        mJobScheduler.schedule(builder.build());

    }

    private void queryData() {
        mItemList.clear();

        //mItems.whereEqualTo()
        mItems.orderBy("address").limit(5).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                RealEstateItem item = doc.toObject(RealEstateItem.class);
                item.setId(doc.getId());
                mItemList.add(item);
            }

            if (mItemList.isEmpty()) {
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });

    }

    public void deleteItem(RealEstateItem item){
        DocumentReference ref = mItems.document(item._getId());
        ref.delete().addOnSuccessListener(unused -> {
            mItemList.remove(item);
            mAdapter.notifyDataSetChanged();
        })
        .addOnFailureListener(e -> Log.w(LOG_TAG, "Error deleting document", e));

        mNotificationHandler.send(item.getPhoneNumber() + "sikeresen törölve");
        queryData();
    }

    private void updateItem(RealEstateItem item){
        //TODO?
    }

    private void initializeData() {
        String[] itemAddress = getResources().getStringArray(R.array.real_estate_titles);
        String[] itemDescription = getResources().getStringArray(R.array.shopping_item_desc);
        String[] itemBaseArea = getResources().getStringArray(R.array.baseArea);
        String[] itemPrice = getResources().getStringArray(R.array.shopping_item_price);
        String[] itemRooms = getResources().getStringArray(R.array.shopping_item_rooms);
        String[] itemPhone = getResources().getStringArray(R.array.phoneNums);
        String[] itemImageUrl = getResources().getStringArray(R.array.shopping_item_images); // String[]!


        mItemList.clear();

        for (int i = 0; i < itemAddress.length; i++) {
            mItems.add(new RealEstateItem(itemAddress[i],
                    itemBaseArea[i],
                    itemDescription[i],
                    itemPrice[i],
                    itemRooms[i],
                    itemPhone[i],
                    itemImageUrl[i]
            ));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu");
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
        } else if (itemId == R.id.action_add){
            startActivity(new Intent(this, AddEstateActivity.class));
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        queryData();
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

}