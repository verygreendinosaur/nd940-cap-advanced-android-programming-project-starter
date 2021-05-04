package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(election: Election)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(elections: List<Election>)

    @Query("SELECT * FROM election_table")
    fun getElections(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table WHERE id = :id ")
    fun getElection(id: Int): LiveData<Election>

    @Query("DELETE FROM election_table WHERE id = :id ")
    fun delete(id: Int)

}
