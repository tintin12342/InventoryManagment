package com.nested.tin.inventar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CameraViewFragment extends Fragment {

    private SurfaceView mSurfaceView;
    private TextView mKameraText;
    private CameraSource mSource;
    private final int RequestCameraPermissionID = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera_view, container, false);

        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            Toolbar toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle("Skener");
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
        }

        mSurfaceView = v.findViewById(R.id.surficeView);
        mKameraText = v.findViewById(R.id.kameraText);

        BarcodeDetector mBarcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        mSource = new CameraSource.Builder(requireContext(), mBarcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (getActivity() != null && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            RequestCameraPermissionID);
                    return;
                }
                try {
                    mSource.start(mSurfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                mSource.stop();
            }
        });

        mBarcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0){

                    mKameraText.post(new Runnable() {
                        @Override
                        public void run() {
                            mSource.stop();

                            if (getActivity() != null){
                                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                if (vibrator != null){
                                    long[] pattern = {0, 100, 50, 100};
                                    vibrator.vibrate(pattern, -1);
                                }
                            }

                            CharSequence barkod = qrcodes.valueAt(0).displayValue;

                            Bundle bundle = new Bundle();
                            bundle.putCharSequence("Kod", barkod);

                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            TraziFragment traziFragment = new TraziFragment();
                            traziFragment.setArguments(bundle);

                            fragmentTransaction.replace(R.id.fragmentContainer, traziFragment);
                            fragmentTransaction.commit();

                        }
                    });
                }
            }
        });

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        mSource.start(mSurfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

}
