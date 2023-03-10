package com.br.guardapaginas.classes.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.br.guardapaginas.classes.Gender;

import java.util.List;

@Dao
public interface GenderDAO {
    @Query("SELECT * FROM gender")
    List<Gender> getAll();

    @Query("SELECT * FROM gender WHERE id IN (:userIds)")
    List<Gender> fetchByIds(int[] userIds);

    @Query("SELECT * FROM gender WHERE id = :userId")
    Gender getById(int userId);

    @Insert
    void insert(Gender gender);

    @Update
    void update(Gender gender);

    @Delete
    void delete(Gender gender);
}
