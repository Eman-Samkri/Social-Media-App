package com.t1000.capstone21.ui.searchUser

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
import java.util.*

private const val TAG = "SearchUserFragment"

class SearchUserFragment : Fragment() {

private lateinit var binding :SearchUserFragmentBinding


    private val viewModel by lazy { ViewModelProvider(this).get(SearchUserViewModel::class.java) }

    private lateinit var searchQueryList: MutableList<User>

    private  var allUserList: MutableList<User> = mutableListOf()
    

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

        viewModel.fetchAllUser().observe(
            viewLifecycleOwner, Observer {
                    allUserList +=  it
            }
        )

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
                override fun onQueryTextSubmit(charSequence: String?): Boolean {

                    return true
                }

                override fun onQueryTextChange(charSequence: String): Boolean {
                    searchQueryList = mutableListOf()

                    if (charSequence.isEmpty()){
                        searchQueryList = mutableListOf()
                    }else{
                        for (user in allUserList) {
                            if (user?.username?.toLowerCase(Locale.ENGLISH)?.contains(
                                    charSequence.toLowerCase(Locale.ENGLISH))!!) {
                                searchQueryList.add(user)
                            }
                        }
                    }
                    binding.findUser.adapter = SearchUserAdapter(searchQueryList)

                    return true
                }
            })

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.searchAction -> {

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