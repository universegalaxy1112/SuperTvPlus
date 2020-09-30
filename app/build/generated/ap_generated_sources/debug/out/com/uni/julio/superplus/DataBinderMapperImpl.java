package com.uni.julio.superplus;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(0);

  static {
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(22);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "currentProgram");
      sKeys.put(2, "liveCategoryAdapter");
      sKeys.put(3, "serverAdapter");
      sKeys.put(4, "currentCategory");
      sKeys.put(5, "activityMainBindingVM");
      sKeys.put(6, "accountDetailsVM");
      sKeys.put(7, "moviesAdapter");
      sKeys.put(8, "moviesMenuItem");
      sKeys.put(9, "liveTVFragmentVM");
      sKeys.put(10, "movieDetailItem");
      sKeys.put(11, "liveProgramItem");
      sKeys.put(12, "categoryAdapter");
      sKeys.put(13, "moviesGridFragmentVM");
      sKeys.put(14, "movieDetailsVM");
      sKeys.put(15, "livetvAdapter");
      sKeys.put(16, "movieCategory");
      sKeys.put(17, "liveCategory");
      sKeys.put(18, "SearchFM");
      sKeys.put(19, "user");
      sKeys.put(20, "moviesMenuFragmentVM");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(0);

    static {
    }
  }
}
