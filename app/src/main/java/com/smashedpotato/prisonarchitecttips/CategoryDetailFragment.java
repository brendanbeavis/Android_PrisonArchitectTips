package com.smashedpotato.prisonarchitecttips;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A fragment representing a single Category detail screen.
 * This fragment is either contained in a {@link CategoryListActivity}
 * in two-pane mode (on tablets) or a {@link CategoryDetailActivity}
 * on handsets.
 */
public class CategoryDetailFragment extends Fragment implements Html.ImageGetter {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private PrisonContent.PrisonItem mItem;

    private TextView thisContent;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CategoryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = PrisonContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.category_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            //((TextView) rootView.findViewById(R.id.category_detail)).setText(Html.fromHtml(mItem.details));
            Spanned spanned = Html.fromHtml(mItem.details, this, null);
            thisContent= (TextView) rootView.findViewById(R.id.category_detail);

            SpannableStringBuilder strBuilder = new SpannableStringBuilder(spanned);
            URLSpan[] urls = strBuilder.getSpans(0, spanned.length(), URLSpan.class);
            for(URLSpan span : urls) {
                makeLinkClickable(strBuilder, span);
            }
            thisContent.setText(strBuilder);
            thisContent.setMovementMethod(LinkMovementMethod.getInstance());
            thisContent.setTextIsSelectable(true);
            thisContent.setFocusable(false);
            thisContent.setFocusableInTouchMode(false);
        }
        AdView mAdView;
        mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AdView mAdView1;
        mAdView1 = (AdView) rootView.findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);
        return rootView;
    }

    @Override
    public Drawable getDrawable(String source) {
        int width=0;
        int height=0;
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LevelListDrawable d = new LevelListDrawable();
        try{



            Context c=getActivity();

            Drawable empty = c.getResources().getDrawable(c.getResources().getIdentifier(source, "drawable", c.getPackageName()));//getResources().getDrawable(R.drawable.ic_launcher);
            d.addLevel(0, 0, empty);

            if(metrics.widthPixels>metrics.heightPixels){
                height=(int)((float)metrics.heightPixels*0.9);
                float ratio=(float)height/(float)empty.getIntrinsicHeight();
                width=(int)(((float) empty.getIntrinsicWidth())*ratio);
            }
            else{
                width=(int)((float)metrics.widthPixels*0.9);
                float ratio=(float)width/(float)empty.getIntrinsicWidth();
                height=(int)(((float) empty.getIntrinsicHeight())*ratio);
            }
            d.setBounds(0, 0, width, height);
            new LoadImage().execute(source, d);
            return d;
        }catch(Exception e){
            Log.e("getDrawable",e.getMessage());
        }
        return d;
    }
    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;
        //private DisplayMetrics metrics = new DisplayMetrics();

        @Override
        protected void onPreExecute() {
            // we need this to properly scale the images later
            //getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            Log.d("LoadImage", "doInBackground " + source);
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                try{

                    BitmapDrawable d = new BitmapDrawable(bitmap);
                    mDrawable.addLevel(1, 1, d);
                    mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    mDrawable.setLevel(1);
                    CharSequence t = thisContent.getText();
                    thisContent.setText(t);
                }catch(Exception e){
                    Log.e("onPostExecute",e.getMessage());
                }
            }
        }
    }
    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
    {
        try{

            int start = strBuilder.getSpanStart(span);
            int end = strBuilder.getSpanEnd(span);
            int flags = strBuilder.getSpanFlags(span);
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(View view) {

                    Log.d("clicked", "onClick: "+ span.getURL());
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(span.getURL()));
                    startActivity(browserIntent);
                }
            };
            strBuilder.setSpan(clickable, start, end, flags);
            strBuilder.removeSpan(span);
        }catch(Exception e){
            Log.e("makeLinkClickable",e.getMessage());
        }
    }
}
