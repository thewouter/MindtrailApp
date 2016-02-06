package nl.wouter.mindtrail2015;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import nl.wouter.mindtrail2015.R;
 
public class ViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    private Context context;
	private Bitmap[] images;
	LayoutInflater inflater;
 
    public ViewPagerAdapter(Context context, Bitmap[] images) {
        this.context = context;
        this.images = images;
    }
 
    @Override
    public int getCount() {
        return images.length;
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }
 
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    	
    	inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
        View itemView = inflater.inflate(R.layout.viewpager_item, container, false);
        
    	ImageView imgCrossing = (ImageView) itemView.findViewById(R.id.image);
        imgCrossing.setImageBitmap(images[position]);
        
        ((ViewPager) container).addView(itemView);
        
        return itemView;
    }
 
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);
 
    }
}