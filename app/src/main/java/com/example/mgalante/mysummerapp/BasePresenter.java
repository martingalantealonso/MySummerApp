package com.example.mgalante.mysummerapp;


public interface BasePresenter<T,V> {

    void attach(T context, V view);

}
