package com.moreyeahs.financeapp.presentation.side_navigation.web_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.moreyeahs.financeapp.databinding.FragmentWebPageBinding
import com.moreyeahs.financeapp.util.LoaderDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebPageFragment : Fragment() {

    private lateinit var binding: FragmentWebPageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWebPageBinding.inflate(layoutInflater)

        initViews()

        return binding.root
    }

    private fun initViews() {

        val title = arguments?.getString("Title") ?: ""
        binding.tvTitleWebPage.text = title

        binding.wvWebPage.settings.javaScriptEnabled = true

        val loaderDialog = LoaderDialog(requireContext())

        binding.wvWebPage.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url ?: "")
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                loaderDialog.dismissDialog()
                super.onPageFinished(view, url)
            }
        }

        val webUrl = arguments?.getString("WebUrl") ?: ""
        binding.wvWebPage.loadUrl(webUrl)
        loaderDialog.showLoaderDialog()

        binding.ivBackWebPage.setOnClickListener {
            val fromScreen = arguments?.getString("From") ?: ""
            if (fromScreen.isNotEmpty() && fromScreen.equals("SignUp", true)) {
                activity?.onBackPressedDispatcher?.onBackPressed()
            } else {
                activity?.finish()
            }
        }

    }

}