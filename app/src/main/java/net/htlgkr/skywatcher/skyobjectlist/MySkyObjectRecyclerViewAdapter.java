package net.htlgkr.skywatcher.skyobjectlist;

import static java.security.AccessController.getContext;

import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.htlgkr.skywatcher.MyOnItemClickListener;
import net.htlgkr.skywatcher.databinding.FragmentSkyObjectBinding;

import java.util.List;

public class MySkyObjectRecyclerViewAdapter extends RecyclerView.Adapter<MySkyObjectRecyclerViewAdapter.ViewHolder> {

    private final List<SkyObject> skyObjects;
    private MyOnItemClickListener onItemClickListener;

    public MySkyObjectRecyclerViewAdapter(List<SkyObject> skyObjects) {
        this.skyObjects = skyObjects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentSkyObjectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = skyObjects.get(position);
        holder.ivImage.setImageResource(holder.item.getImage());
        holder.ivImage.setMaxHeight(holder.item.getSize());
        holder.ivImage.setMaxWidth(holder.item.getSize());



//        ViewGroup.LayoutParams layoutParams = holder.ivImage.getLayoutParams();
//        layoutParams.height = holder.item.getSize();
//        holder.ivImage.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return skyObjects.size();
    }

    public void setOnItemClickListener(MyOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public SkyObject item;
        public final ImageView ivImage;

        public ViewHolder(FragmentSkyObjectBinding binding) {
            super(binding.getRoot());
            ivImage = binding.ivImage;
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public void onClick(View v) {

        }
    }
}