package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TOfDO: Add testing implementation to the RemindersDao.kt
@get:Rule
var instantExecutorRule = InstantTaskExecutorRule()
//data fake
    val reminders1= ReminderDTO("fcf","fxf","laf",45.4,43.34)
    val reminders2= ReminderDTO("fff","fdf","ldf",4.54,43.324)
    val reminders3= ReminderDTO("fcf","faf","ldf",4.4,433.34)

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed."like the lessons"
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()


    }

    @After
    fun closeDb() = database.close()

@Test
fun saveReminder_insertdat_sizeNOTZERO()= runBlockingTest {
    //give
    database.reminderDao().saveReminder(reminders1)
    database.reminderDao().saveReminder(reminders2)
    database.reminderDao().saveReminder(reminders3)
//when
    val test=database.reminderDao().getReminders()
//than
assertThat(test.size ,`is`(3))

}
@Test
fun getReminders_emptybd_size0 ()= runBlockingTest {

    //no give

    //when
    val test=database.reminderDao().getReminders()
    //than
    assertThat(test.size ,`is`(0) )

}

@Test
fun  getReminderById_wrongId_null ()= runBlockingTest {
    //give
    database.reminderDao().saveReminder(reminders1)
//when
    val test=database.reminderDao().getReminderById("dd")
  //than
    assertThat(test ,`is`(nullValue()))
}
}