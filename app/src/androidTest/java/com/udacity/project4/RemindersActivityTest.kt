package com.udacity.project4

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed

import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso

import com.google.android.material.internal.ContextUtils.getActivity
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.utils.EspressoIdlingResource
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test
private val dataBindingIdlingResource = DataBindingIdlingResource()

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
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


//    TODOc: add End to End testing to the app

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
    @Test
    fun showSnackBar_TitleError(){
        // launch activity
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
//        Go to SaveReminder
        onView(withId(R.id.addReminderFAB)).perform(click())
//        Save the reminder without a title
        onView(withId(R.id.saveReminder)).perform(click())
//        show snackbr
        onView(withText(R.string.err_enter_title)).check(matches(isDisplayed()))
        activityScenario.close()
    }
    @Test
    fun showSnackBar_Location(){
        // launch activity
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
//        Go to SaveReminder
        onView(withId(R.id.addReminderFAB)).perform(click())
//        enter the reminder  title
        onView(withId(R.id.reminderTitle)).perform(typeText("home"),
            closeSoftKeyboard())
//        Save the reminder without a location
        onView(withId(R.id.saveReminder)).perform(click())
//        show snack-bar
        onView(withText(R.string.err_select_location)).check(matches(isDisplayed()))
        activityScenario.close()
    }



    @Test
    fun saveReminder_showReminderSavedToast() = runBlocking{
         fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity? {
            var activity: Activity? = null
            activityScenario.onActivity {
                activity = it
            }
            return activity
        }

        // launch activity

        val activityScenario = launchActivity<RemindersActivity>()
        dataBindingIdlingResource.monitorActivity(activityScenario)
//        Go to SaveReminder
        onView(withId(R.id.addReminderFAB)).perform(click())

        onView(withId(R.id.reminderTitle)).perform(typeText("it pass on SM J600"))
      closeSoftKeyboard()
        //        Go to map
        onView(withId(R.id.selectLocation)).perform(click())
        //select form map
        onView(withId(R.id.mapView)).perform(longClick())
        onView(withId(R.id.button)).perform(click())
//        Go to SaveReminder
        onView(withId(R.id.saveReminder)).perform(click())

        //show toast
        onView(withText(R.string.reminder_saved))
            .inRoot(withDecorView(not((getActivity(activityScenario)?.window?.decorView)))).check(matches(isDisplayed()))

        activityScenario.close()
    }

}
