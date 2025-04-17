package com.example.reinforcement.ui.cigarettes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.reinforcement.R
import com.example.reinforcement.databinding.FragmentCigarettesBinding

class CigarettesFragment : Fragment() {

    private var _binding: FragmentCigarettesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CigarettesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCigarettesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CigarettesViewModel::class.java]
        
        setupAddButton()
        observeViewModel()
    }

    private fun setupAddButton() {
        binding.buttonAddCigarette.setOnClickListener {
            val bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce_animation)
            binding.buttonAddCigarette.startAnimation(bounceAnimation)
            viewModel.incrementCigaretteCount()
        }
    }

    private fun observeViewModel() {
        // Observar datos de cigarros
        viewModel.cigaretteData.observe(viewLifecycleOwner) { data ->
            binding.textCigarettesCount.text = "${data.count} ${getString(R.string.cigarettes_today)}"
        }
        
        // Observar porcentaje de progreso
        viewModel.progressPercentage.observe(viewLifecycleOwner) { percentage ->
            binding.circularProgress.setProgress(percentage)
        }
        
        // Observar mensaje de cigarros restantes
        viewModel.remainingMessage.observe(viewLifecycleOwner) { message ->
            binding.textRemainingMessage.text = message
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}