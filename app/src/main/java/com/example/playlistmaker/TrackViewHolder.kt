package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup):
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.track_view, parent, false)
        ){
            private val trackArtwork: ImageView = itemView.findViewById(R.id.trackArtwork)
            private val trackName: TextView = itemView.findViewById(R.id.trackName)
            private val artistName: TextView = itemView.findViewById(R.id.artistName)
            private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

            fun bind(model: Track){
                trackName.text = model.trackName
                artistName.text = model.artistName

                val formatterdTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(model.trackTimeMillis ?: 0L)

                trackTime.text = formatterdTime

                Glide.with(itemView)
                    .load(model.artworkUrl100)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.artwork_corner_radius)))
                    .into(trackArtwork)
            }
        }