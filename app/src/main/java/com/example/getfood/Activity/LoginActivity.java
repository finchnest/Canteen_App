package com.example.getfood.Activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.getfood.Fragment.LoginFragment;
import com.example.getfood.Fragment.RegisterFragment;
import com.example.getfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    int exitCount;
    long currTime, prevTime;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    FirebaseAuth auth;
    DatabaseReference vCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageInfo pInfo = null;
        try {
            pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            final String version = pInfo.versionName;
            vCheck = FirebaseDatabase.getInstance().getReference().child("version-check");

            vCheck.child(version).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue().toString().equals("Yes")){
                        Log.d("vcheck", "Inside db check for yes");
                        auth = FirebaseAuth.getInstance();
                        if(auth.getCurrentUser() != null){
                            startActivity(new Intent(LoginActivity.this, FoodMenuDisplayActivity.class));
                            Log.d("vcheck", "everything green");

                        }
                        else{
                            setContentView(R.layout.activity_login);

                            Log.d("vcheck", "not logged in");

                            // Create the adapter that will return a fragment for each of the three
                            // primary sections of the activity.
                            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                            // Set up the ViewPager with the sections adapter.
                            mViewPager = (ViewPager) findViewById(R.id.container);
                            mViewPager.setAdapter(mSectionsPagerAdapter);

                            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

                            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

                        }
                    }
                    else{
                        setContentView(R.layout.activity_login);

                        Log.d("vcheck", "not logged in");

                        // Create the adapter that will return a fragment for each of the three
                        // primary sections of the activity.
                        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                        // Set up the ViewPager with the sections adapter.
                        mViewPager = (ViewPager) findViewById(R.id.container);
                        mViewPager.setAdapter(mSectionsPagerAdapter);

                        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

                        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

                        Log.d("vcheck", "version outdated");

                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                        builder.setMessage("This version of the app is outdated now. Update to the latest version.")
                                .setTitle("Warning!");
                        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(LoginActivity.this, "Update", Toast.LENGTH_SHORT).show();
                                String url = "https://github.com/aditmodhvadia/Canteen_App/releases";
                                try {
                                    Intent i = new Intent("android.intent.action.MAIN");
                                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                                    i.addCategory("android.intent.category.LAUNCHER");
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                                catch(ActivityNotFoundException e) {
                                    // Chrome is not installed
                                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(i);
                                }
                            }
                        });
                        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.setCancelable(false);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                        Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
//                    Toast.makeText(getApplicationContext(), dataSnapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

//            Toast.makeText(this, vCheck.child(version).getKey(), Toast.LENGTH_LONG).show();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() != null) {
//            startActivity(new Intent(LoginActivity.this, FoodMenuDisplayActivity.class));
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new LoginFragment();

                case 1:
                    return new RegisterFragment();

                default:
                    return new LoginFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    @Override
    public void onBackPressed() {
        exitCount++;
        if (exitCount == 1) {
            makeText("Press back once more to exit");
            prevTime = System.currentTimeMillis();
        }
        if (exitCount == 2) {
            currTime = System.currentTimeMillis();
            if (currTime - prevTime > 2000) {
                makeText("Press back once more to exit");
                prevTime = System.currentTimeMillis();
                exitCount = 1;
            } else {
                FirebaseAuth.getInstance().signOut();
                finish();/*
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finish();*/
            }
        }
    }

    public void makeText(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
