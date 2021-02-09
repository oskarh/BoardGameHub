package se.oskarh.boardgamehub.ui.videolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.video_list_fragment.*
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideoInfo
import se.oskarh.boardgamehub.databinding.VideoListFragmentBinding
import se.oskarh.boardgamehub.ui.base.BaseFragment
import se.oskarh.boardgamehub.ui.details.VideoLiteAdapter
import se.oskarh.boardgamehub.util.KEY_VIDEO_LIST
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.startVideoActivity
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class VideoListFragment : BaseFragment() {

    private lateinit var binding: VideoListFragmentBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var videos: List<YouTubeVideoInfo>

    private val videoAdapter = VideoLiteAdapter {
        Timber.d("Clicked video $it")
        activity?.startVideoActivity(it)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = VideoListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        video_list.layoutManager = LinearLayoutManager(requireActivity())
        video_list.adapter = videoAdapter
        videos = requireArguments().getParcelableArrayList<YouTubeVideoInfo>(KEY_VIDEO_LIST) as ArrayList<YouTubeVideoInfo>
        videoAdapter.submitList(videos)
//        videoAdapter.updateResults(videos)
    }

    companion object {
        fun newInstance(videos: List<YouTubeVideoInfo>): VideoListFragment =
            VideoListFragment().apply {
                arguments = bundleOf(KEY_VIDEO_LIST to videos)
            }
    }
}