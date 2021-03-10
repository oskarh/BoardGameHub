package se.oskarh.boardgamehub.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.comments_page.*
import se.oskarh.boardgamehub.databinding.CommentsPageBinding
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.repository.ApiResponse
import se.oskarh.boardgamehub.repository.EmptyResponse
import se.oskarh.boardgamehub.repository.ErrorResponse
import se.oskarh.boardgamehub.repository.LoadingResponse
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.ui.common.LazyLoadableFragment
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.log
import se.oskarh.boardgamehub.util.extension.visibleIf
import timber.log.Timber
import javax.inject.Inject

class CommentsFragment : LazyLoadableFragment() {

    private lateinit var binding: CommentsPageBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var detailsViewModel: DetailsViewModel

    private val commentAdapter = CommentsAdapter()

    private var isLoadInitiated = false

    private val comments: LiveData<ApiResponse<List<BoardGame.Comment>>> by lazy {
        detailsViewModel.boardGameComments
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = CommentsPageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        detailsViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(DetailsViewModel::class.java)
        if (isLoadInitiated) {
            loadData()
        }
        comment_list.run {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = commentAdapter
        }
        comments_try_again_button.setOnClickListener {
            Timber.d("Clicked comments try again..")
        }
    }

    override fun onLoad() {
        Timber.d("Loading comments...")
        isLoadInitiated = true
        if (::detailsViewModel.isInitialized) {
            loadData()
        }
    }

    private fun loadData() {
        // TODO: Remove previous subscriptions
        Timber.d("Loading comments commence observing")
        comments.observe(viewLifecycleOwner, { response ->
            Timber.d("Got comments ${response.log()}")
            comments_loading.visibleIf { response is LoadingResponse }
            comments_empty_message.visibleIf { response is EmptyResponse }
            comments_error_message.visibleIf { response is ErrorResponse }
            comments_try_again_button.visibleIf { response is ErrorResponse }
            content_root.visibleIf { response is SuccessResponse }

            if(response is SuccessResponse) {
                commentAdapter.updateResults(response.data)
            }
        })
    }
}