package in.co.opensoftlab.yourstore.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.R;


public class ImageSlideAdapterSeller extends PagerAdapter {
    private Context context;
    List<String> productUrl = new ArrayList<>();

    public ImageSlideAdapterSeller(Context context, List<String> productUrl) {
        this.context = context;
        this.productUrl = productUrl;
    }

    @Override
    public int getCount() {
        return productUrl.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pager_item, container, false);

        ImageView productImg = (ImageView) view.findViewById(R.id.product_img);
        Picasso.with(context)
                .load(productUrl.get(position))
                .into(productImg);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}