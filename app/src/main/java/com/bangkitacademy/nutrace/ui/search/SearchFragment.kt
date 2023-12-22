package com.bangkitacademy.nutrace.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkitacademy.nutrace.adapter.FoodAdapter
import com.bangkitacademy.nutrace.data.model.Food
import com.bangkitacademy.nutrace.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*class SearchFragment : Fragment() {

    private var fragmentSearchBinding: FragmentSearchBinding? = null
    private val binding get() = fragmentSearchBinding!!
    private lateinit var foodAdapter: FoodAdapter

    private val searchViewModel: SearchViewModel by activityViewModels()
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupAction()
    }

    private fun setupAdapter() {
        foodAdapter = FoodAdapter()
    }

    private fun setupAction() {
        binding.apply {
            rvSearch.layoutManager = LinearLayoutManager(requireContext())
            rvSearch.setHasFixedSize(true)
            rvSearch.adapter = foodAdapter

            foodAdapter.setOnItemClickCallback(object : FoodAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Food) {
                    /*Intent(requireContext(), DetailUserActivity::class.java).also {
                        it.putExtra(DetailUserActivity.EXTRA_USERNAME, data.login)
                        it.putExtra(DetailUserActivity.EXTRA_ID, data.id)
                        it.putExtra(DetailUserActivity.EXTRA_HTML, data.htmlUrl)
                        it.putExtra(DetailUserActivity.EXTRA_AVATAR, data.avatarUrl)
                        startActivity(it)
                    }*/
                }
            })
        }

        setupViewModel()
    }

    private fun setupViewModel() {
        binding.apply {
            showLoading(false)
            searchViewModel.isLoading.observe(requireActivity()) {
                showLoading(it)
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (!newText.isNullOrBlank()) {
                        searchUser(newText)
                    } else {
                        foodAdapter.updateList(emptyList())
                        showLoading(false)
                    }
                    return true
                }
            })
        }

        searchViewModel.getSearchFood().observe(requireActivity()) {
            if (it != null) {
                foodAdapter.updateList(it)
                showLoading(false)
            } else {
                foodAdapter.updateList(emptyList())
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun searchUser(query: String) {
        searchJob?.cancel()
        searchJob = CoroutineScope(Dispatchers.Main).launch {
            delay(300)
            if (query.isNotBlank()) {
                showLoading(true)
                searchViewModel.setSearchFood(query)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.searchView.setQuery("", false)
        searchViewModel.clearSearchFood()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchViewModel.clearSearchFood()
    }
}*/

class SearchFragment : Fragment() {

    private var fragmentSearchBinding: FragmentSearchBinding? = null
    private val binding get() = fragmentSearchBinding!!
    private lateinit var foodAdapter: FoodAdapter

    private val searchViewModel: SearchViewModel by activityViewModels()
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupAction()
    }

    private fun setupAdapter() {
        foodAdapter = FoodAdapter()
    }

    private fun setupAction() {
        binding.apply {
            rvSearch.layoutManager = LinearLayoutManager(requireContext())
            rvSearch.setHasFixedSize(true)
            rvSearch.adapter = foodAdapter

            foodAdapter.setOnItemClickCallback(object : FoodAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Food) {
                    /*Intent(requireContext(), DetailUserActivity::class.java).also {
                        it.putExtra(DetailUserActivity.EXTRA_USERNAME, data.login)
                        it.putExtra(DetailUserActivity.EXTRA_ID, data.id)
                        it.putExtra(DetailUserActivity.EXTRA_HTML, data.htmlUrl)
                        it.putExtra(DetailUserActivity.EXTRA_AVATAR, data.avatarUrl)
                        startActivity(it)
                    }*/
                }
            })
        }

        setupViewModel()
    }

    private fun setupViewModel() {
        binding.apply {
            showLoading(false)

            // Mengamati LiveData dari dummyFoodList pada SearchViewModel
            searchViewModel.getDummyFoodListLiveData().observe(viewLifecycleOwner) { dummyFoodList ->
                if (dummyFoodList != null) {
                    foodAdapter.updateList(dummyFoodList)
                    showLoading(false)
                } else {
                    foodAdapter.updateList(emptyList())
                }
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (!newText.isNullOrBlank()) {
                        searchUser(newText)
                    } else {
                        foodAdapter.updateList(emptyList())
                        showLoading(false)
                    }
                    return true
                }
            })
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun searchUser(query: String) {
        searchJob?.cancel()
        searchJob = CoroutineScope(Dispatchers.Main).launch {
            delay(300)
            if (query.isNotBlank()) {
                showLoading(true)
                searchViewModel.setSearchFood(query)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.searchView.setQuery("", false)
        searchViewModel.clearSearchFood()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchViewModel.clearSearchFood()
    }
}

