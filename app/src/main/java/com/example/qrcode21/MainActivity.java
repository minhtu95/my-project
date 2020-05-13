package com.example.qrcode21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.qrcode21.R.drawable.danhba;


public class MainActivity extends AppCompatActivity {
    private SurfaceView cameraView;
    private TextView textQRcode;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private RelativeLayout QRcodeView;
    private Button btnQRcode;
    private ImageView imageQRcode;
    private Barcode lastBarcode;
    private Barcode lastBarcode2;
    private static final String TAG = "MainActivity";
    private String textCopy = "";
    GestureDetector gestureDetector;
    private int SWIPE_THRESHOLD = 50;
    private int SWIPE_VELOCITY_THRESHOLD = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageQRcode = (ImageView) findViewById(R.id.image_QRcode);
        QRcodeView = (RelativeLayout) findViewById(R.id.QRcode_View);
        btnQRcode = (Button) findViewById(R.id.btn_QRcode);
        textQRcode = (TextView) findViewById(R.id.text_QRcode);
        QRcodeView.setVisibility(View.GONE);
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        final Animation animationOn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_qrcode);
        final Animation animationOff = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_qrcodeoff);
        gestureDetector = new GestureDetector(MainActivity.this, new MyGesture());
        //cap quyen tru cap
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(cameraSource!=null){
                            try {
                                cameraSource.start(cameraView.getHolder());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "acc?",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 1 || barcodes.size() < 1) {
                    return;
                } else {
                    final Barcode barcode = barcodes.valueAt(0);

                    if (QRcodeView.isShown()) {
                        //xu li khi vuot tat thong bao QRcode
                        if (barcode.displayValue.equals(lastBarcode.displayValue)) {
                            textQRcode.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    gestureDetector.onTouchEvent(event);
                                    lastBarcode2 = barcode;
                                    Log.d(TAG, "onTouch: " + barcode.displayValue);
                                    return true;
                                }
                            });
                            imageQRcode.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    gestureDetector.onTouchEvent(event);
                                    lastBarcode2 = barcode;
                                    Log.d(TAG, "onTouch: " + barcode.displayValue);
                                    return true;
                                }
                            });
                            QRcodeView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    gestureDetector.onTouchEvent(event);
                                    lastBarcode2 = barcode;
                                    Log.d(TAG, "onTouch: " + barcode.displayValue);
                                    return true;
                                }
                            });
                        } else {
                            QRcodeView.startAnimation(animationOn);
                            switch (barcode.valueFormat) {
                                case Barcode.CONTACT_INFO:
                                    //hien thi thong bao khi cos ma QRcode truyen vao
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("GO");
                                            imageQRcode.setImageResource(danhba);
                                            textQRcode.setText(barcode.contactInfo.name.formattedName);

                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    //xu ly khi nhan vao button QRcode
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_INSERT);
                                            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                                            intent.putExtra(ContactsContract.Intents.Insert.NAME, barcode.contactInfo.name.formattedName);
                                            if (barcode.contactInfo.emails.length > 0) {
                                                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, barcode.contactInfo.emails[0].address);
                                            }
                                            if (barcode.contactInfo.phones.length > 0) {
                                                intent.putExtra(ContactsContract.Intents.Insert.PHONE, barcode.contactInfo.phones[0].number);
                                            }
                                            intent.putExtra(ContactsContract.Intents.Insert.COMPANY, barcode.contactInfo.organization);
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case Barcode.EMAIL:
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("GO");
                                            imageQRcode.setImageResource(R.drawable.mail11);
                                            textQRcode.setText(barcode.displayValue);
                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{barcode.email.address});
                                            intent.setType("texT/plan");
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case Barcode.PHONE:
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("Call");
                                            imageQRcode.setImageResource(R.drawable.phone11);
                                            textQRcode.setText(barcode.displayValue);
                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:" + barcode.phone.number));
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case Barcode.URL:
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("GO");
                                            imageQRcode.setImageResource(R.drawable.url11);
                                            textQRcode.setText(barcode.displayValue);
                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(barcode.url.url));
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case Barcode.CALENDAR_EVENT:
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("GO");
                                            imageQRcode.setImageResource(R.drawable.calendar11);
                                            textQRcode.setText(barcode.displayValue);
                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Calendar startTymVar = Calendar.getInstance();
                                            Calendar endTymVar = Calendar.getInstance();
                                            startTymVar.set(barcode.calendarEvent.start.year, barcode.calendarEvent.start.month, barcode.calendarEvent.start.day, barcode.calendarEvent.start.hours, barcode.calendarEvent.start.minutes);
                                            endTymVar.set(barcode.calendarEvent.end.year, barcode.calendarEvent.end.month, barcode.calendarEvent.end.day, barcode.calendarEvent.end.hours, barcode.calendarEvent.end.minutes);
                                            Intent intent = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI);
                                            intent.putExtra(CalendarContract.Events.TITLE, barcode.calendarEvent.summary);
                                            intent.putExtra(CalendarContract.Events.DESCRIPTION, barcode.calendarEvent.description);
                                            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, barcode.calendarEvent.location);
                                            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTymVar.getTimeInMillis());
                                            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTymVar.getTimeInMillis());
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case Barcode.GEO:
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("GO");
                                            imageQRcode.setImageResource(R.drawable.geo11);
                                            textQRcode.setText(barcode.displayValue);
                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setPackage("com.google.android.apps.maps");
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                default:
                                    if (textCopy.equals(barcode.displayValue)) {
                                        QRcodeView.post(new Runnable() {
                                            public void run() {
                                                btnQRcode.setText("Search");
                                                imageQRcode.setImageResource(R.drawable.gg);
                                                textQRcode.setText("Text already in clipboard: " + barcode.displayValue);
                                                QRcodeView.setVisibility(View.VISIBLE);
                                                textQRcode.setMovementMethod(new ScrollingMovementMethod());
                                            }
                                        });
                                        btnQRcode.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Uri uri = Uri.parse("http://www.google.com/#q=" + barcode.displayValue);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                                textCopy = barcode.displayValue;
                                            }
                                        });
                                    } else {
                                        QRcodeView.post(new Runnable() {
                                            public void run() {
                                                btnQRcode.setText("Copy");
                                                imageQRcode.setImageResource(R.drawable.text2);
                                                textQRcode.setText(barcode.displayValue);
                                                QRcodeView.setVisibility(View.VISIBLE);
                                                textQRcode.setMovementMethod(new ScrollingMovementMethod());
                                            }
                                        });
                                        btnQRcode.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Object clipboardService = getSystemService(CLIPBOARD_SERVICE);
                                                final ClipboardManager clipboardManager = (ClipboardManager) clipboardService;
                                                ClipData clipData = ClipData.newPlainText("text", barcode.displayValue);
                                                clipboardManager.setPrimaryClip(clipData);
                                                Toast.makeText(MainActivity.this, "Copy successful!", Toast.LENGTH_LONG).show();
                                                textCopy = barcode.displayValue;
                                            }
                                        });
                                    }
                                    break;

                            }

                        }
                    } else {

                        if (lastBarcode2 != null && barcode.displayValue.equals(lastBarcode2.displayValue)) {
                            return;
                        } else {
                            QRcodeView.startAnimation(animationOn);
                            switch (barcode.valueFormat) {
                                case Barcode.CONTACT_INFO:
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("GO");
                                            imageQRcode.setImageResource(danhba);
                                            textQRcode.setText(barcode.contactInfo.name.formattedName);

                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_INSERT);
                                            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                                            intent.putExtra(ContactsContract.Intents.Insert.NAME, barcode.contactInfo.name.formattedName);
                                            if (barcode.contactInfo.emails.length > 0) {
                                                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, barcode.contactInfo.emails[0].address);
                                            }
                                            if (barcode.contactInfo.phones.length > 0) {
                                                intent.putExtra(ContactsContract.Intents.Insert.PHONE, barcode.contactInfo.phones[0].number);
                                            }
                                            intent.putExtra(ContactsContract.Intents.Insert.COMPANY, barcode.contactInfo.organization);
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case Barcode.EMAIL:
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("GO");
                                            imageQRcode.setImageResource(R.drawable.mail11);
                                            textQRcode.setText(barcode.displayValue);
                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{barcode.email.address});
                                            intent.setType("texT/plan");
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case Barcode.PHONE:
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("Call");
                                            imageQRcode.setImageResource(R.drawable.phone11);
                                            textQRcode.setText(barcode.displayValue);
                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:" + barcode.phone.number));
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case Barcode.URL:
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("GO");
                                            imageQRcode.setImageResource(R.drawable.url11);
                                            textQRcode.setText(barcode.displayValue);
                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(barcode.url.url));
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case Barcode.CALENDAR_EVENT:
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("GO");
                                            imageQRcode.setImageResource(R.drawable.calendar11);
                                            textQRcode.setText(barcode.displayValue);
                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Calendar startTymVar = Calendar.getInstance();
                                            Calendar endTymVar = Calendar.getInstance();
                                            startTymVar.set(barcode.calendarEvent.start.year, barcode.calendarEvent.start.month, barcode.calendarEvent.start.day, barcode.calendarEvent.start.hours, barcode.calendarEvent.start.minutes);
                                            endTymVar.set(barcode.calendarEvent.end.year, barcode.calendarEvent.end.month, barcode.calendarEvent.end.day, barcode.calendarEvent.end.hours, barcode.calendarEvent.end.minutes);
                                            Intent intent = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI);
                                            intent.putExtra(CalendarContract.Events.TITLE, barcode.calendarEvent.summary);
                                            intent.putExtra(CalendarContract.Events.DESCRIPTION, barcode.calendarEvent.description);
                                            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, barcode.calendarEvent.location);
                                            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTymVar.getTimeInMillis());
                                            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTymVar.getTimeInMillis());
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case Barcode.GEO:
                                    QRcodeView.post(new Runnable() {
                                        public void run() {
                                            btnQRcode.setText("GO");
                                            imageQRcode.setImageResource(R.drawable.geo11);
                                            textQRcode.setText(barcode.displayValue);
                                            QRcodeView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    btnQRcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setPackage("com.google.android.apps.maps");
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                default:
                                    if (textCopy.equals(barcode.displayValue)) {
                                        QRcodeView.post(new Runnable() {
                                            public void run() {
                                                btnQRcode.setText("Search");
                                                imageQRcode.setImageResource(R.drawable.gg);
                                                textQRcode.setText("Text already in clipboard: " + barcode.displayValue);
                                                QRcodeView.setVisibility(View.VISIBLE);
                                                textQRcode.setMovementMethod(new ScrollingMovementMethod());
                                            }
                                        });
                                        btnQRcode.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Uri uri = Uri.parse("http://www.google.com/#q=" + barcode.displayValue);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                                textCopy = barcode.displayValue;
                                            }
                                        });
                                    } else {
                                        QRcodeView.post(new Runnable() {
                                            public void run() {
                                                btnQRcode.setText("Copy");
                                                imageQRcode.setImageResource(R.drawable.text2);
                                                textQRcode.setText(barcode.displayValue);
                                                QRcodeView.setVisibility(View.VISIBLE);
                                                textQRcode.setMovementMethod(new ScrollingMovementMethod());
                                            }
                                        });
                                        btnQRcode.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Object clipboardService = getSystemService(CLIPBOARD_SERVICE);
                                                final ClipboardManager clipboardManager = (ClipboardManager) clipboardService;
                                                ClipData clipData = ClipData.newPlainText("text", barcode.displayValue);
                                                clipboardManager.setPrimaryClip(clipData);
                                                Toast.makeText(MainActivity.this, "Copy successful!", Toast.LENGTH_LONG).show();
                                                textCopy = barcode.displayValue;
                                            }
                                        });
                                    }
                                    break;

                            }
                        }


                        //time
                    }


                    lastBarcode = barcode;
                }
            }
        });
    }

    //xu ly su kien vuot tren QRcode
    class MyGesture extends GestureDetector.SimpleOnGestureListener {
        final Animation animationOffDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_qrcodeoff);
        final Animation animationOffRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_qrcodeoffright);
        final Animation animationOffLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_qrcodeoffleft);

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e2.getX() - e1.getX() > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                QRcodeView.startAnimation(animationOffLeft);
                QRcodeView.setVisibility(View.GONE);

            }
            if (e1.getX() - e2.getX() > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                QRcodeView.startAnimation(animationOffRight);
                QRcodeView.setVisibility(View.GONE);

            }
            if (e2.getY() - e1.getY() > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                QRcodeView.startAnimation(animationOffDown);
                QRcodeView.setVisibility(View.GONE);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDown(MotionEvent e) {

            Log.d(TAG, "onDown: " + e.toString());
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp: " + e.toString());
            return super.onSingleTapUp(e);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(TAG, "onShowPress: " + e.toString());
            super.onShowPress(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress: " + e.toString());
            super.onLongPress(e);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
        barcodeDetector.release();
        // barcodeDetector.release();
    }
}

