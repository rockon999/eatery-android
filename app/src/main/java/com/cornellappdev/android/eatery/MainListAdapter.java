package com.cornellappdev.android.eatery;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.model.EateryModel.Status;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final Context mContext;
  private final ListAdapterOnClickHandler mListAdapterOnClickHandler;
  private int mCount;
  private String mQuery;
  private List<EateryModel> cafeListFiltered;
  private final int TEXT = 1;
  private final int IMAGE = 0;

  public interface ListAdapterOnClickHandler {

    void onClick(int position, List<EateryModel> list);
  }

  MainListAdapter(
      Context context,
      ListAdapterOnClickHandler clickHandler,
      int count,
      List<EateryModel> list) {
    mContext = context;
    mListAdapterOnClickHandler = clickHandler;
    mCount = count;
    cafeListFiltered = list;

    // Logcat for Fresco
    Set<RequestListener> requestListeners = new HashSet<>();
    requestListeners.add(new RequestLoggingListener());
    ImagePipelineConfig config =
        ImagePipelineConfig.newBuilder(context).setRequestListeners(requestListeners).build();
    Fresco.initialize(context, config);
    FLog.setMinimumLoggingLevel(FLog.VERBOSE);
  }

  void setList(ArrayList<EateryModel> list, int count, String query) {
    mQuery = query;
    mCount = count;
    cafeListFiltered = list;
    notifyDataSetChanged();
  }

  /**
   * Set view to layout of CardView
   */
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view;
    final int layoutId;
    RecyclerView.ViewHolder viewHolder = null;
    switch (viewType) {
      case IMAGE:
        layoutId = R.layout.card_item;
        view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);
        viewHolder = new ListAdapterViewHolder(view);
        break;
      case TEXT:
        layoutId = R.layout.card_text;
        view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        viewHolder = new TextAdapterViewHolder(view);
        break;
    }
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder inputHolder, int position) {
    final EateryModel eateryModel = cafeListFiltered.get(position);
    switch (inputHolder.getItemViewType()) {
      case IMAGE:
        ListAdapterViewHolder holder = (ListAdapterViewHolder) inputHolder;

        holder.cafeName.setText(eateryModel.getNickName());

        String imageLocation =
            "https://raw.githubusercontent.com/cuappdev/assets/master/eatery/eatery-images/"
                + convertName(eateryModel.getNickName() + ".jpg");
        Uri uri = Uri.parse(imageLocation);
        holder.cafeDrawee.setImageURI(uri);

        Collections.sort(cafeListFiltered);

        if (eateryModel.getCurrentStatus() == EateryModel.Status.CLOSED) {
          holder.cafeOpen.setText(mContext.getString(R.string.closed));
          holder.cafeOpen.setTextColor(ContextCompat.getColor(mContext, R.color.red));
          holder.rlayout.setAlpha(.6f);
        } else if (eateryModel.getCurrentStatus() == EateryModel.Status.CLOSING_SOON) {
          holder.cafeOpen.setText(mContext.getString(R.string.closing_soon));
          holder.cafeOpen.setTextColor(ContextCompat.getColor(mContext, R.color.red));
          holder.rlayout.setAlpha(1f);
        } else {
          holder.cafeOpen.setText(mContext.getString(R.string.open));
          holder.cafeOpen.setTextColor(ContextCompat.getColor(mContext, R.color.green));
          holder.rlayout.setAlpha(1f);
        }

        holder.brb_icon.setVisibility(View.GONE);
        holder.swipe_icon.setVisibility(View.GONE);
        for (String pay : eateryModel.getPayMethods()) {
          if (pay.equalsIgnoreCase("Meal Plan - Debit")) {
            holder.brb_icon.setVisibility(View.VISIBLE);
          }
        }

        if (eateryModel instanceof DiningHallModel) {
          holder.swipe_icon.setVisibility(View.VISIBLE);
        }

        holder.cafeTime.setText(eateryModel.getCloseTime());
        break;
      case TEXT:
        TextAdapterViewHolder holder2 = (TextAdapterViewHolder) inputHolder;
        holder2.cafe_name.setText(eateryModel.getNickName());
        Status status = eateryModel.getCurrentStatus();

        if (status == Status.OPEN) {
          holder2.cafe_time.setText(mContext.getString(R.string.open));
        } else if (status == Status.CLOSING_SOON) {
          holder2.cafe_time.setText(mContext.getString(R.string.closing_soon));
        } else {
          holder2.cafe_time.setText(mContext.getString(R.string.closed));
        }

        holder2.cafe_time.setText(mContext.getString(R.string.open));
        holder2.cafe_time_info.setText(eateryModel.getCloseTime());

        List<String> itemList = eateryModel.getSearchedItems();
        eateryModel.setSearchedItems(null);
        if (itemList == null)

        {
          holder2.cafe_items.setText("");
          break;
        }
        Collections.sort(itemList);
        String items = itemList.toString().substring(1, itemList.toString().length() - 1);

        if (mQuery != null)

        {
          // Fixes conflict with replacing character 'b' after inserting HTML bold tags
          if (mQuery.equals("B")) {
            mQuery = "b";
          }

          // Find case-matching instances to bold
          items = items.replaceAll(mQuery, "<b>" + mQuery + "</b>");

          // Find instances that don't match the case of the query and bold them
          int begIndex = items.replaceAll(mQuery, " ").toLowerCase().indexOf(mQuery.toLowerCase());
          if (begIndex >= 0) {
            String queryMatchingItemCase = items.substring(begIndex, begIndex + mQuery.length());
            items = items.replaceAll(queryMatchingItemCase, "<b>" + queryMatchingItemCase + "</b>");
          }
        }

        holder2.cafe_items.setText(Html.fromHtml(items.replace(", ", "<br/>")));
        break;
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (!MainActivity.searchPressed) {
      return IMAGE;
    } else {
      return TEXT;
    }
  }

  @Override
  public int getItemCount() {
    return mCount;
  }

  class ListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView cafeName;
    TextView cafeTime;
    TextView cafeOpen;
    SimpleDraweeView cafeDrawee;
    CardView rlayout;
    ImageView swipe_icon;
    ImageView brb_icon;

    ListAdapterViewHolder(View itemView) {
      super(itemView);
      cafeName = itemView.findViewById(R.id.cafe_name);
      cafeTime = itemView.findViewById(R.id.cafe_time);
      cafeOpen = itemView.findViewById(R.id.cafe_open);
      swipe_icon = itemView.findViewById(R.id.card_swipe);
      brb_icon = itemView.findViewById(R.id.card_brb);
      cafeDrawee = itemView.findViewById(R.id.cafe_image);
      rlayout = itemView.findViewById(R.id.cv);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      int adapterPosition = getAdapterPosition();
      mListAdapterOnClickHandler.onClick(adapterPosition, cafeListFiltered);
    }
  }

  class TextAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView cafe_name;
    TextView cafe_time;
    TextView cafe_time_info;
    TextView cafe_items;

    TextAdapterViewHolder(View itemView) {
      super(itemView);
      cafe_name = itemView.findViewById(R.id.searchview_name);
      cafe_time = itemView.findViewById(R.id.searchview_open);
      cafe_time_info = itemView.findViewById(R.id.searchview_opentime);
      cafe_items = itemView.findViewById(R.id.searchview_items);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      int adapterPosition = getAdapterPosition();
      mListAdapterOnClickHandler.onClick(adapterPosition, cafeListFiltered);
    }

  }

  private static String convertName(String str) {
    if (str.equals("104West!.jpg")) {
      return "104-West.jpg";
    }
    if (str.equals("McCormick's.jpg")) {
      return "mccormicks.jpg";
    }
    if (str.equals("Franny's.jpg")) {
      return "frannys.jpg";
    }
    if (str.equals("Ice Cream Cart.jpg")) {
      return "icecreamcart.jpg";
    }
    if (str.equals("Risley Dining Room.jpg")) {
      return "Risley-Dining.jpg";
    }
    if (str.equals("Martha's Express.jpg")) {
      return "Marthas-Cafe.jpg";
    }
    if (str.equals("Bus Stop Bagels.jpg")) {
      return "Bug-Stop-Bagels.jpg";
    }

    str = str.replaceAll("!", "");
    str = str.replaceAll("[&\']", "");
    str = str.replaceAll(" ", "-");
    str = str.replaceAll("é", "e");
    return str;
  }
}
