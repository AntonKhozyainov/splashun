package ru.khozyainov.splashun.presentation.home

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.khozyainov.splashun.R
import ru.khozyainov.splashun.databinding.FragmentHomeBinding
import ru.khozyainov.splashun.models.ItemPhoto
import ru.khozyainov.splashun.presentation.home.adapter.DefaultLoadStateAdapter
import ru.khozyainov.splashun.presentation.home.adapter.ItemPhotoAdapter
import ru.khozyainov.splashun.utils.*

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@AndroidEntryPoint
class HomeFragment : ViewBindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    private var adapter: ItemPhotoAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarSettings()
        initList()
        bind()
        observePhotoFlow()
        observeLoadStateFlow()
        setupSwipeToRefresh()
        handleScrollingToTopWhenSearching()
        smoothScrollUp()
    }

    private fun setToolbarSettings() {
        val toolbar = getToolbar()
        toolbar.title = getString(R.string.ribbon)
        toolbar.menu.forEach { manuItems ->
            manuItems.isVisible = manuItems.itemId == R.id.toolbar_menu_search
        }
        expandingSearch()
    }

    private fun smoothScrollUp() {
        val bottomNavigationView = getBottomNavigationView()
        bottomNavigationView.setOnItemReselectedListener { menuItem ->
            if (menuItem.isChecked) binding.photoList.smoothScrollToPosition(0)
        }
    }

    private fun expandingSearch() {
        //раскрытие и закрытие элемента поиска
        val searchItem = getSearchView()
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                return true
            }
        })
    }

    private fun observePhotoFlow() =
        viewModel.itemPhotoFlow.launchAndCollectLatest(viewLifecycleOwner) { pagingData ->
            adapter.submitData(pagingData)
        }

    private fun observeLoadStateFlow() =
        adapter.loadStateFlow.debounce(200).launchAndCollectLatest(viewLifecycleOwner) { state ->
            when (state.refresh) {
                is LoadState.Loading -> updateIsLoading(true)
                is LoadState.NotLoading -> updateIsLoading(false)
                is LoadState.Error -> updateErrorState(state.refresh)
                else -> throw Exception("")
            }
        }

    private fun setupSwipeToRefresh() = binding.swipeRefreshLayout.setOnRefreshListener {
        adapter.refresh()
    }

    private fun updateErrorState(errorState: LoadState) = with(binding) {
        if (errorState !is LoadState.Error) return@with
        photoList.isVisible = false
        appBarLayout.isVisible = false
        swipeRefreshLayout.isRefreshing = false
        exceptionTextView.text = errorState.error.message
        exceptionTextView.isVisible = true
    }

    private fun updateIsLoading(isLoading: Boolean) = with(binding) {
        exceptionTextView.isVisible = false
        appBarLayout.isVisible = !isLoading
        photoList.isVisible = !isLoading
        swipeRefreshLayout.isRefreshing = isLoading
    }

    private fun initList() {

        adapter = ItemPhotoAdapter(object : ItemPhotoAdapter.Listener {
            override fun navigateToDetailPhoto(itemPhoto: ItemPhoto) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToPhotoDetailFragment(
                        itemPhoto.id
                    )
                )
            }

            override fun likePhoto(itemPhoto: ItemPhoto) {
                viewModel.setLike(itemPhoto, !itemPhoto.like)
            }
        })

        val adapterWithLoadState = adapter.withLoadStateFooter(DefaultLoadStateAdapter())

        val spanCount = if (requireContext().orientationIsPortrait()) 2 else 4 //todo сделать для маленьких экранов 3

        with(binding.photoList) {
            adapter = adapterWithLoadState
            layoutManager = StaggeredGridLayoutManager(
                spanCount,
                StaggeredGridLayoutManager.VERTICAL,
            ).apply {
                //Устанавливаем отступ из ItemOffsetDecoration для списка
                addItemDecoration(ItemOffsetDecoration(requireContext()))
            }
            (itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false

        }
    }

    private fun getRefreshLoadStateFlow(): Flow<LoadState> =
        adapter.loadStateFlow.map { it.refresh }

    private fun handleScrollingToTopWhenSearching() = viewLifecycleOwner.lifecycleScope.launch {
        getRefreshLoadStateFlow()
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading) {
                    binding.photoList.scrollToPosition(0)
                }
            }
    }

    private fun bind() {
        val searchView = getSearchActionView()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bind(
                queryFlow = searchView.searchChangeFlow()
                    .onStart { emit("") }
                    .onEach { currentQuery ->
                        setHeadline(currentQuery)
                    }
            )
        }
    }

    private fun setHeadline(query: String) = if (query.isNotEmpty()) {
        binding.toolbarTitleTextView.text = getString(R.string.search_collapse_toolbar)
        binding.toolbarTitle2TextView.text = getString(R.string.request_collapse_toolbar, query)
    } else {
        binding.toolbarTitleTextView.text = getString(R.string.new_collapse_toolbar)
        binding.toolbarTitle2TextView.text = getString(R.string.top_collapse_toolbar)
    }

    private fun getToolbar(): Toolbar =
        activity?.findViewById(R.id.toolbar) ?: throw Exception("")//TODO

    private fun getSearchView(): MenuItem =
        getToolbar().menu?.findItem(R.id.toolbar_menu_search) ?: throw Exception("")//TODO

    private fun getSearchActionView(): SearchView = getSearchView().actionView as SearchView

    private fun getBottomNavigationView(): BottomNavigationView =
        activity?.findViewById(R.id.bottomNavigation) as BottomNavigationView

}