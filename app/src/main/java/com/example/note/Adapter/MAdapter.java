package com.example.note.Adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.Model.Note;
import com.example.note.R;

import java.util.ArrayList;
import java.util.List;

public class MAdapter extends RecyclerView.Adapter<MAdapter.MViewHolder> {
    private Context context;
    private List<Note> notes;
    private MViewHolder.onNoteclickListener noteclickListener;
    private SparseBooleanArray item_selected;


    public MAdapter(Context context, List<Note> notes, MViewHolder.onNoteclickListener noteclickListener) {
        this.context = context;
        this.notes = notes;
        this.noteclickListener = noteclickListener;
        item_selected = new SparseBooleanArray();

    }


    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_sample, parent, false);
        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, final int position) {
        final Note note = notes.get(position);


        if (item_selected.get(position, false)) {
            holder.cardview.setBackgroundColor(context.getColor(R.color.item_selected));
        } else {
            holder.cardview.setBackgroundColor(context.getColor(R.color.white));
        }

        holder.title_tv.setText(note.getTitle());
        holder.description_tv.setText(note.getDescription());
        holder.date_tv.setText(note.getDate());


        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteclickListener.onNoteclicked(note);
            }
        });

        holder.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                noteclickListener.onLongclicked(note);
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    public void toggleSelection(int pos) {
        if (item_selected.get(pos, false)) {
            item_selected.delete(pos);
        } else {
            item_selected.put(pos, true);
        }

        notifyItemChanged(pos);
    }

    public void clearSelection() {
        item_selected.clear();
        notifyDataSetChanged();
    }


    public int getSelectedCount() {
        return item_selected.size();
    }

    public List<Integer> getSelectedItem() {
        List<Integer> items = new ArrayList<>(item_selected.size());
        for (int i = 0; i < item_selected.size(); i++) {
            items.add(item_selected.keyAt(i));
        }
        return items;
    }

    public void removeItem(int pos) {
        notes.remove(pos);

    }

    private Filter filter_list = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Note> filteredNotelist = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredNotelist.addAll(notes);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();//trim remove char space from first and end of string
                for (Note note :
                        notes) {
                    if (note.getTitle().toLowerCase().contains(filterPattern) || note.getDescription().toLowerCase().contains(filterPattern))
                        filteredNotelist.add(note);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredNotelist;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes.clear();
            notes.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter_list;
    }


    public static class MViewHolder extends RecyclerView.ViewHolder {
        public TextView title_tv;
        public TextView description_tv;
        public TextView date_tv;
        public CardView cardview;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.tv_itemSample_title);
            date_tv = itemView.findViewById(R.id.tv_itemSample_date);
            description_tv = itemView.findViewById(R.id.tv_itemSample_description);
            cardview = itemView.findViewById(R.id.cv_itemSample);
        }

        public interface onNoteclickListener {
            void onNoteclicked(Note note);

            void onLongclicked(Note note);
        }

    }
}
