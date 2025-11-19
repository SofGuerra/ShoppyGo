//package com.example.shoppygo;
//
//import android.os.Build;
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//
//public class AccountFragment extends Fragment {
//
//    EditText newCompanyName;
//    Spinner prodline;
//
//    public AccountFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_account, container, false);
//
//        newCompanyName = view.findViewById(R.id.newCompanyName);
//        prodline = view.findViewById(R.id.prodline);
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            newCompanyName.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
////                @Override
////                public void onDateChanged(DatePicker view, int year, int month, int day) {
////                    String pickdate = day + "/" + (month+1) + "/" + year;
////                    Toast.makeText(requireContext(), "you pick" + pickdate, Toast.LENGTH_SHORT).show();
////                }
////            });
////        }
////        return view;
//    }
//}