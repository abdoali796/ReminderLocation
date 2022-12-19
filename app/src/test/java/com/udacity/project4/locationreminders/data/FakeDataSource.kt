package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private val fakeReminder: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {

    //    TOcDO: Create a fake data source to act as a double to the real data source
    var shouldReturnError = false
    fun setshouldReturnError(v: Boolean) {
        shouldReturnError = v
    }

    override suspend fun getReminders(): com.udacity.project4.locationreminders.data.dto.Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Error")
        }
        return  com.udacity.project4.locationreminders.data.dto.Result.Success(
            ArrayList(
                fakeReminder
            ))

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        fakeReminder.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
if (shouldReturnError){
    return Result.Error("Error")
}
        val remind= fakeReminder.find { it.id==id }
        if (remind!=null){
            return com.udacity.project4.locationreminders.data.dto.Result.Success(remind)
        }else{
            return com.udacity.project4.locationreminders.data.dto.Result.Error("Reminder not found")
        }

    }

    override suspend fun deleteAllReminders() {
        fakeReminder.clear()
    }


}