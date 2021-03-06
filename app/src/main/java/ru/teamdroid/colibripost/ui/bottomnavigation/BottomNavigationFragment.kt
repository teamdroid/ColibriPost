package ru.teamdroid.colibripost.ui.bottomnavigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.OnBackPressedListener
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentBottomNavigationBinding
import ru.teamdroid.colibripost.ui.auth.SignInFragment
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.core.getColorFromResource
import ru.teamdroid.colibripost.ui.core.getImageDrawable
import ru.teamdroid.colibripost.ui.main.MainFragment
import ru.teamdroid.colibripost.ui.newpost.NewPostFragment
import ru.teamdroid.colibripost.ui.settings.SettingsFragment
import ru.teamdroid.colibripost.ui.settings.channels.ChannelsSettingsFragment

class BottomNavigationFragment : BaseFragment(), OnBackPressedListener {

    override val layoutId: Int = R.layout.fragment_bottom_navigation
    override val toolbarTitle = 0
    private var selectedFragment = MainFragment.TAG

    private var _binding: FragmentBottomNavigationBinding? = null
    private val binding: FragmentBottomNavigationBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomNavigationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        base {
            toolbar.visibility = View.VISIBLE
        }
        setupBottomNavigator()
    }

    private fun setupBottomNavigator() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            when (it.itemId) {
                R.id.navigation_main -> {
                    changeActionBar(getString(R.string.main), isVisible = false)
                    displayFragment(MainFragment.TAG)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_newPost -> {
                    changeActionBar(getString(R.string.new_post))
                    binding.bottomNavigation.visibility = View.GONE
                    displayFragment(NewPostFragment.TAG)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_settings -> {
                    changeActionBar(getString(R.string.settings))
                    displayFragment(SettingsFragment.TAG)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        displayFragment(selectedFragment)
        changeActionBar(getString(R.string.main), isVisible = false)
    }

    private fun changeActionBar(title: String, isShowHomeButton: Boolean = false, isVisible:Boolean = true) {
        setToolbarTitle(title)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            isShowHomeButton
        )
        if(!isVisible) (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        else {
                (requireActivity() as AppCompatActivity).supportActionBar?.show()
        }
    }

    fun displayFragment(tag: String) {
        if (tag == SignInFragment.TAG)
            base {
                setNavigationFragment(SignInFragment())
                (requireActivity() as AppCompatActivity).supportActionBar?.hide()
                lifecycleScope.launch { authHolder.logOut() }
            }
        childFragmentManager.beginTransaction().apply {
            childFragmentManager.findFragmentByTag(selectedFragment)
                ?.let { if (it.isAdded) hide(it) }
            selectedFragment = tag
            val fragment =
                childFragmentManager.findFragmentByTag(selectedFragment) ?: createFragment(
                    selectedFragment
                )
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.navigationContainer, fragment, selectedFragment).addToBackStack(
                    selectedFragment
                )
            }
        }.commit()
    }

    private fun createFragment(tag: String): Fragment {
        return when (tag) {
            MainFragment.TAG -> MainFragment()
            NewPostFragment.TAG -> NewPostFragment()
            SettingsFragment.TAG -> SettingsFragment()
            ChannelsSettingsFragment.TAG -> ChannelsSettingsFragment()
            SignInFragment.TAG -> SignInFragment()
            else -> MainFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "BottomNavigationFragment"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            backPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun backPressed() {
        Log.d("!!!", "onBackPressed")
        displayFragment(MainFragment.TAG)
        binding.bottomNavigation.selectedItemId = R.id.navigation_main
        binding.bottomNavigation.visibility = View.VISIBLE
    }
}