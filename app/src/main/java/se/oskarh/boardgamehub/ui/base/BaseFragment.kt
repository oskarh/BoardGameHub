package se.oskarh.boardgamehub.ui.base

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    companion object {
        inline fun <reified T : Fragment> newInstance(vararg parameters: Pair<String, Any>): T =
            T::class.java.newInstance().apply {
                arguments = bundleOf(*parameters)
            }
    }
}