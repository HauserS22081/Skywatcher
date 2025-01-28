package net.htlgkr.skywatcher.news;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.htlgkr.skywatcher.databinding.FragmentItemBinding;
import net.htlgkr.skywatcher.http.ExtendedNews;

import java.util.List;

public class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder> {

    private final List<ExtendedNews> values;
    private MyOnNewsClickListener onNewsClickListener;

    public MyNewsRecyclerViewAdapter(List<ExtendedNews> items) {
        values = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.news = values.get(position);
        holder.tvTitle.setText(values.get(position).getTitle());
        holder.tvSubtitle.setText(values.get(position).getSubtitle());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void setOnNewsClickListener(MyOnNewsClickListener onNewsClickListener) {
        this.onNewsClickListener = onNewsClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView tvTitle;
        public final TextView tvSubtitle;
        public ExtendedNews news;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            tvTitle = binding.itemTitle;
            tvSubtitle = binding.itemSubtitle;
            binding.cvItem.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString();        }

        @Override
        public void onClick(View view) {
            // funktioniert nicht wegen cardview
            onNewsClickListener.onNewsClick(getLayoutPosition());
        }
    }
}