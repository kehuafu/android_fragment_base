package com.example.mvr.use.tab

import android.os.Bundle
import com.example.mvr.core.container.base.BaseFragment
import com.example.mvr.core.fragment.constant.Router
import com.example.mvr.core.ktx.viewBindings
import com.example.mvr.databinding.FragmentHomeBinding
import com.example.mvr.databinding.ItemTabMainBinding
import com.example.mvr.use.mvr.MainState
import com.example.mvr.use.mvr.MainViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, MainViewModel, MainState>() {

    companion object {
        @JvmStatic
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private val itemTab by viewBindings<ItemTabMainBinding>()

    override fun onViewCreated(savedInstanceState: Bundle?) {
        viewBinding.btnHome.setOnClickListener {
            baseActivity.navigation(Router.PAGE1)
        }
    }
}