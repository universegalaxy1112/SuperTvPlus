package com.uni.julio.superplus.databinding;
import com.uni.julio.superplus.R;
import com.uni.julio.superplus.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class GridviewRowBindingImpl extends GridviewRowBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.img, 2);
    }
    // views
    // variables
    // values
    // listeners
    private OnClickListenerImpl mMoviesAdapterOnClickItemAndroidViewViewOnClickListener;
    // Inverse Binding Event Handlers

    public GridviewRowBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 3, sIncludes, sViewsWithIds));
    }
    private GridviewRowBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.LinearLayout) bindings[0]
            , (android.widget.ImageView) bindings[2]
            , (android.widget.TextView) bindings[1]
            );
        this.flMainLayout.setTag(null);
        this.title.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x4L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.moviesAdapter == variableId) {
            setMoviesAdapter((com.uni.julio.superplus.adapter.GridViewAdapter) variable);
        }
        else if (BR.moviesMenuItem == variableId) {
            setMoviesMenuItem((com.uni.julio.superplus.model.Movie) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setMoviesAdapter(@Nullable com.uni.julio.superplus.adapter.GridViewAdapter MoviesAdapter) {
        this.mMoviesAdapter = MoviesAdapter;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.moviesAdapter);
        super.requestRebind();
    }
    public void setMoviesMenuItem(@Nullable com.uni.julio.superplus.model.Movie MoviesMenuItem) {
        this.mMoviesMenuItem = MoviesMenuItem;
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.moviesMenuItem);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        android.view.View.OnClickListener moviesAdapterOnClickItemAndroidViewViewOnClickListener = null;
        java.lang.String moviesMenuItemGetTitle = null;
        com.uni.julio.superplus.adapter.GridViewAdapter moviesAdapter = mMoviesAdapter;
        com.uni.julio.superplus.model.Movie moviesMenuItem = mMoviesMenuItem;

        if ((dirtyFlags & 0x5L) != 0) {



                if (moviesAdapter != null) {
                    // read moviesAdapter::onClickItem
                    moviesAdapterOnClickItemAndroidViewViewOnClickListener = (((mMoviesAdapterOnClickItemAndroidViewViewOnClickListener == null) ? (mMoviesAdapterOnClickItemAndroidViewViewOnClickListener = new OnClickListenerImpl()) : mMoviesAdapterOnClickItemAndroidViewViewOnClickListener).setValue(moviesAdapter));
                }
        }
        if ((dirtyFlags & 0x6L) != 0) {



                if (moviesMenuItem != null) {
                    // read moviesMenuItem.getTitle
                    moviesMenuItemGetTitle = moviesMenuItem.getTitle();
                }
        }
        // batch finished
        if ((dirtyFlags & 0x5L) != 0) {
            // api target 1

            this.flMainLayout.setOnClickListener(moviesAdapterOnClickItemAndroidViewViewOnClickListener);
        }
        if ((dirtyFlags & 0x6L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.title, moviesMenuItemGetTitle);
        }
    }
    // Listener Stub Implementations
    public static class OnClickListenerImpl implements android.view.View.OnClickListener{
        private com.uni.julio.superplus.adapter.GridViewAdapter value;
        public OnClickListenerImpl setValue(com.uni.julio.superplus.adapter.GridViewAdapter value) {
            this.value = value;
            return value == null ? null : this;
        }
        @Override
        public void onClick(android.view.View arg0) {
            this.value.onClickItem(arg0); 
        }
    }
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): moviesAdapter
        flag 1 (0x2L): moviesMenuItem
        flag 2 (0x3L): null
    flag mapping end*/
    //end
}