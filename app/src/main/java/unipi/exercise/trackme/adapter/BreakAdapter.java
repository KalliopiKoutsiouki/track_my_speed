package unipi.exercise.trackme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import unipi.exercise.trackme.R;
import unipi.exercise.trackme.model.Break;

public class BreakAdapter extends RecyclerView.Adapter<BreakAdapter.BreakViewHolder>{

    Context context;
    private List<Break> breaks;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Break item);
    }

    public BreakAdapter(Context context, List<Break> breaks, OnItemClickListener listener) {
        this.context = context;
        this.breaks = breaks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BreakAdapter.BreakViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view  = inflater.inflate(R.layout.list_item_break, parent, false);
        return new BreakAdapter.BreakViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BreakAdapter.BreakViewHolder holder, int position) {
        holder.timestampTextView.setText((CharSequence) breaks.get(position).getTimestamp());
        final Break breakItem = breaks.get(position);
        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(breakItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return breaks.size();
    }

    public static class BreakViewHolder extends RecyclerView.ViewHolder {
        private TextView timestampTextView;
        private Button mapButton;

        public BreakViewHolder(@NonNull View itemView) {
            super(itemView);
            timestampTextView = itemView.findViewById(R.id.text_timestamp);
            mapButton = itemView.findViewById(R.id.button_click_me);
        }
    }
}
