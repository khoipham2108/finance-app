package com.example.financeapp.presentation.chat

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeapp.databinding.FragmentChatBinding

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatAdapter()
        binding.rvChat.apply {
            this.adapter = this@ChatFragment.adapter
            layoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
        }

        binding.btnSend.setOnClickListener {
            val msg = binding.etMessage.text.toString().trim()
            if (msg.isNotEmpty()) {
                viewModel.sendMessage(msg)
                binding.etMessage.text?.clear()
            }
        }

        viewModel.messages.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.toList())
            if (list.isNotEmpty()) binding.rvChat.smoothScrollToPosition(list.size - 1)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.btnSend.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}