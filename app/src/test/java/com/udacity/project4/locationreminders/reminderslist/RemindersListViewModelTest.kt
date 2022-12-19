package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {


    //    fakeDataSource class that acts as a test double
    private lateinit var dataSource: FakeDataSource

    // Subject under test
    private lateinit var remindersListViewModel: RemindersListViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mcoroutineRule = MCoroutineRule()

    @Before
    fun setupViewModel() {
        dataSource = FakeDataSource()
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
//   val r1=ReminderDTO("r","r","r",5.5,5.7)
//        val r2=ReminderDTO("fr","fr","r",5.5,5.7)

    }

    @After
    fun clean() = runBlocking { stopKoin() }


    @Test
    fun loadReminders_shouldReturnError_error() = mcoroutineRule.runBlockingTest {
        //give
        val r1 = ReminderDTO("r", "r", "r", 5.5, 5.7)
        dataSource.saveReminder(r1)
//when
        dataSource.shouldReturnError = true
        remindersListViewModel.loadReminders()
        //than
        assertThat(remindersListViewModel.showSnackBar.value, `is`("Error"))
    }

    @Test
    fun `loadReminders_Success_sizeof-data_2`() = mcoroutineRule.runBlockingTest {
        //clean
        dataSource.deleteAllReminders()
        //give
        val r1 = ReminderDTO("r", "r", "r", 5.5, 5.7)
        val r2 = ReminderDTO("fr", "fr", "r", 5.5, 5.7)
        dataSource.saveReminder(r1)
        dataSource.saveReminder(r2)
//when
        remindersListViewModel.loadReminders()
        //than
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().size, `is`(2))


    }

    @Test
    fun showLoad_checkLoading_TrueFales() = mcoroutineRule.runBlockingTest {
        //give
        val r1 = ReminderDTO("r", "r", "r", 5.5, 5.7)
//when
        mcoroutineRule.pauseDispatcher()
        dataSource.deleteAllReminders()
        dataSource.saveReminder(r1)
        remindersListViewModel.loadReminders()
        // than
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mcoroutineRule.resumeDispatcher()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))


    }

}