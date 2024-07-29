package com.example.appjava;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuditorViewModel extends ViewModel {
   private final MutableLiveData<String> mText;

   public AuditorViewModel() {
       mText = new MutableLiveData<>();
       mText.setValue("fragment do auditor ");
   }
    public LiveData<String> getText() {
        return mText;
    }
}