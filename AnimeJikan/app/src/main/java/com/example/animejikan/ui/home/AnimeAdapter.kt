package com.example.animejikan.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animejikan.data.local.AnimeEntity
import com.example.animejikan.databinding.ItemAnimeBinding

class AnimeAdapter(private val onClick: (AnimeEntity) -> Unit) :
    ListAdapter<AnimeEntity, AnimeAdapter.AnimeViewHolder>(AnimeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class AnimeViewHolder(private val binding: ItemAnimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(anime: AnimeEntity) {
            binding.tvTitle.text = anime.title
            binding.tvEpisodes.text = "${anime.episodes ?: "?"} Episodes"
            binding.tvScore.text = "Score: ${anime.score ?: "N/A"}"
            
            if (com.example.animejikan.util.AppConfig.SHOW_IMAGES) {
                Glide.with(itemView.context)
                    .load(anime.imageUrl)
                    .into(binding.ivPoster)
            } else {
                binding.ivPoster.setImageDrawable(null) // Or set a placeholder color/icon
                binding.ivPoster.setBackgroundColor(android.graphics.Color.DKGRAY)
            }
                
            itemView.setOnClickListener { onClick(anime) }
        }
    }

    class AnimeDiffCallback : DiffUtil.ItemCallback<AnimeEntity>() {
        override fun areItemsTheSame(oldItem: AnimeEntity, newItem: AnimeEntity): Boolean {
            return oldItem.malId == newItem.malId
        }

        override fun areContentsTheSame(oldItem: AnimeEntity, newItem: AnimeEntity): Boolean {
            return oldItem == newItem
        }
    }
}
