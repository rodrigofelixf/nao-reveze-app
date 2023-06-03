package br.com.rodrigo.naoreveze.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.rodrigo.naoreveze.R

import br.com.rodrigo.naoreveze.databinding.FragmentImcBinding
import br.com.rodrigo.naoreveze.ui.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class ImcFragment : Fragment() {
    private val binding: FragmentImcBinding by lazy {
        FragmentImcBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy { ViewModelProvider(this).get(UserViewModel::class.java) }

    private val args: ImcFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




    }

    override fun onResume() {
        super.onResume()

        initResultadoImc()
        openScreenImcCalculate()


    }


    private fun updateProgressBar() {
        binding.progressBar.progress = viewModel.resultado.value!!.toInt()
    }

    private fun initResultadoImc() {
        viewModel.altura.value = args.altura.toDouble()
        viewModel.peso.value = args.peso.toDouble()
        viewModel.calcularImc()


        viewModel.resultado.observe(this) { resultado ->
            updateProgressBar()
            if (resultado <= 39.9) {
                binding.textViewResultadoImc.text = "%.1f".format(resultado)
            } else {
                binding.textViewResultadoImc.text = getString(R.string.text_mais_de_40)
            }


            binding.textViewPeso.text = "${args.peso} Kg"
            binding.textViewAltura.text = "${args.altura} Cm"



            val tituloResultado = when {
                resultado < 18.5 -> getString(R.string.peso_abaixo)
                resultado < 25 -> getString(R.string.peso_normal)
                resultado < 30 -> getString(R.string.peso_sobrepeso)
                resultado < 35 -> getString(R.string.peso_grau01)
                resultado < 40 -> getString(R.string.peso_grau02)
                else -> getString(R.string.peso_grau03)
            }

            //  Regras do bottomsheet - manda informacoes para o bottomSheet
            val infoTextoBottomSheet = when {
                resultado < 18.5 -> getString(R.string.text_info_resultado_peso_abaixo)
                resultado < 25 -> getString(R.string.text_info_resultado_peso_normal)
                resultado < 30 -> getString(R.string.text_info_resultado_peso_sobrepeso)
                resultado < 35 -> getString(R.string.text_info_resultado_peso_obesidade01)
                resultado < 40 -> getString(R.string.text_info_resultado_peso_obesidade02)
                else -> getString(R.string.text_info_resultado_peso_obesidade03)
            }


            //  Regras do background - coloca um background na tabela do resultado do imc
            when {
                resultado < 18.5 -> binding.tableResultAbaixoPeso.setBackgroundResource(R.drawable.background_table)
                resultado < 25 -> binding.tableResultNormal.setBackgroundResource(R.drawable.background_table)
                resultado < 30 -> binding.tableResultSobrepesoPeso.setBackgroundResource(R.drawable.background_table)
                resultado < 35 -> binding.tableResultObesidade1Peso.setBackgroundResource(R.drawable.background_table)
                resultado < 40 -> binding.tableResultObesidade2Peso.setBackgroundResource(R.drawable.background_table)
                resultado > 39.9 -> binding.tableResultObesidade3Peso.setBackgroundResource(R.drawable.background_table)
            }


            // captura o resultado da variavel da regra do bottomsheet e coloca no evento de click do incone INFO
            binding.imageViewInfo.setOnClickListener {
                BottomSheetDialog(requireContext()).apply {
                    setContentView(R.layout.bottom_sheet_info_imc)
                    findViewById<TextView>(R.id.textView_imc_info)?.text = infoTextoBottomSheet
                    show()
                }
            }

            // validao de usuario que nao fez o calculo ainda - deixa os icones invisivel quando possivel
            if (resultado.isNaN()) {
                hideResultViews()
            } else {
                showResultViews(tituloResultado)
            }

        }
    }

    private fun openScreenImcCalculate() {
        binding.cardViewResultadoPeso.setOnClickListener {
            findNavController().navigate(R.id.action_imcFragment_to_calculateImcFragment)
        }
        binding.cardViewResultadoAltura.setOnClickListener {
            findNavController().navigate(R.id.action_imcFragment_to_calculateImcFragment)
        }
        binding.buttonTelaCalcular.setOnClickListener {
            findNavController().navigate(R.id.action_imcFragment_to_calculateImcFragment)
        }
    }

    private fun hideResultViews() {
        binding.imageViewInfo.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.textViewResultadoImc.visibility = View.GONE
        binding.textViewTituloTesultadoImc.text = getString(R.string.titulo_resultado_imc)
    }

    private fun showResultViews(tituloResultado: String) {
        binding.imageViewInfo.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.textViewResultadoImc.visibility = View.VISIBLE
        binding.textViewTituloTesultadoImc.text = tituloResultado
    }


}