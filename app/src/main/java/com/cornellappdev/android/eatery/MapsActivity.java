package com.cornellappdev.android.eatery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.util.EateryStringsUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
  private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
  private List<EateryModel> mEateries;
  private GoogleMap mMap;
  private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
      new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
          mMap.setMinZoomPreference(15);
          return false;
        }
      };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    mEateries = (ArrayList<EateryModel>) intent.getSerializableExtra("mEatery");
    setContentView(R.layout.activity_maps);
    Fragment mapFragment = getSupportFragmentManager().findFragmentById(R.id.map);

    if (mapFragment instanceof SupportMapFragment) {
      ((SupportMapFragment) mapFragment).getMapAsync(this);
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    // (42.4471,-76.4832) is the location for Day Hall
    LatLng cornell = new LatLng(42.451092, -76.482654);
    for (int i = 0; i < mEateries.size(); i++) {
      EateryModel cafe = mEateries.get(i);
      LatLng latLng = cafe.getLatLng();
      String name = cafe.getNickName();
      Marker cafeMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(name));
      String openingClosingDescription = EateryStringsUtil
          .getOpeningClosingDescription(this, cafe);
      if (openingClosingDescription != null) {
        cafeMarker.setSnippet(openingClosingDescription);
      }
      if (cafe.getCurrentStatus() == EateryModel.Status.CLOSED) {
        cafeMarker.setIcon(
            BitmapDescriptorFactory.fromBitmap(
                Bitmap.createScaledBitmap(
                    bitmapDescriptorFromVector(this, R.drawable.gray_pin), 72, 96, false)));
      } else if (cafe.getCurrentStatus() == EateryModel.Status.OPEN) {
        cafeMarker.setIcon(
            BitmapDescriptorFactory.fromBitmap(
                Bitmap.createScaledBitmap(
                    bitmapDescriptorFromVector(this, R.drawable.blue_pin), 72, 96, false)));
      } else {
        cafeMarker.setIcon(
            BitmapDescriptorFactory.fromBitmap(
                Bitmap.createScaledBitmap(
                    bitmapDescriptorFromVector(this, R.drawable.blue_pin), 72, 96, false)));
      }
    }
    // Clicking on an eatery icon on the map will take the user to the MenuActivity of that eatery
    mMap.setOnInfoWindowClickListener(
        marker -> {
          Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
          String markerName = marker.getTitle();
          int position = 0;
          for (int i = 0; i < mEateries.size(); i++) {
            if (mEateries.get(i).getNickName().equalsIgnoreCase(markerName)) {
              position = i;
            }
          }
          intent.putExtra("cafeInfo", mEateries.get(position));
          intent.putExtra("locName", mEateries.get(position).getNickName());
          startActivity(intent);
        });
    mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
    mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
    enableMyLocationIfPermitted();
    mMap.getUiSettings().setZoomControlsEnabled(true);
    mMap.setMinZoomPreference(15);
    mMap.moveCamera(CameraUpdateFactory.newLatLng(cornell));
  }

  // Gets user permission to use location
  private void enableMyLocationIfPermitted() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(
          this,
          new String[]{
              Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
          },
          LOCATION_PERMISSION_REQUEST_CODE);
    } else if (mMap != null) {
      mMap.setMyLocationEnabled(true);
    }
  }

  private void showDefaultLocation() {
    Toast.makeText(
        this,
        "Location permission not granted, " + "showing default location",
        Toast.LENGTH_SHORT)
        .show();
    // (42.4471,-76.4832) is the location for Day Hall
    LatLng cornell = new LatLng(42.4471, -76.4832);
    ;
    mMap.moveCamera(CameraUpdateFactory.newLatLng(cornell));
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case LOCATION_PERMISSION_REQUEST_CODE: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          enableMyLocationIfPermitted();
        } else {
          showDefaultLocation();
        }
        return;
      }
    }
  }

  private Bitmap bitmapDescriptorFromVector(Context context, int vectorResId) {
    Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
    vectorDrawable.setBounds(
        0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
    Bitmap bitmap =
        Bitmap.createBitmap(
            vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    vectorDrawable.draw(canvas);
    return bitmap;
  }

  class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private View myContentsView;

    MyInfoWindowAdapter() {
      myContentsView = getLayoutInflater().inflate(R.layout.map_info_layout, null);
    }

    @Override
    public View getInfoContents(Marker marker) {
      return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
      myContentsView = getLayoutInflater().inflate(R.layout.map_info_layout, null);
      TextView cafe_name = ((TextView) myContentsView.findViewById(R.id.info_cafe_name));
      cafe_name.setText(marker.getTitle());
      TextView cafe_open = ((TextView) myContentsView.findViewById(R.id.info_cafe_open));
      TextView cafe_desc = ((TextView) myContentsView.findViewById(R.id.info_cafe_desc));
      String firstword = marker.getSnippet().split(" ")[0];
      if (firstword.equalsIgnoreCase("open")) {
        cafe_open.setText("Open");
        cafe_open.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        cafe_desc.setText(marker.getSnippet().substring(5));
      } else if (firstword.equalsIgnoreCase("closing")) {
        cafe_open.setText("Closing Soon");
        cafe_open.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        cafe_desc.setText(marker.getSnippet().substring(13));
      } else {
        cafe_open.setText(firstword);
        cafe_open.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        cafe_desc.setText(marker.getSnippet().substring(7));
      }
      return myContentsView;
    }
  }

  public class SnackBarListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      Intent browser =
          new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cornellappdev.com/apply/"));
      startActivity(browser);
    }
  }
}
