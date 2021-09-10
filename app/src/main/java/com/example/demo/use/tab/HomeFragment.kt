package com.example.demo.use.tab

import android.os.Bundle
import com.example.demo.app.Router
import com.example.demo.databinding.FragmentHomeBinding
import com.example.demo.databinding.ItemTabMainBinding
import com.example.demo.use.mvvm.MainState
import com.example.demo.use.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.ktx.viewBindings

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