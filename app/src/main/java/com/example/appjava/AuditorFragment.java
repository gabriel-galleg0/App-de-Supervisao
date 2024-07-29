package com.example.appjava;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.appjava.databinding.FragmentAuditorBinding;

public class AuditorFragment extends Fragment {
    private FragmentAuditorBinding mBinding;
    private AuditorViewModel mViewModel;

    public static AuditorFragment newInstance() {
        return new AuditorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auditor, container, false);



    }


}