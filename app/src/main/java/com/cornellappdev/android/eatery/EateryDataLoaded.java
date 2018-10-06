package com.cornellappdev.android.eatery;

import com.cornellappdev.android.eatery.model.EateryModel;
import java.util.List;

public interface EateryDataLoaded {
  void onDataLoaded(List<EateryModel> eateries);
}
