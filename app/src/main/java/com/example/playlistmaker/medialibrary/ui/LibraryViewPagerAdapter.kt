package com.example.playlistmaker.medialibrary.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.medialibrary.ui.favorites.FavoriteTracksFragment
import com.example.playlistmaker.medialibrary.ui.playlists.PlaylistsFragment

class LibraryViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteTracksFragment()
            1 -> PlaylistsFragment()
            else -> throw IllegalStateException("Unknown position: $position")
        }
    }
}