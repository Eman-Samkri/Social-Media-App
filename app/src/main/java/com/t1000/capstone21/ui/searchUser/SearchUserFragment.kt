package com.t1000.capstone21.ui.searchUser

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.ItemUserFollowBinding
import com.t1000.capstone21.databinding.SearchUserFragmentBinding
import com.t1000.capstone21.models.User
import com.t1000.capstone21.ui.home.HomeFragmentDirections
import com.t1000.capstone21.ui.sticker.StickerFragmentDirections
import com.t1000.capstone21.utils.ConnectionManager
import com.t1000.capstone21.utils.hideKeyboard
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

        if (FirebaseAuth.getInstance().currentUser?.uid == null){
            findNavController().navigate(R.id.loginUserFragment)
        }

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

        when(ConnectionManager.isOnline(requireContext())){
            true -> binding.internet.visibility = View.GONE
            false -> binding.internet.visibility = View.VISIBLE
        }


        viewModel.fetchAllUser().observe(
            viewLifecycleOwner, Observer {
                    allUserList +=  it
            }
        )


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
                    binding.searchAnimation.visibility = View.GONE
                    if (charSequence.isEmpty()){
                        searchQueryList = mutableListOf()
                    }else{
                        for (user in allUserList) {
                            if (user.username.toLowerCase(Locale.ENGLISH).contains(
                                    charSequence.toLowerCase(Locale.ENGLISH))) {
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

    private inner class SearchUserHolder(val binding: ItemUserFollowBinding):
        RecyclerView.ViewHolder(binding.root),View.OnClickListener{


        private lateinit var currentUser: String

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(user: User){
            currentUser = user.userId
            binding.imageView.load(user.profilePictureUrl)
            binding.usernamTv.text = user.username
            Log.e(TAG, "bind: ${user.username}", )
        }

        override fun onClick(v: View?) {
            if (v == itemView){
                hideKeyboard()
                val action = SearchUserFragmentDirections.actionNavigationFindFrindToProfileFragment(currentUser)
                findNavController().navigate(action)
            }
        }

    }

    private inner class SearchUserAdapter(val usersFind:List<User>):
        RecyclerView.Adapter<SearchUserHolder>(){

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

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }


}