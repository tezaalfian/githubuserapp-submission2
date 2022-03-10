package com.tezaalfian.githubusersapi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.tezaalfian.githubusersapi.viewmodel.DetailViewModel
import com.tezaalfian.githubusersapi.R
import com.tezaalfian.githubusersapi.adapter.SectionsPagerAdapter
import com.tezaalfian.githubusersapi.response.UserDetailResponse
import com.tezaalfian.githubusersapi.databinding.ActivityDetailUserBinding

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private val detailViewModel by viewModels<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.title = resources.getString(R.string.app_name2)

        val username = intent.getStringExtra(EXTRA_USER) as String

        detailViewModel.getUser(username)
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        sectionsPagerAdapter.username = username

        binding.viewPager.adapter = sectionsPagerAdapter
        supportActionBar?.elevation = 0f

        detailViewModel.userDetail.observe(this) { user ->
            setUserData(user)
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        detailViewModel.toastText.observe(this) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUserData(user: UserDetailResponse) {
        binding.tvName.text = user.name
        binding.tvUsername.text = user.username
        binding.tvCompany.text = user.company
        binding.tvLocation.text = user.location
        binding.tvRepository.text = resources.getString(R.string.repository, user.repository)
        Glide.with(this)
            .load(user.avatar)
            .circleCrop()
            .into(binding.imgAvatar)
        val countFollow = arrayOf(user.followers, user.following)
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position], countFollow[position])
        }.attach()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_USER = "extra_user"
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
}