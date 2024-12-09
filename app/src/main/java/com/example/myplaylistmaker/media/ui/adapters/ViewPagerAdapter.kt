package com.example.myplaylistmaker.media.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myplaylistmaker.media.ui.fragments.FavoritesFragment
import com.example.myplaylistmaker.media.ui.fragments.PlaylistsFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment.newInstance()
            1 -> PlaylistsFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
