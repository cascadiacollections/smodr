package com.kevintcoughlin.smodr.views.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.kevintcoughlin.smodr.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class DetailActivity extends AppCompatActivity {
    @Bind(R.id.list)
    RecyclerView mRecyclerView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_layout);
        ButterKnife.bind(this);
    }
}
