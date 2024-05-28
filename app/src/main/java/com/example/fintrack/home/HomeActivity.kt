package com.example.fintrack.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.room.Room
import com.example.fintrack.R
import com.example.fintrack.adapter.CategoryListAdapter
import com.example.fintrack.adapter.TransactionsAdapter
import com.example.fintrack.data.local.CategoryUiData
import com.example.fintrack.databinding.ActivityHomeBinding
import com.example.fintrack.db.CategoryDao
import com.example.fintrack.db.CategoryEntity
import com.example.fintrack.db.ExpenseDao
import com.example.fintrack.db.ExpenseDatabase
import com.example.fintrack.fragments.CreateExpenseFragment
import com.example.fintrack.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: TransactionsAdapter
    private lateinit var expenseDao: ExpenseDao
    private val homeViewModel: HomeViewModel by viewModels()

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ExpenseDatabase::class.java, "database-expense"
        ).build()
    }

    private val categoryDao: CategoryDao by lazy {
        db.getCategoryDao()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar_home)
        setSupportActionBar(toolbar)

        insertDefaultCategory()

        adapter = TransactionsAdapter(emptyList()) { transaction ->
            openDetailActivity(transaction)
        }

        val rvTransaction = binding.rvTransactions
        val rvCategory = binding.rvCategories

        val categoryAdapter = CategoryListAdapter()


        expenseDao = db.getExpenseDao()

        rvCategory.adapter = categoryAdapter
        getCategoriesFromDatabase(categoryAdapter)

        rvTransaction.adapter = adapter

        selectACategory(categoryAdapter)
        setupNewExpense()
        getTransactions()
        updateListTransactions()

    }

    private fun selectACategory(categoryAdapter: CategoryListAdapter) {
        categoryAdapter.setOnClickListener { selected ->
            val categoryTemp = categories.map { item ->
                when {
                    item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                    item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                    else -> item
                }
            }
        }
    }

    private fun updateListTransactions() {
        homeViewModel.expenseData.observe(this, Observer { expenses ->
            expenses?.let {
                adapter.updateTransactions(it)
            }
        })
    }

    private fun insertDefaultCategory() {
        val categoriesEntity = categories.map {
            CategoryEntity(
                name = it.name,
                isSelected = it.isSelected
            )
        }

        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insertAll(categoriesEntity)
        }

    }

    private fun getCategoriesFromDatabase(adapter: CategoryListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val categoriesFromDb: List<CategoryEntity> = categoryDao.getAll()
            val categoriesUiData = categoriesFromDb.map {
                CategoryUiData(
                    name = it.name,
                    isSelected = it.isSelected
                )
            }
            adapter.submitList(categoriesUiData)

        }

    }

    fun getTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val listTransactions = expenseDao.getAllExpense().collect { transactions ->
                homeViewModel.updateTransactions(transactions)
            }
        }
    }

    private fun setupNewExpense() {
        binding.fabModalNewExpense.setOnClickListener {
            openModalNewExpense()
        }
    }

    private fun openModalNewExpense() {
        val dialog = CreateExpenseFragment()
        dialog.show(supportFragmentManager, "CreateExpenseFragment")
    }

    private fun openDetailActivity(transaction: Transaction) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("TRANSACTION", transaction)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_more -> true
            R.id.menu_light -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

val categories = listOf(
    CategoryUiData(
        name = "ALL",
        isSelected = false
    ),
    CategoryUiData(
        name = "FOOD",
        isSelected = false
    ),
    CategoryUiData(
        name = "TRANSPORT",
        isSelected = false
    ),
    CategoryUiData(
        name = "ENTERTAINMENT",
        isSelected = false
    ),
    CategoryUiData(
        name = "HEALTH",
        isSelected = false
    ),
    CategoryUiData(
        name = "INTERNET",
        isSelected = false
    ),
    CategoryUiData(
        name = "HOME",
        isSelected = false
    ),
    CategoryUiData(
        name = "CLOTHE",
        isSelected = false
    ),
    CategoryUiData(
        name = "ELETRICITY",
        isSelected = false
    ),
    CategoryUiData(
        name = "GAS STATION",
        isSelected = false
    ),
    CategoryUiData(
        name = "GAMES",
        isSelected = false
    ),
    CategoryUiData(
        name = "OTHERS",
        isSelected = false
    ),
)

