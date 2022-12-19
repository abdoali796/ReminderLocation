package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //    TODvO: Add testing implementation to the RemindersLocalRepository.kt
// Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var database: RemindersDatabase
 //   lateinit var dataSource: ReminderDataSource

    // Class under test
    lateinit var repository: RemindersLocalRepository

    @Before
    fun setUpRepository() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun cleanDatabase() = database.close()

    @Test
    fun saveReminder_addReminder_databaseNOTNULL() = runBlocking {
        //give ga
        val remindDao = ReminderDTO("test", "test", "test", 4.3, 4.4)
        //when
        repository.saveReminder(remindDao)
        val result = repository.getReminder(remindDao.id)
//than
        result as Result.Success<ReminderDTO>
        assertThat(result.data.equals(null), `is`(false))
    }


    @Test
    fun getReminders_noData_isNull() = runBlocking {
        //Give no data
        //
        val result = repository.getReminders()

        result as com.udacity.project4.locationreminders.data.dto.Result.Success
        assertThat(result.data.isEmpty(), `is`(true))
    }

    @Test
    fun deleteAllReminders_noData_isNull() = runBlocking {
        //Give  data

        val remindDao = ReminderDTO("test", "test", "test", 4.3, 4.4)
//when
        repository.saveReminder(remindDao)
        repository.deleteAllReminders()
        val result = repository.getReminder(remindDao.id)
//than
        result as com.udacity.project4.locationreminders.data.dto.Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }


}