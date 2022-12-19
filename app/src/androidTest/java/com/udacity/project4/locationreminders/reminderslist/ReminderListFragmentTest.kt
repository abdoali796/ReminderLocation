package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import android.view.Display
import com.udacity.project4.R
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavAction
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest:AutoCloseKoinTest() {
    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    //    TOD O: test the navigation of the fragments.
//    TOD O: test the displayed data on the UI.
//    TOD O: add testing for the error messages.




    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }


    @Test
    fun clickTask_navigateToSaveReminderFragmentOne()= runBlockingTest {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
//dataBindingIdlingResource.monitorFragment(scenario)
        val navController = mock(NavController::class.java)

        scenario.onFragment{Navigation.setViewNavController(it.view!!, navController)}
        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )

    }

@Test
fun DisplayUI_date(){
    val reminders1= ReminderDTO("fcf","fxf","laf",45.4,43.34)
    runBlocking {
        repository.saveReminder(reminders1)
    }

    val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

    val navController = mock(NavController::class.java)
    scenario.onFragment{Navigation.setViewNavController(it.view!!, navController)}

    onView(withText(reminders1.title)).check(matches(isDisplayed()))
    onView(withText(reminders1.description)).check(matches(isDisplayed()))
    onView(withText(reminders1.title)).check(matches(isDisplayed()))
    scenario.onFragment{Navigation.setViewNavController(it.view!!, navController)}

}
@Test
fun Display_ondata(){
    runBlocking {
        repository.deleteAllReminders()
    }

    val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

    val navController = mock(NavController::class.java)
    scenario.onFragment{Navigation.setViewNavController(it.view!!, navController)}
    onView(withText(R.string.no_data)).check(matches(isDisplayed()))
    onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
}



}