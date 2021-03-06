package com.cornellappdev.android.eatery.model;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.ZonedDateTime;

/**
 * Created by JC on 2/15/18. This represents a single Cafeteria (either a cafe or a dining hall)
 */

public abstract class EateryModel implements Model, Cloneable, Serializable,
    Comparable<EateryModel> {

  /* Comparator for sorting the list by EateryModel's nickname */
  public static Comparator<EateryModel> cafeNameComparator = (s1, s2) -> {
    String str1 = s1.getNickName();
    String str2 = s2.getNickName();
    // TODO Why?
    if (str1.startsWith("1")) {
      return -1;
    }
    if (str2.startsWith("1")) {
      return 1;
    }
    // ascending order
    return str1.compareToIgnoreCase(str2);
  };
  protected CampusArea mArea;
  protected int mId;
  protected String mName, mNickName, mSlug;
  protected boolean mOpenPastMidnight;
  protected List<PaymentMethod> mPayMethods;
  private String mBuildingLocation;
  private double mLatitude, mLongitude;

  @BindingAdapter({"app:imageUrl"})
  public static void loadImage(SimpleDraweeView view, Uri uri) {
    view.setImageURI(uri);
  }

  public abstract ZonedDateTime getNextOpening();

  public abstract ZonedDateTime getCloseTime();

  public abstract List<String> getMealItems();

  public abstract Status getCurrentStatus();

  public String stringTo() {
    String info = "Name/mNickName: " + mName + "/" + mNickName;
    String locationString = "Location: " + ", Area: " + mArea;
    String payMethodsString = "Pay Methods: " + mPayMethods.toString();
    String menuString = "";
    return info + "\n" + locationString + "\n" + payMethodsString + "\n" + "Menu" + "\n"
        + menuString;
  }

  public boolean isOpen() {
    Status status = getCurrentStatus();
    return status == Status.OPEN || status == Status.CLOSING_SOON;
  }

  public String getName() {
    return mName;
  }

  public void setName(String name) {
    this.mName = name;
  }

  public String getNickName() {
    return mNickName;
  }

  public List<PaymentMethod> getPaymentMethods() {
    return mPayMethods;
  }

  public boolean hasPaymentMethod(PaymentMethod method) {
    return mPayMethods.contains(method);
  }

  public String getBuildingLocation() {
    return mBuildingLocation;
  }

  public int getId() {
    return mId;
  }

  public void setId(int id) {
    this.mId = id;
  }

  public LatLng getLatLng() {
    return new LatLng(mLatitude, mLongitude);
  }

  public boolean isOpenPastMidnight() {
    return mOpenPastMidnight;
  }

  public CampusArea getArea() {
    return mArea;
  }

  public String getSlug() {
    return mSlug;
  }

  /**
   * Compared the time of two EateryModel
   **/
  public int compareTo(@NonNull EateryModel cm) {
    if (cm.getCurrentStatus() == getCurrentStatus()) {
      return this.getNickName().compareTo(cm.getNickName());
    } else if (isOpen() && !cm.isOpen()) {
      return -1;
    } else {
      return 1;
    }
  }

  @Override
  public void parseJSONObject(Context context, boolean hardcoded, JSONObject eatery)
      throws JSONException {
    mName = eatery.getString("name");
    mBuildingLocation = eatery.getString("location");
    mNickName = eatery.getString("nameshort");
    mLatitude = eatery.getDouble("latitude");
    mLongitude = eatery.getDouble("longitude");
    mSlug = eatery.getString("slug");
    // Parse payment methods available at eatery
    JSONArray methods = eatery.getJSONArray("payMethods");
    List<PaymentMethod> payMethods = new ArrayList<>();
    for (int j = 0; j < methods.length(); j++) {
      JSONObject method = methods.getJSONObject(j);
      payMethods.add(PaymentMethod.fromShortDescription(method.getString("descrshort")));
    }
    mPayMethods = payMethods;
    // Find geographical area for eatery
    String area = eatery.getJSONObject("campusArea").getString("descrshort");
    mArea = CampusArea.fromShortDescription(area);
  }

  public enum Status {
    OPEN,
    CLOSING_SOON,
    CLOSED;
  }
}



