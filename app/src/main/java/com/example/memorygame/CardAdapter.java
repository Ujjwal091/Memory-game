package com.example.memorygame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> implements View.OnClickListener {
    private final List<Card> cards;
    private final OnCardClickListener listener;

    public CardAdapter(@NotNull List<Card> cards, OnCardClickListener listener) {
        this.cards = cards;
        this.listener = listener;

        // flip all cards to backside initially
        for (Card card : cards) {
            card.setFlipped(false);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card card = cards.get(position);

        if (card.isFlipped()) {
            holder.flipText.setVisibility(View.GONE);
            holder.cardImage.setImageResource(card.getImageId());
        } else {
            holder.flipText.setVisibility(View.VISIBLE);
            holder.cardImage.setImageResource(R.drawable.card_back);
        }

        holder.itemView.setTag(card);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    @Override
    public void onClick(@NotNull View view) {
        Card card = (Card) view.getTag();
        listener.onCardClick(card);
    }

    public void flipCard(Card card) {
        int position = cards.indexOf(card);
        card.setFlipped(true);
        notifyItemChanged(position);
    }

    public interface OnCardClickListener {
        void onCardClick(Card card);
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImage;
        TextView flipText;

        public ViewHolder(View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.card_image);
            flipText = itemView.findViewById(R.id.flip_text);
        }
    }
}
