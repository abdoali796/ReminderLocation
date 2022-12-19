package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.udacity.project4.R
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    //test ba
    private lateinit var dataSource: FakeDataSource

    // Subject under test
    lateinit private var saveReminderViewModel: SaveReminderViewModel


    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        dataSource = FakeDataSource()
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @After
    fun clean() = runBlocking { stopKoin() }

    @Test
    fun onCl() {

//   Given fake date
        val dataFake: ReminderDataItem =
            ReminderDataItem("location", "reminderDescription", "location", 28.536920, 30.775890)
        saveReminderViewModel.latitude.value = dataFake.latitude
        saveReminderViewModel.longitude.value = dataFake.longitude
        saveReminderViewModel.reminderTitle.value = dataFake.title
        saveReminderViewModel.reminderDescription.value = dataFake.description
        saveReminderViewModel.reminderSelectedLocationStr.value = dataFake.location
        saveReminderViewModel.selectedPOI.value =
            PointOfInterest(LatLng(28.536920, 28.536920), "Title", "Description")

        // When Clear data
        saveReminderViewModel.onClear()


        // Then the check the action
        assertThat(saveReminderViewModel.reminderTitle.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.reminderDescription.getOrAwaitValue(), `is`(nullValue()))
        assertThat(
            saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(),
            `is`(nullValue())
        )
        assertThat(saveReminderViewModel.selectedPOI.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.latitude.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.longitude.getOrAwaitValue(), `is`(nullValue()))
    }

    @Test
    fun validateEnteredData_corrtData_null () {
        //give
        val reminder = ReminderDataItem("c", "t", "s", null, null)
//when
        saveReminderViewModel.validateAndSaveReminder(reminder)
//than
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(nullValue()))

    }

    @Test
    fun validateEnteredD_wrong_select_location () {
        //give
        val reminder = ReminderDataItem("null", "t", null, null, null)
//when
        saveReminderViewModel.validateEnteredData(reminder)
//than
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_select_location))

    }
}