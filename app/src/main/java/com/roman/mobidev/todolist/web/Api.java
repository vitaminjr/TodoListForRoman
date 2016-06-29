package com.roman.mobidev.todolist.web;

import com.roman.mobidev.todolist.models.EditTask;
import com.roman.mobidev.todolist.models.Task;
import com.roman.mobidev.todolist.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by roman on 25.04.16.
 */
public interface Api {

    @POST("users/sign_up")
    Call<User> signUp(@Body Map<String, User> map);

    @POST("users/sign_in")
    Call<Map<String, User>> signIn(@Body Map<String, User> map);

    @POST("tasks" )
    Call<Task> createTask(@Header("token") String token, @Body  EditTask task);

    @PUT("tasks/{id}")
    Call <Task> updateTask(@Header("token") String token, @Body Task task, @Path("id") long id);

    @DELETE("tasks/{id}")
    Call <Task> deleteTask(@Header("token") String token, @Path("id") long id);

    @GET("tasks")
    Call <List<Task>> getTasksOfIndex(@Header("token") String token);

}
