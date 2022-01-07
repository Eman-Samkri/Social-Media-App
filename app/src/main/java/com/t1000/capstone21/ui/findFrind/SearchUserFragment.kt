package com.t1000.capstone21.ui.findFrind

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.ItemUserFollowBinding
import com.t1000.capstone21.databinding.SearchUserFragmentBinding
import com.t1000.capstone21.models.User
import com.t1000.capstone21.ui.chat.ChatViewModel

private const val TAG = "SearchUserFragment"

class SearchUserFragment : Fragment() {

private lateinit var binding :SearchUserFragmentBinding

    private lateinit var adapter: SearchUserAdapter


    private val viewModel by lazy { ViewModelProvider(this).get(SearchUserViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = SearchUserFragmentBinding.inflate(layoutInflater)
        binding.findUser.layoutManager = LinearLayoutManager(context)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.fetchFollow().observe(
//            viewLifecycleOwner, Observer {
//                val followers = mutableListOf<User>()
//                // must check come from followers or following
//
//                it.following.forEach { usersFollow ->
//                    viewModel.fetchFollow(usersFollow).observe(
//                        viewLifecycleOwner, Observer { follower ->
//                            followers += follower
//                            binding.findUser.adapter = adapter
//                        }
//                    )
//                }
//            }
//                )

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search,menu)


        val searchItem =menu.findItem(R.id.searchAction)
        val searchView  = searchItem.actionView as SearchView

        searchView.apply {

            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    return true
                }
            })

            setOnSearchClickListener {
               // searchView.setQuery(viewModel.searchTerm,false)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.searchAction -> {
//                viewModel.sendQueryFetchPhotos("")
                true
            }

            else -> super.onOptionsItemSelected(item)


        }
    }

    private inner class SearchUserHolder(val binding: ItemUserFollowBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(user: User){
            binding.imageView.load(user.profilePictureUrl)
            binding.usernamTv.text = user.username
        }

    }

    private inner class SearchUserAdapter(val usersFind:List<User>):
        RecyclerView.Adapter<SearchUserHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserHolder {
            val binding = ItemUserFollowBinding.inflate(
                layoutInflater,
                parent,
                false
            )

            return SearchUserHolder(binding)

        }

        override fun onBindViewHolder(holder: SearchUserHolder, position: Int) {
            val userItem : User = usersFind[position]
            holder.bind(userItem)
        }

        override fun getItemCount(): Int = usersFind.size


    }
}