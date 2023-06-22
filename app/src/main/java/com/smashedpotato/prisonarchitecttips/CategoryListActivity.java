package com.smashedpotato.prisonarchitecttips;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.List;


/**
 * An activity representing a list of Categories. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CategoryDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CategoryListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        View recyclerView = findViewById(R.id.category_list);
        assert recyclerView != null;

        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.category_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        PrisonContent p = new PrisonContent(this.getApplicationContext(),getPackageName());
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(p.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<PrisonContent.PrisonItem> mValues;
        private int selectedItem;
        public SimpleItemRecyclerViewAdapter(List<PrisonContent.PrisonItem> items) {
            mValues = items;
            selectedItem=0;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            //holder.mIdView.setText(mValues.get(position).name);
            holder.mContentView.setText(mValues.get(position).content);


            //Drawable drawable = getResources().getDrawable(getResources().getIdentifier("d002_p00"+j, "drawable", getPackageName()));

            Context context = holder.mImageView.getContext();
            int res = context.getResources().getIdentifier((mValues.get(position).name).toLowerCase(),"drawable", context.getPackageName());
            holder.mImageView.setImageResource(res);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CategoryDetailFragment.ARG_ITEM_ID, holder.mItem.name);
                        CategoryDetailFragment fragment = new CategoryDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.category_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CategoryDetailActivity.class);
                        intent.putExtra(CategoryDetailFragment.ARG_ITEM_ID, holder.mItem.name);

                        context.startActivity(intent);
                    }
                }
            });

            if (selectedItem == position && mTwoPane) {
                holder.mView.performClick();
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final TextView mIdView;
            public final TextView mContentView;
            public final ImageView mImageView;
            public PrisonContent.PrisonItem mItem;

            public ViewHolder(View view) {

                super(view);
                mView = view;

                //mIdView = (TextView) view.findViewById(R.id.name);
                mContentView = (TextView) view.findViewById(R.id.content);
                mImageView = (ImageView) view.findViewById(R.id.img);




            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
