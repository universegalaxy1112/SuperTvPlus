package com.uni.julio.superplus.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.uni.julio.superplus.R;
import com.uni.julio.superplus.helper.VideoStreamManager;
import com.uni.julio.superplus.listeners.MoviesGridViewModelContract;
import com.uni.julio.superplus.model.ModelTypes;
import com.uni.julio.superplus.utils.Device;
import com.uni.julio.superplus.utils.Dialogs;
import com.uni.julio.superplus.viewmodel.Lifecycle;
import com.uni.julio.superplus.viewmodel.MoviesGridViewModel;
public class MoreVideoActivity extends BaseActivity implements MoviesGridViewModelContract.View{
    private MoviesGridViewModel moviesGridViewModel;

    @Override
    protected Lifecycle.ViewModel getViewModel() {
        return moviesGridViewModel;
    }



    @Override
    protected Lifecycle.View getLifecycleView() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         Bundle extras=getIntent().getExtras();
        selectedType=(ModelTypes.SelectedType)extras.get("selectedType");
        mainCategoryId=extras.getInt("mainCategoryId",-1);
        movieCategoryId=extras.getInt("movieCategoryId",-1);
        moviesGridViewModel=new MoviesGridViewModel(this,selectedType);
        com.uni.julio.superplus.databinding.ActivityMorevideoBinding activityMorevideoBinding = DataBindingUtil.setContentView(this, R.layout.activity_morevideo);
        activityMorevideoBinding.setMoviesGridFragmentVM(moviesGridViewModel);
        Toolbar toolbar = activityMorevideoBinding.toolbar;
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if(Device.treatAsBox){
             (activityMorevideoBinding.appBarLayout).setVisibility(View.GONE);
        }
        try {
            if(VideoStreamManager.getInstance().getMainCategory(mainCategoryId) != null) {
                String title= VideoStreamManager.getInstance().getMainCategory(mainCategoryId).getMovieCategories().get(movieCategoryId).getCatName();
                getSupportActionBar().setTitle(title);
            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            Dialogs.showOneButtonDialog(getActivity(), R.string.something_wrong_title, R.string.something_wrong, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    getActivity().finish();
                }
            });
        }
        moviesGridViewModel.showMovieList(activityMorevideoBinding.moreVideoRecycler,mainCategoryId,movieCategoryId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        if(keycode== KeyEvent.KEYCODE_BACK){
            finishActivity();
        }
        return false;
    }


}
